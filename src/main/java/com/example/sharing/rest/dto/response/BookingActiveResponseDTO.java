package org.example.sharing.rest.dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BookingActiveResponseDTO extends BookingResponseDTO {

    private String password;

    private Boolean isLockOnline;
}
