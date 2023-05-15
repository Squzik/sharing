package org.example.sharing.rest.mapper;

import org.example.sharing.rest.dto.response.sciener.ScienerApiCodeResponseDTO;
import org.example.sharing.rest.dto.response.sciener.ScienerApiLocksResponseDTO;
import org.example.sharing.rest.dto.response.sciener.ScienerApiUserResponseDTO;
import org.example.sharing.rest.dto.response.sciener.ScienerLockResponseDTO;
import org.example.sharing.rest.dto.response.sciener.ScienerLocksResponseDto;
import org.example.sharing.rest.dto.response.sciener.ScienerUserResponseDTO;
import org.example.sharing.store.entity.ScienerCode;
import org.example.sharing.store.entity.ScienerLock;
import org.example.sharing.store.entity.ScienerUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface ScienerMapper {

    @Mapping(source = "uid", target = "id")
    @Mapping(source = "access_token", target = "accessToken")
    @Mapping(source = "refresh_token", target = "refreshToken")
    @Mapping(target = "tokenEndTime", expression = "java(System.currentTimeMillis() + dto.getExpires_in())")
    ScienerUser userResponseToEntity(ScienerApiUserResponseDTO dto);

    @Mapping(source = "lockId", target = "id")
    @Mapping(source = "lockAlias", target = "lockName")
    ScienerLock lockToScienerLock(ScienerApiLocksResponseDTO.Lock lock);

    @Mapping(source = "keyboardPwdId", target = "id")
    @Mapping(source = "keyboardPwd", target = "password")
    ScienerCode codeToScienerCode(ScienerApiCodeResponseDTO.Code code);

    ScienerUserResponseDTO userToScienerUserResponseDTO(ScienerUser user);

    @Mapping(source = "flat.id", target = "flatId")
    ScienerLockResponseDTO lockToScienerLockResponseDTO(ScienerLock lock);

    @Mapping(source = "user", target = "user")
    ScienerLocksResponseDto toDto(ScienerUser user, Set<ScienerLock> locks);
}

