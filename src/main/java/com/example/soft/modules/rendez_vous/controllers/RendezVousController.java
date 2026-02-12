package com.example.soft.modules.rendez_vous.controllers;

import com.example.soft.modules.patients.models.PatientsModel;
import com.example.soft.modules.patients.services.PatientsService;
import com.example.soft.modules.rendez_vous.dtos.RendezVousDto;
import com.example.soft.modules.rendez_vous.models.RendezVousModel;
import com.example.soft.modules.rendez_vous.services.RendezVousService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controller pour la gestion des rendez-vous patients.
 * Réutilise le layout et les patterns (toasts, modals) des autres modules.
 */
@Controller
@RequestMapping("/rendez-vous")
@RequiredArgsConstructor
public class RendezVousController {

    private final RendezVousService rendezVousService;
    private final PatientsService patientsService;

    private static String currentUserName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal()))
                ? auth.getName() : "SYSTEM";
    }

    @GetMapping
    public String list(Model model,
                       @RequestParam(required = false) String codePatient,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
                       @RequestParam(required = false) Integer statut) {
        List<RendezVousModel> list = rendezVousService.search(codePatient, dateDebut, dateFin, statut);
        model.addAttribute("rendezVousList", list);
        model.addAttribute("codeToNom", buildCodeToNom(list));
        model.addAttribute("patients", patientsService.getActivePatients());
        return "pages/rendez-vous/index";
    }

    private java.util.Map<String, String> buildCodeToNom(List<RendezVousModel> list) {
        java.util.Map<String, String> map = new java.util.HashMap<>();
        for (RendezVousModel r : list) {
            if (r.getCode() != null && !map.containsKey(r.getCode())) {
                try {
                    PatientsModel p = patientsService.getPatientByCode(r.getCode());
                    map.put(r.getCode(), p != null ? p.getNoms() : r.getCode());
                } catch (Exception e) {
                    map.put(r.getCode(), r.getCode());
                }
            }
        }
        return map;
    }

    @GetMapping("/api/search")
    @ResponseBody
    public ResponseEntity<List<RendezVousDto>> search(
            @RequestParam(required = false) String codePatient,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam(required = false) Integer statut) {
        List<RendezVousModel> list = rendezVousService.search(codePatient, dateDebut, dateFin, statut);
        List<RendezVousDto> dtos = list.stream().map(r -> toDto(r)).toList();
        return ResponseEntity.ok(dtos);
    }

    private RendezVousDto toDto(RendezVousModel r) {
        String nomPatient = null;
        try {
            PatientsModel p = patientsService.getPatientByCode(r.getCode());
            if (p != null) nomPatient = p.getNoms();
        } catch (Exception ignored) {}
        return RendezVousDto.builder()
                .id(r.getId())
                .code(r.getCode())
                .nomPatient(nomPatient)
                .codeConsultation(r.getCodeConsultation())
                .dateJour(r.getDateJour())
                .dateRendezVous(r.getDateRendezVous())
                .heureRendezVous(r.getHeureRendezVous())
                .matTraitent(r.getMatTraitent())
                .objet(r.getObjet())
                .user(r.getUser())
                .statut(r.getStatut())
                .build();
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<RendezVousModel> getById(@PathVariable Integer id) {
        RendezVousModel r = rendezVousService.getById(id);
        return ResponseEntity.ok(r);
    }

    @GetMapping("/api/patients")
    @ResponseBody
    public ResponseEntity<List<PatientsModel>> listPatients() {
        return ResponseEntity.ok(patientsService.getActivePatients());
    }

    /**
     * Autocomplete patients pour Select2 (recherche par code ou nom).
     * Retourne { results: [ { id: code, text: "code - noms" } ] }.
     */
    @GetMapping("/api/patients/autocomplete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> patientsAutocomplete(@RequestParam(required = false) String q) {
        List<PatientsModel> list;
        if (q != null && !q.isBlank()) {
            list = patientsService.filterPatients(Map.of("noms", q.trim()));
            if (list.isEmpty()) {
                try {
                    PatientsModel byCode = patientsService.getPatientByCode(q.trim());
                    if (byCode != null) list = List.of(byCode);
                } catch (Exception ignored) {}
            }
            list = list.stream().limit(20).toList();
        } else {
            list = patientsService.getActivePatients().stream().limit(30).toList();
        }
        List<Map<String, String>> results = list.stream()
                .map(p -> Map.<String, String>of("id", p.getCode(), "text", p.getCode() + " - " + (p.getNoms() != null ? p.getNoms() : "")))
                .toList();
        return ResponseEntity.ok(Map.of("results", results));
    }

    @PostMapping("/create")
    public String create(@ModelAttribute RendezVousModel rendezVous, RedirectAttributes ra) {
        try {
            rendezVousService.create(rendezVous, currentUserName());
            ra.addFlashAttribute("success", "Rendez-vous enregistré avec succès.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Erreur lors de l'enregistrement du rendez-vous.");
        }
        return "redirect:/rendez-vous";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Integer id, @ModelAttribute RendezVousModel rendezVous, RedirectAttributes ra) {
        try {
            rendezVousService.update(id, rendezVous);
            ra.addFlashAttribute("success", "Rendez-vous modifié avec succès.");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage() != null ? ex.getMessage() : "Erreur lors de la modification.");
        }
        return "redirect:/rendez-vous";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            rendezVousService.delete(id);
            ra.addFlashAttribute("success", "Rendez-vous supprimé.");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Impossible de supprimer le rendez-vous.");
        }
        return "redirect:/rendez-vous";
    }
}
