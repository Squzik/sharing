package org.example.sharing.dataset.impl;

import org.example.sharing.dataset.Creator;
import org.example.sharing.dataset.Finder;
import org.example.sharing.dataset.Remover;
import org.example.sharing.store.entity.ScienerCode;
import org.example.sharing.store.repository.ScienerCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ScienerCodeDatasetImpl implements Creator<ScienerCode>, Remover, Finder<ScienerCode> {
    private final ScienerCodeRepository scienerCodeRepository;
    private final ScienerLockDatasetImpl scienerLockDataset;

    @Override
    @Transactional
    public ScienerCode createData() {
        return scienerCodeRepository.save(createScienerCode());
    }

    @Override
    @Transactional
    public ScienerCode getData() {
        return scienerCodeRepository.findAll().stream()
                .findAny()
                .orElse(null);
    }

    @Override
    @Transactional
    public void removeData() {
        scienerCodeRepository.deleteAllInBatch();
    }

    private ScienerCode createScienerCode() {
        return ScienerCode.builder()
                .id(1)
                .password("password")
                .lock(scienerLockDataset.getData())
                .build();
    }
}
