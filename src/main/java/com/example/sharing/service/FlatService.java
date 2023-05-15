package org.example.sharing.service;

import org.example.sharing.rest.dto.request.FlatFilterRequestDTO;
import org.example.sharing.rest.dto.request.FlatRequestDTO;
import org.example.sharing.rest.dto.request.FlatUpdateRequestDto;
import org.example.sharing.rest.dto.response.FlatResponseDTO;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface FlatService {

    FlatResponseDTO getFlatById(UUID flatId);

    Set<FlatResponseDTO> getFlatByActiveUser();

    Set<FlatResponseDTO> getEarlyBookedFlatsByClient();

    FlatResponseDTO addFlat(FlatRequestDTO flatAddressDTO);

    void deleteFlat(UUID id);

    void deleteAllUserFlats();

    boolean editViewStatus(UUID id);

    List<FlatResponseDTO> filterFlat(FlatFilterRequestDTO flatFilterRequestDTO);

    FlatResponseDTO updateFlat(UUID flatId, FlatUpdateRequestDto flatUpdateRequestDto);
}