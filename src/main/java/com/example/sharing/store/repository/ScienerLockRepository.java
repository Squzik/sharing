package org.example.sharing.store.repository;

import org.example.sharing.store.entity.ScienerLock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface ScienerLockRepository extends JpaRepository<ScienerLock, Integer> {
    Set<ScienerLock> getAllByScienerUserId(Integer id);
}
