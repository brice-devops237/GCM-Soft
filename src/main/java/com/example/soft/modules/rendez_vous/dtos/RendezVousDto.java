package com.example.soft.modules.rendez_vous.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO pour l'affichage et l'API des rendez-vous (avec nom patient).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RendezVousDto {

    private Integer id;
    private String code;
    private String nomPatient;
    private String codeConsultation;
    private LocalDate dateJour;
    private LocalDate dateRendezVous;
    private LocalTime heureRendezVous;
    private String matTraitent;
    private String objet;
    private String user;
    private Integer statut;
}
