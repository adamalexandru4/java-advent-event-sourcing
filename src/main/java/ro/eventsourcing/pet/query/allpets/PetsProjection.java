package ro.eventsourcing.pet.query.allpets;

import ro.eventsourcing.core.EventListener;
import ro.eventsourcing.core.repository.Repository;
import ro.eventsourcing.pet.MedicalEntry;
import ro.eventsourcing.pet.PetEvent;

import java.util.*;

public class PetsProjection implements EventListener {

    private final Repository<PetDetails, UUID> repository;

    public PetsProjection(Repository<PetDetails, UUID> repository) {
        this.repository = repository;
    }

    @Override
    public void on(Object event) {
        switch (event) {
            case PetEvent.PetRegistered registered -> addPet(registered);
            case PetEvent.PetDetailsUpdated detailsUpdated -> updatePetDetails(detailsUpdated);
            case PetEvent.PetMedicalEntryAdded medicalEntryAdded -> addMedicalEntry(medicalEntryAdded);
            case PetEvent.PetOwnerTransferred ownerTransferred -> changeOwner(ownerTransferred);
            case PetEvent.PetDeactivated petDeactivated -> deactivatePet(petDeactivated);
            default -> System.out.println("Event " + event.getClass().getName() + " not handled");
        }
    }

    private void deactivatePet(PetEvent.PetDeactivated petDeactivated) {
        var petDetails = repository.findById(petDeactivated.petId());

        petDetails.ifPresent(pet -> {
            pet.deactivatePet();
            repository.save(pet);
        });

    }

    private void changeOwner(PetEvent.PetOwnerTransferred ownerTransferred) {
        var petDetails = repository.findById(ownerTransferred.petId());

        petDetails.ifPresent(pet -> {
            pet.changeOwner(ownerTransferred.newOwner());
            repository.save(pet);
        });
    }

    private void addMedicalEntry(PetEvent.PetMedicalEntryAdded medicalEntryAdded) {
        var petDetails = repository.findById(medicalEntryAdded.petId());

        petDetails.ifPresent(pet -> {
            pet.addMedicalEntry(new MedicalEntry(medicalEntryAdded.entryDescription(), medicalEntryAdded.entryDate()));
            repository.save(pet);
        });
    }

    private void updatePetDetails(PetEvent.PetDetailsUpdated detailsUpdated) {
        var petDetails = repository.findById(detailsUpdated.petId());

        petDetails.ifPresent(pet -> {
            pet.updateDetails(detailsUpdated.name(), detailsUpdated.birthDate(), detailsUpdated.type());
            repository.save(pet);
        });
    }

    private void addPet(PetEvent.PetRegistered registered) {
        repository.save(
                new PetDetails(
                        registered.id(),
                        registered.name(),
                        registered.birthDate(),
                        registered.type(),
                        registered.owner(),
                        true,
                        new ArrayList<>()
                )
        );
    }

}
