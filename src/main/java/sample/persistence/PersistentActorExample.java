package sample.persistence;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import sample.persistence.logging.LoggingActor;
import sample.persistence.logging.LoggingMessage;
import sample.persistence.messages.Command;
import sample.persistence.write.MyPersistentActor;

public class PersistentActorExample {
    public static void main(String... args) throws Exception {
        final ActorSystem system = ActorSystem.create("example_writer_actor_system");
        final ActorRef persistentActor = system.actorOf(Props.create(MyPersistentActor.class), "MyPersistentActor");
        final ActorRef loggingActor = system.actorOf(Props.create(LoggingActor.class));

        system.eventStream().subscribe(loggingActor, LoggingMessage.class);

        persistentActor.tell(new Command("foo_data"), null);
        persistentActor.tell(new Command("baz_data"), null);

        persistentActor.tell("print_state", null);

        persistentActor.tell(new Command("bar_data"), null);

        persistentActor.tell("create_snapshot", null);

        persistentActor.tell(new Command("buzz_data"), null);

        persistentActor.tell("print_state", null);

        Thread.sleep(10000);
        system.terminate();
    }
}