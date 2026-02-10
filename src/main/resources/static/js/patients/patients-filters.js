window.PatientsFilters = (function () {

    let timer = null;
    let controller = null;

    function init() {
        [
            'filterCode',
            'filterSearch',
            'filterSexe',
            'filterDateDebut',
            'filterDateFin'
        ].forEach(id => {
            document.getElementById(id)?.addEventListener('input', debounce);
            document.getElementById(id)?.addEventListener('change', search);
        });
    }

    function debounce() {
        clearTimeout(timer);
        timer = setTimeout(search, 300);
    }

    function search() {
        controller?.abort();
        controller = new AbortController();

        const params = new URLSearchParams();

        ['filterCode', 'filterSearch', 'filterSexe',
            'filterDateDebut', 'filterDateFin']
            .forEach(id => {
                const el = document.getElementById(id);
                if (el?.value) params.append(el.id.replace('filter', '').toLowerCase(), el.value);
            });

        fetch(`/patients/api/search?${params}`, { signal: controller.signal })
            .then(r => r.json())
            .then(updateTable)
            .catch(() => { });
    }

    function updateTable(list) {
        const tbody = document.getElementById('patientsTableBody');

        if (!list.length) {
            tbody.innerHTML =
                '<tr><td colspan="8" class="text-center">Aucun patient</td></tr>';
            return;
        }

        tbody.innerHTML = list.map(p => `
        <tr>
          <td>${p.code}</td>
          <td>${p.noms}</td>
          <td>${p.sexe}</td>
          <td>${new Date(p.dateNaissance).toLocaleDateString()}</td>
          <td>${p.phone}</td>
          <td>${p.typePatient}</td>
          <td>${p.estActif ? 'Actif' : 'Inactif'}</td>
          <td>
            <button class="btn btn-sm btn-edit-patient" data-patient-id="${p.id}">✏️</button>
          </td>
        </tr>
      `).join('');
    }

    return { init };
})();
