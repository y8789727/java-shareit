package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.dto.ItemWithBookInfoDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    @Query("""
            select i
              from Item i
                   join fetch i.owner as u
             where u.id = :userId
            """)
    List<Item> findByUser(@Param("userId") int userId);

    @Query("""
            select i
              from Item i
             where (lower(name) like lower(concat('%', ?1, '%')) or lower(description) like lower(concat('%',?1,'%')))
               and i.isAvailable
            """)
    List<Item> findByParams(String text);

    @Query("""
            select new ru.practicum.shareit.item.dto.ItemWithBookInfoDto (
                    i.id,
                    i.name,
                    i.description,
                    i.isAvailable,
                    prevBook.startDate,
                    prevBook.endDate,
                    nextBook.startDate,
                    nextBook.endDate)
              from Item i
                   join i.owner as u
                   left join (select b.startDate startDate,
                                     b.endDate endDate,
                                     bi.id itemId
                                from Booking b
                                     join b.item bi
                               where b.startDate < Instant
                               order by b.startDate desc
                               limit 1) as prevBook
                           on prevBook.itemId = i.id
                   left join (select b.startDate startDate,
                                     b.endDate endDate,
                                     bi.id itemId
                                from Booking b
                                     join b.item bi
                               where b.startDate > Instant
                               order by b.startDate
                               limit 1) as nextBook
                           on nextBook.itemId = i.id
             where u.id = :userId
             order by i.id
            """)
    List<ItemWithBookInfoDto> findByUserWithBookInfo(@Param("userId") int userId);

}
