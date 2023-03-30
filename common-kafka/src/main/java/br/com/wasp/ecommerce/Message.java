package br.com.wasp.ecommerce;

public class Message<T> {

    private final CorrelationId id;
    private final T payload;

    Message(CorrelationId id, T payload) {
        this.id = id;
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", payload=" + payload +
                '}';
    }

    public Object getPayload() {
        return this.payload;
    }

    public CorrelationId getId() {
        return this.id;
    }
}
