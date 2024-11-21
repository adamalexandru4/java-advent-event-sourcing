package ro.eventsourcing.pet;

import java.time.Instant;

public record MedicalEntry(
        String description,
        Instant date
) {
}
