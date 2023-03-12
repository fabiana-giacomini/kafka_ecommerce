package br.com.wasp.ecommerce;

import java.math.BigDecimal;

public class Order {

    private final String userId, orderId, email;
    private final BigDecimal amount;

    public Order(String userId, String orderId, BigDecimal amount, String email) {
        this.userId = userId;
        this.orderId = orderId;
        this.amount = amount;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }
}
