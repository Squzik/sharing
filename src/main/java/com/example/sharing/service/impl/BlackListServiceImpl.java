package org.example.sharing.service.impl;

import org.example.sharing.rest.dto.request.BlackListRequestDto;
import org.example.sharing.rest.dto.response.BlackListResponseDto;
import org.example.sharing.rest.dto.response.OwnerBlackListResponseDto;
import org.example.sharing.rest.mapper.BlackListMapper;
import org.example.sharing.service.BlackListService;
import org.example.sharing.store.repository.BlackListRepository;
import org.example.sharing.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class BlackListServiceImpl implements BlackListService {

    private final BlackListMapper mapper;
    private final BlackListRepository blackListRepository;

    @Override
    public BlackListResponseDto addToBlackList(BlackListRequestDto blackListRequestDto) {
        return mapper.toDto(
                blackListRepository.saveAndFlush(
                        mapper.fromDto(blackListRequestDto,
                                ServiceUtils.getUserIdFromCurrentSession())));
    }

    @Override
    public void deleteUserFromBlackList(UUID userId){
        UUID ownerUserId = ServiceUtils.getUserIdFromCurrentSession();
        blackListRepository.deleteByOwnerUserIdAndUserId(ownerUserId, userId);
    }

    @Override
    public OwnerBlackListResponseDto getBlackList(){
        UUID ownerUserId = ServiceUtils.getUserIdFromCurrentSession();
        return mapper.toOwnerDto(ownerUserId, blackListRepository.findAllByOwnerUserId(ownerUserId));
    }
}
