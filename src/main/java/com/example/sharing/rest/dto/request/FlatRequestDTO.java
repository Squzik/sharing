package org.example.sharing.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlatRequestDTO {

    @Schema(description = "ID пользователя в БД")
    @NotNull(message = "Ошибка! Владелец не указан")
    private UUID userId;

    @Schema(description = "Наименование квартиры")
    @NotNull(message = "Ошибка! Название не указано")
    private String name;

    @Schema(description = "Адрес квартиры")
    @NotNull(message = "Ошибка! Адрес не указан")
    private AddressRequestDTO address;

    @Schema(description = "ID замка в БД")
    private Integer scienerLockId;

    @Schema(description = "Количество комнат")
    @NotNull(message = "Ошибка! Количество комнат не указано")
    private Integer numberOfRooms;

    @Schema(description = "Цена аренды")
    @NotNull(message = "Ошибка! Цена не указана")
    private Integer price;

    @Schema(description = "Описание квартиры")
    private String description;

    @Schema(description = "Список из id фотографий в БД")
    private List<UUID> photoIds;

    @Schema(description = "ID типа квартиры в БД")
    @NotNull(message = "Ошибка! Тип квартиры не указан")
    private UUID typeOfFlatId;

    @Schema(description = "ID типа аренды в БД")
    @NotNull(message = "Ошибка! Тип аренды не указан")
    private UUID typeOfRentalId;

    @Schema(description = "ID типа здания в БД")
    @NotNull(message = "Ошибка! Тип здания не указан")
    private UUID typeOfBuildingId;

    @Schema(description = "ID статуса квартиры в БД")
    @NotNull(message = "Ошибка! Статус квартиры не указан")
    private UUID flatStatusId;

    @Schema(description = "Этаж арендуемой квартиры")
    @NotNull(message = "Ошибка! Этаж не указан")
    private Integer floor;

    @Schema(description = "Наличие санузла")
    @NotNull(message = "Ошибка! Не выбран тип санузла")
    private Boolean isCombinedBathroom;

    @Schema(description = "Наличие мебели")
    @NotNull(message = "Ошибка! Наличие мебели не указано")
    private Boolean furniture;

    @Schema(description = "Наличие балкона")
    @NotNull(message = "Ошибка! Наличие балкона не указано")
    private Boolean balcony;

    @Schema(description = "Наличие техники")
    @NotNull(message = "Ошибка! Наличие техники не указано")
    private Boolean appliances;

    @Schema(description = "Наличие интернет кабельного телевиденья")
    @NotNull(message = "Ошибка! Наличие интернета и кабельного телевидения не указано")
    private Boolean internetCableTv;
}