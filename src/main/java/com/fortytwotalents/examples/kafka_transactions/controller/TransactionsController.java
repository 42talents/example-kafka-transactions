package com.fortytwotalents.examples.kafka_transactions.controller;

import com.fortytwotalents.examples.kafka_transactions.domain.Order;
import com.fortytwotalents.examples.kafka_transactions.producer.OrdersProducer;
import com.fortytwotalents.examples.kafka_transactions.repository.OrderRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/orders")
public class TransactionsController {

    OrdersProducer producer;
    OrderRepository repository;

    public TransactionsController(OrdersProducer producer, OrderRepository repository) {
        this.producer = producer;
        this.repository = repository;
    }

    @DeleteMapping
    public void removeAllOrders() {
        repository.deleteAll();
    }

    @PostMapping("/createAndSend")
    public void createAndSendOrders(@RequestBody ErrorInput error) {
        producer.createAndSendOrders(error.isError());
    }

    @PostMapping("/withinDbTransaction")
    @Transactional("transactionManager")
    public void createAndSendOrdersWithinTransaction(@RequestBody ErrorInput error) {
        producer.createAndSendOrders(error.isError());
    }

    @PostMapping(path = "/createThenSend")
    public void createThenSend(@RequestBody Input input) throws InterruptedException {
        List<Order> orders;
        if (input.jdbcTransaction) {
            orders = producer.createTransactional(input.createError, 20);
        } else {
            orders = producer.createNoTransaction(input.createError, 20);
        }
        if (input.kafkaTransaction) {
            producer.sendWithinKafkaTransaction(input.sendError, orders);
        } else {
            producer.sendNoTransaction(input.sendError, orders);
        }
    }


    @GetMapping
    public List<Order> findAll() {
        return (List<Order>) repository.findAll();
    }

    public static class ErrorInput {
        boolean error;

        public boolean isError() {
            return error;
        }

        public void setError(boolean error) {
            this.error = error;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ErrorInput that = (ErrorInput) o;
            return error == that.error;
        }

        @Override
        public int hashCode() {
            return Objects.hash(error);
        }

        @Override
        public String toString() {
            return "ErrorInput{" +
                    "error=" + error +
                    '}';
        }
    }

    public static class Input {
        boolean createError;
        boolean sendError;
        boolean jdbcTransaction;
        boolean kafkaTransaction;

        public boolean isCreateError() {
            return createError;
        }

        public void setCreateError(boolean createError) {
            this.createError = createError;
        }

        public boolean isSendError() {
            return sendError;
        }

        public void setSendError(boolean sendError) {
            this.sendError = sendError;
        }

        public boolean isJdbcTransaction() {
            return jdbcTransaction;
        }

        public void setJdbcTransaction(boolean jdbcTransaction) {
            this.jdbcTransaction = jdbcTransaction;
        }

        public boolean isKafkaTransaction() {
            return kafkaTransaction;
        }

        public void setKafkaTransaction(boolean kafkaTransaction) {
            this.kafkaTransaction = kafkaTransaction;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Input input = (Input) o;
            return createError == input.createError && sendError == input.sendError && jdbcTransaction == input.jdbcTransaction && kafkaTransaction == input.kafkaTransaction;
        }

        @Override
        public int hashCode() {
            return Objects.hash(createError, sendError, jdbcTransaction, kafkaTransaction);
        }

        @Override
        public String toString() {
            return "Input{" +
                    "createError=" + createError +
                    ", sendError=" + sendError +
                    ", jdbcTransaction=" + jdbcTransaction +
                    ", kafkaTransaction=" + kafkaTransaction +
                    '}';
        }
    }
}
