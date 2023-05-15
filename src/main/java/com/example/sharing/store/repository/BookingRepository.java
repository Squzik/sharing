package org.example.sharing.store.repository;

import org.example.sharing.store.entity.Booking;
import org.example.sharing.store.entity.enums.BookingStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID>, JpaSpecificationExecutor<Booking> {

    List<Booking> getStartAndEndByFlatIdAndBookingStatusNotAndEndAfter(UUID id, BookingStatusEnum status, LocalDateTime time);
}
