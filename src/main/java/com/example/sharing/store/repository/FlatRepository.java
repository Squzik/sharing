package org.example.sharing.store.repository;

import org.example.sharing.store.entity.Flat;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;


public interface FlatRepository extends JpaRepository<Flat, UUID>, JpaSpecificationExecutor<Flat> {

    Set<Flat> findAllByUser_Id(@NotNull UUID id);

    void deleteAllByUser_Id(@NotNull UUID id);

    @Query(value = "select distinct on (b.flat_id) * from flat f, booking b where b.end_date_and_time < :time and b.user_id = :userId", nativeQuery = true)
    Set<Flat> findAllFlatsByClientBookingHistory(@NotNull UUID userId, @NotNull LocalDateTime time);
}
