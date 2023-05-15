package org.example.sharing.rest.mapper;

import org.example.sharing.rest.dto.request.BlackListRequestDto;
import org.example.sharing.rest.dto.response.BlackListResponseDto;
import org.example.sharing.rest.dto.response.OwnerBlackListResponseDto;
import org.example.sharing.store.entity.BlackList;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface BlackListMapper {

    BlackListResponseDto toDto(BlackList blackList);

    @Mapping(source = "ownerUserId", target = "ownerUser.id")
    @Mapping(source = "blackListRequestDto.userId", target = "user.id")
    BlackList fromDto(BlackListRequestDto blackListRequestDto, UUID ownerUserId);

    OwnerBlackListResponseDto toOwnerDto(UUID ownerUserId, List<BlackList> blackList);
}
