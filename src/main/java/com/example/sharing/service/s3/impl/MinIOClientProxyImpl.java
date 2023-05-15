package org.example.sharing.service.s3.impl;

import org.example.sharing.rest.dto.s3.ResponseStatus;
import org.example.sharing.rest.dto.s3.S3FileDto;
import org.example.sharing.rest.handler.exception.BadRequestException;
import org.example.sharing.rest.handler.exception.FileException;
import org.example.sharing.service.s3.MinIOClientProxy;
import org.example.sharing.store.entity.File;
import org.example.sharing.store.repository.FileRepository;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioAsyncClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.text.MessageFormat;
import java.util.Map;
import java.util.UUID;

import static org.example.sharing.rest.handler.exception.FileException.INCORRECT_FILE_ID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinIOClientProxyImpl implements MinIOClientProxy {

    private final Map<String, MinioAsyncClient> clientMap;

    private final FileRepository fileRepository;

    @Override
    public S3FileDto getFile(String bucketName, String id) {
        S3FileDto result;
        try (GetObjectResponse objectResponse = clientMap.get(bucketName).getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(id)
                        .build()).get()
        ) {
            byte[] content = IOUtils.toByteArray(objectResponse);
            result = new S3FileDto(id, bucketName, content, ResponseStatus.EXISTS);
        } catch (Exception e) {
            log.error(e.getMessage());
            result = new S3FileDto(id, bucketName, new byte[0], ResponseStatus.FILE_NOT_EXISTS);
        }
        log.debug("Файл с id: {} получен из бакета {}", result.getId(), result.getBucket());
        return result;
    }

    @Override
    @Transactional
    public void putFile(String bucketName, String id, byte[] content) {
        try {
            clientMap.get(bucketName).putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(id)
                    .stream(new ByteArrayInputStream(content), content.length, -1)
                    .build()).thenAccept(objectWriteResponse -> {
                File file = fileRepository.findById(UUID.fromString(id)).orElseThrow();
                file.setStatus(ResponseStatus.SUCCESS);
                fileRepository.save(file);
            });
        } catch (Exception e) {
            log.error(e.getMessage());
            File file = fileRepository.findById(UUID.fromString(id)).orElseThrow(
                    () -> new FileException(MessageFormat.format(INCORRECT_FILE_ID, id)));
            file.setStatus(ResponseStatus.FILE_NOT_UPLOAD);
        }
        log.debug("Файл с id: {} добавлен в бакет {}", id, bucketName);
    }

    @Override
    @Transactional
    public void deleteFile(String bucketName, String id) {
        try {
            clientMap.get(bucketName)
                    .removeObject(RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(id)
                            .build()).thenAccept(unused ->
                    fileRepository.deleteById(UUID.fromString(id))
            );
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
        log.debug("Файл с id: {} удален из бакета {}", id, bucketName);
    }
}
