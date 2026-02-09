package com.example.soft.modules.paramatre_patients.controllers;

import com.example.soft.modules.paramatre_patients.models.ParametresPatientModel;
import com.example.soft.modules.paramatre_patients.services.ParametresPatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller Spring MVC pour la gestion des paramètres cliniques des patients.
 *
 * <p>
 * Ce controller gère :
 * <ul>
 *     <li>L'affichage des paramètres patients</li>
 *     <li>La création de nouveaux paramètres</li>
 *     <li>La suppression</li>
 *     <li>La recherche et le filtrage dynamique</li>
 * </ul>
 * </p>
 *
 * <p>
 * Toutes les vues sont rendues via Thymeleaf.
 * Les messages utilisateurs (succès / erreur) sont transmis
 * via RedirectAttributes.
 * </p>
 */
@Controller
@RequestMapping("/parametres-patient")
@RequiredArgsConstructor
public class ParametresPatientController {

    /**
     * Service métier gérant la logique des paramètres patients.
     */
    private final ParametresPatientService service;

    /**
     * Affiche la liste complète des paramètres patients.
     *
     * <p>Scénario :</p>
     * <ul>
     *     <li>Chargement normal de la page</li>
     *     <li>Retour après création / suppression</li>
     * </ul>
     *
     * @param model modèle Thymeleaf
     * @return page HTML listant les paramètres
     */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("parametres", service.getAll());
        return "parametres/list";
    }

    /**
     * Affiche le formulaire de création d'un paramètre patient.
     *
     * <p>
     * Aucun champ n'est obligatoire côté formulaire,
     * mais des validations métier seront effectuées à la soumission.
     * </p>
     *
     * @param model modèle Thymeleaf
     * @return formulaire de saisie
     */
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("parametre", new ParametresPatientModel());
        return "parametres/form";
    }

    /**
     * Enregistre les paramètres cliniques d'un patient.
     *
     * <p>Règles métier :</p>
     * <ul>
     *     <li>Le code patient doit exister</li>
     *     <li>La source est obligatoire</li>
     * </ul>
     *
     * <p>Scénarios d'erreur :</p>
     * <ul>
     *     <li>Code patient inexistant → blocage</li>
     *     <li>Données incohérentes → message utilisateur</li>
     * </ul>
     */
    @PostMapping("/create")
    public String create(@ModelAttribute ParametresPatientModel parametre,
                         RedirectAttributes ra) {
        try {
            service.create(parametre);
            ra.addFlashAttribute("success",
                    "Les paramètres du patient ont été enregistrés avec succès.");
            return "redirect:/parametres-patient";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            return "redirect:/parametres-patient/create";
        } catch (Exception ex) {
            ra.addFlashAttribute("error",
                    "Une erreur inattendue est survenue lors de l'enregistrement.");
            return "redirect:/parametres-patient/create";
        }
    }

    /**
     * Supprime un enregistrement de paramètres patients.
     *
     * <p>
     * La suppression est définitive.
     * </p>
     *
     * @param id identifiant du paramètre
     */
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            service.delete(id);
            ra.addFlashAttribute("success",
                    "Les paramètres ont été supprimés avec succès.");
        } catch (Exception ex) {
            ra.addFlashAttribute("error",
                    "Impossible de supprimer ces paramètres.");
        }
        return "redirect:/parametres-patient";
    }

    /**
     * Recherche et filtrage dynamique des paramètres patients.
     *
     * <p>Critères possibles :</p>
     * <ul>
     *     <li>Code patient</li>
     *     <li>Période (date début / date fin)</li>
     * </ul>
     */
    @GetMapping("/search")
    public String filter(@RequestParam(required = false) String codePatient,
                         @RequestParam(required = false)
                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
                         @RequestParam(required = false)
                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
                         Model model) {

        Map<String, Object> filters = new HashMap<>();

        if (codePatient != null && !codePatient.isBlank()) {
            filters.put("codePatient", codePatient);
        }

        if (dateDebut != null && dateFin != null) {
            filters.put("dateDebut", dateDebut);
            filters.put("dateFin", dateFin);
        }

        model.addAttribute("parametres", service.filter(filters));
        return "parametres/list";
    }
}
