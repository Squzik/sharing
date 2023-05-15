package org.example.sharing.service.s3.impl;

import org.example.sharing.rest.dto.s3.S3FileDto;
import org.example.sharing.service.s3.MinIOClientProxy;
import org.example.sharing.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.UUID;

import static org.example.sharing.utils.ServiceUtils.checkNulls;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private final MinIOClientProxy minIOClientProxy;

    @Override
    @Nullable
    public S3FileDto getFile(String bucketName, UUID id) {
        return checkNulls(id, bucketName)
                ? this.minIOClientProxy.getFile(bucketName, id.toString())
                : null;
    }

    @Override
    public void putFile(String bucketName, UUID id, byte[] content) {
        if (checkNulls(id, bucketName, content) && ArrayUtils.isNotEmpty(content)) {
            this.minIOClientProxy.putFile(bucketName, id.toString(), content);
        }
    }

    @Override
    public void deleteFile(String bucketName, UUID id) {
        log.debug("Checking for null before deleting id: {} in bucket: {}", id, bucketName);
        if (checkNulls(id, bucketName)) {
            log.debug("Delete file with id: {} in bucket: {}", id, bucketName);
            minIOClientProxy.deleteFile(bucketName, id.toString());
        }
    }
}
