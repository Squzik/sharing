package org.example.sharing.rest.mapper;

import org.example.sharing.rest.dto.request.UserRequestDTO;
import org.example.sharing.rest.dto.response.UserResponseDTO;
import org.example.sharing.store.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        uses = {
                FileMapper.class,
                PassportVerifyStatusMapper.class
        },
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {

    @Mapping(source = "userRole.name", target = "roleName")
    UserResponseDTO toDTO(User user);

    @Mapping(target = "password", ignore = true)
    User toEntity(UserRequestDTO userRequestDTO);

    @Mapping(target = "mail", ignore = true)
    @Mapping(target = "password", ignore = true)
    User update(@MappingTarget User user, UserRequestDTO userRequestDTO);
}
