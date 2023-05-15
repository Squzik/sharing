package org.example.sharing.service;

import org.example.sharing.AbstractTest;
import org.example.sharing.dataset.impl.BookingDatasetImpl;
import org.example.sharing.dataset.impl.FlatDatasetImpl;
import org.example.sharing.dataset.impl.ScienerCodeDatasetImpl;
import org.example.sharing.dataset.impl.ScienerDatasetImpl;
import org.example.sharing.dataset.impl.ScienerLockDatasetImpl;
import org.example.sharing.dataset.impl.UserDatasetImpl;
import org.example.sharing.rest.dto.request.AddFileRequestDto;
import org.example.sharing.rest.dto.request.BookingRequestDTO;
import org.example.sharing.rest.dto.response.BookingActiveResponseDTO;
import org.example.sharing.rest.dto.response.BookingDatesResponseDTO;
import org.example.sharing.rest.dto.response.BookingResponseDTO;
import org.example.sharing.security.jwt.JwtAuthentication;
import org.example.sharing.store.entity.Booking;
import org.example.sharing.store.entity.ScienerUser;
import org.example.sharing.store.entity.User;
import org.example.sharing.store.entity.enums.BookingStatusEnum;
import org.example.sharing.store.repository.BookingRepository;
import org.example.sharing.store.repository.ScienerUserRepository;
import org.example.sharing.store.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.example.sharing.store.entity.enums.BookingStatusEnum.BOOKING_ACCEPT;
import static org.example.sharing.store.entity.enums.BookingStatusEnum.BOOKING_DENY;
import static org.example.sharing.store.entity.enums.BookingStatusEnum.BOOKING_WAITING;

@Transactional
public class BookingServiceTest extends AbstractTest {

    private static final String TEST = "test";

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ScienerUserRepository scienerRepository;
    @Autowired
    private UserDatasetImpl userDataset;
    @Autowired
    private FlatDatasetImpl flatDataset;
    @Autowired
    private BookingDatasetImpl bookingDataset;
    @Autowired
    private ScienerLockDatasetImpl scienerLockDataset;
    @Autowired
    private ScienerCodeDatasetImpl scienerCodeDataset;
    @Autowired
    private ScienerDatasetImpl scienerDataset;

    @MockBean
    private ScienerService scienerService;

    @BeforeEach
    void addData() {
        userDataset.createData();
        var id = scienerDataset.createData().getId();
        scienerRepository.saveAndFlush(createSciener(id));
        User user = userDataset.getData();
        user.setScienerUser(scienerRepository.getById(1));
        user.setIsMailConfirm(true);
        userRepository.saveAndFlush(user);
        scienerLockDataset.createData();
        flatDataset.createData();
        scienerCodeDataset.createData();
        Booking booking = bookingDataset.createData();
        JwtAuthentication jwtAuthentication = new JwtAuthentication();
        jwtAuthentication.setAuthenticated(true);
        jwtAuthentication.setId(booking.getFlat().getUser().getId());
        jwtAuthentication.setMail(getUser().getMail());
        SecurityContextHolder.getContext().setAuthentication(jwtAuthentication);
    }

    @AfterEach
    void flushAllRepositories() {
        bookingDataset.removeData();
        scienerCodeDataset.removeData();
        flatDataset.removeData();
        scienerLockDataset.removeData();
        userDataset.removeData();
        scienerDataset.removeData();
    }

    private ScienerUser createSciener(Integer id) {
        ScienerUser scienerUser = scienerDataset.getData();
        scienerUser.setId(1);
        return scienerUser;
    }

    User getUser() {
        return userDataset.getData();
    }

    Booking getBooking() {
        return bookingDataset.getData();
    }

    UUID getFlatId() {
        return flatDataset.getData().getId();
    }

    @Test
    void addBookingTest() {
        LocalDateTime dateStart = LocalDateTime.of(LocalDate.of(2073, 12, 12), LocalTime.of(6, 30));
        LocalDateTime dateEnd = LocalDateTime.of(LocalDate.of(2083, 12, 25), LocalTime.of(6, 30));
        UUID userId = getUser().getId();
        UUID flatId = getFlatId();
        bookingService.addBooking(BookingRequestDTO.builder()
                .flatId(flatId)
                .startDateAndTime(dateStart)
                .endDateAndTime(dateEnd)
                .numberOfPeople(500)
                .bookingStatus(BOOKING_WAITING)
                .build());
        List<Booking> bookingList = bookingRepository.findAll();
        Assertions.assertFalse(bookingList.isEmpty());
        Booking booking = getBooking();
        Assertions.assertEquals(booking.getUser().getId(), userId);
        Assertions.assertEquals(booking.getFlat().getId(), flatId);
        Assertions.assertEquals(booking.getStart(), dateStart);
        Assertions.assertEquals(booking.getEnd(), dateEnd);
        Assertions.assertEquals(booking.getNumberOfPeople(), 500);
    }

