package org.example.sharing.rest.dto.response.sciener;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class ScienerApiCodeResponseDTO extends ScienerApiErrorResponseDTO {
    private List<Code> list;

    @Getter
    @Setter
    public static class Code extends ScienerApiErrorResponseDTO {
        private Integer keyboardPwdId;
        private String keyboardPwd;
    }
}
