package ro.eventsourcing.core;

public interface Aggregrate<ID> {

    ID id();
    int version();

    Object[] uncommitedEvents();

}
