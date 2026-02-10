window.PatientsTable = (function () {

    function init() {
        const table = document.getElementById('patientsTable');
        if (!table) return;

        table.addEventListener('click', e => {
            const btn = e.target.closest('button');
            if (!btn) return;

            if (btn.classList.contains('btn-edit-patient')) {
                editPatient(btn.dataset.patientId);
            }

            if (btn.classList.contains('btn-delete-patient')) {
                deletePatient(btn.dataset.patientId, btn.dataset.patientName);
            }

            if (btn.classList.contains('btn-show-parametres')) {
                PatientsParametres.show(
                    btn.dataset.patientCode,
                    btn.dataset.patientName
                );
            }
        });
    }

    function editPatient(id) {
        fetch(`/patients/api/${id}`)
            .then(r => r.json())
            .then(fillPatientForm)
            .catch(() => alert('Erreur de chargement patient'));
    }

    function deletePatient(id, name) {
        ConfirmModal.show(
            'Supprimer patient',
            `Supprimer <strong>${name}</strong> ?<br><small class="text-danger">Action irréversible</small>`,
            () => location.href = `/patients/delete/${id}`
        );
    }

    function fillPatientForm(p) {
        const map = {
            patientId: p.id,
            noms: p.noms,
            sexe: p.sexe,
            dateNaissance: p.dateNaissance,
            lieuNaissance: p.lieuNaissance,
            nationalite: p.nationalite,
            groupe: p.groupe,
            adresse: p.adresse,
            ville: p.ville,
            phone: p.phone,
            cni: p.cni,
            profession: p.profession,
            etatMatrimonial: p.etatMatrimonial,
            nomMere: p.nomMere,
            nomPere: p.nomPere,
            ethnie: p.ethnie,
            religion: p.religion,
            typePatient: p.typePatient,
            urgence: p.urgence,
            contactUrgence: p.contactUrgence,
            date: p.date?.split('T')[0],
            consulteur: p.consulteur,
            printBy: p.printBy,
            commentaire: p.commentaire
        };

        Object.entries(map).forEach(([id, value]) => {
            const el = document.getElementById(id);
            if (el) el.value = value ?? '';
        });

        const form = document.getElementById('patientForm');
        form.action = `/patients/edit/${p.id}`;
        document.getElementById('modalPatientTitle').textContent = 'Modifier patient';

        const d1 = document.getElementById('contactNomDisplay');
        const d2 = document.getElementById('contactRelationDisplay');
        const d3 = document.getElementById('contactTelDisplay');
        if (d1) d1.textContent = p.urgence || '—';
        if (d2) d2.textContent = '—';
        if (d3) d3.textContent = p.contactUrgence || '—';

        new bootstrap.Modal(document.getElementById('modalPatient')).show();
    }

    return { init };
})();
