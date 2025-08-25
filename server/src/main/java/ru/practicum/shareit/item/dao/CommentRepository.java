package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByItemId(int itemId);

    @Query("""
            select c
              from Comment as c
                   join c.item as i
                   join i.owner as o
             where o.id = :ownerId
             order by i.id, c.created desc
            """)
    List<Comment> findByItemsOwner(@Param("ownerId") int ownerId);
}
