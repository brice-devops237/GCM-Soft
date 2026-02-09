package com.example.soft.modules.patients.controllers;

import com.example.soft.modules.patients.models.PatientsModel;
import com.example.soft.modules.patients.services.PatientsService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller Spring MVC pour la gestion des patients.
 *
 * <p>
 * Fournit les endpoints pour :
 * <ul>
 *     <li>Liste, création, modification et suppression des patients</li>
 *     <li>Activation et désactivation</li>
 *     <li>Recherche simple et filtrage dynamique</li>
 * </ul>
 * </p>
 *
 * <p>
 * Intègre Thymeleaf pour les vues côté serveur.
 * </p>
 */
@Controller
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientsController {

    private final PatientsService patientsService;

    /* ======================
       LISTE DES PATIENTS
       ====================== */

    @GetMapping
    public String listPatients(Model model) {
        List<PatientsModel> patients = patientsService.getAllPatients();
        model.addAttribute("patients", patients);
        return "pages/patients/index"; // Thymeleaf template : patients/list.html
    }

    @GetMapping("/active")
    public String listActivePatients(Model model) {
        List<PatientsModel> patients = patientsService.getActivePatients();
        model.addAttribute("patients", patients);
        return "pages/patients/index";
    }

    @GetMapping("/inactive")
    public String listInactivePatients(Model model) {
        List<PatientsModel> patients = patientsService.getInactivePatients();
        model.addAttribute("patients", patients);
        return "pages/patients/index";
    }

    /* ======================
       CREATION PATIENT
       ====================== */

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("patient", new PatientsModel());
        return "pages/patients/index"; // Redirection vers la page principale avec modal
    }

    @PostMapping("/create")
    public String createPatient(@ModelAttribute PatientsModel patient,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        try {
            patientsService.createPatient(patient);
            redirectAttributes.addFlashAttribute("success", "Patient créé avec succès !");
            return "redirect:/patients";
        } catch (IllegalArgumentException ex) {
            // Extraire le nom du champ depuis le message d'erreur
            String errorMessage = ex.getMessage();
            redirectAttributes.addFlashAttribute("error", errorMessage);
            model.addAttribute("patients", patientsService.getAllPatients());
            model.addAttribute("patient", patient);
            return "pages/patients/index";
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + ex.getMessage());
            model.addAttribute("patients", patientsService.getAllPatients());
            model.addAttribute("patient", patient);
            return "pages/patients/index";
        }
    }

    /* ======================
       MODIFICATION PATIENT
       ====================== */

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            PatientsModel patient = patientsService.getPatientById(id);
            model.addAttribute("patient", patient);
            model.addAttribute("patients", patientsService.getAllPatients());
            return "pages/patients/index";
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/patients";
        }
    }
    
    /**
     * Endpoint REST pour récupérer un patient par son ID (JSON).
     * Utilisé par JavaScript pour charger les données du patient dans le modal.
     */
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<PatientsModel> getPatientByIdApi(@PathVariable Integer id) {
        try {
            PatientsModel patient = patientsService.getPatientById(id);
            return ResponseEntity.ok(patient);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/edit/{id}")
    public String updatePatient(@PathVariable Integer id,
                                @ModelAttribute PatientsModel patient,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        try {
            patientsService.updatePatient(id, patient);
            redirectAttributes.addFlashAttribute("success", "Patient mis à jour avec succès !");
            return "redirect:/patients";
        } catch (IllegalArgumentException ex) {
            String errorMessage = ex.getMessage();
            redirectAttributes.addFlashAttribute("error", errorMessage);
            model.addAttribute("patients", patientsService.getAllPatients());
            model.addAttribute("patient", patient);
            return "pages/patients/index";
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + ex.getMessage());
            model.addAttribute("patients", patientsService.getAllPatients());
            model.addAttribute("patient", patient);
            return "pages/patients/index";
        }
    }

    /* ======================
       SUPPRESSION PATIENT
       ====================== */

    @GetMapping("/delete/{id}")
    public String deletePatient(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            patientsService.deletePatient(id);
            redirectAttributes.addFlashAttribute("success", "Patient supprimé avec succès !");
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/patients";
    }

    /* ======================
       ACTIVATION / DESACTIVATION
       ====================== */

    @GetMapping("/activate/{id}")
    public String activatePatient(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            patientsService.activatePatient(id);
            redirectAttributes.addFlashAttribute("success", "Patient activé !");
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/patients";
    }

    @GetMapping("/deactivate/{id}")
    public String deactivatePatient(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            patientsService.deactivatePatient(id);
            redirectAttributes.addFlashAttribute("success", "Patient désactivé !");
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/patients";
    }

    /* ======================
       FILTRAGE ET RECHERCHE
       ====================== */

    @GetMapping("/search")
    public String searchPatients(@RequestParam(required = false) String code,
                                 @RequestParam(required = false) String noms,
                                 @RequestParam(required = false) String sexe,
                                 @RequestParam(required = false) String typePatient,
                                 @RequestParam(required = false) Integer categorie,
                                 @RequestParam(required = false) Boolean estActif,
                                 @RequestParam(required = false)
                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
                                 @RequestParam(required = false)
                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
                                 Model model) {

        Map<String, Object> filters = new HashMap<>();
        if (code != null && !code.isBlank()) filters.put("code", code);
        if (noms != null && !noms.isBlank()) filters.put("noms", noms);
        if (sexe != null && !sexe.isBlank()) filters.put("sexe", sexe);
        if (typePatient != null && !typePatient.isBlank()) filters.put("typePatient", typePatient);
        if (categorie != null) filters.put("categorie", categorie);
        if (estActif != null) filters.put("estActif", estActif);
        if (dateDebut != null && dateFin != null) {
            filters.put("dateDebut", dateDebut);
            filters.put("dateFin", dateFin);
        }

        List<PatientsModel> patients = patientsService.filterPatients(filters);
        model.addAttribute("patients", patients);
        model.addAttribute("filters", filters);
        return "pages/patients/index";
    }
    
    /**
     * Endpoint REST pour la recherche AJAX en temps réel.
     * Retourne les patients filtrés en JSON.
     */
    @GetMapping("/api/search")
    @ResponseBody
    public ResponseEntity<List<PatientsModel>> searchPatientsApi(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String noms,
            @RequestParam(required = false) String sexe,
            @RequestParam(required = false) String typePatient,
            @RequestParam(required = false) Integer categorie,
            @RequestParam(required = false) Boolean estActif,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {

        Map<String, Object> filters = new HashMap<>();
        if (code != null && !code.isBlank()) filters.put("code", code);
        if (noms != null && !noms.isBlank()) filters.put("noms", noms);
        if (sexe != null && !sexe.isBlank()) filters.put("sexe", sexe);
        if (typePatient != null && !typePatient.isBlank()) filters.put("typePatient", typePatient);
        if (categorie != null) filters.put("categorie", categorie);
        if (estActif != null) filters.put("estActif", estActif);
        if (dateDebut != null && dateFin != null) {
            filters.put("dateDebut", dateDebut);
            filters.put("dateFin", dateFin);
        }

        // Si aucun filtre, retourner tous les patients
        List<PatientsModel> patients;
        if (filters.isEmpty()) {
            patients = patientsService.getAllPatients();
        } else {
            patients = patientsService.filterPatients(filters);
        }
        
        return ResponseEntity.ok(patients);
    }
}
