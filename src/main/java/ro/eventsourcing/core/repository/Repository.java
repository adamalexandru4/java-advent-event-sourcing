package ro.eventsourcing.core.repository;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class Repository<V extends View<ID>, ID> {

    private final LinkedList<V> items = new LinkedList<>();

    public List<V> findAll() {
        return items;
    }

    public Optional<V> findById(ID id) {
        List<V> list = items.stream().filter(i -> i.id().equals(id)).toList();

        if (list.size() > 1) {
            throw new IllegalStateException("Multiple items found for id " + id);
        }

        return list.stream().findFirst();
    }

    public void save(V view) {
        Optional<V> existing = items.stream().filter(i -> i.id().equals(view.id())).findFirst();

        if (existing.isPresent()) {
            delete(existing.get().id());

            if (existing.get().version() != view.version()) {
                throw new IllegalStateException("Optimistic locking exception");
            }
        }

        view.incrementVersion();

        items.add(view);
    }

    public void delete(ID id) {
        items.removeIf(v -> v.id().equals(id));
    }
}
