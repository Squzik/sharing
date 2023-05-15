package org.example.sharing.dataset.impl;

import org.example.sharing.dataset.Creator;
import org.example.sharing.dataset.Finder;
import org.example.sharing.dataset.Remover;
import org.example.sharing.store.entity.Address;
import org.example.sharing.store.entity.Flat;
import org.example.sharing.store.repository.FlatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class FlatDatasetImpl implements Creator<Flat>, Remover, Finder<Flat> {
    private final FlatRepository flatRepository;
    private final ScienerLockDatasetImpl scienerLockDataset;
    private final UserDatasetImpl userDataset;

    @Override
    @Transactional
    public Flat createData() {
        return flatRepository.save(createFlat());
    }

    @Override
    @Transactional(readOnly = true)
    public Flat getData() {
        return flatRepository.findAll().stream()
                .findAny()
                .orElse(null);
    }

    @Override
    @Transactional
    public void removeData() {
        flatRepository.deleteAllInBatch();
    }

    private Flat createFlat() {
        return Flat.builder()
                .price(5000.0)
                .numberOfRooms(1)
                .description("Test description")
                .appliances(true)
                .balcony(true)
                .floor(1)
                .furniture(true)
                .internetCableTv(true)
                .isCombinedBathroom(true)
                .isHidden(true)
                .user(userDataset.getData())
                .scienerLock(scienerLockDataset.getData())
                .address(Address.builder()
                        .region("обл.Пензенская")
                        .city("г.Пенза")
                        .street("ул.Ферганская")
                        .house("49")
                        .numFlat(1).build()
                ).build();
    }
}
