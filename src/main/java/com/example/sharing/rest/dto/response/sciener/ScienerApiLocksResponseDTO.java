package org.example.sharing.rest.dto.response.sciener;

import lombok.Getter;

import java.util.Set;

@Getter
public class ScienerApiLocksResponseDTO extends ScienerApiErrorResponseDTO {
    private Set<Lock> list;
    private Integer pageNo;
    private Integer pages;

    @Getter
    public static class Lock extends ScienerApiLocksResponseDTO{
        private Integer lockId;
        private String lockAlias;
        private Integer hasGateway;
    }
}
