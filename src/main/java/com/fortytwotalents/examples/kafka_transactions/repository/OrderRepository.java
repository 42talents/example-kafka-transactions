package com.fortytwotalents.examples.kafka_transactions.repository;

import com.fortytwotalents.examples.kafka_transactions.domain.Order;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OrderRepository extends
        CrudRepository<Order, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Order> findById(Long groupId);

    @Override
    Order save(Order entity);
}
