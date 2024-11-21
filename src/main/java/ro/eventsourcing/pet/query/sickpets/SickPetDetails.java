package ro.eventsourcing.pet.query.sickpets;

import ro.eventsourcing.core.repository.View;
import ro.eventsourcing.pet.MedicalEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class SickPetDetails extends View<UUID> {
    private final List<MedicalEntry> medicalEntries = new ArrayList<>();

    public SickPetDetails(UUID petId, MedicalEntry medicalEntries) {
        this.id = petId;
        this.medicalEntries.add(medicalEntries);
    }

    public void addMedicalEntry(MedicalEntry medicalEntry) {
        this.medicalEntries.add(medicalEntry);
    }

    public List<MedicalEntry> medicalEntries() {
        return medicalEntries;
    }
}