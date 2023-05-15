package org.example.sharing.service.impl;

import org.example.sharing.rest.dto.request.AddFileRequestDto;
import org.example.sharing.rest.dto.s3.ResponseStatus;
import org.example.sharing.rest.dto.s3.S3FileDto;
import org.example.sharing.rest.handler.exception.FileException;
import org.example.sharing.rest.mapper.FileMapper;
import org.example.sharing.service.PhotoService;
import org.example.sharing.service.s3.S3Service;
import org.example.sharing.store.entity.File;
import org.example.sharing.store.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

import static org.example.sharing.rest.handler.exception.FileException.FILE_IS_BROKEN;
import static org.example.sharing.utils.constant.s3.S3BucketConstant.PHOTOS;

@Service
@RequiredArgsConstructor
public class PhotoServiceImpl implements PhotoService {

    private final S3Service s3Service;

    private final FileMapper fileMapper;

    private final FileRepository fileRepository;

    /**
     * Добавление фото в базу данных
     *
     * @param addFileRequestDto - внутри находится только сама фотография в байтах
     * @return - объект фотографии после сохранения в базу данных
     */
    @Override
    public S3FileDto save(AddFileRequestDto addFileRequestDto) {
        S3FileDto s3FileDto;
        try {
            UUID id = UUID.randomUUID();
            File file = File.builder()
                    .id(id)
                    .bucket(PHOTOS)
                    .status(ResponseStatus.IN_PROGRESS)
                    .build();
            fileRepository.save(file);
            s3FileDto = fileMapper.toDto(file);
            s3Service.putFile(PHOTOS, id, addFileRequestDto.getFile().getBytes());
        } catch (IOException e) {
            throw new FileException(FILE_IS_BROKEN);
        }
        return s3FileDto;
    }

    /**
     * Удаление фотографии из базы данных по ее id
     *
     * @param id - id фотографии в базе данных
     */
    @Override
    public void delete(UUID id) {
        s3Service.deleteFile(PHOTOS, id);
    }
}