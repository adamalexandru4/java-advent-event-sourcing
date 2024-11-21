package ro.eventsourcing.notification;

import ro.eventsourcing.core.EventListener;
import ro.eventsourcing.core.repository.Repository;
import ro.eventsourcing.pet.PetEvent;

import java.util.UUID;

public class NotificationPolicy implements EventListener {

    private final Repository<Notification, UUID> repository;

    public NotificationPolicy(Repository<Notification, UUID> repository) {
        this.repository = repository;
    }

    @Override
    public void on(Object event) {
        switch (event) {
            case PetEvent.PetOwnerTransferred petOwnerTransferred -> {
                repository.save(new Notification(petOwnerTransferred.newOwner().email(), "Your new pet", "Congratulations for owning!"));
            }
            default -> System.out.println("Event " + event.getClass().getName() + " not handled");
        }
    }

}
