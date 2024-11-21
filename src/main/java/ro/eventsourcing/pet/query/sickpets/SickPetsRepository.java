package ro.eventsourcing.pet.query.sickpets;

import ro.eventsourcing.core.repository.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class SickPetsRepository extends Repository<SickPetDetails, UUID> {

    public List<SickPetDetails> findSickPetsOrderDesc() {
        return findAll().stream()
                .sorted(Comparator.comparing(p -> p.medicalEntries().size()))
                .toList();
    }
}
