package org.example.sharing.service;

import org.example.sharing.rest.dto.request.AddFileRequestDto;
import org.example.sharing.rest.dto.s3.S3FileDto;

import java.util.UUID;

public interface PhotoService {

    S3FileDto save(AddFileRequestDto addFileRequestDto);

    void delete(UUID id);

}