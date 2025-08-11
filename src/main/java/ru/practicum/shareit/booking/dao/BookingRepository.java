package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;

import java.time.Instant;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer>, BookingRepositoryCustom {
    @Query("""
            select b
              from Booking b
                   join b.booker u
                   join b.item i
             where u.id = :bookerId
               and b.endDate < :before
               and i.id = :itemId
            """)
    List<Booking> findPastBookingsByBookerAndItem(@Param("bookerId") int bookerId, @Param("itemId") int itemId, @Param("before") Instant before);
}
