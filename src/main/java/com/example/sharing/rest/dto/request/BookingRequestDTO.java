package org.example.sharing.rest.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.example.sharing.store.entity.enums.BookingStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class BookingRequestDTO {

    @NotNull(message = "Ошибка! Квартира не указана")
    @Schema(description = "Id квартиры")
    private UUID flatId;

    @NotNull(message = "Ошибка! Дата не указана")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(
            description = "Дата и время начала",
            type = "string",
            example = "2024-04-01 12:00:00"
    )
    private LocalDateTime startDateAndTime;

    @NotNull(message = "Ошибка! Дата не указана")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(
            description = "Дата и время конца",
            type = "string",
            example = "2024-04-02 12:00:00"
    )
    private LocalDateTime endDateAndTime;

    @NotNull(message = "Ошибка! Количество людей не указано")
    @Min(value = 1, message = "Ошибка! Неверные данные о количестве людей")
    @Schema(description = "Количество людей")
    private Integer numberOfPeople;

    @NotNull(message = "Ошибка! Статус не указан")
    @Schema(description = "Статусы BOOKING_WAITING или RENT_WAITING")
    private BookingStatusEnum bookingStatus;
}
