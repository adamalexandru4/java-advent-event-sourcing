package ro.eventsourcing.pet;

import java.time.Instant;
import java.util.UUID;

public sealed interface PetEvent
        permits PetEvent.PetRegistered,
        PetEvent.PetDetailsUpdated,
        PetEvent.PetOwnerTransferred,
        PetEvent.PetDeactivated,
        PetEvent.PetMedicalEntryAdded {

    record PetRegistered(
            UUID id,
            String name,
            Instant birthDate,
            PetType type,
            Owner owner
    ) implements PetEvent {
    }

    record PetDetailsUpdated(
            UUID petId,
            String name,
            Instant birthDate,
            PetType type
    ) implements PetEvent {
    }

    record PetOwnerTransferred(
            UUID petId,
            Owner newOwner
    ) implements PetEvent {
    }

    record PetDeactivated(
            UUID petId
    ) implements PetEvent {
    }

    record PetMedicalEntryAdded(
            UUID petId,
            String entryDescription,
            Instant entryDate
    ) implements PetEvent {
    }

}
