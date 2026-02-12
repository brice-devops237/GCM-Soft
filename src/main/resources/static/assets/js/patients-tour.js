/**
 * Tour Shepherd - Gestion des patients et paramètres
 * Guide pas à pas : ajouter, modifier, supprimer patient ; paramètres.
 */
(function () {
  'use strict';

  var btnClassSecondary = 'btn btn-sm btn-label-secondary';
  var btnClassPrimary = 'btn btn-sm btn-primary';

  function startPatientsTour() {
    if (typeof Shepherd === 'undefined') {
      console.warn('Shepherd non chargé.');
      return;
    }

    var tour = new Shepherd.Tour({
      defaultStepOptions: {
        scrollTo: true,
        cancelIcon: { enabled: true },
        modalOverlayOpeningPadding: 4,
        modalOverlayOpeningRadius: 8
      },
      useModalOverlay: true
    });

    // 1. Bienvenue
    tour.addStep({
      id: 'welcome',
      title: 'Bienvenue',
      text: 'Ce guide vous présente les actions disponibles : <strong>ajouter</strong>, <strong>modifier</strong> et <strong>supprimer</strong> des patients, ainsi que la gestion des <strong>paramètres cliniques</strong>.',
      attachTo: {
        element: '#patientsModuleCard',
        on: 'top'
      },
      buttons: [
        { text: 'Passer', classes: btnClassSecondary, action: tour.cancel },
        { text: 'Suivant', classes: btnClassPrimary, action: tour.next }
      ]
    });

    // 2. Ajouter patient
    tour.addStep({
      id: 'add-patient',
      title: 'Ajouter un patient',
      text: 'Cliquez ici pour ouvrir le formulaire et <strong>enregistrer un nouveau patient</strong>. Le modal contient toutes les informations (identité, contact, nationalité, etc.).',
      attachTo: {
        element: '#patientsModuleCard button[data-bs-target="#modalPatient"]',
        on: 'bottom'
      },
      buttons: [
        { text: 'Passer', classes: btnClassSecondary, action: tour.cancel },
        { text: 'Retour', classes: btnClassSecondary, action: tour.back },
        { text: 'Suivant', classes: btnClassPrimary, action: tour.next }
      ]
    });

    // 3. Filtres
    tour.addStep({
      id: 'filters',
      title: 'Filtres de recherche',
      text: 'Filtrez la liste par <strong>date</strong>, <strong>code patient</strong>, <strong>nom et prénom</strong> ou <strong>sexe</strong>. La recherche se met à jour en temps réel.',
      attachTo: {
        element: '#filterCode',
        on: 'bottom'
      },
      buttons: [
        { text: 'Passer', classes: btnClassSecondary, action: tour.cancel },
        { text: 'Retour', classes: btnClassSecondary, action: tour.back },
        { text: 'Suivant', classes: btnClassPrimary, action: tour.next }
      ]
    });

    // 4. Table des patients - Actions (Modifier, Paramètres, Supprimer)
    tour.addStep({
      id: 'table-actions',
      title: 'Actions sur un patient',
      text: 'Dans chaque ligne, le menu <strong>Actions (⋮)</strong> permet de :<br>' +
        '• <strong>Modifier</strong> : éditer les informations du patient<br>' +
        '• <strong>Paramètres</strong> : afficher et gérer les paramètres cliniques (PAM, FC, tension, etc.)<br>' +
        '• <strong>Supprimer</strong> : supprimer le patient (avec confirmation)',
      attachTo: {
        element: '#patientsTable',
        on: 'top'
      },
      buttons: [
        { text: 'Passer', classes: btnClassSecondary, action: tour.cancel },
        { text: 'Retour', classes: btnClassSecondary, action: tour.back },
        { text: 'Suivant', classes: btnClassPrimary, action: tour.next }
      ]
    });

    // 5. Section Paramètres
    tour.addStep({
      id: 'parametres-card',
      title: 'Paramètres du patient',
      text: 'En cliquant sur <strong>Paramètres</strong> pour un patient, cette section s\'affiche. Vous y gérez les <strong>paramètres cliniques</strong> (PAM, FC, FR, température, SaO2, PAS, PAD, poids, taille, IMC, etc.) de ce patient.',
      attachTo: {
        element: '#parametresCard',
        on: 'top'
      },
      buttons: [
        { text: 'Passer', classes: btnClassSecondary, action: tour.cancel },
        { text: 'Retour', classes: btnClassSecondary, action: tour.back },
        { text: 'Suivant', classes: btnClassPrimary, action: tour.next }
      ]
    });

    // 6. Ajouter paramètres
    tour.addStep({
      id: 'add-parametres',
      title: 'Ajouter des paramètres',
      text: 'Cliquez ici pour <strong>ajouter une fiche de paramètres</strong> (date, heure, constantes) pour le patient sélectionné. Un modal s\'ouvre pour saisir les valeurs.',
      attachTo: {
        element: '#btnAddParametres',
        on: 'bottom'
      },
      buttons: [
        { text: 'Passer', classes: btnClassSecondary, action: tour.cancel },
        { text: 'Retour', classes: btnClassSecondary, action: tour.back },
        { text: 'Suivant', classes: btnClassPrimary, action: tour.next }
      ]
    });

    // 7. Table paramètres - Modifier / Supprimer
    tour.addStep({
      id: 'parametres-table-actions',
      title: 'Modifier ou supprimer un paramètre',
      text: 'Les paramètres enregistrés s\'affichent ici. Pour chaque ligne, le menu Actions permet de <strong>Modifier</strong> ou <strong>Supprimer</strong> la fiche de paramètres.',
      attachTo: {
        element: '#parametresTable',
        on: 'top'
      },
      buttons: [
        { text: 'Passer', classes: btnClassSecondary, action: tour.cancel },
        { text: 'Retour', classes: btnClassSecondary, action: tour.back },
        { text: 'Suivant', classes: btnClassPrimary, action: tour.next }
      ]
    });

    // 8. Fin
    tour.addStep({
      id: 'fin',
      title: 'Guide terminé',
      text: 'Vous savez maintenant <strong>ajouter</strong>, <strong>modifier</strong> et <strong>supprimer</strong> des patients, ainsi que gérer leurs <strong>paramètres cliniques</strong>. Bonne utilisation !',
      attachTo: {
        element: '#patientsModuleCard',
        on: 'top'
      },
      buttons: [
        { text: 'Retour', classes: btnClassSecondary, action: tour.back },
        { text: 'Terminer', classes: btnClassPrimary, action: tour.cancel }
      ]
    });

    tour.start();
  }

  // Exposer globalement pour le bouton
  window.startPatientsTour = startPatientsTour;

  // Démarrer automatiquement si l’élément existe au chargement (optionnel)
  document.addEventListener('DOMContentLoaded', function () {
    var trigger = document.getElementById('patientsTourTrigger');
    if (trigger) {
      trigger.addEventListener('click', function () {
        startPatientsTour();
      });
    }
  });
})();
