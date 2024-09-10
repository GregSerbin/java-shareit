package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query("select ir " +
            "from ItemRequest as ir " +
            "join ir.owner as o " +
            "where o.id = :ownerId " +
            "group by ir.id " +
            "order by ir.created desc ")
    List<ItemRequest> findByOwnerId(@Param("ownerId") Long ownerId);

    @Query("select ir " +
            "from ItemRequest as ir " +
            "join ir.owner as o " +
            "where o.id != :ownerId " +
            "group by ir.id " +
            "order by ir.created desc ")
    List<ItemRequest> findByOwnerIdNotEquals(@Param("ownerId") Long ownerId);
}