package org.example.sharing.rest;

import org.example.sharing.rest.dto.request.BlackListRequestDto;
import org.example.sharing.rest.dto.response.BlackListResponseDto;
import org.example.sharing.rest.dto.response.OwnerBlackListResponseDto;
import org.example.sharing.service.BlackListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.util.UUID;

import static org.example.sharing.config.OpenApiConfig.SwaggerDependency.SCHEME_NAME;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/blacklist")
@Tag(name = "BlackListController", description = "Контроллер черных списков")
public class BlackListController {

    private final BlackListService blackListService;

    /**
     * Добавление пользователя в черный список
     *
     * @param blackListRequestDto   - объект содержащий id пользователя и комментарий
     * @return - response объекта черного списка
     */
    @SecurityRequirement(name = SCHEME_NAME)
    @PostMapping
    @Operation(summary = "Добавление пользователя в черный список")
    public ResponseEntity<BlackListResponseDto> addToBlackList(@RequestBody @Valid BlackListRequestDto blackListRequestDto){
        return ResponseEntity.created(URI.create("/blacklist"))
                             .body(blackListService.addToBlackList(blackListRequestDto));
    }

    /**
     * Удаление пользователя из черного списка
     *
     * @param userId   -  id пользователя
     */
    @SecurityRequirement(name = SCHEME_NAME)
    @DeleteMapping("/{userId}")
    @Operation(summary = "Удаление пользователя из черного списка")
    @Parameter(in = ParameterIn.PATH, name = "userId", description = "Id пользователя", schema = @Schema(type = "string(uuid)"))
    public void deleteUserFromBlackList(@PathVariable UUID userId){
        blackListService.deleteUserFromBlackList(userId);
    }

    /**
     * Получение списка пользователей из черного списка
     *
     * @return - response списка пользователей добавленных в черный список
     */
    @SecurityRequirement(name = SCHEME_NAME)
    @GetMapping
    @Operation(summary = "Получение черного списка")
    public ResponseEntity<OwnerBlackListResponseDto> getBlackList(){
        return ResponseEntity.ok(blackListService.getBlackList());
    }
}
