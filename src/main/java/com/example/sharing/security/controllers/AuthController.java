package org.example.sharing.security.controllers;

import org.example.sharing.rest.dto.request.MailConfirmRequestDto;
import org.example.sharing.rest.dto.request.MailRequestDto;
import org.example.sharing.rest.dto.request.PasswordRecoveryRequestDto;
import org.example.sharing.rest.dto.request.UpdatePasswordRequestDTO;
import org.example.sharing.rest.dto.request.UserRequestDTO;
import org.example.sharing.rest.dto.response.UserResponseDTO;
import org.example.sharing.security.jwt.JwtRequest;
import org.example.sharing.security.jwt.JwtResponse;
import org.example.sharing.security.jwt.RefreshJwtRequest;
import org.example.sharing.security.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "AuthController", description = "Контроллер авторизации и регистрации")
@Validated
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Регистрация нового клиента, с привязкой к нему существующий в базе данных фотографии
     *
     * @param userRequestDTO - данные для регистрации
     * @return информация добавленная в базу данных
     */
    @PostMapping("/registration")
    @Operation(summary = "Добавление клиента")
    public ResponseEntity<UserResponseDTO> createRegistration(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.createUser(userRequestDTO));
    }

    @PostMapping("/login")
    @Operation(summary = "Авторизация")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest authRequest) {
        return ResponseEntity.ok(authService.login(authRequest));
    }

    @PostMapping("/token")
    @Operation(summary = "Получения нового токена доступа")
    public ResponseEntity<JwtResponse> getNewAccessToken(@RequestBody RefreshJwtRequest request) {
        return ResponseEntity.ok(authService.getAccessToken(request.getRefreshToken()));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Получение новых токенов")
    public ResponseEntity<JwtResponse> getNewRefreshToken(@RequestBody RefreshJwtRequest request) {
        return ResponseEntity.ok(authService.refresh(request.getRefreshToken()));
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{user_id}/password")
    @Operation(summary = "Изменение пароля")
    public void updatePasswordClient(@PathVariable("user_id") UUID userId,
                                     @RequestBody UpdatePasswordRequestDTO updatePasswordRequestDTO) {
        authService.updatePassword(userId, updatePasswordRequestDTO);
    }

    @PostMapping("/mail/send")
    @Operation(summary = "Отправка нового кода на почту")
    public void sendNewVerificationCode(@RequestBody @Valid MailRequestDto mailRequestDto) {
        authService.sendVerificationCode(mailRequestDto.getMail(), false);
    }

    @PostMapping("/mail/confirm")
    @Operation(summary = "Подтверждерние кода с почты")
    public void mailConfirmation(@RequestBody @Valid MailConfirmRequestDto mailConfirmRequestDto) {
        authService.mailConfirmation(mailConfirmRequestDto.getMail(), mailConfirmRequestDto.getCode());
    }

    @PostMapping("/recovery")
    @Operation(summary = "Восстановление пароля")
    public void passwordRecovery(@RequestBody @Valid MailRequestDto mailRequestDto) {
        authService.sendVerificationCode(mailRequestDto.getMail(), true);
    }

    @PostMapping("/recovery/confirm")
    @Operation(summary = "Подтверждерние кода с почты для восстановления пароля")
    public ResponseEntity<UUID> recoveryConfirm(@RequestBody @Valid MailConfirmRequestDto mailConfirmRequestDto) {
        return ResponseEntity.ok(authService.recoveryConfirmation(mailConfirmRequestDto.getMail(), mailConfirmRequestDto.getCode()));
    }

    @PatchMapping("/{userId}/recovery")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Метод восстановление пароля пользователя")
    @Parameter(in = ParameterIn.PATH, name = "userId", description = "id пользователя", schema = @Schema(type = "uuid"))
    public void addNewPassword(@PathVariable UUID userId,
                               @RequestBody @Valid PasswordRecoveryRequestDto passwordRecoveryRequestDto) {
        authService.addNewPassword(userId, passwordRecoveryRequestDto.getPassword());
    }
}