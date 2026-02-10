package com.example.soft.modules.patients.serviceImpl;

import com.example.soft.modules.patients.models.PatientsModel;
import com.example.soft.modules.patients.repositories.PatientsRepository;
import com.example.soft.modules.patients.services.PatientsService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Implémentation du service métier pour la gestion des patients.
 *
 * <p>
 * Fournit toutes les fonctionnalités CRUD, activation/désactivation,
 * recherches simples et filtrage polymorphe performant.
 * </p>
 *
 * <p>
 * Utilise Spring Data JPA pour les opérations persistantes et les Specifications
 * pour un filtrage dynamique et flexible.
 * </p>
 *
 * <p>
 * Transactionnel par défaut pour toutes les méthodes. Les méthodes de lecture
 * sont marquées `readOnly = true` pour optimiser les performances.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PatientsServiceImpl implements PatientsService {

    private final PatientsRepository patientsRepository;

    /* ======================
       CRUD STANDARD
       ====================== */

    @Override
    public PatientsModel createPatient(PatientsModel patient) {
        validatePatientMandatoryFields(patient);
        patient.setCode(generatePatientCode());
        patient.setDateCreation(LocalDateTime.now());
        try {
            return patientsRepository.save(patient);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException(
                    "Création échouée : code patient déjà existant [" + patient.getCode() + "]", ex
            );
        }
    }

    @Override
    public PatientsModel updatePatient(Integer id, PatientsModel patient) {
        if (id == null || patient == null) {
            throw new IllegalArgumentException("ID ou patient null");
        }
        PatientsModel existing = getPatientById(id);

        // Protection des champs critiques
        patient.setId(existing.getId());
        patient.setCode(existing.getCode());
        patient.setDateCreation(existing.getDateCreation());
        validatePatientMandatoryFields(patient);

        try {
            return patientsRepository.save(patient);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("Mise à jour échouée pour l'ID " + id, ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PatientsModel getPatientById(Integer id) {
        if (id == null) throw new IllegalArgumentException("ID null");
        return patientsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aucun patient trouvé avec l'ID " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientsModel> getAllPatients() {
        return patientsRepository.findAll();
    }

    @Override
    public void deletePatient(Integer id) {
        PatientsModel patient = getPatientById(id);
        try {
            patientsRepository.delete(patient);
        } catch (Exception ex) {
            throw new IllegalStateException("Suppression impossible pour l'ID " + id, ex);
        }
    }

    /* ============================
       ACTIVATION / DESACTIVATION
    =============================== */

    @Override
    public void deactivatePatient(Integer id) {
        PatientsModel patient = getPatientById(id);
        if (!patient.isActive()) throw new IllegalStateException("Patient déjà inactif");
        patient.setEstActif(false);
        patientsRepository.save(patient);
    }

    @Override
    public void activatePatient(Integer id) {
        PatientsModel patient = getPatientById(id);
        if (patient.isActive()) throw new IllegalStateException("Patient déjà actif");
        patient.setEstActif(true);
        patientsRepository.save(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientsModel> getActivePatients() {
        return patientsRepository.findByEstActifTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientsModel> getInactivePatients() {
        return patientsRepository.findByEstActifFalse();
    }

    /* ======================
       RECHERCHES SIMPLES
       ====================== */

    @Override
    @Transactional(readOnly = true)
    public PatientsModel getPatientByCode(String code) {
        if (code == null || code.isBlank()) throw new IllegalArgumentException("Code vide");
        return patientsRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Aucun patient trouvé avec le code " + code));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientsModel> searchByName(String noms) {
        if (noms == null || noms.isBlank()) throw new IllegalArgumentException("Nom vide");
        return patientsRepository.findByNomsContainingIgnoreCase(noms);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientsModel> getPatientsBySexe(String sexe) {
        if (sexe == null || sexe.isBlank()) throw new IllegalArgumentException("Sexe vide");
        return patientsRepository.findBySexe(sexe);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientsModel> getPatientsByDate(LocalDate date) {
        if (date == null) throw new IllegalArgumentException("Date null");
        return patientsRepository.findByDate(date);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientsModel> getPatientsBetweenDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) throw new IllegalArgumentException("Dates null");
        if (endDate.isBefore(startDate)) throw new IllegalArgumentException("DateFin < DateDebut");
        return patientsRepository.findByDateBetween(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientsModel> getPatientsByType(String typePatient) {
        if (typePatient == null || typePatient.isBlank()) throw new IllegalArgumentException("Type vide");
        return patientsRepository.findByTypePatient(typePatient);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientsModel> getPatientsByCategorie(Integer categorie) {
        if (categorie == null) throw new IllegalArgumentException("Catégorie null");
        return patientsRepository.findByCategorie(categorie);
    }

    /* ======================
       FILTRE POLYMORPHE UNIQUE
       ====================== */

    @Override
    @Transactional(readOnly = true)
    public List<PatientsModel> filterPatients(Map<String, Object> filters) {
        Specification<PatientsModel> spec = (root, query, cb) -> cb.conjunction(); // <-- corrigé ici

        if (filters.containsKey("code")) {
            String code = (String) filters.get("code");
            spec = spec.and((root, query, cb) -> cb.equal(root.get("code"), code));
        }

        if (filters.containsKey("noms")) {
            String noms = (String) filters.get("noms");
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("noms")), "%" + noms.toLowerCase() + "%"));
        }

        if (filters.containsKey("sexe")) {
            String sexe = (String) filters.get("sexe");
            spec = spec.and((root, query, cb) -> cb.equal(root.get("sexe"), sexe));
        }

        if (filters.containsKey("typePatient")) {
            String type = (String) filters.get("typePatient");
            spec = spec.and((root, query, cb) -> cb.equal(root.get("typePatient"), type));
        }

        if (filters.containsKey("categorie")) {
            Integer cat = (Integer) filters.get("categorie");
            spec = spec.and((root, query, cb) -> cb.equal(root.get("categorie"), cat));
        }

        if (filters.containsKey("estActif")) {
            Boolean actif = (Boolean) filters.get("estActif");
            spec = spec.and((root, query, cb) -> cb.equal(root.get("estActif"), actif));
        }

        if (filters.containsKey("dateDebut") && filters.containsKey("dateFin")) {
            LocalDate start = (LocalDate) filters.get("dateDebut");
            LocalDate end = (LocalDate) filters.get("dateFin");
            spec = spec.and((root, query, cb) -> cb.between(root.get("dateCreation"),
                    start.atStartOfDay(), end.atTime(23, 59, 59, 999_999_999)));
        }

        return patientsRepository.findAll(spec);
    }


    /* ======================
       MÉTHODES UTILITAIRES
       ====================== */

    /**
     * Valide tous les champs obligatoires d’un patient avant création ou mise à jour.
     *
     * @param patient instance à valider
     * @throws IllegalArgumentException si un champ obligatoire est manquant
     */
    private void validatePatientMandatoryFields(PatientsModel patient) {
        if (patient == null) throw new IllegalArgumentException("Patient null");
        if (patient.getNoms() == null || patient.getNoms().isBlank())
            throw new IllegalArgumentException("Nom du patient obligatoire");
        if (patient.getSexe() == null || patient.getSexe().isBlank())
            throw new IllegalArgumentException("Sexe du patient obligatoire");
        if (patient.getDateNaissance() == null)
            throw new IllegalArgumentException("Date de naissance obligatoire");
        if (patient.getPhone() == null || patient.getPhone().isBlank())
            throw new IllegalArgumentException("Téléphone du patient obligatoire");
        if (patient.getConsulteur() == null || patient.getConsulteur().isBlank())
            throw new IllegalArgumentException("Nom du consulteur obligatoire");
        if (patient.getPrintBy() == null || patient.getPrintBy().isBlank())
            throw new IllegalArgumentException("Champ printBy obligatoire");
        if (patient.getDate() == null)
            throw new IllegalArgumentException("Date d'enregistrement obligatoire");
    }

    /**
     * Génère un code patient unique au format AA+PA+N.
     *
     * @return code patient unique
     */
    private String generatePatientCode() {
        String annee = String.valueOf(LocalDate.now().getYear()).substring(2);
        long compteur = patientsRepository.count() + 1;
        String code = annee + "PA" + compteur;

        while (patientsRepository.existsByCode(code)) {
            compteur++;
            code = annee + "PA" + compteur;
        }
        return code;
    }
}
