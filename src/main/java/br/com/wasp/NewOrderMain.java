package br.com.wasp;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try (var orderDispatcher = new KafkaDispatcher<Order>()) {
            try (var emailDispatcher = new KafkaDispatcher<String>()) {
                for (var i = 0; i < 3; i++) {
                    var userId = UUID.randomUUID().toString();
                    var orderId = UUID.randomUUID().toString();
                    var amount = BigDecimal.valueOf(Math.random() * 5000 + 1);
                    var order = new Order(userId, orderId, amount);

                    orderDispatcher.send("ECOMMERCE_NEW_ORDER", orderId, order);

                    var email = "Thanks! We are processing your order!";
                    emailDispatcher.send("ECOMMERCE_SEND_EMAIL", orderId, email);
                }
            }
        }
    }
}
