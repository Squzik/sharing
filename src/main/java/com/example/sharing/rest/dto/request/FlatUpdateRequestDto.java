package org.example.sharing.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlatUpdateRequestDto {

    @Schema(description = "Наименование квартиры")
    private String name;

    @Schema(description = "Id типа квартиры")
    private UUID typeOfFlatId;

    @Schema(description = "Id типа аренды")
    private UUID typeOfRentalId;

    @Schema(description = "Id типа здания")
    private UUID typeOfBuildingId;

    @Schema(description = "Адрес")
    private AddressRequestDTO addressRequestDTO;

    @Schema(description = "Id замка")
    private Integer lockId;

    @Schema(description = "Количество комнат")
    private Integer numberOfRooms;

    @Schema(description = "Этаж")
    private Integer floor;

    @Schema(description = "Наличие санузла")
    private Boolean isCombinedBathroom;

    @Schema(description = "Наличие мебели")
    private Boolean furniture;

    @Schema(description = "Наличие балкона")
    private Boolean balcony;

    @Schema(description = "Наличие техники")
    private Boolean appliances;

    @Schema(description = "Наличие интернета и кабельного телевиденья")
    private Boolean internetCableTV;

    @Schema(description = "Цена")
    private Double price;

    @Schema(description = "Описание квартиры")
    private String description;

    @Schema(description = "Список удаления фото")
    private List<UUID> deletePhotoIds;

    @Schema(description = "Список добавления фото")
    private List<MultipartFile> addPhotos;
}
