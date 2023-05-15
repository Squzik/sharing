package org.example.sharing.service.impl;

import org.example.sharing.rest.dto.request.FlatFilterRequestDTO;
import org.example.sharing.rest.dto.request.FlatRequestDTO;
import org.example.sharing.rest.dto.request.FlatUpdateRequestDto;
import org.example.sharing.rest.dto.response.BookingDatesResponseDTO;
import org.example.sharing.rest.dto.response.FlatResponseDTO;
import org.example.sharing.rest.dto.s3.ResponseStatus;
import org.example.sharing.rest.dto.s3.S3FileDto;
import org.example.sharing.rest.handler.exception.BadRequestException;
import org.example.sharing.rest.handler.exception.FileException;
import org.example.sharing.rest.handler.exception.NotFoundException;
import org.example.sharing.rest.mapper.FlatMapper;
import org.example.sharing.service.FlatService;
import org.example.sharing.service.s3.S3Service;
import org.example.sharing.store.entity.Booking;
import org.example.sharing.store.entity.File;
import org.example.sharing.store.entity.Flat;
import org.example.sharing.store.entity.Flat_;
import org.example.sharing.store.entity.ScienerLock;
import org.example.sharing.store.entity.User;
import org.example.sharing.store.entity.enums.BookingStatusEnum;
import org.example.sharing.store.repository.FileRepository;
import org.example.sharing.store.repository.FlatRepository;
import org.example.sharing.store.repository.ScienerLockRepository;
import org.example.sharing.store.repository.spetifications.FlatSpecification;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.example.sharing.rest.handler.exception.FileException.FILE_IS_BROKEN;
import static org.example.sharing.rest.handler.exception.NotFoundException.FLAT_NOT_FOUND;
import static org.example.sharing.utils.BeanUtilsHelper.getIgnoredPropertyNames;
import static org.example.sharing.utils.ServiceUtils.getUserIdFromCurrentSession;
import static org.example.sharing.utils.constant.s3.S3BucketConstant.PHOTOS;

@Service
@Transactional
@RequiredArgsConstructor
public class FlatServiceImpl implements FlatService {

    private final FlatRepository flatRepository;

    private final S3Service s3Service;

    private final ScienerLockRepository lockRepository;

    private final FlatMapper flatMapper;

    private final FileRepository fileRepository;

    @Override
    public FlatResponseDTO getFlatById(UUID flatId) {
        return mapFlatAndSetFlags(flatRepository.findById(flatId).orElseThrow(
                () -> new NotFoundException(FLAT_NOT_FOUND)
        ));
    }

