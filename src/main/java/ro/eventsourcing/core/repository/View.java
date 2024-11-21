package ro.eventsourcing.core.repository;

public abstract class View<I> {

    protected I id;
    protected int version;

    public I id() {
        return id;
    }

    public int version() {
        return version;
    }

    public void incrementVersion() {
        version++;
    }


}
