package org.example.sharing.rest.dto.s3;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class S3FileDto {

    private String id;

    private String bucket;

    private byte[] content;

    private ResponseStatus responseStatus;

}
