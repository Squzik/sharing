package org.example.sharing.rest.dto.s3;

import io.minio.ObjectWriteResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.concurrent.CompletableFuture;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class S3PutFileDto {

    private String id;

    private String bucket;

    private CompletableFuture<ObjectWriteResponse> content;

    private ResponseStatus responseStatus;

}
