package org.example.sharing.rest;

import org.example.sharing.rest.dto.request.ScienerUserRequestDTO;
import org.example.sharing.rest.dto.response.sciener.ScienerLocksResponseDto;
import org.example.sharing.service.ScienerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.example.sharing.config.OpenApiConfig.SwaggerDependency.SCHEME_NAME;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sciener")
@SecurityRequirement(name = SCHEME_NAME)
@Tag(name = "ScienerController", description = "Контроллер ScienerOpenApi")
public class ScienerController {

    private final ScienerService scienerService;

    /**
     * Интеграция аккаунта Sciener, для дальнейшей работы с API
     *
     * @param requestDTO - данные для авторизации
     * @return - список замков данного аккаунта
     */
    @PostMapping("/auth")
    @Operation(summary = "Синхронизация аккаунта Sciener с аккаунтом пользователя")
    public ResponseEntity<ScienerLocksResponseDto> authInSciener(@RequestBody ScienerUserRequestDTO requestDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(scienerService.createUser(requestDTO));
    }

    /**
     * Выход из аккаунта Sciener
     */
    @PutMapping("/exit")
    @Operation(summary = "Выход из аккаунта Sciener")
    public void accountExit() {
        scienerService.accountExit();
    }

    /**
     * Получение списка замков по id аккаунту, если имеется подключенный аккаунт Sciener
     *
     * @return - список замков
     */
    @GetMapping("/locks")
    @Operation(summary = "Получение списка замков")
    public ResponseEntity<ScienerLocksResponseDto> getLocks() {
        return ResponseEntity.status(HttpStatus.OK).body(scienerService.getLocks());
    }

    @GetMapping("/locks/free")
    @Operation(summary = "Получение списка свободных замков")
    public ResponseEntity<ScienerLocksResponseDto> getFreeLocks() {
        return ResponseEntity.status(HttpStatus.OK).body(scienerService.getFreeLocks());
    }

    @DeleteMapping("/locks/{lockId}")
    @Operation(summary = "Удаление непривязанного замка")
    @ResponseStatus(HttpStatus.OK)
    @Parameter(in = ParameterIn.PATH, name = "lockId", description = "Id замка", schema = @Schema(type = "number"))
    public void deleteLock(@PathVariable Integer lockId) {
        scienerService.deleteLock(lockId);
    }
}
