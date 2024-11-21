package ro.eventsourcing;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ro.eventsourcing.core.store.EventStore;
import ro.eventsourcing.pet.*;
import ro.eventsourcing.pet.query.allpets.GetPetsQuery;
import ro.eventsourcing.pet.query.allpets.PetsProjection;
import ro.eventsourcing.pet.query.allpets.PetsRepository;
import ro.eventsourcing.pet.query.sickpets.MostSickPetsProjection;
import ro.eventsourcing.pet.query.sickpets.SickPetsRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class PetSickProjectionTest {

    static final EventStore eventStore = new EventStore();

    static final SickPetsRepository sickPetsRepository = new SickPetsRepository();
    static final MostSickPetsProjection mostSickPetsProjection = new MostSickPetsProjection(sickPetsRepository);

    static final PetsRepository petsRepository = new PetsRepository();
    static final PetsProjection petsProjection = new PetsProjection(petsRepository);

    @BeforeAll
    static void setUp() {
        eventStore.subscribe(mostSickPetsProjection);
        eventStore.subscribe(petsProjection);
    }

    @Test
    void PetTest() {
        var type = PetType.DOG;
        var birthDate = LocalDateTime.of(2020, 1, 1, 0, 0).toInstant(ZoneOffset.UTC);
        var owner = new Owner("Alex", "alex@gmail.com", "Street 1");

        var pet1 = new Pet(new PetCommand.RegisterPet("Dog 1", birthDate, type, owner));
        eventStore.appendStream(pet1.id().toString(), EventStore.BEGIN_STREAM_VERSION, pet1.uncommitedEvents());

        var pet2 = new Pet(new PetCommand.RegisterPet("Dog 2", birthDate, type, owner));
        eventStore.appendStream(pet2.id().toString(), EventStore.BEGIN_STREAM_VERSION, pet2.uncommitedEvents());

        var pet3 = new Pet(new PetCommand.RegisterPet("Dog 3", birthDate, type, owner));
        eventStore.appendStream(pet3.id().toString(), EventStore.BEGIN_STREAM_VERSION, pet3.uncommitedEvents());

        assertThat(petsRepository.findPets(GetPetsQuery.noFilter())).hasSize(3);
        assertThat(sickPetsRepository.findSickPetsOrderDesc()).isEmpty();

        var pet1v1 = new Pet(eventStore.readStream(pet1.id().toString()));
        pet1v1.handle(new PetCommand.AddPetMedicalEntry("Sick 1", Instant.now()));
        eventStore.appendStream(pet1v1.id().toString(), pet1v1.version(), pet1v1.uncommitedEvents());

        assertThat(petsRepository.findPets(GetPetsQuery.noFilter())).hasSize(3);
        assertThat(petsRepository.findPets(GetPetsQuery.builder().name("Dog 3").build())).hasSize(1);
        assertThat(sickPetsRepository.findSickPetsOrderDesc()).hasSize(1);

        var pet1v2 = new Pet(eventStore.readStream(pet1.id().toString()));
        pet1v2.handle(new PetCommand.DeactivatePet());
        eventStore.appendStream(pet1v2.id().toString(), pet1v2.version(), pet1v2.uncommitedEvents());

        assertThat(sickPetsRepository.findSickPetsOrderDesc()).isEmpty();

    }

}
