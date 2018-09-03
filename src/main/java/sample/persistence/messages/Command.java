package sample.persistence.messages;

import java.io.Serializable;

public class Command implements Message, Serializable {
    private static final long serialVersionUID = 1L;
    private final String data;

    public Command(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Command{" +
                "data='" + data + '\'' +
                '}';
    }
}

