package com.example.soft.modules.patients.services;

import com.example.soft.modules.patients.models.PatientsModel;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Service métier pour les patients.
 *
 * <p>
 * Contient :
 * <ul>
 *     <li>CRUD standard</li>
 *     <li>Activation / désactivation</li>
 *     <li>Recherches métier et filtres dynamiques</li>
 * </ul>
 * </p>
 */
public interface PatientsService {

    // CRUD
    PatientsModel createPatient(PatientsModel patient);
    PatientsModel updatePatient(Integer id, PatientsModel patient);
    PatientsModel getPatientById(Integer id);
    List<PatientsModel> getAllPatients();
    void deletePatient(Integer id);

    // Activation / désactivation
    void deactivatePatient(Integer id);
    void activatePatient(Integer id);
    List<PatientsModel> getActivePatients();
    List<PatientsModel> getInactivePatients();

    // Recherches simples
    PatientsModel getPatientByCode(String code);
    List<PatientsModel> searchByName(String noms);
    List<PatientsModel> getPatientsBySexe(String sexe);
    List<PatientsModel> getPatientsByDate(LocalDate date);
    List<PatientsModel> getPatientsBetweenDates(LocalDate startDate, LocalDate endDate);
    List<PatientsModel> getPatientsByType(String typePatient);
    List<PatientsModel> getPatientsByCategorie(Integer categorie);

    /**
     *
     * Filtre générique et polymorphe pour les patients.
     *
     * <p>
     * La map de critères peut contenir :
     * <ul>
     *     <li>code</li>
     *     <li>noms</li>
     *     <li>sexe</li>
     *     <li>typePatient</li>
     *     <li>categorie</li>
     *     <li>dateDebut / dateFin</li>
     *     <li>estActif</li>
     * </ul>
     * </p>
     *
     * @param filters Map<String, Object> des critères
     * @return liste des patients correspondants
     */
    List<PatientsModel> filterPatients(Map<String, Object> filters);
}
