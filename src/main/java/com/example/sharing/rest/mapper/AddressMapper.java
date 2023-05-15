package org.example.sharing.rest.mapper;

import org.example.sharing.rest.dto.request.AddressRequestDTO;
import org.example.sharing.rest.dto.response.AddressResponseDTO;
import org.example.sharing.store.entity.Address;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    AddressResponseDTO addressToAddressResponseDto(Address address);

    Address addressRequestDtoToAddress(AddressRequestDTO addressRequestDTO);

}
