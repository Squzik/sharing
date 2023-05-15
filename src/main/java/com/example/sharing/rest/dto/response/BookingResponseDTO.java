package org.example.sharing.rest.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.example.sharing.store.entity.enums.BookingStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDTO {

    @Schema(description = "ID аренды в БД")
    private UUID id;

    @Schema(description = "Пользователь")
    private UserListResponseDTO user;

    @Schema(description = "Квартира")
    private FlatBookingResponseDTO flat;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(
            description = "Дата и время начала",
            type = "string",
            example = "2024-04-01 12:00:00"
    )
    private LocalDateTime start;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(
            description = "Дата и время конца",
            type = "string",
            example = "2024-04-02 12:00:00"
    )
    private LocalDateTime end;

    @Schema(description = "Дата создания бронирования")
    private LocalDate dateOfBookingRequest;

    @Schema(description = "Количество людей")
    private Integer numberOfPeople;

    @Schema(description = "ID документа")
    private UUID contractFileId;

    @Schema(description = "Наименовнание")
    private BookingStatusEnum bookingStatus;
}
