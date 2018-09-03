package sample.persistence.write;

import akka.persistence.AbstractPersistentActor;
import akka.persistence.SnapshotOffer;
import sample.persistence.logging.LoggingMessage;
import sample.persistence.messages.Command;
import sample.persistence.messages.Event;

import static java.util.Arrays.asList;

public class MyPersistentActor extends AbstractPersistentActor {

    private ExampleState state = new ExampleState();

    public int getNumEvents() {
        return state.size();
    }

    @Override
    public String persistenceId() { return "MY_TOPIC_NAME"; }

    @Override
    public Receive createReceiveRecover() {
        return receiveBuilder()
            .match(Event.class, (Event e) -> {
                System.out.println("Recover: " + e);
                state.update(e);
            })
            .match(SnapshotOffer.class, (SnapshotOffer ss) -> {
                System.out.println("Recover: " + ss);
                state = (ExampleState) ss.snapshot();
            })
            .build();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(Command.class, c -> {
                System.out.println("Received Command: " + c);
                final String data = c.getData();
                final Event evt1 = new Event(data + "-" + getNumEvents());
                final Event evt2 = new Event(data + "-" + (getNumEvents() + 1));
                persistAll(asList(evt1, evt2), (Event evt) -> {
                    state.update(evt);
                    if (evt.equals(evt2)) {
                        getContext().system().eventStream().publish(new LoggingMessage("Second event detected. Message: " + evt.getData()));
                    }
                });
            })
            .matchEquals(Event.class, e -> {
                System.out.println("Received Event: " + e);
            })
            .matchEquals("create_snapshot", s -> saveSnapshot(state.copy()))
            .matchEquals("print_state", s -> System.out.println(state))
            .build();
    }

}