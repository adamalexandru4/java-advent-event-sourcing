package ro.eventsourcing.core;

public interface EventListener {
    void on(Object event);
}
