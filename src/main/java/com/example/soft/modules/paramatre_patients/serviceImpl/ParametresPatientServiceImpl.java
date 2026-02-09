package com.example.soft.modules.paramatre_patients.serviceImpl;

import com.example.soft.modules.paramatre_patients.models.ParametresPatientModel;
import com.example.soft.modules.paramatre_patients.repositories.ParametresPatientRepository;
import com.example.soft.modules.paramatre_patients.services.ParametresPatientService;
import com.example.soft.modules.patients.repositories.PatientsRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Implémentation métier du service de gestion des paramètres patients.
 *
 * <p>
 * Cette classe centralise toute la logique métier liée aux paramètres cliniques
 * (signes vitaux, constantes biologiques, mensurations, etc.).
 * </p>
 *
 * <p>
 * Responsabilités principales :
 * <ul>
 *     <li>Création et mise à jour sécurisées des paramètres</li>
 *     <li>Vérification de l'existence du patient avant toute insertion</li>
 *     <li>Gestion fine des exceptions métier</li>
 *     <li>Filtrage dynamique polymorphe via Specifications</li>
 * </ul>
 * </p>
 *
 * <p>
 * Toutes les méthodes sont transactionnelles par défaut.
 * Les lectures sont optimisées via <code>readOnly = true</code>.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ParametresPatientServiceImpl implements ParametresPatientService {

    /**
     * Repository JPA des paramètres patients.
     */
    private final ParametresPatientRepository parametresRepository;

    /**
     * Repository des patients utilisé pour valider l'existence du code patient.
     */
    private final PatientsRepository patientsRepository;

    /* ==========================================================
       CRUD MÉTIER
       ========================================================== */

    /**
     * Enregistre un nouveau jeu de paramètres pour un patient.
     *
     * <p><b>Scénario nominal :</b></p>
     * <ul>
     *     <li>Le code patient est renseigné</li>
     *     <li>Le patient existe en base</li>
     *     <li>La source est renseignée</li>
     *     <li>L'enregistrement est effectué avec succès</li>
     * </ul>
     *
     * <p><b>Scénarios d’erreur :</b></p>
     * <ul>
     *     <li>Code patient vide → {@link IllegalArgumentException}</li>
     *     <li>Patient inexistant → {@link IllegalStateException}</li>
     *     <li>Source absente → {@link IllegalArgumentException}</li>
     *     <li>Erreur d’intégrité → {@link DataIntegrityViolationException}</li>
     * </ul>
     *
     * @param parametre paramètres cliniques à enregistrer
     * @return paramètres persistés
     */
    @Override
    public ParametresPatientModel create(ParametresPatientModel parametre) {

        if (parametre == null) {
            throw new IllegalArgumentException(
                    "Impossible d'enregistrer les paramètres : données absentes.");
        }

        validateCodePatient(parametre.getCodePatient());
        validateSource(parametre.getSource());

        try {
            return parametresRepository.save(parametre);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException(
                    "Échec de l'enregistrement des paramètres du patient : données invalides ou incohérentes.",
                    ex
            );
        }
    }

    /**
     * Met à jour un enregistrement existant de paramètres patients.
     *
     * <p>
     * L'identité de l'enregistrement est protégée.
     * </p>
     *
     * @param id identifiant du paramètre à modifier
     * @param parametre nouvelles valeurs
     * @return paramètres mis à jour
     */
    @Override
    public ParametresPatientModel update(Integer id, ParametresPatientModel parametre) {

        if (id == null) {
            throw new IllegalArgumentException("ID du paramètre patient manquant.");
        }

        if (parametre == null) {
            throw new IllegalArgumentException("Données de mise à jour absentes.");
        }

        ParametresPatientModel existing = getById(id);

        validateCodePatient(parametre.getCodePatient());
        validateSource(parametre.getSource());

        // Protection de l'identité
        parametre.setId(existing.getId());

        return parametresRepository.save(parametre);
    }

    /**
     * Recherche un paramètre patient par son identifiant.
     *
     * @param id identifiant technique
     * @return paramètre trouvé
     * @throws EntityNotFoundException si inexistant
     */
    @Override
    @Transactional(readOnly = true)
    public ParametresPatientModel getById(Integer id) {

        if (id == null) {
            throw new IllegalArgumentException("ID du paramètre patient non fourni.");
        }

        return parametresRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Aucun paramètre patient trouvé avec l'identifiant " + id));
    }

    /**
     * Retourne l'ensemble des paramètres patients enregistrés.
     *
     * @return liste complète (peut être vide)
     */
    @Override
    @Transactional(readOnly = true)
    public List<ParametresPatientModel> getAll() {
        return parametresRepository.findAll();
    }

    /**
     * Retourne tous les paramètres associés à un patient donné.
     *
     * @param codePatient code unique du patient
     * @return liste des paramètres
     */
    @Override
    @Transactional(readOnly = true)
    public List<ParametresPatientModel> getByCodePatient(String codePatient) {
        validateCodePatient(codePatient);
        return parametresRepository.findByCodePatient(codePatient);
    }

    /**
     * Supprime définitivement un paramètre patient.
     *
     * @param id identifiant du paramètre
     */
    @Override
    public void delete(Integer id) {
        ParametresPatientModel parametre = getById(id);
        parametresRepository.delete(parametre);
    }

    /* ==========================================================
       FILTRAGE POLYMORPHE DYNAMIQUE
       ========================================================== */

    /**
     * Filtre dynamiquement les paramètres patients selon plusieurs critères.
     *
     * <p>Filtres supportés :</p>
     * <ul>
     *     <li>codePatient</li>
     *     <li>dateDebut / dateFin</li>
     *     <li>source</li>
     * </ul>
     *
     * <p>
     * Les filtres sont cumulables.
     * </p>
     *
     * @param filters map de critères dynamiques
     * @return liste filtrée
     */
    @Override
    @Transactional(readOnly = true)
    public List<ParametresPatientModel> filter(Map<String, Object> filters) {

        Specification<ParametresPatientModel> spec =
                (root, query, cb) -> cb.conjunction();

        if (filters == null || filters.isEmpty()) {
            return parametresRepository.findAll();
        }

        if (filters.containsKey("codePatient")) {
            spec = spec.and((r, q, cb) ->
                    cb.equal(r.get("codePatient"), filters.get("codePatient")));
        }

        if (filters.containsKey("dateDebut") && filters.containsKey("dateFin")) {
            LocalDate d1 = (LocalDate) filters.get("dateDebut");
            LocalDate d2 = (LocalDate) filters.get("dateFin");

            if (d2.isBefore(d1)) {
                throw new IllegalArgumentException(
                        "La date de fin est antérieure à la date de début.");
            }

            spec = spec.and((r, q, cb) -> cb.between(r.get("date"), d1, d2));
        }

        if (filters.containsKey("source")) {
            spec = spec.and((r, q, cb) ->
                    cb.equal(r.get("source"), filters.get("source")));
        }

        return parametresRepository.findAll(spec);
    }

    /* ==========================================================
       VALIDATIONS MÉTIER INTERNES
       ========================================================== */

    /**
     * Vérifie que le code patient est valide et existant.
     *
     * @param codePatient code patient à valider
     */
    private void validateCodePatient(String codePatient) {

        if (codePatient == null || codePatient.isBlank()) {
            throw new IllegalArgumentException(
                    "Le code patient doit être renseigné.");
        }

        if (!patientsRepository.existsByCode(codePatient)) {
            throw new IllegalStateException(
                    "Impossible d'enregistrer les paramètres : " +
                    "le patient avec le code [" + codePatient + "] n'existe pas.");
        }
    }

    /**
     * Vérifie que la source de la saisie est renseignée.
     *
     * @param source origine des paramètres
     */
    private void validateSource(String source) {
        if (source == null || source.isBlank()) {
            throw new IllegalArgumentException(
                    "La source des paramètres (consultation, urgence, hospitalisation...) est obligatoire.");
        }
    }
}
