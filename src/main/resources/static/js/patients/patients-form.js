window.PatientsForm = (function () {

    function init() {
        const form = document.getElementById('patientForm');
        if (!form) return;

        document
            .querySelector('[data-bs-target="#modalPatient"]')
            ?.addEventListener('click', resetForm);

        form.addEventListener('submit', onSubmit);

        document
            .getElementById('modalPatient')
            ?.addEventListener('hidden.bs.modal', resetForm);

        syncContactUrgenceTable();
    }

    function syncContactUrgenceTable() {
        const nom = document.getElementById('urgence');
        const relation = document.getElementById('relationUrgence');
        const tel = document.getElementById('contactUrgence');
        const displayNom = document.getElementById('contactNomDisplay');
        const displayRelation = document.getElementById('contactRelationDisplay');
        const displayTel = document.getElementById('contactTelDisplay');
        if (!nom || !relation || !tel || !displayNom) return;

        function update() {
            displayNom.textContent = nom.value.trim() || '—';
            let relationText = '—';
            if (relation && relation.value) {
                if (relation.value === 'Autre') {
                    const autre = document.getElementById('relationUrgenceAutre');
                    relationText = (autre && autre.value.trim()) ? autre.value.trim() : '—';
                } else {
                    relationText = relation.options[relation.selectedIndex].text;
                }
            }
            displayRelation.textContent = relationText;
            displayTel.textContent = tel.value.trim() || '—';
        }
        nom.addEventListener('input', update);
        nom.addEventListener('change', update);
        if (relation) relation.addEventListener('change', update);
        const autreInput = document.getElementById('relationUrgenceAutre');
        if (autreInput) {
            autreInput.addEventListener('input', update);
            autreInput.addEventListener('change', update);
        }
        tel.addEventListener('input', update);
        tel.addEventListener('change', update);
    }

    function onSubmit(e) {
        e.preventDefault();

        if (!validate()) return;

        const name = document.getElementById('noms').value;
        const isEdit = e.target.action.includes('/edit/');

        ConfirmModal.show(
            isEdit ? 'Modifier patient' : 'Créer patient',
            `${isEdit ? 'Modifier' : 'Créer'} <strong>${name}</strong> ?`,
            () => e.target.submit(),
            'btn-primary'
        );
    }

    function validate() {
        let valid = true;

        document.querySelectorAll('#patientForm [required]').forEach(el => {
            if (!el.value.trim()) {
                el.classList.add('is-invalid');
                valid = false;
            } else {
                el.classList.remove('is-invalid');
            }
        });

        return valid;
    }

    function resetForm() {
        const form = document.getElementById('patientForm');
        form.reset();
        form.action = '/patients/create';
        document.getElementById('patientId').value = '';
        document.getElementById('modalPatientTitle').textContent =
            'Enregistrement d’un patient';

        document.querySelectorAll('#patientForm .is-invalid').forEach(el => el.classList.remove('is-invalid'));
        const d1 = document.getElementById('contactNomDisplay');
        const d2 = document.getElementById('contactRelationDisplay');
        const d3 = document.getElementById('contactTelDisplay');
        if (d1) d1.textContent = '—';
        if (d2) d2.textContent = '—';
        if (d3) d3.textContent = '—';
    }

    return { init };
})();
