package ro.eventsourcing.core.store;

import ro.eventsourcing.core.EventListener;
import ro.eventsourcing.core.EventSerializer;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EventStore {

    public static final int BEGIN_STREAM_VERSION = -1;

    static final Map<String, LinkedList<EventEnvelope>> STORE = new ConcurrentHashMap<>();
    static final List<EventListener> EVENTS_SUBSCRIBERS = new LinkedList<>();

    public void subscribe(EventListener listener) {
        EVENTS_SUBSCRIBERS.add(listener);
    }

    public <T> List<T> readStream(String streamId) {
        return STORE.get(streamId)
                .stream()
                .map(event -> (T) EventSerializer.deserialize(event.data()))
                .toList();
    }

    public void appendStream(String streamId, int expectedVersion, Object[] events) {

        var eventPayloads = Arrays.stream(events)
                .map(EventSerializer::serialize)
                .toList();

        if (!STORE.containsKey(streamId)) {
            if (expectedVersion != BEGIN_STREAM_VERSION) {
                throw new IllegalStateException("Version mismatch for uninitialized stream");
            }

            int lastVersion = 1;

            var newEvents = new LinkedList<EventEnvelope>();
            for (var payload : eventPayloads) {
                newEvents.add(new EventEnvelope(new EventMetadata(lastVersion++, Instant.now()), payload));
            }

            STORE.put(streamId, newEvents);
        } else {
            var currentEvents = STORE.get(streamId);

            if (currentEvents == null) {
                throw new IllegalStateException("Stream not initialized");
            }

            var lastVersion = currentEvents.getLast().metadata().version();
            if (lastVersion != expectedVersion) {
                throw new IllegalStateException("Optimistic locking failure");
            }

            int newVersion = lastVersion + 1;
            for (var eventPayload : eventPayloads) {
                currentEvents.addLast(new EventEnvelope(new EventMetadata(newVersion++, Instant.now()), eventPayload));
            }
        }

        // TODO: Event bus
        notifySubscribers(events);
    }

    private void notifySubscribers(Object[] events) {
        for (var event : events) {
            for (var eventSubscriber : EVENTS_SUBSCRIBERS) {
                eventSubscriber.on(event);
            }
        }
    }

}
