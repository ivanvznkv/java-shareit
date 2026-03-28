package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerId(Long bookerId, Sort sort);

    @Query("select b from Booking b where b.booker.id = :userId and b.start <= :now and b.end > :now")
    List<Booking> findCurrentByBooker(@Param("userId") Long userId, @Param("now") LocalDateTime now, Sort sort);

    List<Booking> findByBookerIdAndEndBefore(Long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findByBookerIdAndStartAfter(Long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    List<Booking> findByItemOwnerId(Long ownerId, Sort sort);

    @Query("select b from Booking b where b.item.owner.id = :ownerId and b.start <= :now and b.end > :now")
    List<Booking> findCurrentByOwner(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now, Sort sort);

    List<Booking> findByItemOwnerIdAndEndBefore(Long ownerId, LocalDateTime now, Sort sort);

    List<Booking> findByItemOwnerIdAndStartAfter(Long ownerId, LocalDateTime now, Sort sort);

    List<Booking> findByItemOwnerIdAndStatus(Long ownerId, BookingStatus status, Sort sort);

    @Query("select b from Booking b where b.item.id = :itemId and b.status = 'APPROVED' and b.end < :now order by b.end desc")
    List<Booking> findLastBooking(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);

    @Query("select b from Booking b where b.item.id = :itemId and b.status = 'APPROVED' and b.start > :now order by b.start asc")
    List<Booking> findNextBooking(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);

    List<Booking> findByBookerIdAndItemIdAndStatus(Long bookerId, Long itemId, BookingStatus status);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Booking b " +
            "WHERE b.item.id = :itemId AND b.status IN :statuses " +
            "AND b.start < :end AND b.end > :start")
    boolean existsOverlappingBookings(@Param("itemId") Long itemId,
                                      @Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end,
                                      @Param("statuses") Collection<BookingStatus> statuses);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN :itemIds AND b.status = 'APPROVED' AND b.end < :now ORDER BY b.end DESC")
    List<Booking> findAllLastBookings(@Param("itemIds") List<Long> itemIds, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN :itemIds AND b.status = 'APPROVED' AND b.start > :now ORDER BY b.start ASC")
    List<Booking> findAllNextBookings(@Param("itemIds") List<Long> itemIds, @Param("now") LocalDateTime now);

    List<Booking> findByBookerIdAndItemIdAndStatusIn(Long bookerId, Long itemId, Collection<BookingStatus> statuses);

    List<Booking> findAllByItemIdInAndStatusOrderByStartDesc(List<Long> itemIds, BookingStatus status);
}
