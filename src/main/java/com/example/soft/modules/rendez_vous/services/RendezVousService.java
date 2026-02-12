package com.example.soft.modules.rendez_vous.services;

import com.example.soft.modules.rendez_vous.models.RendezVousModel;

import java.time.LocalDate;
import java.util.List;

public interface RendezVousService {

    RendezVousModel create(RendezVousModel rendezVous, String currentUser);

    RendezVousModel update(Integer id, RendezVousModel rendezVous);

    RendezVousModel getById(Integer id);

    List<RendezVousModel> getAll();

    void delete(Integer id);

    List<RendezVousModel> getByCodePatient(String codePatient);

    List<RendezVousModel> search(String codePatient, LocalDate dateDebut, LocalDate dateFin, Integer statut);
}
