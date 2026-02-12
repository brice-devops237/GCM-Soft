package com.example.soft.modules.rendez_vous.serviceImpl;

import com.example.soft.modules.patients.repositories.PatientsRepository;
import com.example.soft.modules.rendez_vous.models.RendezVousModel;
import com.example.soft.modules.rendez_vous.repositories.RendezVousRepository;
import com.example.soft.modules.rendez_vous.services.RendezVousService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RendezVousServiceImpl implements RendezVousService {

    private final RendezVousRepository repository;
    private final PatientsRepository patientsRepository;

    @Override
    @Transactional(readOnly = false)
    public RendezVousModel create(RendezVousModel rendezVous, String currentUser) {
        if (rendezVous.getCode() == null || rendezVous.getCode().isBlank()) {
            throw new IllegalArgumentException("Le code patient est obligatoire.");
        }
        if (!patientsRepository.existsByCode(rendezVous.getCode().trim())) {
            throw new IllegalStateException("Aucun patient trouvé avec le code : " + rendezVous.getCode());
        }
        if (rendezVous.getDateRendezVous() == null) {
            throw new IllegalArgumentException("La date du rendez-vous est obligatoire.");
        }
        if (rendezVous.getHeureRendezVous() == null) {
            throw new IllegalArgumentException("L'heure du rendez-vous est obligatoire.");
        }
        LocalDateTime rdvDateTime = LocalDateTime.of(rendezVous.getDateRendezVous(), rendezVous.getHeureRendezVous());
        if (rdvDateTime.isBefore(LocalDateTime.now().plusMinutes(5))) {
            throw new IllegalArgumentException("La date et l'heure du rendez-vous doivent être au moins 5 minutes dans le futur.");
        }
        if (rendezVous.getMatTraitent() == null || rendezVous.getMatTraitent().isBlank()) {
            throw new IllegalArgumentException("Le médecin traitant est obligatoire.");
        }
        if (rendezVous.getObjet() == null || rendezVous.getObjet().isBlank()) {
            throw new IllegalArgumentException("L'objet du rendez-vous est obligatoire.");
        }
        rendezVous.setId(null);
        rendezVous.setDateJour(rendezVous.getDateRendezVous() != null ? rendezVous.getDateRendezVous() : LocalDate.now());
        rendezVous.setUser(currentUser != null ? currentUser : "SYSTEM");
        if (rendezVous.getStatut() == null) {
            rendezVous.setStatut(RendezVousModel.STATUT_ATTENTE_PATIENT);
        }
        return repository.save(rendezVous);
    }

    @Override
    @Transactional(readOnly = false)
    public RendezVousModel update(Integer id, RendezVousModel rendezVous) {
        RendezVousModel existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rendez-vous introuvable : id " + id));
        existing.setDateRendezVous(rendezVous.getDateRendezVous());
        existing.setHeureRendezVous(rendezVous.getHeureRendezVous());
        existing.setMatTraitent(rendezVous.getMatTraitent());
        existing.setObjet(rendezVous.getObjet());
        if (rendezVous.getStatut() != null) {
            existing.setStatut(rendezVous.getStatut());
        }
        if (rendezVous.getCodeConsultation() != null) {
            existing.setCodeConsultation(rendezVous.getCodeConsultation());
        }
        return repository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public RendezVousModel getById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rendez-vous introuvable : id " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVousModel> getAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Rendez-vous introuvable : id " + id);
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVousModel> getByCodePatient(String codePatient) {
        return repository.findByCodeOrderByDateRendezVousDescHeureRendezVousDesc(codePatient);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVousModel> search(String codePatient, LocalDate dateDebut, LocalDate dateFin, Integer statut) {
        return repository.search(codePatient, dateDebut, dateFin, statut);
    }
}
