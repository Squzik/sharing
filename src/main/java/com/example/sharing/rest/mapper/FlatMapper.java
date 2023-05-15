package org.example.sharing.rest.mapper;

import org.example.sharing.rest.dto.request.FlatRequestDTO;
import org.example.sharing.rest.dto.request.FlatUpdateRequestDto;
import org.example.sharing.rest.dto.response.BookingDatesResponseDTO;
import org.example.sharing.rest.dto.response.FlatBookingResponseDTO;
import org.example.sharing.rest.dto.response.FlatResponseDTO;
import org.example.sharing.store.entity.Flat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UtilityMapper.class, ScienerMapper.class, ReviewsMapper.class})
public interface FlatMapper {

    @Mapping(source = "reviews", target = "score", qualifiedByName = "avgGetScore")
    @Mapping(target = "photos", ignore = true)
    FlatResponseDTO flatToFlatResponseDTO(Flat flat);

    @Mapping(source = "flat.reviews", target = "score", qualifiedByName = "avgGetScore")
    @Mapping(target = "photos", ignore = true)
    FlatResponseDTO flatToFlatResponseDTOWithFlags(
            Flat flat,
            Boolean isInClientFavorite,
            Boolean isHasActiveBookings,
            BookingDatesResponseDTO clientActiveBooking
    );

    @Mapping(source = "userId", target = "user.id")
    @Mapping(target = "files", ignore = true)
    Flat flatRequestDTOToFlat(FlatRequestDTO flatRequestDTO);

    @Mapping(target = "photo", ignore = true)
    FlatBookingResponseDTO flatToFlatBookingResponseDTO(Flat flat);

    @Mapping(target = "files", ignore = true)
    Flat updateDtoToEntity(FlatUpdateRequestDto flatUpdateRequestDto);
}
