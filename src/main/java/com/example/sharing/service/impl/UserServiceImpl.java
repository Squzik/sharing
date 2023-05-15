package org.example.sharing.service.impl;

import org.example.sharing.rest.dto.request.UserRequestDTO;
import org.example.sharing.rest.dto.response.FlatResponseDTO;
import org.example.sharing.rest.dto.response.UserResponseDTO;
import org.example.sharing.rest.dto.s3.ResponseStatus;
import org.example.sharing.rest.dto.s3.S3FileDto;
import org.example.sharing.rest.handler.exception.NotFoundException;
import org.example.sharing.rest.mapper.FlatMapper;
import org.example.sharing.rest.mapper.UserMapper;
import org.example.sharing.service.UserService;
import org.example.sharing.service.s3.S3Service;
import org.example.sharing.store.entity.File;
import org.example.sharing.store.entity.Flat;
import org.example.sharing.store.entity.User;
import org.example.sharing.store.entity.User_;
import org.example.sharing.store.repository.FlatRepository;
import org.example.sharing.store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.example.sharing.rest.handler.exception.NotFoundException.CLIENT_NOT_FOUND;
import static org.example.sharing.rest.handler.exception.NotFoundException.FLAT_NOT_FOUND;
import static org.example.sharing.utils.BeanUtilsHelper.getIgnoredPropertyNames;
import static org.example.sharing.utils.constant.s3.S3BucketConstant.PHOTOS;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final FlatRepository flatRepository;

    private final FlatMapper flatMapper;

    private final S3Service s3Service;

    /**
     * Добавление квартиры в избранное
     *
     * @param userId - id пользователя в базе данных
     * @param flatId - id квартиры в базе данных
     * @return - response клиента, в котором есть список избранных квартир
     */
    @Override
    @Transactional
    public UserResponseDTO addFavoriteFlat(UUID userId, UUID flatId) {
        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new NotFoundException(CLIENT_NOT_FOUND)
                );
        Flat flat = flatRepository.findById(flatId)
                .orElseThrow(
                        () -> new NotFoundException(FLAT_NOT_FOUND)
                );
        user.getFavouritesFlats().add(flat);
        userRepository.saveAndFlush(user);
        UserResponseDTO userResponseDTO = userMapper.toDTO(user);
        File avatar = user.getFile();
        if (Objects.nonNull(avatar)) {
            S3FileDto file = s3Service.getFile(PHOTOS, avatar.getId());
            userResponseDTO.setAvatarFile(file);
        }
        return userResponseDTO;
    }

    @Override
    @Transactional
    public UserResponseDTO update(UUID id, UserRequestDTO userRqDTO) {
        //TODO: добавить в обновление avatarFile
        return userRepository.findById(id)
                .map(persistentUser -> {
                    var user = userMapper.toEntity(userRqDTO);
                    BeanUtils.copyProperties(user, persistentUser,
                            getIgnoredPropertyNames(
                                    user,
                                    User_.PASSWORD,
                                    User_.SCIENER_USER, User_.REVIEWS, User_.FLATS, User_.FAVOURITES_FLATS,
                                    User_.BOOKINGS, User_.FIRST_USER, User_.SECOND_USER, User_.SENDER, User_.MAIL_CONFIRMATION,
                                    User_.PASSPORT_VERIFY_STATUS, User_.USER_ROLE, User_.OWNED_BLACK_LISTS, User_.MEMBER_BLACK_LISTS
                            )
                    );
                    return persistentUser;
                })
                .map(userMapper::toDTO)
                .orElseThrow(() -> new NotFoundException(CLIENT_NOT_FOUND));
    }

    @Override
    public Set<FlatResponseDTO> getFavoriteByUserId(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(CLIENT_NOT_FOUND)
        );
        return user.getFavouritesFlats().stream()
                .map(flat -> {
                    var dto = flatMapper.flatToFlatResponseDTO(flat);
                    List<File> files = flat.getFiles();
                    if (CollectionUtils.isNotEmpty(files)) {
                        List<S3FileDto> photos = files
                                .stream()
                                .filter(file -> ResponseStatus.SUCCESS.equals(file.getStatus()))
                                .map(file -> s3Service.getFile(PHOTOS, file.getId()))
                                .collect(Collectors.toList());
                        dto.setPhotos(photos);
                    }
                    return dto;
                })
                .collect(Collectors.toSet());
    }

    /**
     * Удаление квартиры из избранного пользователя
     *
     * @param userId - id клиента в базе данных
     * @param flatId - id квартиры в базе данных
     * @return - response клиенат, в котором есть список избранных квартир
     */
    @Override
    @Transactional
    public UserResponseDTO deleteFavoriteFlat(UUID userId, UUID flatId) {
        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new NotFoundException(CLIENT_NOT_FOUND)
                );
        Flat flat = flatRepository.findById(flatId)
                .orElseThrow(
                        () -> new NotFoundException(FLAT_NOT_FOUND)
                );
        user.getFavouritesFlats()
                .remove(flat);
        userRepository.saveAndFlush(user);
        UserResponseDTO userResponseDTO = userMapper.toDTO(user);
        File file = user.getFile();
        if (Objects.nonNull(file)) {
            userResponseDTO.setAvatarFile(s3Service.getFile(PHOTOS, file.getId()));
        }
        return userResponseDTO;
    }

    /**
     * Получение клиента по его id  базе данных
     *
     * @param userId - id клиента в базе данных
     * @return - response клиента из базы данных
     */
    @Override
    public UserResponseDTO getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new NotFoundException(CLIENT_NOT_FOUND)
                );
        UserResponseDTO userResponseDTO = userMapper.toDTO(user);
        File file = user.getFile();
        if (Objects.nonNull(file)) {
            userResponseDTO.setAvatarFile(s3Service.getFile(PHOTOS, file.getId()));
        }
        return userResponseDTO;
    }
}
