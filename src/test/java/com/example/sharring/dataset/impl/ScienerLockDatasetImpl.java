package org.example.sharing.dataset.impl;

import org.example.sharing.dataset.Creator;
import org.example.sharing.dataset.Finder;
import org.example.sharing.dataset.Remover;
import org.example.sharing.store.entity.ScienerLock;
import org.example.sharing.store.repository.ScienerLockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Component
@RequiredArgsConstructor
public class ScienerLockDatasetImpl implements Creator<ScienerLock>, Remover, Finder<ScienerLock> {
    private final ScienerLockRepository scienerLockRepository;
    private final UserDatasetImpl userDataset;

    private static final String TEST = "test";

    @Override
    @Transactional
    public ScienerLock createData() {
        return scienerLockRepository.save(createScienerLock());
    }

    @Override
    @Transactional
    public ScienerLock getData() {
        return scienerLockRepository.findAll().stream()
                .findAny()
                .orElse(null);
    }

    @Override
    @Transactional
    public void removeData() {
        scienerLockRepository.deleteAllInBatch();
    }

    private ScienerLock createScienerLock() {
        return ScienerLock.builder()
                .id(1)
                .codes(new HashSet<>())
                .lockName(TEST)
                .scienerUser(userDataset.getData().getScienerUser())
                .isLinked(true)
                .build();
    }
}
