package ro.eventsourcing;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ro.eventsourcing.core.store.EventStore;
import ro.eventsourcing.notification.NotificationPolicy;
import ro.eventsourcing.notification.NotificationRepository;
import ro.eventsourcing.pet.Owner;
import ro.eventsourcing.pet.Pet;
import ro.eventsourcing.pet.PetCommand;
import ro.eventsourcing.pet.PetType;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class PetTransferOwnerNotificationTest {

    static final EventStore eventStore = new EventStore();

    static final NotificationRepository notificationRepository = new NotificationRepository();
    static final NotificationPolicy notificationPolicy = new NotificationPolicy(notificationRepository);

    @BeforeAll
    static void setUp() {
        eventStore.subscribe(notificationPolicy);
    }

    @Test
    void PetTest() {

        Pet pet1 = new Pet(
                new PetCommand.RegisterPet(
                        "Doggy",
                        LocalDateTime.of(2024, 1, 1, 0, 0).toInstant(ZoneOffset.UTC),
                        PetType.DOG,
                        new Owner("Alex", "my@mail.com", "Address")
                )
        );

        String streamId = pet1.id().toString();

        eventStore.appendStream(streamId, EventStore.BEGIN_STREAM_VERSION, pet1.uncommitedEvents());

        Pet pet1After = new Pet(eventStore.readStream(streamId));
        pet1After.handle(new PetCommand.TransferPetOwner(new Owner("Adam", "adam@gmail.com", "Street 2")));
        eventStore.appendStream(streamId, pet1After.version(), pet1After.uncommitedEvents());

        assertThat(notificationRepository.findAll()).hasSize(1);

    }

}
