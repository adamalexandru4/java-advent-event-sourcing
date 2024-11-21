package ro.eventsourcing.core.store;

import ro.eventsourcing.core.EventPayload;

public record EventEnvelope(
        EventMetadata metadata,
        EventPayload data
) {
}
