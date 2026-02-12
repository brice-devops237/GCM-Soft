package com.example.soft.modules.rendez_vous.repositories;

import com.example.soft.modules.rendez_vous.models.RendezVousModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RendezVousRepository extends JpaRepository<RendezVousModel, Integer> {

    List<RendezVousModel> findByCodeOrderByDateRendezVousDescHeureRendezVousDesc(String codePatient);

    List<RendezVousModel> findByDateRendezVousBetweenOrderByDateRendezVousAscHeureRendezVousAsc(
            LocalDate dateDebut, LocalDate dateFin);

    List<RendezVousModel> findByStatut(Integer statut);

    @Query("SELECT r FROM RendezVousModel r WHERE " +
            "(:codePatient IS NULL OR r.code = :codePatient) AND " +
            "(:dateDebut IS NULL OR r.dateRendezVous >= :dateDebut) AND " +
            "(:dateFin IS NULL OR r.dateRendezVous <= :dateFin) AND " +
            "(:statut IS NULL OR r.statut = :statut) " +
            "ORDER BY r.dateRendezVous ASC, r.heureRendezVous ASC")
    List<RendezVousModel> search(@Param("codePatient") String codePatient,
                                 @Param("dateDebut") LocalDate dateDebut,
                                 @Param("dateFin") LocalDate dateFin,
                                 @Param("statut") Integer statut);
}
