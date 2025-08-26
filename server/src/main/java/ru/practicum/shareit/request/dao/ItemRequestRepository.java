package ru.practicum.shareit.request.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {
    @Query("""
            select ir
              from ItemRequest ir
                   join fetch ir.requester as u
             where u.id = :requesterId
            """)
    List<ItemRequest> findByRequester(@Param("requesterId") int requesterId);

    @Query("""
            select ir
              from ItemRequest ir
                   join fetch ir.requester as u
             where u.id != :requesterId
            """)
    List<ItemRequest> findByOtherUsers(@Param("requesterId") int requesterId);

}
