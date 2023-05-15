package org.example.sharing.service.impl;

import org.example.sharing.rest.dto.request.AddressRequestDTO;
import org.example.sharing.rest.dto.response.AddressResponseDTO;
import org.example.sharing.store.entity.Address;
import org.example.sharing.rest.mapper.AddressMapper;
import org.example.sharing.store.repository.AddressRepository;
import org.example.sharing.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressMapper mapper;
    private final AddressRepository addressRepository;

    @Override
    public AddressResponseDTO addAddress(AddressRequestDTO addressRequestDTO) {
        Address address = addressRepository.save(mapper.addressRequestDtoToAddress(addressRequestDTO));
        return mapper.addressToAddressResponseDto(address);
    }
}
