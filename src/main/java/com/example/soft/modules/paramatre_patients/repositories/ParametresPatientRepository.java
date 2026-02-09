package com.example.soft.modules.paramatre_patients.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.example.soft.modules.paramatre_patients.models.ParametresPatientModel;

import java.util.List;

/**
 * Repository Spring Data JPA pour les paramètres patients.
 *
 * <p>
 * Fournit :
 * <ul>
 *     <li>CRUD standard</li>
 *     <li>Filtrage dynamique via Specification</li>
 *     <li>Recherche par code patient</li>
 * </ul>
 * </p>
 */
public interface ParametresPatientRepository
        extends JpaRepository<ParametresPatientModel, Integer>,
                JpaSpecificationExecutor<ParametresPatientModel> {

    /**
     * Retourne tous les paramètres associés à un patient.
     *
     * @param codePatient code du patient
     * @return liste des paramètres
     */
    List<ParametresPatientModel> findByCodePatient(String codePatient);
}
