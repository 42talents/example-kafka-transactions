

## Usage

1. Start Kafka Containers `docker compose up`
2. Set the kafka consumer isolation level as required by the scenario
2. Start this application
3. Use Swagger UI to create orders


## Scenarios

### Kafka listener with default transaction isolation read_uncommitted

Property `spring.kafka.consumer.properties.isolation.level` is set to `read_uncommitted`, the default. 
See also https://docs.confluent.io/platform/current/installation/configuration/consumer-configs.html#isolation-level

When executing `POST /orders/createAndSend` with the error input to true the system starts retrieving messages also the kafka transaction is not commited. Leaving the db with "NEW" and "DONE" orders.

### Kafka listener with transaction isolation read_committed

Property `spring.kafka.consumer.properties.isolation.level` is set to `read_committed`.
See also https://docs.confluent.io/platform/current/installation/configuration/consumer-configs.html#isolation-level

When executing `POST /orders/createAndSend` with the error input to true the system does not retrieve messages as the kafka transaction is not commited. Leaving the db with "NEW" orders only.


## UI 

- Kafdrop: http://localhost:9000/
- Swagger UI: http://localhost:8080/swagger-ui/index.html