    @Override
    public Set<FlatResponseDTO> getFlatByActiveUser() {
        Set<Flat> flats = flatRepository.findAllByUser_Id(getUserIdFromCurrentSession());
        return flats.stream()
                .map(this::mapFlatAndSetFlags)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<FlatResponseDTO> getEarlyBookedFlatsByClient() {
        Set<Flat> earlyBookedFlatSet = flatRepository.findAllFlatsByClientBookingHistory(
                getUserIdFromCurrentSession(),
                LocalDateTime.now()
        );
        return earlyBookedFlatSet.stream()
                .map(this::mapFlatAndSetFlags)
                .collect(Collectors.toSet());
    }

    private FlatResponseDTO mapFlatAndSetFlags(Flat flat) {
        UUID userId = getUserIdFromCurrentSession();
        FlatResponseDTO flatResponseDTO = flatMapper.flatToFlatResponseDTOWithFlags(
                flat,
                existsInClientFavorite(userId, flat.getFavouritesUsers()),
                existsActiveBookings(flat.getBookings()),
                existsClientActiveBooking(userId, flat.getBookings())
        );
        if (CollectionUtils.isNotEmpty(flat.getFiles())) {
            List<S3FileDto> photoList = flat.getFiles().stream()
                    .filter(file -> ResponseStatus.SUCCESS.equals(file.getStatus()))
                    .map(file -> s3Service.getFile(PHOTOS, file.getId()))
                    .collect(Collectors.toList());
            flatResponseDTO.setPhotos(photoList);
        }
        return flatResponseDTO;
    }

    private Boolean existsInClientFavorite(UUID userId, Set<User> likes) {
        if (likes != null && userId != null) {
            return likes.stream()
                    .anyMatch(user -> user.getId().equals(userId));
        }
        return false;
    }

    private BookingDatesResponseDTO existsClientActiveBooking(UUID userId, Set<Booking> bookings) {
        if (bookings != null && userId != null) {
            return bookings.stream()
                    .filter(booking -> LocalDateTime.now().isBefore(booking.getEnd())
                            && !BookingStatusEnum.BOOKING_WAITING.equals(booking.getBookingStatus()) &&
                            booking.getUser().getId().equals(userId))
                    .min((booking1, booking2) -> {
                        if (booking1.getStart().equals(booking2.getStart())) {
                            return 0;
                        } else if (booking1.getStart().isAfter(booking2.getStart())) {
                            return 1;
                        } else {
                            return -1;
                        }
                    })
                    .map(booking -> new BookingDatesResponseDTO(booking.getStart(), booking.getEnd()))
                    .orElse(null);
        }
        return null;
    }

    /**
     * Добавление квартиры в базу данных
     * При добавлении квартиры так-же добавляется адрес к которому привязана квартира
     * Так же конвертируется список id фотографий в persist объекты из базы данных, для их привязки
     *
     * @param flatDTO - объект квартиры для добавления в базу данных
     * @return - объект полученный после сохранения в базу данных
     */
    @Override
    public FlatResponseDTO addFlat(FlatRequestDTO flatDTO) {
        Flat flat = flatMapper.flatRequestDTOToFlat(flatDTO);
        if (CollectionUtils.isNotEmpty(flatDTO.getPhotoIds())) {
            List<File> files = flatDTO.getPhotoIds().stream()
                    .filter(uuid -> ResponseStatus.EXISTS.equals(
                            s3Service.getFile(PHOTOS, uuid).getResponseStatus()))
                    .map(uuid -> File.builder()
                            .id(uuid)
                            .flat(flat)
                            .bucket(PHOTOS)
                            .status(ResponseStatus.SUCCESS)
                            .build())
                    .collect(Collectors.toList());
            flat.setFiles(files);
        }
        if (Objects.nonNull(flatDTO.getScienerLockId())) {
            ScienerLock lock = lockRepository.findById(flatDTO.getScienerLockId()).orElseThrow(
                    () -> new NotFoundException(NotFoundException.SCIENER_LOCK_NOT_FOUND)
            );
            if (BooleanUtils.isTrue(lock.getIsLinked())) {
                flat.setScienerLock(lock);
            } else {
                throw new BadRequestException(BadRequestException.SCIENER_LOCK_EXISTS);
            }
        }
        flat.setIsHidden(true);
        return mapFlatAndSetFlags(flatRepository.save(flat));
    }

    /**
     * Удаление фотографии по ее id
     *
     * @param id - id фотографии в базе данных
     */
    @Override
    public void deleteFlat(UUID id) {
        Set<Booking> bookings = flatRepository.findById(id)
                .orElseThrow(
                        () -> new NotFoundException(NotFoundException.FLAT_NOT_FOUND)
                ).getBookings();
        if (!existsActiveBookings(bookings)) {
            flatRepository.deleteById(id);
        } else {
            throw new BadRequestException(BadRequestException.FLAT_HAVE_ACTIVE_BOOKING);
        }
    }

    @Override
    public void deleteAllUserFlats() {
        UUID userId = getUserIdFromCurrentSession();
        Set<Flat> flatSet = flatRepository.findAllByUser_Id(userId);
        if (!flatSet.isEmpty() && flatSet.stream().noneMatch(flat -> existsActiveBookings(flat.getBookings()))) {
            flatRepository.deleteAllByUser_Id(userId);
        } else {
            throw new BadRequestException(BadRequestException.FLAT_HAVE_ACTIVE_BOOKING);
        }
    }

    private boolean existsActiveBookings(Set<Booking> bookings) {
        if (bookings == null) {
            return false;
        }
        return bookings.stream()
                .anyMatch(booking -> LocalDateTime.now()
                        .isBefore(booking.getEnd())
                        && BookingStatusEnum.BOOKING_ACCEPT.equals(booking.getBookingStatus()));
    }

    @Override
    public boolean editViewStatus(UUID id) {
        Flat flat = flatRepository.findById(id)
                .orElseThrow(
                        () -> new NotFoundException(FLAT_NOT_FOUND)
                );
        flat.setIsHidden(!flat.getIsHidden());
        flat = flatRepository.save(flat);
        return flat.getIsHidden();
    }

    @Override
    public List<FlatResponseDTO> filterFlat(FlatFilterRequestDTO flatFilterRequestDTO) {
        Collection<Flat> flats = flatRepository.findAll(FlatSpecification.filterBy(flatFilterRequestDTO));
        List<FlatResponseDTO> flatResponseDTOS = flats.stream()
                .map(this::mapFlatAndSetFlags)
                .collect(Collectors.toList());
        if (StringUtils.isNotEmpty(flatFilterRequestDTO.getSortField())) {
            switch (flatFilterRequestDTO.getSortField()) {
                case "price": {
                    flatResponseDTOS.sort(Comparator.comparing(FlatResponseDTO::getPrice));
                    break;
                }
                case "city": {
                    flatResponseDTOS.sort(Comparator.comparing(flatResponseDTO -> flatResponseDTO.getAddress()
                            .getCity()));
                    break;
                }
                default: {
                    throw new BadRequestException("Сортировка по полю " + flatFilterRequestDTO.getSortField() + " не возможна");
                }
            }
        }
        return flatResponseDTOS;
    }

    @Override
    public FlatResponseDTO updateFlat(UUID flatId, FlatUpdateRequestDto flatUpdateRequestDto) {
        return flatRepository.findById(flatId)
                .map(persistentFlat -> {
                    Flat flat = flatMapper.updateDtoToEntity(flatUpdateRequestDto);
                    BeanUtils.copyProperties(flat, persistentFlat,
                            getIgnoredPropertyNames(
                                    flat,
                                    Flat_.NAME, Flat_.ADDRESS, Flat_.FLOOR, Flat_.NUMBER_OF_ROOMS,
                                    Flat_.IS_COMBINED_BATHROOM, Flat_.FURNITURE, Flat_.BALCONY,
                                    Flat_.APPLIANCES, Flat_.INTERNET_CABLE_TV, Flat_.PRICE,
                                    Flat_.SCIENER_LOCK, Flat_.DESCRIPTION)
                    );
                    if (CollectionUtils.isNotEmpty(flatUpdateRequestDto.getDeletePhotoIds())) {
                        flatUpdateRequestDto.getDeletePhotoIds().forEach(uuid -> {
                            Optional<File> optionalFile = persistentFlat.getFiles().stream()
                                    .filter(file -> file.getId().equals(uuid))
                                    .findFirst();
                            optionalFile.ifPresent(file -> {
                                s3Service.deleteFile(PHOTOS, uuid);
                                persistentFlat.getFiles().remove(file);
                            });
                        });
                    }
                    if (CollectionUtils.isNotEmpty(flatUpdateRequestDto.getAddPhotos())) {
                        List<File> fileIdList = flatUpdateRequestDto.getAddPhotos().stream()
                                .map(file -> createFile(file, flat))
                                .collect(Collectors.toList());
                        persistentFlat.getFiles().addAll(fileIdList);
                    }
                    return persistentFlat;
                })
                .map(this::mapFlatAndSetFlags)
                .orElseThrow(() -> new
                        NotFoundException(FLAT_NOT_FOUND));
    }

    private File createFile(MultipartFile file, Flat flat) {
        try {
            UUID uuid = UUID.randomUUID();
            File build = File.builder()
                    .id(uuid)
                    .flat(flat)
                    .status(ResponseStatus.IN_PROGRESS)
                    .bucket(PHOTOS)
                    .build();
            fileRepository.save(build);
            s3Service.putFile(PHOTOS, uuid, file.getBytes());
            return build;
        } catch (IOException e) {
            throw new FileException(FILE_IS_BROKEN);
        }
    }
}