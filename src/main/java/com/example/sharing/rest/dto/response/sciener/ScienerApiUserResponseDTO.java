package org.example.sharing.rest.dto.response.sciener;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ScienerApiUserResponseDTO extends ScienerApiErrorResponseDTO {
    private String uid;
    private String access_token;
    private Integer expires_in;
    private String refresh_token;
}
