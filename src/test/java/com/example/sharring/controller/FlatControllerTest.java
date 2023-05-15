package org.example.sharing.controller;

import org.example.sharing.common.DatabaseContainer;
import org.example.sharing.common.MinioContainer;
import org.example.sharing.store.entity.Address;
import org.example.sharing.store.entity.Flat;
import org.example.sharing.store.repository.AddressRepository;
import org.example.sharing.store.repository.FlatRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

@Disabled
@WithMockUser
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FlatControllerTest {

    @Container
    public static PostgreSQLContainer<DatabaseContainer> dbContainer = DatabaseContainer.getInstance();
    @Container
    public static GenericContainer<MinioContainer> minioContainer = MinioContainer.getInstance();

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private FlatRepository flatRepository;
    @Autowired
    private AddressRepository addressRepository;


    @BeforeEach
    public void initDatabase() {
        flatRepository.deleteAllInBatch();
        Flat flat = new Flat();
        flat.setId(UUID.randomUUID());
        flat.setAddress(new Address(1L, "обл.Пензенская", "г.Заречный", "Зеленая", "5", 29, 53.123458, 46.553644));
        flat.setPrice(5000.0);
        flat.setNumberOfRooms(1);
        flat.setDescription("test");
        flatRepository.save(flat);
    }

    @AfterEach
    private void flushAllRepositories() {
        flatRepository.deleteAllInBatch();
        addressRepository.deleteAllInBatch();
    }
}
