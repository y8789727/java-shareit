package ru.practicum.shareit.booking.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import ru.practicum.shareit.booking.dto.BookingStateParam;
import ru.practicum.shareit.booking.model.Booking;

import java.time.Instant;
import java.util.List;

public class BookingRepositoryCustomImpl implements BookingRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    private List<Booking> findByUserWithSpecifiedState(String userQuery, int userId, BookingStateParam state) {
        String statePredicates = """
                and (:state = 'ALL'
                     or (:state = 'CURRENT' and :instantNow between b.startDate and b.endDate)
                     or (:state = 'PAST' and b.endDate < :instantNow)
                     or (:state = 'FUTURE' and b.startDate > :instantNow)
                     or (:state = 'WAITING' and b.status = 'WAITING')
                     or (:state = 'REJECTED' and b.status = 'REJECTED'))
                """;

        String orderBy = " order by b.startDate desc";

        return entityManager.createQuery(userQuery + " " + statePredicates + orderBy, Booking.class)
                .setParameter("userId", userId)
                .setParameter("state", state.name())
                .setParameter("instantNow", Instant.now())
                .getResultList();
    }

    @Override
    public List<Booking> findByBookerWithSpecifiedState(int bookerId, BookingStateParam state) {
        String queryByBooker = """
                select b
                  from Booking b
                       join b.booker u
                 where u.id = :userId
                """;

        return findByUserWithSpecifiedState(queryByBooker, bookerId, state);
    }

    @Override
    public List<Booking> findByOwnerWithSpecifiedState(int ownerId, BookingStateParam state) {
        String queryByOwner = """
                select b
                  from Booking b
                       join b.item i
                       join i.owner u
                 where u.id = :userId
                """;

        return findByUserWithSpecifiedState(queryByOwner, ownerId, state);
    }
}
