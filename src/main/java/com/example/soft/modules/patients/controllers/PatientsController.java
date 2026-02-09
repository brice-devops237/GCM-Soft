package com.example.soft.modules.patients.controllers;

import com.example.soft.modules.patients.models.PatientsModel;
import com.example.soft.modules.patients.services.PatientsService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
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
        return "patients/list";
    }

    @GetMapping("/inactive")
    public String listInactivePatients(Model model) {
        List<PatientsModel> patients = patientsService.getInactivePatients();
        model.addAttribute("patients", patients);
        return "patients/list";
    }

    /* ======================
       CREATION PATIENT
       ====================== */

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("patient", new PatientsModel());
        return "patients/form"; // formulaire Thymeleaf : patients/form.html
    }

    @PostMapping("/create")
    public String createPatient(@ModelAttribute PatientsModel patient,
                                RedirectAttributes redirectAttributes) {
        try {
            patientsService.createPatient(patient);
            redirectAttributes.addFlashAttribute("success", "Patient créé avec succès !");
            return "redirect:/patients";
        } catch (IllegalArgumentException | DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/patients/create";
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
            return "patients/form";
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/patients";
        }
    }

    @PostMapping("/edit/{id}")
    public String updatePatient(@PathVariable Integer id,
                                @ModelAttribute PatientsModel patient,
                                RedirectAttributes redirectAttributes) {
        try {
            patientsService.updatePatient(id, patient);
            redirectAttributes.addFlashAttribute("success", "Patient mis à jour avec succès !");
            return "redirect:/patients";
        } catch (IllegalArgumentException | DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/patients/edit/" + id;
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
        return "patients/list";
    }
}
