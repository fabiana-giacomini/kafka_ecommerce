package br.com.wasp.ecommerce;

public class User {
    private final String uuid;

    User(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }
}
