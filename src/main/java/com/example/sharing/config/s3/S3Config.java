package org.example.sharing.config.s3;

import org.example.sharing.rest.handler.exception.FileException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioAsyncClient;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.XmlParserException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

import static org.example.sharing.utils.constant.s3.S3BucketConstant.DOCUMENTS;
import static org.example.sharing.utils.constant.s3.S3BucketConstant.PHOTOS;

@Slf4j
@Getter
@Setter
@RequiredArgsConstructor
@Configuration
public class S3Config {

    private final S3Settings s3Settings;

    //Если нужен будет еще один бакет:
    //Создать соответствующую конфигурацию в пропертях
    //Создать константу в S3BucketConstant
    //Создать такой же бин с Qualifier новой константы
    //Заинжектить в MinIOClientProxy созданный бин и добавить его в мапу
    //Далее работаем с нужным бакетом, прокидывая его как параметр
    @Bean(name = PHOTOS)
    public MinioAsyncClient photoMinioClient() {
        return createClient(PHOTOS);
    }

    @Bean(name = DOCUMENTS)
    public MinioAsyncClient documentMinioClient() {
        return createClient(DOCUMENTS);
    }

    private MinioAsyncClient createClient(String bucketName) {
        MinioAsyncClient minioClient = MinioAsyncClient.builder()
                .credentials(s3Settings.getBuckets().get(bucketName).getAccess(),
                        s3Settings.getBuckets().get(bucketName).getSecret())
                .endpoint(s3Settings.getUrl())
                .build();
        createBucket(minioClient, bucketName);
        return minioClient;
    }

    /**
     * Создание бакета
     *
     * @param minioClient клиент подключения к minio
     * @param bucketName  название бакета
     */
    private void createBucket(MinioAsyncClient minioClient, String bucketName) {
        try {
            var found = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());
            if (!found.get()) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
            }
        } catch (InsufficientDataException | IOException |
                NoSuchAlgorithmException | InvalidKeyException |
                XmlParserException | InternalException |
                ExecutionException | InterruptedException e) {
            log.error("Ошибка {} создания бакета, проверьте подключение к MinIO. Текст ошибки: {}",
                    e.getClass(),
                    e.getMessage());
            throw new FileException(e.getMessage());
        }
    }
}
