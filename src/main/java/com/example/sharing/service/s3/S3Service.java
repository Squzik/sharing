package org.example.sharing.service.s3;

import org.example.sharing.rest.dto.s3.S3FileDto;

import java.util.UUID;

public interface S3Service {

    /**
     * Извлечение файла
     *
     * @param bucketName имя корзины
     * @param id         ключ файла
     * @return файл
     */
    S3FileDto getFile(String bucketName, UUID id);


    /**
     * Сохранение файла
     *
     * @param bucketName имя корзины
     * @param id         ключ файла
     * @param content    файл
     */
    void putFile(String bucketName, UUID id, byte[] content);

    void deleteFile(String bucketName, UUID id);
}
