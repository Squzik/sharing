package org.example.sharing.dataset.impl;

import org.example.sharing.dataset.Creator;
import org.example.sharing.dataset.Finder;
import org.example.sharing.dataset.Remover;
import org.example.sharing.store.entity.ScienerUser;
import org.example.sharing.store.repository.ScienerUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ScienerDatasetImpl implements Creator<ScienerUser>, Remover, Finder<ScienerUser> {
    private final ScienerUserRepository scienerUserRepository;

    @Override
    @Transactional
    public ScienerUser createData() {
        return scienerUserRepository.save(createScienerUser());
    }

    @Override
    @Transactional
    public ScienerUser getData() {
        return scienerUserRepository.findAll().stream()
                .findAny()
                .orElse(null);
    }

    @Override
    @Transactional
    public void removeData() {
        scienerUserRepository.deleteAllInBatch();
    }

    public ScienerUser createScienerUser() {
        return ScienerUser
                .builder()
                .id(1)
                .build();
    }
}
