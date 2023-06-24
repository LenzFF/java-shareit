package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {

    ItemRequest findById(long id);

    List<ItemRequest> findAllByRequestorId(long userId, Sort sort);

    List<ItemRequest> findAllByRequestorIdNot(long userId, Pageable page);
}
