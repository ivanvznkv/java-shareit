package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    private Long ownerId;
    private Long bookerId;
    private Long itemId;

    @BeforeEach
    void setUp() {
        UserCreateRequest ownerRequest = new UserCreateRequest(null, "Владелец", "owner@example.com");
        ownerId = userService.createUser(ownerRequest).getId();

        ItemCreateRequest itemCreateRequest = new ItemCreateRequest("Молоток", "Тяжелый молоток", true, null);
        itemId = itemService.createItem(ownerId, itemCreateRequest).getId();

        UserCreateRequest bookerRequest = new UserCreateRequest(null, "Бронирующий", "booker@example.com");
        bookerId = userService.createUser(bookerRequest).getId();
    }

    @Test
    void createBooking_shouldSaveWithStatusWaiting() {
        LocalDateTime now = LocalDateTime.now();
        BookingCreateRequest createRequest = new BookingCreateRequest(
                itemId,
                now.plusDays(1),
                now.plusDays(2)
        );

        BookingResponseDto response = bookingService.createBooking(createRequest, bookerId);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
        assertThat(response.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(response.getBooker().getId()).isEqualTo(bookerId);
        assertThat(response.getItem().getId()).isEqualTo(itemId);
    }

    @Test
    void createBooking_ownItem_shouldThrow() {
        LocalDateTime now = LocalDateTime.now();
        BookingCreateRequest createRequest = new BookingCreateRequest(
                itemId,
                now.plusDays(1),
                now.plusDays(2)
        );

        assertThatThrownBy(() -> bookingService.createBooking(createRequest, ownerId))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("не может бронировать свою собственную вещь");
    }

    @Test
    void createBooking_itemNotAvailable_shouldThrow() {
        ItemCreateRequest itemCreateRequest = new ItemCreateRequest("Молоток", "Тяжелый молоток", false, null);
        Long unavailableItemId = itemService.createItem(ownerId, itemCreateRequest).getId();

        LocalDateTime now = LocalDateTime.now();
        BookingCreateRequest createRequest = new BookingCreateRequest(
                unavailableItemId,
                now.plusDays(1),
                now.plusDays(2)
        );

        assertThatThrownBy(() -> bookingService.createBooking(createRequest, bookerId))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("недоступна для бронирования");
    }

    @Test
    void approveBooking_shouldSetApproved() {
        LocalDateTime now = LocalDateTime.now();
        BookingCreateRequest createRequest = new BookingCreateRequest(
                itemId,
                now.plusDays(1),
                now.plusDays(2)
        );
        BookingResponseDto created = bookingService.createBooking(createRequest, bookerId);

        BookingResponseDto approved = bookingService.approveBooking(created.getId(), true, ownerId);

        assertThat(approved.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void approveBooking_notOwner_shouldThrow() {
        LocalDateTime now = LocalDateTime.now();
        BookingCreateRequest createRequest = new BookingCreateRequest(
                itemId,
                now.plusDays(1),
                now.plusDays(2)
        );
        BookingResponseDto created = bookingService.createBooking(createRequest, bookerId);

        assertThatThrownBy(() -> bookingService.approveBooking(created.getId(), true, bookerId))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Только владелец вещи");
    }

    @Test
    void approveBooking_alreadyApproved_shouldThrow() {
        LocalDateTime now = LocalDateTime.now();
        BookingCreateRequest createRequest = new BookingCreateRequest(
                itemId,
                now.plusDays(1),
                now.plusDays(2)
        );
        BookingResponseDto created = bookingService.createBooking(createRequest, bookerId);
        bookingService.approveBooking(created.getId(), true, ownerId);

        assertThatThrownBy(() -> bookingService.approveBooking(created.getId(), true, ownerId))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("уже обработано");
    }

    @Test
    void getBookingById_shouldReturnBooking() {
        LocalDateTime now = LocalDateTime.now();
        BookingCreateRequest createRequest = new BookingCreateRequest(
                itemId,
                now.plusDays(1),
                now.plusDays(2)
        );
        BookingResponseDto created = bookingService.createBooking(createRequest, bookerId);

        BookingResponseDto found = bookingService.getBookingById(created.getId(), bookerId);
        assertThat(found.getId()).isEqualTo(created.getId());
    }

    @Test
    void getBookingById_notFound_shouldThrow() {
        assertThatThrownBy(() -> bookingService.getBookingById(999L, bookerId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getUserBookings_withStateAll_shouldReturnList() {
        LocalDateTime now = LocalDateTime.now();
        BookingCreateRequest req1 = new BookingCreateRequest(itemId, now.plusDays(1), now.plusDays(2));
        BookingCreateRequest req2 = new BookingCreateRequest(itemId, now.plusDays(3), now.plusDays(4));
        bookingService.createBooking(req1, bookerId);
        bookingService.createBooking(req2, bookerId);

        List<BookingResponseDto> bookings = bookingService.getUserBookings(bookerId, BookingState.ALL);

        assertThat(bookings).hasSize(2);
    }

    @Test
    void getOwnerBookings_shouldReturnList() {
        LocalDateTime now = LocalDateTime.now();
        BookingCreateRequest req = new BookingCreateRequest(itemId, now.plusDays(1), now.plusDays(2));
        bookingService.createBooking(req, bookerId);

        List<BookingResponseDto> bookings = bookingService.getOwnerBookings(ownerId, BookingState.ALL);

        assertThat(bookings).hasSize(1);
    }
}
