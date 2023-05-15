package org.example.sharing.rest.mapper;

import org.example.sharing.rest.dto.s3.S3FileDto;
import org.example.sharing.store.entity.File;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FileMapper {
    @Mapping(target = "responseStatus", source = "status")
    S3FileDto toDto(File file);
}
