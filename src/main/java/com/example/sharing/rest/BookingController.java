package org.example.sharing.rest;

import org.example.sharing.rest.dto.request.AddFileRequestDto;
import org.example.sharing.rest.dto.request.BookingRequestDTO;
import org.example.sharing.rest.dto.response.BookingActiveResponseDTO;
import org.example.sharing.rest.dto.response.BookingDatesResponseDTO;
import org.example.sharing.rest.dto.response.BookingResponseDTO;
import org.example.sharing.rest.handler.exception.UnAuthorisedException;
import org.example.sharing.service.BookingService;
import org.example.sharing.store.entity.enums.BookingStatusEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

import static org.example.sharing.config.OpenApiConfig.SwaggerDependency.SCHEME_NAME;

@RestController
@RequestMapping("/api/v1/booking")
@RequiredArgsConstructor
@Tag(name = "BookingController", description = "Контроллер бронирования")
public class BookingController {

    private final BookingService bookingService;

    /**
     * Получение списка дат бронирований относительно квартиры
     *
     * @param flatId - id квартиры
     * @return список объектов состоящик из дат начала и конца бронирования
     */
    @GetMapping("/flat/{flatId}/dates")
    public List<BookingDatesResponseDTO> getBookingsDates(@PathVariable UUID flatId) {
        return bookingService.getBookingsDatesByFlatId(flatId);
    }

    /**
     * Добавление бронирования или аренды квартиры, включающее в себя квартиру и даты начала и конца бронирования
     *
     * @param bookingRequestDTO - объект содержащий в себе вышеперечисленные данные для внесения в базу
     * @return объект, содержащий в себе входные данные дополненные ключем для открытия замка в квартиру на указанные период бронирования
     */
    @SecurityRequirement(name = SCHEME_NAME)
    @PostMapping
    @Operation(summary = "Добавление бронирование или аренды")
    public ResponseEntity<BookingResponseDTO> addBooking(@Valid @RequestBody BookingRequestDTO bookingRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookingService.addBooking(bookingRequestDTO));
    }

    /**
     * Изменение статуса аренды хозяином квартиры
     *
     * @param bookingId - id аренды
     * @param status    - статус подтверждения хозяина
     * @return все данные аренды, включая созданный ключ и статус
     * @throws UnAuthorisedException - ошибки связанные с работой Sciener Api
     */
    @SecurityRequirement(name = SCHEME_NAME)
    @PatchMapping("/{bookingId}/status")
    @Parameter(in = ParameterIn.PATH, name = "bookingId", description = "ID аренды",
            schema = @Schema(type = "uuid"))
    @Parameter(in = ParameterIn.QUERY, name = "status", description = "Статус аренды",
            schema = @Schema(type = "BookingStatusEnum"))
    public ResponseEntity<BookingResponseDTO> editBookingStatus(@PathVariable UUID bookingId, @RequestParam BookingStatusEnum status) {
        return ResponseEntity.ok(bookingService.editBookingStatus(bookingId, status));
    }

    /**
     * Запрос на открытие замка у забронированной квартиры
     *
     * @param id - бронирования
     * @throws UnAuthorisedException - ошибки связанные с авторизацией ScienerAPI
     */
    @SecurityRequirement(name = SCHEME_NAME)
    @PostMapping("/{id}/open")
    public void openBookingLock(@PathVariable UUID id) {
        bookingService.openBookingLock(id);
    }

    /**
     * Удаление бронирования из базы данных
     *
     * @param bookingId - id аренды
     */
    @SecurityRequirement(name = SCHEME_NAME)
    @DeleteMapping("/{bookingId}")
    public void deleteBooking(@PathVariable UUID bookingId) {
        bookingService.deleteBooking(bookingId);
    }

    @SecurityRequirement(name = SCHEME_NAME)
    @GetMapping("/client/active")
    public List<BookingActiveResponseDTO> getActiveBooking() {
        return bookingService.getActiveBooking();
    }

    @SecurityRequirement(name = SCHEME_NAME)
    @GetMapping("/client/waiting/landlord")
    public List<BookingResponseDTO> getWaitingForLandlord() {
        return bookingService.getWaitingBookingForLandlord();
    }

    @SecurityRequirement(name = SCHEME_NAME)
    @GetMapping("/client/waiting/rented")
    public List<BookingResponseDTO> getWaitingForRented() {
        return bookingService.getWaitingBookingForRented();
    }

    @SecurityRequirement(name = SCHEME_NAME)
    @GetMapping("/flat/{flatId}/active")
    public List<BookingResponseDTO> getFlatActiveBooking(@PathVariable UUID flatId) {
        return bookingService.getFlatActiveBooking(flatId);
    }

    @SecurityRequirement(name = SCHEME_NAME)
    @GetMapping("/flat/{flatId}/waiting")
    public List<BookingResponseDTO> getFlatWaitingBooking(@PathVariable UUID flatId) {
        return bookingService.getFlatWaitingBooking(flatId);
    }

    @SecurityRequirement(name = SCHEME_NAME)
    @GetMapping("/flat/{flatId}/history")
    public List<BookingResponseDTO> getFlatHistoryBooking(@PathVariable UUID flatId) {
        return bookingService.getFlatHistoryBooking(flatId);
    }

    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = SCHEME_NAME)
    public ResponseEntity<String> addFile(@RequestParam UUID id,
                                          @Valid @ModelAttribute AddFileRequestDto file) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookingService.addFile(id, file));
    }

    @DeleteMapping("/{booking_id}/file/{document_id}")
    @SecurityRequirement(name = SCHEME_NAME)
    @ResponseStatus(HttpStatus.OK)
    public void deleteFile(@PathVariable("booking_id") UUID bookingId,
                           @PathVariable("document_id") UUID documentId) {
        bookingService.deleteFile(bookingId, documentId);
    }
}
