-- Table des rendez-vous patients (structure alignée sur l'existant)
CREATE TABLE IF NOT EXISTS tb_rendez_vous_patient (
  id INT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(200) NOT NULL COMMENT 'Code patient (référence accueil.code)',
  code_consultation VARCHAR(50) DEFAULT NULL,
  date_jour DATE NOT NULL COMMENT 'Date du jour d''enregistrement',
  date_rendez_vous DATE NOT NULL,
  heure_rendez_vous TIME NOT NULL,
  mat_traitent VARCHAR(100) NOT NULL COMMENT 'Médecin / traitant',
  objet VARCHAR(500) NOT NULL,
  user VARCHAR(50) NOT NULL COMMENT 'Utilisateur ayant enregistré',
  statut INT DEFAULT 0 COMMENT '0=En attente patient, 1=En attente traitant, 2=Honoré'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_rendez_vous_code ON tb_rendez_vous_patient(code);
CREATE INDEX idx_rendez_vous_date ON tb_rendez_vous_patient(date_rendez_vous);
CREATE INDEX idx_rendez_vous_statut ON tb_rendez_vous_patient(statut);
