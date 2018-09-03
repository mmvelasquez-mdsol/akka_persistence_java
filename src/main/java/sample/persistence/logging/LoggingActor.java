package sample.persistence.logging;

import akka.actor.AbstractActor;

public class LoggingActor extends AbstractActor {

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(LoggingMessage.class, msg -> {
                    System.out.println("Logging Message: " + msg);
                })
                .build();
    }
}
