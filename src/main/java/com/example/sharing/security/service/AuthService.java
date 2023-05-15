package org.example.sharing.security.service;

import org.example.sharing.rest.dto.request.UpdatePasswordRequestDTO;
import org.example.sharing.rest.dto.request.UserRequestDTO;
import org.example.sharing.rest.dto.response.UserResponseDTO;
import org.example.sharing.rest.handler.exception.BadRequestException;
import org.example.sharing.rest.handler.exception.NotFoundException;
import org.example.sharing.rest.handler.exception.UnAuthorisedException;
import org.example.sharing.rest.mapper.UserMapper;
import org.example.sharing.security.jwt.JwtRequest;
import org.example.sharing.security.jwt.JwtResponse;
import org.example.sharing.service.MailSenderService;
import org.example.sharing.store.entity.MailConfirmation;
import org.example.sharing.store.entity.User;
import org.example.sharing.store.repository.MailConformationRepository;
import org.example.sharing.store.repository.UserRepository;
import org.example.sharing.utils.ServiceUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final Map<String, String> refreshStorage = new HashMap<>();
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final MailConformationRepository mailConformationRepository;

    private final static String VERIFICATION_CODE_MESSAGE =
            "Доброго времени суток, данное сообщение было отправлено в связи с регистрацией в приложении \"Flat-renta\".\n" +
                    "Оно содержит код для подтверждения электронной почты пользователя и дальнейшей активации аккаунта в приложении.\n" +
                    "Код активен на протяжении %d секунд с момента отправки сообщения.\n\n" +
                    "Код: %d";

    private final static String PASSWORD_RECOVERY_MESSAGE =
            "Доброго времени суток, данное сообщение было отправлено в связи с восстановлением пароля в приложении \"Flat-renta\".\n" +
                    "Оно содержит код для подтверждения электронной почты пользователя и дальнейшего восстановления доступа к аккаунту в приложении.\n" +
                    "Код активен на протяжении %d секунд с момента отправки сообщения.\n\n" +
                    "Код: %d";

    private final MailSenderService mailSenderService;

    private static final Integer LIMIT_OF_ATTEMPTS = 3;

    private static final Integer INTERVAL_BEFORE_LIMIT = 120;

    private static final Integer INTERVAL_AFTER_LIMIT = 240;

    public JwtResponse login(@NonNull JwtRequest authRequest) {
        User user = userRepository.findByMail(authRequest.getLogin())
                .orElseThrow(
                        () -> new NotFoundException(NotFoundException.CLIENT_WITH_CURRENT_EMAIL_NOT_FOUND)
                );
        if (!user.getIsMailConfirm()) {
            throw new UnAuthorisedException(UnAuthorisedException.MAIL_IS_NOT_CONFIRM);
        }
        if (passwordEncoder.matches(String.valueOf(authRequest.getPassword()), user.getPassword())) {
            if (Objects.nonNull(user.getMailConfirmation())) {
                mailConformationRepository.delete(user.getMailConfirmation());
            }
            final String accessToken = jwtProvider.generateAccessToken(user.getMail(), user.getId());
            final String refreshToken = jwtProvider.generateRefreshToken(user.getMail());
            refreshStorage.put(user.getMail(), refreshToken);
            return new JwtResponse(accessToken, refreshToken, user.getId());
        } else {
            throw new UnAuthorisedException(UnAuthorisedException.INCORRECT_PASSWORD);
        }
    }

    public JwtResponse getAccessToken(@NonNull String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final String email = jwtProvider.getRefreshClaims(refreshToken)
                    .getSubject();
            final String saveRefreshToken = refreshStorage.get(email);
            User user = userRepository.findByMail(email)
                    .orElseThrow(
                            () -> new NotFoundException(NotFoundException.CLIENT_WITH_CURRENT_EMAIL_NOT_FOUND)
                    );
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final String accessToken = jwtProvider.generateAccessToken(email, user.getId());
                return new JwtResponse(accessToken, null, user.getId());
            }
        }
        throw new UnAuthorisedException(UnAuthorisedException.JWT_TOKEN);
    }

    public JwtResponse refresh(@NonNull String refreshToken) {
        JwtResponse response = getAccessToken(refreshToken);
        final String email = jwtProvider.getRefreshClaims(refreshToken).getSubject();
        final String newRefreshToken = jwtProvider.generateRefreshToken(email);
        refreshStorage.put(email, newRefreshToken);
        response.setRefreshToken(newRefreshToken);
        return response;
    }

    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        if (LocalDate.now().isBefore(userRequestDTO.getDateOfBirth())) {
            throw new BadRequestException(BadRequestException.DATE_OF_BIRTH_IS_GREATER_THAN_CURRENT_TIME);
        }
        Optional<User> userOptional = userRepository.findByMail(userRequestDTO.getMail());
        User user = userOptional.isPresent() && BooleanUtils.isFalse(userOptional.get().getIsMailConfirm())
                ? userMapper.update(userOptional.get(), userRequestDTO)
                : userMapper.toEntity(userRequestDTO);
        if (ObjectUtils.isNotEmpty(user.getMailConfirmation())) {
            mailConformationRepository.delete(user.getMailConfirmation());
            user.setMailConfirmation(null);
        }
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        userRepository.saveAndFlush(user);
        sendVerificationCode(user, false);
        return userMapper.toDTO(user);
    }

    public void updatePassword(UUID userId, @NonNull UpdatePasswordRequestDTO requestDTO) {
        if (requestDTO.getNewPassword().equals(requestDTO.getOldPassword())) {
            throw new BadRequestException(BadRequestException.EQUALS_NEW_AND_OLD_PASSWORD);
        }
        User user = userRepository.getById(userId);
        if (passwordEncoder.matches(requestDTO.getOldPassword(), user.getPassword())) {
            user.setPassword(
                    passwordEncoder.encode(requestDTO.getNewPassword())
            );
        } else {
            throw new BadRequestException(BadRequestException.WRONG_OLD_PASSWORD);
        }
    }

    /**
     * Отправка кода подтверждения почты в зависимости от isMailConfirm
     * True, если почта уже подтверждена (для восстановленя пароля)
     * False, если почта не подтверждена (во время регистрации нового пользователя)
     *
     * @param mail          - почта
     * @param isMailConfirm - статус подтверждения почты
     */
    public void sendVerificationCode(@NonNull String mail, @NonNull Boolean isMailConfirm) {
        User user = getUserWithCheckForMailConfirm(mail, isMailConfirm);
        if (Objects.nonNull(user.getMailConfirmation()) &&
                LocalDateTime.now().isBefore(user.getMailConfirmation().getCodeExpireDatetime())
        ) {
            throw new BadRequestException(BadRequestException.CODE_IS_STILL_ACTIVE);
        }
        sendVerificationCode(user, isMailConfirm);
    }

    /**
     * Отправка кода подтверждения почты в зависимости от isMailConfirm
     * True, если почта уже подтверждена (для восстановленя пароля)
     * False, если почта не подтверждена (во время регистрации нового пользователя)
     *
     * @param user          - пользователь
     * @param isMailConfirm - статус подтверждения почты
     */
    private void sendVerificationCode(User user, Boolean isMailConfirm) {
        MailConfirmation mailConfirmation = Objects.nonNull(user.getMailConfirmation())
                ? user.getMailConfirmation()
                : MailConfirmation.builder()
                .user(user)
                .numberOfAttempts(0)
                .build();
        Integer code = ServiceUtils.genCode(1000, 9999);
        mailConfirmation.setCode(code);
        int numberOfAttempts = mailConfirmation.getNumberOfAttempts() + 1;
        mailConfirmation.setNumberOfAttempts(numberOfAttempts);
        int codeActiveTime = numberOfAttempts <= LIMIT_OF_ATTEMPTS
                ? INTERVAL_BEFORE_LIMIT
                : INTERVAL_AFTER_LIMIT;
        mailConfirmation.setCodeExpireDatetime(LocalDateTime.now().plusSeconds(codeActiveTime));
        mailConformationRepository.saveAndFlush(mailConfirmation);
        String text = BooleanUtils.isTrue(isMailConfirm)
                ? String.format(PASSWORD_RECOVERY_MESSAGE, codeActiveTime, code)
                : String.format(VERIFICATION_CODE_MESSAGE, codeActiveTime, code);
        mailSenderService.sendMessage(user.getMail(), text);
    }

    public void mailConfirmation(String mail, Integer code) {
        User user = getUserWithCheckForMailConfirm(mail, false);
        checkUserForCodeConfirm(user, code);
        user.setIsMailConfirm(true);
        mailConformationRepository.delete(user.getMailConfirmation());
    }

    public UUID recoveryConfirmation(String mail, Integer code) {
        User user = getUserWithCheckForMailConfirm(mail, true);
        checkUserForCodeConfirm(user, code);
        mailConformationRepository.delete(user.getMailConfirmation());
        return user.getId();
    }

    public void addNewPassword(UUID userId, String password) {
        User user = getUserWithCheckForMailConfirm(userId, true);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.saveAndFlush(user);
    }

    private void checkUserForCodeConfirm(User user, Integer code) {
        if (Objects.isNull(user.getMailConfirmation())) {
            throw new NotFoundException(NotFoundException.MAIL_CONFIRMATION_NOT_FOUND);
        }
        if (!user.getMailConfirmation().getCode().equals(code)) {
            throw new BadRequestException(BadRequestException.CODE_NOT_EQUALS);
        }
        if (LocalDateTime.now().isAfter(user.getMailConfirmation().getCodeExpireDatetime())) {
            throw new BadRequestException(BadRequestException.CODE_IS_NOT_ACTIVE);
        }
    }

    /**
     * Получение пользователя по почте
     * Проверка подтверждения почты на значение, равное isMailConfirm
     *
     * @param mail          - почта
     * @param isMailConfirm - статус подтверждения почты
     * @return пользователь
     */
    private User getUserWithCheckForMailConfirm(String mail, Boolean isMailConfirm) {
        User user = userRepository.findByMail(mail).orElseThrow(
                () -> new NotFoundException(NotFoundException.CLIENT_NOT_FOUND)
        );
        assertMailConfirm(user.getIsMailConfirm(), isMailConfirm);
        return user;
    }

    /**
     * Получение пользователя по id
     * Проверка подтверждения почты на значение, равное isMailConfirm
     *
     * @param id            - id пользователя
     * @param isMailConfirm - статус подтверждения почты
     * @return пользователь
     */
    private User getUserWithCheckForMailConfirm(UUID id, Boolean isMailConfirm) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new NotFoundException(NotFoundException.CLIENT_NOT_FOUND)
        );
        assertMailConfirm(user.getIsMailConfirm(), isMailConfirm);
        return user;
    }

    /**
     * Проверка подтверждения почты пользователя на значение равное переменной targetStatus
     *
     * @param isMailConfirm - статус подтверждения почты
     * @param targetStatus  - ожидаемое значение
     */
    private void assertMailConfirm(Boolean isMailConfirm, Boolean targetStatus) {
        if (BooleanUtils.isTrue(isMailConfirm) && BooleanUtils.isFalse(targetStatus)) {
            throw new BadRequestException(BadRequestException.MAIL_IS_CONFIRMED);
        }
        if (BooleanUtils.isFalse(isMailConfirm) && BooleanUtils.isTrue(isMailConfirm)) {
            throw new BadRequestException(UnAuthorisedException.MAIL_IS_NOT_CONFIRM);
        }
    }
}