    @Test
    void erroneousEditBookingStatusTest() {
        Booking booking = bookingDataset.getData();
        UUID bookingId = getBooking().getId();
        bookingService.editBookingStatus(bookingId, BOOKING_WAITING);
        Assertions.assertEquals(booking.getBookingStatus(), BOOKING_WAITING);
    }

    @Test
    void openBookingLockTest() {
        Booking booking = bookingDataset.getData();
        UUID bookingId = getBooking().getId();
        booking.setId(bookingId);
        booking.setBookingStatus(BOOKING_ACCEPT);
        bookingRepository.save(booking);
        Mockito.doNothing().when(scienerService).deleteCode(null);
        bookingService.openBookingLock(booking.getId());
        Assertions.assertNotEquals(booking.getFlat().getScienerLock(), null);
    }

    @Test
    void deleteBookingTest() {
        Booking booking = bookingDataset.getData();
        Mockito.doNothing().when(scienerService).deleteCode(null);
        bookingService.deleteBooking(booking.getId());
        Assertions.assertFalse(bookingRepository.existsById(booking.getId()));
    }

    @Test
    void addFileTest() {
        Booking booking = getBooking();
        String test = bookingService.addFile(booking.getId(), AddFileRequestDto.builder()
                .file(new MockMultipartFile(TEST, "String".getBytes()))
                .build());
        Assertions.assertNotNull(test);
    }

    @Test
    void deleteFileTest() {
        Booking booking = bookingDataset.getData();
        booking.setId(getBooking().getId());
        var id = bookingRepository.saveAndFlush(booking).getId();
        String test = bookingService.addFile(booking.getId(), AddFileRequestDto.builder()
                .file(new MockMultipartFile(TEST, "String".getBytes()))
                .build());
        bookingService.deleteFile(UUID.fromString(test), id);
        Assertions.assertNull(booking.getFiles());
    }

    @Test
    void getBookingsDatesByFlatIdTest() {
        List<BookingDatesResponseDTO> listOfDTO = bookingService.getBookingsDatesByFlatId(getFlatId());
        List<LocalDateTime> startDatesOfBooking = listOfDTO.stream().map(BookingDatesResponseDTO::getStart).collect(Collectors.toUnmodifiableList());
        Assertions.assertFalse(startDatesOfBooking.isEmpty());
    }

    @Test
    void getActiveBookingTest() {
        List<BookingActiveResponseDTO> listOfDTO = bookingService.getActiveBooking();
        List<BookingResponseDTO> dtos = new ArrayList<>(listOfDTO);
        List<BookingStatusEnum> statusesOfBooking = dtos.stream().map(BookingResponseDTO::getBookingStatus).collect(Collectors.toUnmodifiableList());
        Assertions.assertFalse(statusesOfBooking.contains(BOOKING_DENY));
    }

    @Test
    void getWaitingBookingForLandlordTest() {
        List<BookingResponseDTO> dtos = bookingService.getWaitingBookingForLandlord();
        List<BookingStatusEnum> statusesOfBooking = dtos.stream().map(BookingResponseDTO::getBookingStatus).collect(Collectors.toUnmodifiableList());
        Assertions.assertTrue(statusesOfBooking.contains(BOOKING_WAITING));
    }

    @Test
    void getWaitingBookingForRentedTest() {
        List<BookingResponseDTO> dtos = bookingService.getWaitingBookingForRented();
        List<BookingStatusEnum> statusesOfBooking = dtos.stream().map(BookingResponseDTO::getBookingStatus).collect(Collectors.toUnmodifiableList());
        Assertions.assertTrue(statusesOfBooking.contains(BOOKING_WAITING));
    }

    @Test
    void getFlatActiveBookingTest() {
        List<BookingResponseDTO> dtos = bookingService.getFlatActiveBooking(getFlatId());
        List<BookingStatusEnum> statusesOfBooking = dtos.stream().map(BookingResponseDTO::getBookingStatus).collect(Collectors.toUnmodifiableList());
        Assertions.assertFalse(statusesOfBooking.contains(BOOKING_WAITING));
    }

    @Test
    void getFlatWaitingBookingTest() {
        List<BookingResponseDTO> dtos = bookingService.getFlatWaitingBooking(getFlatId());
        List<BookingStatusEnum> statusesOfBooking = dtos.stream().map(BookingResponseDTO::getBookingStatus).collect(Collectors.toUnmodifiableList());
        Assertions.assertTrue(statusesOfBooking.contains(BOOKING_WAITING));
    }

    @Test
    void getFlatHistoryBookingTest() {
        List<BookingResponseDTO> dtos = bookingService.getFlatHistoryBooking(getFlatId());
        List<BookingStatusEnum> statusesOfBooking = dtos.stream().map(BookingResponseDTO::getBookingStatus).collect(Collectors.toUnmodifiableList());
        Assertions.assertFalse(statusesOfBooking.contains(BOOKING_WAITING));
    }

}
