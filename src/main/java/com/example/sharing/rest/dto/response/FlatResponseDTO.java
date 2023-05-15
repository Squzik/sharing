package org.example.sharing.rest.dto.response;

import org.example.sharing.rest.dto.response.sciener.ScienerLockResponseDTO;
import org.example.sharing.rest.dto.s3.S3FileDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlatResponseDTO {

    @Schema(description = "ID квартиры в БД")
    private UUID id;

    @Schema(description = "Наименование квартиры")
    private String name;

    @Schema(description = "Адрес квартиры")
    private AddressResponseDTO address;

    @Schema(description = "Количество комнат")
    private Integer numberOfRooms;

    @Schema(description = "Цена аренды")
    private Integer price;

    @Schema(description = "Описание квартиры")
    private String description;

    @Schema(description = "Дто типа квартиры")
    private TypeOfFlatResponseDTO typeOfFlatResponseDTO;

    @Schema(description = "ДТО типа аренды")
    private TypeOfRentalResponseDTO typeOfRentalResponseDTO;

    @Schema(description = "ДТО типа здания")
    private TypeOfBuildingResponseDTO typeOfBuildingResponseDTO;

    @Schema(description = "ДТО статуса квартиры")
    private FlatStatusResponseDTO flatStatusResponseDTO;

    @Schema(description = "Этаж арендуемой квартиры")
    private Integer floor;

    @Schema(description = "Наличие санузла")
    private Boolean isCombinedBathroom;

    @Schema(description = "Наличие мебели")
    private Boolean furniture;

    @Schema(description = "Наличие балкона")
    private Boolean balcony;

    @Schema(description = "Наличие техники")
    private Boolean appliances;

    @Schema(description = "Наличие интернета и кабельного телевидения")
    private Boolean internetCableTv;

    @Schema(description = "ДТО замка")
    private ScienerLockResponseDTO scienerLock;

    @Schema(description = "Среднее значение отзывов")
    private Double score;

    @Schema(description = "Список отзывов")
    private List<ReviewsFlatResponseDTO> reviews;

    @Schema(description = "Список фото")
    private List<S3FileDto> photos;

    @Schema(description = "Наличие квартиры в избранном у пользователя")
    private Boolean isInClientFavorite;

    @Schema(description = "Аренда клиента")
    private BookingDatesResponseDTO clientActiveBooking;

    @Schema(description = "Наличие активной аренды")
    private Boolean isHasActiveBookings;
}