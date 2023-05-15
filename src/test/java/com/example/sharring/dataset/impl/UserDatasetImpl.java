package org.example.sharing.dataset.impl;

import org.example.sharing.dataset.Creator;
import org.example.sharing.dataset.Finder;
import org.example.sharing.dataset.Remover;
import org.example.sharing.store.entity.User;
import org.example.sharing.store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;

@Component
@RequiredArgsConstructor
public class UserDatasetImpl implements Creator<User>, Remover, Finder<User> {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public User createData() {
        return userRepository.save(createUser());
    }

    @Override
    @Transactional
    public void removeData() {
        userRepository.deleteAllInBatch();
    }

    @Override
    @Transactional(readOnly = true)
    public User getData() {
        return userRepository.findAll().stream()
                .findAny()
                .orElse(null);
    }

    private User createUser() {
        return User.builder()
                .name("Иван")
                .patronymic("Иванович")
                .surname("Иванов")
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .favouritesFlats(new HashSet<>())
                .build();
    }
}
