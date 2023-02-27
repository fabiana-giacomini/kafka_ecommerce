package br.com.wasp;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try (var dispatcher = new KafkaDispatcher()) {

            for (var i = 0; i < 3; i++) {
                var key = UUID.randomUUID().toString();
                var value = key + ",123123,6543,12353333333333";

                dispatcher.send("ECOMMERCE_NEW_ORDER", key, value);

                var email = "Thanks! We are processing your order!";
                dispatcher.send("ECOMMERCE_SEND_EMAIL", key, email);
            }
        }
    }
}
