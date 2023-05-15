package org.example.sharing.service;

import org.example.sharing.rest.dto.request.AddFileRequestDto;
import org.example.sharing.rest.dto.request.BookingRequestDTO;
import org.example.sharing.rest.dto.response.BookingActiveResponseDTO;
import org.example.sharing.rest.dto.response.BookingDatesResponseDTO;
import org.example.sharing.rest.dto.response.BookingResponseDTO;
import org.example.sharing.store.entity.enums.BookingStatusEnum;

import java.util.List;
import java.util.UUID;

public interface BookingService {

    BookingResponseDTO addBooking(BookingRequestDTO bookingDto);

    BookingResponseDTO editBookingStatus(UUID bookingId, BookingStatusEnum status);

    void openBookingLock(UUID bookingId);

    void deleteBooking(UUID bookingId);

    List<BookingDatesResponseDTO> getBookingsDatesByFlatId(UUID flatId);

    List<BookingActiveResponseDTO> getActiveBooking();

    List<BookingResponseDTO> getWaitingBookingForLandlord();

    List<BookingResponseDTO> getWaitingBookingForRented();

    List<BookingResponseDTO> getFlatActiveBooking(UUID flatId);

    List<BookingResponseDTO> getFlatWaitingBooking(UUID flatId);

    List<BookingResponseDTO> getFlatHistoryBooking(UUID flatId);

    String addFile(UUID id, AddFileRequestDto file);

    void deleteFile(UUID bookingId, UUID documentId);
}
