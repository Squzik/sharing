package org.example.sharing.rest.dto.response;

import org.example.sharing.rest.dto.s3.S3FileDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserListResponseDTO {

    private UUID id;

    private S3FileDto photo;

    private String name;

    private String surname;
}
