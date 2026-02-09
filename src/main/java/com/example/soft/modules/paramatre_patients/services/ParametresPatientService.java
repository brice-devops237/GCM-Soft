package com.example.soft.modules.paramatre_patients.services;

import java.util.List;
import java.util.Map;
import com.example.soft.modules.paramatre_patients.models.ParametresPatientModel;

/**
 * Interface définissant les règles métier
 * pour la gestion des paramètres patients.
 */
public interface ParametresPatientService {

    /**
     * Enregistre un nouveau paramètre patient.
     */
    ParametresPatientModel create(ParametresPatientModel parametre);

    /**
     * Met à jour un paramètre existant.
     */
    ParametresPatientModel update(Integer id, ParametresPatientModel parametre);

    /**
     * Recherche par identifiant.
     */
    ParametresPatientModel getById(Integer id);

    /**
     * Liste complète.
     */
    List<ParametresPatientModel> getAll();

    /**
     * Liste des paramètres d'un patient donné.
     */
    List<ParametresPatientModel> getByCodePatient(String codePatient);

    /**
     * Suppression définitive.
     */
    void delete(Integer id);

    /**
     * Filtrage dynamique avancé.
     */
    List<ParametresPatientModel> filter(Map<String, Object> filters);
}
