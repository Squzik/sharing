package org.example.sharing.rest.mapper;

import org.example.sharing.rest.dto.response.PassportVerifyStatusResponseDTO;
import org.example.sharing.store.entity.PassportVerifyStatus;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PassportVerifyStatusMapper {

    PassportVerifyStatusResponseDTO toDto(PassportVerifyStatus passportVerifyStatus);
}
