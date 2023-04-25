package br.com.wasp.ecommerce;

import br.com.wasp.ecommerce.consumer.ConsumerService;
import br.com.wasp.ecommerce.consumer.ServiceRunner;
import br.com.wasp.ecommerce.dispatcher.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BatchSendMessageService implements ConsumerService<String> {

    private final Connection connection;

    private final KafkaDispatcher<User> userDispatcher = new KafkaDispatcher<>();

    BatchSendMessageService() throws SQLException {
        String url = "jdbc:sqlite:service-users/target/users_database.db";
        connection = DriverManager.getConnection(url);
        try {
            connection.createStatement().execute("create table Users (" +
                    "uuid varchar(200) primary key," +
                    "email varchar(200)" +
                    ")");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ServiceRunner<>(BatchSendMessageService::new).start(5);
    }

    public void parse(ConsumerRecord<String, Message<String>> record) throws SQLException {
        System.out.println("------------------------------------------");
        System.out.println("Processing new batch");
        Message<String> message = record.value();
        System.out.println("Topic: " + message.getPayload());

        for (User user : getAllUsers()) {
            userDispatcher.sendAsync(
                    (String) message.getPayload(),
                    user.getUuid(),
                    message.getId().continueWith(BatchSendMessageService.class.getSimpleName()),
                    user
            );
        }
    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_SEND_MESSAGES_TO_ALL_USERS";
    }

    @Override
    public String getConsumerGroup() {
        return BatchSendMessageService.class.getSimpleName();
    }

    private List<User> getAllUsers() throws SQLException {
        var results = connection.prepareStatement("select uuid from Users").executeQuery();
        List<User> users = new ArrayList<>();
        while (results.next()) {
            users.add(new User(results.getString(1)));
        }
        return users;
    }
}
