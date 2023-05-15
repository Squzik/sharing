package org.example.sharing.rest;

import org.example.sharing.rest.dto.request.UserRequestDTO;
import org.example.sharing.rest.dto.response.FlatResponseDTO;
import org.example.sharing.rest.dto.response.UserResponseDTO;
import org.example.sharing.service.UserService;
import org.example.sharing.utils.validation.OnUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Set;
import java.util.UUID;

import static org.example.sharing.config.OpenApiConfig.SwaggerDependency.SCHEME_NAME;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Tag(name = "UserController", description = "Контроллер пользователей")
public class UserController {

    private final UserService userService;

    /**
     * Добавление квартирвы в избранное пользователя
     *
     * @param userId - id клиента в базе данных
     * @param flatId   - id квартиры в базе данных
     * @return - response клиента с вложенным списком избранных квартир
     */
    @SecurityRequirement(name = SCHEME_NAME)
    @PostMapping("/{userId}/favorite/{flatId}")
    @Operation(summary = "Добавление квартиры в избранное")
    public ResponseEntity<UserResponseDTO> addFavoriteFlat(@PathVariable UUID userId, @PathVariable UUID flatId) {
        UserResponseDTO userResponseDTO = userService.addFavoriteFlat(userId, flatId);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(userResponseDTO.getId())
                .toUri();
        return ResponseEntity.created(location)
                .body(userResponseDTO);
    }

    @Operation(summary = "Метод обновления пользователя", method = "PATCH",
            security = @SecurityRequirement(name = SCHEME_NAME),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            headers = @Header(name = "content-type", description = "тип контента"),
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserResponseDTO.class))),
                    @ApiResponse(description = "BAD REQUEST", responseCode = "400"),
                    @ApiResponse(description = "UNAUTHORIZED", responseCode = "401"),
                    @ApiResponse(description = "FORBIDDEN", responseCode = "403"),
                    @ApiResponse(description = "NOT FOUND", responseCode = "404")
            }
    )
    @PatchMapping("/{userId}")
    public UserResponseDTO update(@PathVariable("userId") UUID id,
                                  @RequestBody @Validated(OnUpdate.class) UserRequestDTO userRqDTO) {
        log.info("PATCH:/user/{userId} : id={}, userRqDTO={}", id, userRqDTO);
        return userService.update(id, userRqDTO);
    }

    @SecurityRequirement(name = SCHEME_NAME)
    @GetMapping("/{userId}/favorite")
    @Operation(summary = "Получение избранных по id пользователя")
    public Set<FlatResponseDTO> getFavoriteByUserId(@PathVariable UUID userId) {
        return userService.getFavoriteByUserId(userId);
    }

    /**
     * Удаление квартиры из избранного клиента
     *
     * @param userId - id пользователя в базе данных
     * @param flatId   - id квартиры в базе данных
     * @return - response клиента с вложенным списком избранных квартир
     */
    @SecurityRequirement(name = SCHEME_NAME)
    @DeleteMapping("/{userId}/favorite/{flatId}")
    @Operation(summary = "Удаление квартиры из избранного")
    public ResponseEntity<UserResponseDTO> deleteFavoriteFlat(@PathVariable UUID userId, @PathVariable UUID flatId) {
        UserResponseDTO userResponseDTO = userService.deleteFavoriteFlat(userId, flatId);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(userResponseDTO.getId())
                .toUri();
        return ResponseEntity.created(location)
                .body(userResponseDTO);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Получение пользователя по id")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable UUID userId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.getUserById(userId));
    }
}