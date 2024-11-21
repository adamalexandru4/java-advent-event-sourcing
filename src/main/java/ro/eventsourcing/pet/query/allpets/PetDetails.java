package ro.eventsourcing.pet.query.allpets;

import ro.eventsourcing.core.repository.View;
import ro.eventsourcing.pet.MedicalEntry;
import ro.eventsourcing.pet.Owner;
import ro.eventsourcing.pet.PetType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class PetDetails extends View<UUID> {
    private String name;
    private Instant birthDate;
    private PetType type;
    private Owner owner;
    private boolean active;
    private List<MedicalEntry> medicalEntries;

    public PetDetails(
            UUID id,
            String name,
            Instant birthDate,
            PetType type,
            Owner owner,
            boolean active,
            List<MedicalEntry> medicalEntries
    ) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.type = type;
        this.owner = owner;
        this.active = active;
        this.medicalEntries = medicalEntries;
    }

    public void updateDetails(String name, Instant birthDate, PetType type) {
        this.name = name;
        this.birthDate = birthDate;
        this.type = type;
    }

    public void addMedicalEntry(MedicalEntry medicalEntry) {
        medicalEntries.add(medicalEntry);
    }

    public void changeOwner(Owner owner) {
        this.owner = owner;
    }

    public void deactivatePet() {
        this.active = false;
    }

    public String name() {
        return name;
    }

    public Instant birthDate() {
        return birthDate;
    }

    public PetType type() {
        return type;
    }

    public Owner owner() {
        return owner;
    }

    public boolean active() {
        return active;
    }

    public List<MedicalEntry> medicalEntries() {
        return medicalEntries;
    }
}