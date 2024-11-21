package ro.eventsourcing.pet.query.allpets;

import ro.eventsourcing.core.repository.Repository;

import java.util.List;
import java.util.UUID;

public class PetsRepository extends Repository<PetDetails, UUID> {

    public List<PetDetails> findPets(GetPetsQuery query) {
        var pets = findAll();

        if (query.name().isPresent()) {
            pets = pets.stream()
                    .filter(pet -> pet.name().toLowerCase()
                            .contains(query.name().get().toLowerCase()))
                    .toList();
        }

        return pets.subList(query.offset(), Math.min(query.size(), pets.size()));
    }

}
