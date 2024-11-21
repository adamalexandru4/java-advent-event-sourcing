package ro.eventsourcing.core;

public record EventPayload(
        String name,
        String data
) {
}
