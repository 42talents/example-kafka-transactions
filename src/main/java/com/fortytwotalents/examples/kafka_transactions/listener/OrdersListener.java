package com.fortytwotalents.examples.kafka_transactions.listener;

import com.fortytwotalents.examples.kafka_transactions.domain.Order;
import com.fortytwotalents.examples.kafka_transactions.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrdersListener {

    private static final Logger LOG = LoggerFactory
            .getLogger(OrdersListener.class);

    OrderRepository repository;

    public OrdersListener(OrderRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(
            id = "orders",
            topics = "orders",
            groupId = "a",
            concurrency = "3"
    )
    @Transactional(value = "transactionManager")
    public void listen(Order input) {
        LOG.info("Received: {}", input);
        Order order = repository
                .findById(input.getId())
                .orElseThrow();
        if (order.getStatus().equals("NEW")) {
            order.setStatus("DONE");
            repository.save(order);
            LOG.info("Finished: {}", order);
        }
    }
}
