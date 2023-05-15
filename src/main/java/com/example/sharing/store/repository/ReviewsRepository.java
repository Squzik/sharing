package org.example.sharing.store.repository;

import org.example.sharing.store.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;
import java.util.UUID;

public interface ReviewsRepository extends JpaRepository<Review, UUID> {

    Set<Review> findByUser_Id(UUID id);

    Set<Review> findByFlat_Id(UUID id);
}
