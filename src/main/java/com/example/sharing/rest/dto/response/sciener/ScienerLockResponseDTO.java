package org.example.sharing.rest.dto.response.sciener;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScienerLockResponseDTO {
    private Integer id;
    private String lockName;
    private Boolean isLinked;
    private UUID flatId;
}
