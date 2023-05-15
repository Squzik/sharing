package org.example.sharing.store.repository;

import org.example.sharing.store.entity.BlackList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BlackListRepository extends JpaRepository<BlackList, UUID> {

    void deleteByOwnerUserIdAndUserId(UUID ownerUserId, UUID userId);

    List<BlackList> findAllByOwnerUserId(UUID ownerUserId);
}
