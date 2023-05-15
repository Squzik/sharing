package org.example.sharing.service;

import org.example.sharing.rest.dto.request.AddressRequestDTO;
import org.example.sharing.rest.dto.response.AddressResponseDTO;

public interface AddressService {

    AddressResponseDTO addAddress(AddressRequestDTO addressRequestDTO);

}