package ro.eventsourcing;

import org.junit.jupiter.api.Test;
import ro.eventsourcing.core.store.EventStore;
import ro.eventsourcing.pet.Owner;
import ro.eventsourcing.pet.Pet;
import ro.eventsourcing.pet.PetCommand;
import ro.eventsourcing.pet.PetType;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MultiThreadPetTest {
    static final EventStore eventStore = new EventStore();

    static final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Test
    void PetTest() throws InterruptedException, ExecutionException {
        // Register pet
        String name = "My dog";
        PetType type = PetType.DOG;
        Instant birthDate = LocalDateTime.of(2020, 1, 1, 0, 0).toInstant(ZoneOffset.UTC);
        Owner owner = new Owner("Alex", "alex@gmail.com", "Street 1");

        Pet dog = new Pet(new PetCommand.RegisterPet(name, birthDate, type, owner));
        String dogStreamId = dog.id().toString();
        eventStore.appendStream(dogStreamId, EventStore.BEGIN_STREAM_VERSION, dog.uncommitedEvents());

        assertThat(dog)
                .extracting(Pet::name, Pet::type, Pet::birthDate, Pet::owner, Pet::version)
                .containsExactly(name, type, birthDate, owner, 0);
        assertThat(eventStore.readStream(dogStreamId)).hasSize(1);

        // Update pet details in parallel
        Pet storedPet = new Pet(eventStore.readStream(dogStreamId));
        String newName = "My dog is a cat";
        PetType newType = PetType.CAT;

        storedPet.handle(new PetCommand.UpdatePetDetails(newName, birthDate, newType));

        executorService
                .submit(() -> {
                    Pet threadPet = new Pet(eventStore.readStream(dogStreamId));
                    String threadPetName = "Pet from thread name";

                    threadPet.handle(new PetCommand.UpdatePetDetails(threadPetName, birthDate, type));

                    eventStore.appendStream(dogStreamId, threadPet.version(), storedPet.uncommitedEvents());
                })
                .get();

        assertThrows(IllegalStateException.class,
                () -> eventStore.appendStream(dogStreamId, storedPet.version(), storedPet.uncommitedEvents()),
                "Optimistic locking failure");
    }

}
