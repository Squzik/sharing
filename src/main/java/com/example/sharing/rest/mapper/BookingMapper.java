package org.example.sharing.rest.mapper;

import org.example.sharing.rest.dto.request.BookingRequestDTO;
import org.example.sharing.rest.dto.response.BookingActiveResponseDTO;
import org.example.sharing.rest.dto.response.BookingDatesResponseDTO;
import org.example.sharing.rest.dto.response.BookingResponseDTO;
import org.example.sharing.store.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {FlatMapper.class, UserMapper.class})
public interface BookingMapper {

    @Mapping(source = "flatId", target = "flat.id")
    @Mapping(source = "startDateAndTime", target = "start")
    @Mapping(source = "endDateAndTime", target = "end")
    Booking fromDto(BookingRequestDTO bookingRequestDTO);

    BookingResponseDTO toDto(Booking booking);

    @Mapping(source = "booking.user", target = "user")
    @Mapping(source = "booking.code.password", target = "password")
    BookingActiveResponseDTO toActiveDto(Booking booking, Boolean isLockOnline);

    BookingDatesResponseDTO toDatesDto(Booking booking);
}