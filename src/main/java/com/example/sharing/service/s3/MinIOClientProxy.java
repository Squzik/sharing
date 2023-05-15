package org.example.sharing.service.s3;

import org.example.sharing.rest.dto.s3.S3FileDto;

public interface MinIOClientProxy {

    S3FileDto getFile(String bucketName, String id);

    void putFile(String bucketName, String id, byte[] content);

    void deleteFile(String bucketName, String id);
}
