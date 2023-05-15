package org.example.sharing.rest;

import org.example.sharing.rest.dto.request.FlatFilterRequestDTO;
import org.example.sharing.rest.dto.request.FlatRequestDTO;
import org.example.sharing.rest.dto.request.FlatUpdateRequestDto;
import org.example.sharing.rest.dto.response.FlatResponseDTO;
import org.example.sharing.service.FlatService;
import org.example.sharing.utils.validation.OnUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.example.sharing.config.OpenApiConfig.SwaggerDependency.SCHEME_NAME;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/flat")
@Tag(name = "FlatController", description = "Контроллер квартир")
public class FlatController {

    private final FlatService flatService;

    /**
     * Добавление квартиры, вложенного адреса и привязка существующий фотографий по их id в базу данных
     *
     * @param flatRequestDTO - объект квартиры с вложенным объектом адреса и списком id фотографий
     * @return - сохраненный объект квартиры уже с вложенными объектами фотографий
     */
    @SecurityRequirement(name = SCHEME_NAME)
    @PostMapping
    @Operation(summary = "Добавление квартиры в базу данных")
    public ResponseEntity<FlatResponseDTO> addFlat(@Valid @RequestBody FlatRequestDTO flatRequestDTO) {
        FlatResponseDTO responseDTO = flatService.addFlat(flatRequestDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(responseDTO.getId())
                .toUri();
        return ResponseEntity.created(location)
                .body(responseDTO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение квартиры по id")
    public FlatResponseDTO getFlatById(@PathVariable UUID id) {
        return flatService.getFlatById(id);
    }

    @SecurityRequirement(name = SCHEME_NAME)
    @GetMapping("/clientFlats")
    @Operation(summary = "Квартиры арендодателя")
    public Set<FlatResponseDTO> getFlatByClient() {
        return flatService.getFlatByActiveUser();
    }

    @SecurityRequirement(name = SCHEME_NAME)
    @GetMapping("/earlyBooked")
    @Operation(summary = "Ранее арендованные пользователем квартиры")
    public Set<FlatResponseDTO> getEarlyBookedFlats() {
        return flatService.getEarlyBookedFlatsByClient();
    }

    /**
     * Удаление квартиры из базы данных по ее id
     * Так-же удаляется адрес и привязанные фотографии
     *
     * @param id - id квартиры в базе данных
     */
    @SecurityRequirement(name = SCHEME_NAME)
    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление квартиры по ее id")
    public void deleteFlat(@PathVariable UUID id) {
        flatService.deleteFlat(id);
    }

    @SecurityRequirement(name = SCHEME_NAME)
    @DeleteMapping("/all")
    @Operation(summary = "Удаление всех квартир активного пользователя")
    public void deleteAllClientFlats() {
        flatService.deleteAllUserFlats();
    }

    /**
     * Изменение статуса квартиры, для ее сокрытия или показа в общем спискк квартир
     * Необходимая замена удалению, когда имеются активные бронирования на квартиру
     *
     * @param flatId квартиры в базе данных
     * @return нынешний статус квартиры
     */
    @SecurityRequirement(name = SCHEME_NAME)
    @PutMapping("/{flatId}")
    @Operation(summary = "Изменить статус отображения квартиры в общем списке")
    public boolean editViewStatus(@PathVariable UUID flatId) {
        return flatService.editViewStatus(flatId);
    }

    @GetMapping("/filter")
    @Operation(summary = "Фильтрация квартир")
    public List<FlatResponseDTO> filterFlat(@ModelAttribute FlatFilterRequestDTO flatFilterRequestDTO) {
        return flatService.filterFlat(flatFilterRequestDTO);
    }

    @PatchMapping(value = "/{flatId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Редактирование квартиры")
    @Parameter(in = ParameterIn.PATH, name = "flatId", description = "ID квартиры",
            schema = @Schema(type = "uuid"))
    public ResponseEntity<FlatResponseDTO> updateFlat(@PathVariable UUID flatId,
                                                      @Validated(OnUpdate.class) @ModelAttribute
                                                              FlatUpdateRequestDto flatUpdateRequestDto) {
        return ResponseEntity.ok(flatService.updateFlat(flatId, flatUpdateRequestDto));
    }
}