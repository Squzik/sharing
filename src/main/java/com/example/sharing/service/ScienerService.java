package org.example.sharing.service;

import org.example.sharing.rest.dto.request.ScienerUserRequestDTO;
import org.example.sharing.rest.dto.response.sciener.ScienerLocksResponseDto;
import org.example.sharing.store.entity.ScienerCode;
import org.example.sharing.store.entity.ScienerLock;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

public interface ScienerService {

    ScienerLocksResponseDto createUser(ScienerUserRequestDTO requestDTO);

    void accountExit();

    ScienerLocksResponseDto getLocks();

    ScienerLocksResponseDto getFreeLocks();

    void openLock(ScienerLock lock);

    Boolean checkLockGatewayExists(ScienerLock lock);

    Map<Integer, Boolean> checkLocksGatewayExists(Set<ScienerLock> locks);

    ScienerCode createCode(ScienerLock lock, LocalDateTime start, LocalDateTime end);

    void deleteCode(ScienerCode code);

    void deleteLock(Integer lockId);
}
