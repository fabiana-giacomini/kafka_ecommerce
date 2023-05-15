# Ecommerce services with Apache Kafka
Ecommerce monorepo with multiples services made to exercise Apache Kafka features and solidify knowledge.
<br>

This monorepo contains:
- Http servlet to receive a new order
- Http servlet to send batch e-mails for users
- A Service that detects frauds
- A service that create new users on ecommerce, if it's a new one
- A service that create the order
- A service that generate a report
- A service that send e-mails to users indicating the new order created
- Log service that verify every topic read by every service
- The communication between these services is implemented using Apache Kafka
- We have separated services using separated databases to avoid coupling (microservices)

Besides, we validate, using idempotency and fast delegate that an order is only processed once.
<br>