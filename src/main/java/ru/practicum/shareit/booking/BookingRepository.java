package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b " +
            "from Booking as b " +
            "where b.booker.id = ?1 " +
            "order by b.start desc")
    List<Booking> findAllUserBookings(Long bookerId, Pageable page);

    @Query("select b " +
            "from Booking as b " +
            "where b.booker.id = ?1 " +
            "and b.start <= now() and b.end >= now() " +
            "order by b.start desc")
    List<Booking> findBookerCurrentBookings(Long bookerId, Pageable page);

    @Query("select b " +
            "from Booking as b " +
            "where b.booker.id = ?1 " +
            "and b.start < now() and b.end <= now() " +
            "order by b.start desc")
    List<Booking> findBookerPastBookings(Long bookerId, Pageable page);

    @Query("select b " +
            "from Booking as b " +
            "where b.booker.id = ?1 and b.start > now() and b.end > now() " +
            "order by b.start desc")
    List<Booking> findBookerFutureBookings(Long bookerId, Pageable page);

    List<Booking> findByBooker_IdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status, Pageable page);

    List<Booking> findByItem_Owner_IdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status, Pageable page);

    List<Booking> findByItem_Owner_IdOrderByStartDesc(Long ownerId, Pageable page);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.owner.id = ?1 " +
            "and b.start <= now() and b.end >= now() " +
            "order by b.start desc")
    List<Booking> findOwnerCurrentBookings(Long bookerId, Pageable page);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.owner.id = ?1 " +
            "and b.start < now() and b.end <= now() " +
            "order by b.start desc")
    List<Booking> findOwnerPastBookings(Long bookerId, Pageable page);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.owner.id = ?1 and b.start > now() and b.end > now() " +
            "order by b.start desc")
    List<Booking> findOwnerFutureBookings(Long bookerId, Pageable page);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.id = ?1 and b.status = 'APPROVED' " +
            "and b.start < now() " +
            "order by b.start")
    List<Booking> findItemLastBookings(Long itemId);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.id = ?1 and b.status = 'APPROVED' " +
            "and b.start >= now() " +
            "order by b.start")
    List<Booking> findItemNextBookings(Long itemId);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.id = ?1 and b.booker.id = ?2 and b.status = 'APPROVED' " +
            "and b.start < now() and b.end <= now()")
    List<Booking> findBookerItemBookings(Long itemId, Long userId);
}
