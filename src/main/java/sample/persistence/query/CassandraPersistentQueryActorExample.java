package sample.persistence.query;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.persistence.cassandra.query.javadsl.CassandraReadJournal;
import akka.persistence.query.EventEnvelope;
import akka.persistence.query.PersistenceQuery;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Source;
import sample.persistence.messages.Event;

import java.util.Optional;

public class CassandraPersistentQueryActorExample {

  public static void main(String[] args) {
    final ActorSystem system = ActorSystem.create("example_reader_actor_system");
    final ActorMaterializer actorMaterializer = ActorMaterializer.create(system);

    CassandraReadJournal readJournal = PersistenceQuery.get(system).getReadJournalFor(CassandraReadJournal.class, CassandraReadJournal.Identifier());

    Source<EventEnvelope, NotUsed> eventEnvelopeNotUsedSource =
        readJournal.eventsByPersistenceId(
            "MY_TOPIC_NAME",
            0, Long.MAX_VALUE);

    eventEnvelopeNotUsedSource.runForeach(eventEnvelope -> {
      validateEvent(eventEnvelope);

      updateCurrentState(eventEnvelope);
    }, actorMaterializer);

  }

  private static Optional<EventEnvelope> validateEvent(EventEnvelope eventEnvelope) {
    // Tagless
    return Optional.of(eventEnvelope);
  }

  // Update current state
  private static void updateCurrentState(EventEnvelope eventEnvelope) {
    long eventSequence = eventEnvelope.sequenceNr();
    Event event = (Event) eventEnvelope.event();
    System.out.println("Message Number: " + eventSequence + " <> Event message: " + event.getData());
  }

}
