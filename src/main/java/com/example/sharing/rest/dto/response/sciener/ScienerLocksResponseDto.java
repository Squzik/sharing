package org.example.sharing.rest.dto.response.sciener;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScienerLocksResponseDto {
    private ScienerUserResponseDTO user;
    private List<ScienerLockResponseDTO> locks;
}
