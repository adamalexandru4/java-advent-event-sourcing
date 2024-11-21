package ro.eventsourcing.core.store;

import java.time.Instant;

public record EventMetadata(
        int version,
        Instant timestamp
) {
}
