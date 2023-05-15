package org.example.sharing.service;

import org.example.sharing.rest.dto.request.BlackListRequestDto;
import org.example.sharing.rest.dto.response.BlackListResponseDto;
import org.example.sharing.rest.dto.response.OwnerBlackListResponseDto;

import java.util.UUID;

public interface BlackListService {

    BlackListResponseDto addToBlackList(BlackListRequestDto blackListRequestDto);

    void deleteUserFromBlackList(UUID userId);

    OwnerBlackListResponseDto getBlackList();
}
