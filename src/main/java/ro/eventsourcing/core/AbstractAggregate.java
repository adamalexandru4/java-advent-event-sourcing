package ro.eventsourcing.core;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public abstract class AbstractAggregate<I, E> implements Aggregrate<I> {

    protected I id;
    protected int version;

    private final Queue<E> uncommittedEvents = new LinkedList<>();

    @Override
    public I id() {
        return id;
    }

    @Override
    public int version() {
        return version;
    }

    @Override
    public Object[] uncommitedEvents() {
        Object[] events = uncommittedEvents.toArray();
        uncommittedEvents.clear();
        return events;
    }

    public void replay(List<E> events) {
        events.forEach(e -> {
            apply(e);
            version++;
        });
    }

    protected abstract void apply(E event);

    protected void enqueue(E event) {
        uncommittedEvents.add(event);
        apply(event);
    }

}
