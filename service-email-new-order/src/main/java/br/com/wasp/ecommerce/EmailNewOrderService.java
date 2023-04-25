package br.com.wasp.ecommerce;

import br.com.wasp.ecommerce.consumer.ConsumerService;
import br.com.wasp.ecommerce.consumer.ServiceRunner;
import br.com.wasp.ecommerce.dispatcher.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.concurrent.ExecutionException;

public class EmailNewOrderService implements ConsumerService<Order> {

        private final KafkaDispatcher<String> emailDispatcher = new KafkaDispatcher<>();
    public static void main(String[] args) {
        new ServiceRunner<>(EmailNewOrderService::new).start(5);
    }

    public void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException {
        System.out.println("------------------------------------------");
        System.out.println("Processing new order, preparing email");
        Message<Order> message = record.value();
        System.out.println(message);

        var order = (Order) message.getPayload();
        var userEmail = order.getEmail();
        var correlationId = message.getId().continueWith(EmailNewOrderService.class.getSimpleName());
        var emailCode = "Thank you for your order! We are processing your order!";
        emailDispatcher.send("ECOMMERCE_SEND_EMAIL", userEmail, correlationId, emailCode);
    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_NEW_ORDER";
    }

    @Override
    public String getConsumerGroup() {
        return EmailNewOrderService.class.getSimpleName();
    }
}
