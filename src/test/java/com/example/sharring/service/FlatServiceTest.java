package org.example.sharing.service;

import org.example.sharing.AbstractTest;
import org.example.sharing.store.entity.Address;
import org.example.sharing.store.entity.Flat;
import org.example.sharing.store.repository.AddressRepository;
import org.example.sharing.store.repository.FlatRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


@Transactional
class FlatServiceTest extends AbstractTest {

    @Autowired
    private FlatRepository flatRepository;
    @Autowired
    private FlatService flatService;
    @Autowired
    private AddressRepository addressRepository;

    @AfterEach
    void flushAllRepositories() {
        flatRepository.deleteAllInBatch();
        addressRepository.deleteAllInBatch();
    }

    private Flat createFlat() {
        return Flat.builder()
                .price(5000.0)
                .numberOfRooms(1)
                .description("Test description")
                .build();
    }

    private Address createAddress() {
        return Address.builder()
                .region("обл.Пензенская")
                .city("г.Пенза")
                .street("ул.Ферганская")
                .house("49")
                .numFlat(1).build();
    }
}
