package org.example.sharing.dataset.impl;

import org.example.sharing.dataset.Creator;
import org.example.sharing.dataset.Finder;
import org.example.sharing.dataset.Remover;
import org.example.sharing.store.entity.Booking;
import org.example.sharing.store.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static org.example.sharing.store.entity.enums.BookingStatusEnum.BOOKING_WAITING;

@Component
@RequiredArgsConstructor
public class BookingDatasetImpl implements Creator<Booking>, Remover, Finder<Booking> {
    private final BookingRepository bookingRepository;
    private final UserDatasetImpl userDataset;
    private final FlatDatasetImpl flatDataset;
    private final ScienerCodeDatasetImpl scienerCode;

    @Override
    @Transactional
    public Booking createData() {
        return bookingRepository.save(getBooking());
    }

    @Override
    @Transactional
    public Booking getData() {
        return bookingRepository.findAll().stream()
                .findAny()
                .orElse(null);
    }

    @Override
    @Transactional
    public void removeData() {
        bookingRepository.deleteAllInBatch();
    }

    private Booking getBooking() {
        LocalDateTime dateStart = LocalDateTime.of(LocalDate.of(2073, 12, 12), LocalTime.of(6, 30));
        LocalDateTime dateEnd = LocalDateTime.of(LocalDate.of(2083, 12, 25), LocalTime.of(6, 30));
        return Booking.builder()
                .id(UUID.randomUUID())
                .start(dateStart)
                .end(dateEnd)
                .dateOfBookingRequest(LocalDate.now())
                .numberOfPeople(500)
                .bookingStatus(BOOKING_WAITING)
                .user(userDataset.getData())
                .flat(flatDataset.getData())
                .code(scienerCode.getData())
                .build();
    }
}
