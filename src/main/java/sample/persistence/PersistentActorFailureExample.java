/**
 * Copyright (C) 2009-2016 Lightbend Inc. <http://www.lightbend.com>
 */

package sample.persistence;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.persistence.AbstractPersistentActor;

import java.util.ArrayList;

public class PersistentActorFailureExample {
  public static class ExamplePersistentActor extends AbstractPersistentActor {
    private ArrayList<Object> receivedEvents = new ArrayList<Object>();

    @Override
    public String persistenceId() { return "sample-id-2"; }

    @Override
    public Receive createReceiveRecover() {
      return receiveBuilder()
          .match(String.class, s -> receivedEvents.add(s))
          .build();
    }

    @Override
    public Receive createReceive() {
      return receiveBuilder()
        .matchEquals("boom", s -> {
          persist(s, (String evt) -> {
            throw new RuntimeException("boom");
          });
        })
        .matchEquals("print", s -> System.out.println("received " + receivedEvents))
        .match(String.class, s -> {
          persist(s, evt -> receivedEvents.add(evt));
        })
        .build();
    }
  }

  public static void main(String... args) throws Exception {
    final ActorSystem system = ActorSystem.create("example");
    final ActorRef persistentActor = system.actorOf(Props.create(ExamplePersistentActor.class), "persistentActor-2");

    persistentActor.tell("a", null);
    persistentActor.tell("print", null);
    persistentActor.tell("boom", null);
    persistentActor.tell("print", null);
    persistentActor.tell("b", null);
    persistentActor.tell("print", null);
    persistentActor.tell("c", null);
    persistentActor.tell("print", null);

    // Will print in a first run (i.e. with empty journal):

    // received [a]
    // received [a, b]
    // received [a, b, c]

    // Will print in a second run:

    // received [a, b, c, a]
    // received [a, b, c, a, b]
    // received [a, b, c, a, b, c]

    // etc ...

    Thread.sleep(10000);
    system.terminate();
  }
}
