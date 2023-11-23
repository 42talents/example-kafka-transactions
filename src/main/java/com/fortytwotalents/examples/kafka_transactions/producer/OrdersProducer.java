package com.fortytwotalents.examples.kafka_transactions.producer;

import com.fortytwotalents.examples.kafka_transactions.domain.Order;
import com.fortytwotalents.examples.kafka_transactions.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class OrdersProducer {

    private static final Logger LOG = LoggerFactory
            .getLogger(OrdersProducer.class);

    KafkaTemplate<Long, Order> kafkaTemplate;
    OrderRepository repository;

    public OrdersProducer(OrderRepository repository, KafkaTemplate<Long, Order> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional("kafkaTransactionManager")
    public void sendWithinKafkaTransaction(boolean error, List<Order> orders) {
        sendNoTransaction(error, orders);
    }

    public void sendNoTransaction(boolean error, List<Order> orders) {
        int count = 0;
        for (Order order : orders) {
            CompletableFuture<SendResult<Long, Order>> result =
                    kafkaTemplate.send("orders", order.getId(), order);
            result.whenComplete((sr, ex) ->
                    LOG.info("Sent({}): {}", sr.getProducerRecord().key(), sr.getProducerRecord().value()));
            count = count + 1;
            if (error && count > 10)
                throw new RuntimeException();
        }
    }

    @Transactional("kafkaTransactionManager")
    public void createAndSendOrders(boolean error) {
        for (long i = 0; i < 20; i++) {
            Order order = new Order("NEW");
            repository.save(order);
            CompletableFuture<SendResult<Long, Order>> result =
                    kafkaTemplate.send("orders", order.getId(), order);
            result.whenComplete((sr, ex) ->
                    LOG.info("Sent({}): {}", sr.getProducerRecord().key(), sr.getProducerRecord().value()));
            if (error && i > 10)
                throw new RuntimeException();
            sleep(100);
        }
    }

    @Transactional("transactionManager")
    public List<Order> createTransactional(boolean error, int amount)  {
        return createNoTransaction(error, amount);
    }

    public List<Order> createNoTransaction(boolean error, int amount)  {
        List<Order> result = new ArrayList<>(amount);
        for (long i = 0; i < amount; i++) {
            Order order = new Order("NEW");
            repository.save(order);
            result.add(order);
            if (error && i > 10)
                throw new RuntimeException();
            sleep(100);
        }
        return result;
    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
