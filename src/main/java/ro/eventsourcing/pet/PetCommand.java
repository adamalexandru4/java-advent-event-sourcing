package ro.eventsourcing.pet;

import java.time.Instant;

import static org.apache.commons.lang3.StringUtils.isBlank;

public sealed interface PetCommand
        permits PetCommand.RegisterPet,
        PetCommand.UpdatePetDetails,
        PetCommand.TransferPetOwner,
        PetCommand.AddPetMedicalEntry,
        PetCommand.DeactivatePet {

    record RegisterPet(
            String name,
            Instant birthDate,
            PetType type,
            Owner owner
    ) implements PetCommand {
        public RegisterPet {
            if(isBlank(name)) {
                throw new IllegalArgumentException("Name cannot be blank");
            }

            if(birthDate.isAfter(Instant.now())) {
                throw new IllegalArgumentException("Birth date cannot be in the future");
            }

            if(type == null) {
                throw new IllegalArgumentException("Type should be defined");
            }

            if(owner == null) {
                throw new IllegalArgumentException("Owner should be defined");
            }
        }
    }

    record UpdatePetDetails(
            String name,
            Instant birthDate,
            PetType type
    ) implements PetCommand {
        public UpdatePetDetails {
            if(isBlank(name)) {
                throw new IllegalArgumentException("Name cannot be blank");
            }

            if(birthDate.isAfter(Instant.now())) {
                throw new IllegalArgumentException("Birth date cannot be in the future");
            }

            if(type == null) {
                throw new IllegalArgumentException("Type should be defined");
            }
        }
    }

    record AddPetMedicalEntry(
            String entryDescription,
            Instant entryDate
    ) implements PetCommand {
        public AddPetMedicalEntry {
            if(isBlank(entryDescription)) {
                throw new IllegalArgumentException("Medical entry description cannot be blank");
            }

            if(entryDate.isAfter(Instant.now())) {
                throw new IllegalArgumentException("Medical entry date cannot be in the future");
            }
        }
    }

    record TransferPetOwner(
            Owner newOwner
    ) implements PetCommand {
    }

    record DeactivatePet(
    ) implements PetCommand {
    }
}
