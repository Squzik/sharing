package org.example.sharing.rest;

import org.example.sharing.rest.dto.request.AddFileRequestDto;
import org.example.sharing.rest.dto.s3.S3FileDto;
import org.example.sharing.service.PhotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.UUID;

import static org.example.sharing.config.OpenApiConfig.SwaggerDependency.SCHEME_NAME;

@RestController
@RequestMapping("/api/v1/photo")
@SecurityRequirement(name = SCHEME_NAME)
@Tag(name = "Photo controller", description = "Контроллер фотографий")
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;

    /**
     * Добавление фотографии в базу данных
     *
     * @param addFileRequestDto - содержит только кодированные байты фотографии
     * @return - объкт фотографии из базы данных
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Добавление фото")
    public ResponseEntity<S3FileDto> addPhoto(@Valid @ModelAttribute AddFileRequestDto addFileRequestDto) {
        S3FileDto s3FileDto = photoService.save(addFileRequestDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(s3FileDto.getId())
                .toUri();
        return ResponseEntity.created(location)
                .body(s3FileDto);
    }

    /**
     * Удаление фотографии по ее id в базе данных
     *
     * @param id - id фотографии из базы данных
     */
    @DeleteMapping
    @Operation(summary = "Удаление фото по id")
    public void deletePhoto(@RequestParam UUID id) {
        photoService.delete(id);
    }
}