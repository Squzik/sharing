package org.example.sharing.service.impl;

import org.example.sharing.rest.dto.request.AddFileRequestDto;
import org.example.sharing.rest.dto.request.BookingRequestDTO;
import org.example.sharing.rest.dto.response.BookingActiveResponseDTO;
import org.example.sharing.rest.dto.response.BookingDatesResponseDTO;
import org.example.sharing.rest.dto.response.BookingResponseDTO;
import org.example.sharing.rest.dto.s3.ResponseStatus;
import org.example.sharing.rest.handler.exception.BadRequestException;
import org.example.sharing.rest.handler.exception.FileException;
import org.example.sharing.rest.handler.exception.NotFoundException;
import org.example.sharing.rest.handler.exception.UnAuthorisedException;
import org.example.sharing.rest.mapper.BookingMapper;
import org.example.sharing.service.BookingService;
import org.example.sharing.service.ScienerService;
import org.example.sharing.service.s3.S3Service;
import org.example.sharing.store.entity.Booking;
import org.example.sharing.store.entity.File;
import org.example.sharing.store.entity.Flat;
import org.example.sharing.store.entity.ScienerLock;
import org.example.sharing.store.entity.enums.BookingStatusEnum;
import org.example.sharing.store.repository.BookingRepository;
import org.example.sharing.store.repository.FileRepository;
import org.example.sharing.store.repository.FlatRepository;
import org.example.sharing.store.repository.UserRepository;
import org.example.sharing.store.repository.spetifications.BookingSpecification;
import org.example.sharing.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static org.example.sharing.rest.handler.exception.FileException.FILE_IS_BROKEN;
import static org.example.sharing.utils.ServiceUtils.checkNulls;
import static org.example.sharing.utils.ServiceUtils.equalsClientIdWithCurrentSession;
import static org.example.sharing.utils.ServiceUtils.getUserIdFromCurrentSession;
import static org.example.sharing.utils.constant.s3.S3BucketConstant.DOCUMENTS;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final FlatRepository flatRepository;

    private final UserRepository userRepository;

    private final BookingMapper bookingMapper;

    private final ScienerService scienerService;

    private final S3Service s3Service;

    private final FileRepository fileRepository;

    /**
     * Добавление аренды в базу данных
     * Отправка уведомления на почту арендатора и хозяина квартиры
     *
     * @param bookingDto - dto аренды с данными
     * @return responseDto аренды
     */
    public BookingResponseDTO addBooking(BookingRequestDTO bookingDto) {
        if (!BookingStatusEnum.BOOKING_WAITING.equals(bookingDto.getBookingStatus())
                && !BookingStatusEnum.RENT_DENY.equals(bookingDto.getBookingStatus())) {
            throw new BadRequestException(BadRequestException.BOOKING_WRONG_STATUS);
        }
        if (LocalDateTime.now().isAfter(bookingDto.getStartDateAndTime())
                || LocalDateTime.now().isAfter(bookingDto.getEndDateAndTime())
                || bookingDto.getEndDateAndTime().isBefore(bookingDto.getStartDateAndTime())) {
            throw new BadRequestException(BadRequestException.WRONG_BOOKING_DATES);
        }
        Booking booking = bookingMapper.fromDto(bookingDto);
        Flat flat = flatRepository.findById(bookingDto.getFlatId()).orElseThrow(
                () -> new NotFoundException(NotFoundException.FLAT_NOT_FOUND)
        );
        if (BooleanUtils.isFalse(flat.getIsHidden())) {
            throw new BadRequestException(BadRequestException.FLAT_INACTIVE);
        }
        booking.setFlat(flat);
        booking.setUser(userRepository.findById(ServiceUtils.getUserIdFromCurrentSession()).orElseThrow(
                () -> new NotFoundException(NotFoundException.CLIENT_NOT_FOUND)
        ));
        bookingRepository.saveAndFlush(booking);
        return bookingMapper.toDto(booking);
    }

    /**
     * Изменение статуса аренды на подтвержденный или отклоненный
     * При подтверждении создается ключ в привязанном к квартире замке
     *
     * @param bookingId - id аренды
     * @param status    -  статус, который присвоил хозяин
     * @return - responseDto аренлы
     */
    @Override
    public BookingResponseDTO editBookingStatus(UUID bookingId, BookingStatusEnum status) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException(NotFoundException.BOOKING_NOT_FOUND)
        );
        if (!BookingStatusEnum.BOOKING_WAITING.equals(booking.getBookingStatus())) {
            throw new BadRequestException(BadRequestException.BOOKING_EDITED_STATUS);
        }
        booking.setBookingStatus(status);
        if (BookingStatusEnum.BOOKING_ACCEPT.equals(status)) {
            createAndSetCode(booking);
        }
        return bookingMapper.toDto(booking);
    }

    private void createAndSetCode(Booking booking) {
        ScienerLock lock = booking.getFlat().getScienerLock();
        if (lock != null && BooleanUtils.isTrue(lock.getIsLinked())) {
            booking.setCode(
                    scienerService.createCode(
                            lock,
                            booking.getStart(),
                            booking.getEnd()
                    )
            );
        } else {
            throw new NotFoundException(NotFoundException.SCIENER_LOCK_NOT_FOUND);
        }
    }

    /**
     * Открытие замка квартиры при активной аренде
     *
     * @param bookingId - id аренды
     */
    @Override
    public void openBookingLock(@NotNull UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException(NotFoundException.BOOKING_FLAT_NOT_FOUND)
        );
        equalsClientIdWithCurrentSession(booking.getUser().getId());
        if (!BookingStatusEnum.BOOKING_ACCEPT.equals(booking.getBookingStatus())) {
            throw new BadRequestException(BadRequestException.BOOKING_NOT_APPROVED);
        }
        if (isBookingTimeRangeActive(booking.getStart(), booking.getEnd())) {
            throw new BadRequestException(BadRequestException.BOOKING_EXPIRED);
        }
        if (booking.getFlat().getScienerLock() != null) {
            scienerService.openLock(booking.getFlat().getScienerLock());
        } else {
            throw new NotFoundException(NotFoundException.SCIENER_LOCK_NOT_FOUND);
        }
    }

    /**
     * Удаление аренды
     *
     * @param bookingId - id аренды
     * @throws UnAuthorisedException - ошибки связанные с работой ScienerApi
     */
    @Override
    public void deleteBooking(@NotNull UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException(NotFoundException.BOOKING_FLAT_NOT_FOUND)
        );
        if (BookingStatusEnum.BOOKING_ACCEPT.equals(booking.getBookingStatus())
                && LocalDateTime.now().isAfter(booking.getEnd())) {
            throw new BadRequestException(BadRequestException.BOOKING_WAS_APPROVE);
        }
        if (checkNulls(booking.getFlat().getScienerLock(), booking.getCode()) &&
                booking.getFlat().getScienerLock().getId().equals(booking.getCode().getLock().getId())) {
            scienerService.deleteCode(
                    booking.getCode()
            );
        }
        bookingRepository.delete(booking);
    }

    private List<BookingActiveResponseDTO> bookingToActiveResponse(List<Booking> bookings) {
        Map<Integer, Boolean> locksGatewayStatus = scienerService.checkLocksGatewayExists(
                bookings.stream().map(booking -> booking.getCode().getLock()).collect(Collectors.toSet())
        );
        return bookings.stream()
                .map(
                        booking -> bookingMapper.toActiveDto(
                                booking,
                                locksGatewayStatus.get(booking.getCode().getLock().getId())
                        )
                ).collect(Collectors.toList());
    }

    @Override
    public List<BookingActiveResponseDTO> getActiveBooking() {
        return bookingToActiveResponse(
                bookingRepository.findAll(Specification
                        .where(BookingSpecification.getByUserId(getUserIdFromCurrentSession()))
                        .and(BookingSpecification.getByStatuses(Set.of(BookingStatusEnum.BOOKING_ACCEPT)))
                        .and(BookingSpecification.getByEndTime(LocalDateTime.now(), false))
                )
        );
    }

    @Override
    public List<BookingResponseDTO> getWaitingBookingForLandlord() {
        List<Booking> waitingLandlordBookingSet = bookingRepository.findAll(Specification
                .where(BookingSpecification.getByFlatUserId(getUserIdFromCurrentSession()))
                .and(BookingSpecification.getByStatuses(Set.of(BookingStatusEnum.BOOKING_WAITING)))
        );
        return waitingLandlordBookingSet.stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDTO> getWaitingBookingForRented() {
        List<Booking> waitingRentedBookingSet = bookingRepository.findAll(Specification
                .where(BookingSpecification.getByUserId(getUserIdFromCurrentSession()))
                .and(BookingSpecification.getByStatuses(Set.of(BookingStatusEnum.BOOKING_WAITING)))
        );
        return waitingRentedBookingSet.stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Получение дат начала и конца аренд определенной квартиры
     *
     * @param flatId - id квартиры
     * @return массив с датами аренд
     */
    @Override
    public List<BookingDatesResponseDTO> getBookingsDatesByFlatId(@NotNull UUID flatId) {
        if (!flatRepository.existsById(flatId)) {
            throw new NotFoundException(NotFoundException.FLAT_NOT_FOUND);
        }
        List<Booking> bookingDatesSet = bookingRepository.getStartAndEndByFlatIdAndBookingStatusNotAndEndAfter(
                flatId,
                BookingStatusEnum.BOOKING_DENY,
                LocalDateTime.now()
        );
        return bookingDatesSet.stream()
                .map(bookingMapper::toDatesDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDTO> getFlatActiveBooking(UUID flatId) {
        if (!flatRepository.existsById(flatId)) {
            throw new NotFoundException(NotFoundException.FLAT_NOT_FOUND);
        }
        List<Booking> flatActiveBookingSet = bookingRepository.findAll(Specification
                .where(BookingSpecification.getByFlatId(flatId))
                .and(BookingSpecification.getByFlatUserId(getUserIdFromCurrentSession()))
                .and(BookingSpecification.getByStatuses(Set.of(BookingStatusEnum.BOOKING_ACCEPT)))
                .and(BookingSpecification.getByEndTime(LocalDateTime.now(), true))
        );
        return flatActiveBookingSet.stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDTO> getFlatWaitingBooking(UUID flatId) {
        if (!flatRepository.existsById(flatId)) {
            throw new NotFoundException(NotFoundException.FLAT_NOT_FOUND);
        }
        List<Booking> flatWaitingBookingSet = bookingRepository.findAll(Specification
                .where(BookingSpecification.getByFlatId(flatId))
                .and(BookingSpecification.getByFlatUserId(getUserIdFromCurrentSession()))
                .and(BookingSpecification.getByStatuses(Set.of(BookingStatusEnum.BOOKING_WAITING)))
        );
        return flatWaitingBookingSet.stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDTO> getFlatHistoryBooking(UUID flatId) {
        if (!flatRepository.existsById(flatId)) {
            throw new NotFoundException(NotFoundException.FLAT_NOT_FOUND);
        }
        List<Booking> flatHistorySet = bookingRepository.findAll(Specification
                .where(BookingSpecification.getByFlatId(flatId))
                .and(BookingSpecification.getByFlatUserId(getUserIdFromCurrentSession()))
                .and(BookingSpecification.getByStatuses(Set.of(BookingStatusEnum.BOOKING_ACCEPT, BookingStatusEnum.BOOKING_DENY)))
                .and(BookingSpecification.getByEndTime(LocalDateTime.now(), false))
        );
        return flatHistorySet.stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    private Boolean isBookingTimeRangeActive(LocalDateTime start, LocalDateTime end) {
        return LocalDateTime.now().isAfter(start) && LocalDateTime.now().isBefore(end);
    }

    @Override
    public String addFile(UUID id, AddFileRequestDto file) {
        UUID fileUuid = UUID.randomUUID();
        Executors.newCachedThreadPool().submit(() -> {
            try {
                Booking booking = bookingRepository.findById(id).orElseThrow(
                        () -> new NotFoundException(NotFoundException.BOOKING_NOT_FOUND));
                File fileEntity = File.builder()
                        .id(fileUuid)
                        .bucket(DOCUMENTS)
                        .booking(booking)
                        .status(ResponseStatus.IN_PROGRESS)
                        .build();
                fileRepository.save(fileEntity);
                s3Service.putFile(DOCUMENTS, fileUuid, file.getFile().getBytes());
            } catch (IOException e) {
                log.error(e.getMessage());
                throw new FileException(FILE_IS_BROKEN);
            }
        });
        return fileUuid.toString();
    }

    @Override
    public void deleteFile(UUID bookingId, UUID documentId) {
        bookingRepository.deleteById(documentId);
        Executors.newCachedThreadPool().submit(() -> s3Service.deleteFile(DOCUMENTS, documentId));
    }
}