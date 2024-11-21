package ro.eventsourcing.pet.query.sickpets;

import ro.eventsourcing.core.EventListener;
import ro.eventsourcing.core.repository.Repository;
import ro.eventsourcing.core.repository.View;
import ro.eventsourcing.pet.MedicalEntry;
import ro.eventsourcing.pet.PetEvent;

import java.util.*;

public class MostSickPetsProjection implements EventListener {

    private final Repository<SickPetDetails, UUID> repository;

    public MostSickPetsProjection(Repository<SickPetDetails, UUID> repository) {
        this.repository = repository;
    }

    @Override
    public void on(Object event) {
        switch (event) {
            case PetEvent.PetMedicalEntryAdded medicalEntryAdded -> addMedicalEntry(medicalEntryAdded);
            case PetEvent.PetDeactivated petDeactivated -> removePet(petDeactivated);
            default -> System.out.println("Event " + event.getClass().getName() + " not handled");
        }
    }

    private void removePet(PetEvent.PetDeactivated petDeactivated) {
        repository.delete(petDeactivated.petId());
    }

    private void addMedicalEntry(PetEvent.PetMedicalEntryAdded medicalEntryAdded) {
        var petDetails = repository.findById(medicalEntryAdded.petId());

        petDetails.ifPresentOrElse(pet -> {
            pet.addMedicalEntry(new MedicalEntry(medicalEntryAdded.entryDescription(), medicalEntryAdded.entryDate()));
            repository.save(pet);
        }, () -> {
            repository.save(new SickPetDetails(medicalEntryAdded.petId(), new MedicalEntry(medicalEntryAdded.entryDescription(), medicalEntryAdded.entryDate())));
        });

    }

}
