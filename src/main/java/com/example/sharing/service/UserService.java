package org.example.sharing.service;

import org.example.sharing.rest.dto.request.UserRequestDTO;
import org.example.sharing.rest.dto.response.FlatResponseDTO;
import org.example.sharing.rest.dto.response.UserResponseDTO;

import java.util.Set;
import java.util.UUID;

public interface UserService {

    UserResponseDTO getUserById(UUID id);

    UserResponseDTO addFavoriteFlat(UUID userId, UUID flatId);

    UserResponseDTO update(UUID id, UserRequestDTO userRqDTO);

    Set<FlatResponseDTO> getFavoriteByUserId(UUID userId);

    UserResponseDTO deleteFavoriteFlat(UUID userId, UUID flatId);
}
