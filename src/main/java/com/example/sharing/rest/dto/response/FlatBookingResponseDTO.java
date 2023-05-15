package org.example.sharing.rest.dto.response;

import org.example.sharing.rest.dto.s3.S3FileDto;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class FlatBookingResponseDTO {
    private UUID id;
    private String name;
    private AddressResponseDTO address;
    private S3FileDto photo;
}
