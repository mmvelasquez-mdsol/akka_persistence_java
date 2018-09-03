package sample.persistence.logging;

public class LoggingMessage {
    private String message;

    public LoggingMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
    @Override
    public String toString() {
        return "LoggingMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
