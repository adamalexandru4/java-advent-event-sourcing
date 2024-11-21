package ro.eventsourcing.pet;

import ro.eventsourcing.core.AbstractAggregate;
import ro.eventsourcing.pet.PetCommand.*;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static ro.eventsourcing.pet.PetEvent.*;

public class Pet extends AbstractAggregate<UUID, PetEvent> {

    private String name;
    private Instant birthDate;
    private PetType type;
    private Owner owner;
    private boolean active;
    private final List<MedicalEntry> medicalEntries = new LinkedList<>();

    public Pet(List<PetEvent> events) {
        replay(events);
    }

    public Pet(RegisterPet command) {
        enqueue(new PetRegistered(UUID.randomUUID(), command.name(), command.birthDate(), command.type(), command.owner()));
    }

    public void handle(UpdatePetDetails command) {
        if (!active) {
            throw new IllegalStateException("Pet is not active");
        }

        enqueue(new PetDetailsUpdated(this.id, command.name(), command.birthDate(), command.type()));
    }

    public void handle(AddPetMedicalEntry command) {
        if (!active) {
            throw new IllegalStateException("Pet is not active");
        }

        enqueue(new PetMedicalEntryAdded(this.id, command.entryDescription(), command.entryDate()));
    }

    public void handle(TransferPetOwner command) {
        if (!active) {
            throw new IllegalStateException("Pet is not active");
        }

        enqueue(new PetOwnerTransferred(this.id, command.newOwner()));
    }

    public void handle(DeactivatePet command) {
        if (!active) {
            throw new IllegalStateException("Pet is already deactivated");
        }

        enqueue(new PetDeactivated(this.id));
    }

    @Override
    protected void apply(PetEvent event) {
        switch (event) {
            case PetRegistered registered -> {
                this.id = registered.id();
                this.name = registered.name();
                this.birthDate = registered.birthDate();
                this.owner = registered.owner();
                this.type = registered.type();
                this.active = true;
            }
            case PetDetailsUpdated detailsUpdated -> {
                this.name = detailsUpdated.name();
                this.birthDate = detailsUpdated.birthDate();
                this.type = detailsUpdated.type();
            }
            case PetMedicalEntryAdded medicalEntryAdded -> {
                this.medicalEntries.add(new MedicalEntry(medicalEntryAdded.entryDescription(), medicalEntryAdded.entryDate()));
            }
            case PetOwnerTransferred ownerTransferred -> {
                this.owner = ownerTransferred.newOwner();
            }
            case PetDeactivated petDeactivated -> {
                this.active = false;
            }
        }
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
