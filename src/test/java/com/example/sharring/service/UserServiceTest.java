package org.example.sharing.service;

import org.example.sharing.AbstractTest;
import org.example.sharing.dataset.impl.FlatDatasetImpl;
import org.example.sharing.dataset.impl.UserDatasetImpl;
import org.example.sharing.rest.handler.exception.NotFoundException;
import org.example.sharing.store.entity.User;
import org.example.sharing.store.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.example.sharing.rest.handler.exception.NotFoundException.CLIENT_NOT_FOUND;

@Transactional
public class UserServiceTest extends AbstractTest {

    @Autowired
    private UserDatasetImpl userDataset;

    @Autowired
    private FlatDatasetImpl flatDataset;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    void addData() {
        userDataset.createData();
        flatDataset.createData();
    }

    @AfterEach
    void flushAllRepositories() {
        flatDataset.removeData();
        userDataset.removeData();
    }

    UUID getUserId() {
        return userDataset.getData().getId();
    }

    UUID getFlatId() {
        return flatDataset.getData().getId();
    }

    @Test
    void addFavoriteFlat() {
        UUID userId = getUserId();
        UUID flatId = getFlatId();
        userService.addFavoriteFlat(userId, flatId);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(CLIENT_NOT_FOUND)
        );
        Assertions.assertNotNull(user.getFavouritesFlats());
    }

    @Test
    void addFavoriteFlatWithNullUserId() {
        NotFoundException thrown = Assertions
                .assertThrows(
                        NotFoundException.class,
                        () -> userService.addFavoriteFlat(UUID.randomUUID(), getFlatId())
                );
        Assertions.assertEquals("Клиент не найден", thrown.getMessage());
    }

    @Test
    void addFavoriteFlatWithNullFlatId() {
        NotFoundException thrown = Assertions
                .assertThrows(
                        NotFoundException.class,
                        () -> userService.addFavoriteFlat(getUserId(), UUID.randomUUID())
                );
        Assertions.assertEquals("Квартира не найдена", thrown.getMessage());
    }
}
