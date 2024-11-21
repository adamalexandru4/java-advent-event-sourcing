package ro.eventsourcing;

import org.junit.jupiter.api.Test;
import ro.eventsourcing.core.store.EventStore;
import ro.eventsourcing.pet.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class PetFlowTest {

    static final EventStore eventStore = new EventStore();

    @Test
    void PetTest() {
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

        // Update pet details
        Pet storedPet = new Pet(eventStore.readStream(dogStreamId));
        String newName = "My dog is a cat";
        PetType newType = PetType.CAT;

        storedPet.handle(new PetCommand.UpdatePetDetails(newName, birthDate, newType));
        eventStore.appendStream(dogStreamId, storedPet.version(), storedPet.uncommitedEvents());

        assertThat(storedPet)
                .extracting(Pet::name, Pet::type, Pet::birthDate, Pet::owner, Pet::version)
                .containsExactly(newName, newType, birthDate, owner, 1);
        assertThat(eventStore.readStream(dogStreamId)).hasSize(2);

        // Check pet last version
        Pet storedPet2 = new Pet(eventStore.readStream(dogStreamId));
        assertThat(storedPet2)
                .extracting(Pet::name, Pet::type, Pet::birthDate, Pet::owner, Pet::version)
                .containsExactly(newName, newType, birthDate, owner, 2);
    }

}
