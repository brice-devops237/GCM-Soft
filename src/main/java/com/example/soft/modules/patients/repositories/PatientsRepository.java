package com.example.soft.modules.patients.repositories;

import com.example.soft.modules.patients.models.PatientsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

/**
 * Repository JPA pour les patients.
 *
 * <p>
 * Supporte :
 * <ul>
 *     <li>CRUD classique via JpaRepository</li>
 *     <li>Requêtes dynamiques avancées via JpaSpecificationExecutor</li>
 * </ul>
 * </p>
 *
 * Table : accueil
 * Entité : PatientsModel
 */
@Repository
public interface PatientsRepository extends
        JpaRepository<PatientsModel, Integer>,
        JpaSpecificationExecutor<PatientsModel> {

    Optional<PatientsModel> findByCode(String code);
    boolean existsByCode(String code);

    List<PatientsModel> findByEstActifTrue();
    List<PatientsModel> findByEstActifFalse();
    List<PatientsModel> findByStatut(String statut);
    List<PatientsModel> findByNomsContainingIgnoreCase(String noms);
    List<PatientsModel> findBySexe(String sexe);
    Optional<PatientsModel> findByPhone(String phone);
    Optional<PatientsModel> findByCni(String cni);
    List<PatientsModel> findByDate(LocalDate date);
    List<PatientsModel> findByDateBetween(LocalDate startDate, LocalDate endDate);
    List<PatientsModel> findByTypePatient(String typePatient);
    List<PatientsModel> findByCategorie(Integer categorie);
    List<PatientsModel> findByEstFusionneTrue();
    Optional<PatientsModel> findByPatientRefCode(String patientRefCode);
}
