window.PatientsParametres = (function () {

    let currentCode = null;
    let currentName = null;

    function init() {
        setupCalculations();
    }

    function show(code, name) {
        currentCode = code;
        currentName = name;

        document.getElementById('codePatientParam').value = code;
        document.getElementById('parametresPatientCode').textContent = code;
        document.getElementById('parametresPatientName').textContent = name;

        document.getElementById('parametresCard').style.display = 'block';
        document.getElementById('selectedPatientName').textContent =
            `${name} (${code})`;

        load();
        new bootstrap.Modal(document.getElementById('modalParametres')).show();
    }

    function load() {
        fetch(`/parametres-patient/by-code?codePatient=${currentCode}`)
            .then(r => r.json())
            .then(render);
    }

    function render(list) {
        const tbody = document.getElementById('parametresTableBody');

        if (!list.length) {
            tbody.innerHTML =
                '<tr><td colspan="14" class="text-center text-muted">Aucun paramètre</td></tr>';
            return;
        }

        tbody.innerHTML = list.map(p => `
        <tr>
          <td>${formatDate(p.date)}</td>
          <td>${p.heure || '-'}</td>
          <td>${p.ta || '-'}</td>
          <td>${p.fc || '-'}</td>
          <td>${p.fr || '-'}</td>
          <td>${p.temp || '-'}</td>
          <td>${p.sa02 || '-'}</td>
          <td>${p.pas || '-'}</td>
          <td>${p.pad || '-'}</td>
          <td>${p.poids || '-'}</td>
          <td>${p.taille || '-'}</td>
          <td>${p.imc || '-'}</td>
          <td>${p.source || '-'}</td>
          <td>
            <button class="btn btn-sm btn-label-danger"
                    onclick="PatientsParametres.remove(${p.id})">
              🗑
            </button>
          </td>
        </tr>
      `).join('');
    }

    function remove(id) {
        ConfirmModal.show(
            'Supprimer paramètres',
            'Supprimer ces paramètres ?',
            () => {
                fetch(`/parametres-patient/delete/${id}`)
                    .then(() => load());
            }
        );
    }

    function setupCalculations() {
        const pas = document.getElementById('pas');
        const pad = document.getElementById('pad');
        const pam = document.getElementById('pam');
        if (pas && pad && pam) {
            const calcPam = () => {
                if (pas.value && pad.value) {
                    pam.value = (
                        Number(pad.value) +
                        (Number(pas.value) - Number(pad.value)) / 3
                    ).toFixed(1);
                }
            };
            pas.addEventListener('input', calcPam);
            pad.addEventListener('input', calcPam);
        }

        const poids = document.getElementById('poids');
        const taille = document.getElementById('taille');
        const imc = document.getElementById('imc');
        if (poids && taille && imc) {
            const calcImc = () => {
                const p = parseFloat(poids.value);
                const t = parseFloat(taille.value) / 100;
                if (p > 0 && t > 0) {
                    imc.value = (p / (t * t)).toFixed(1);
                } else {
                    imc.value = '0';
                }
            };
            poids.addEventListener('input', calcImc);
            taille.addEventListener('input', calcImc);
        }

        const btnCalculerTerme = document.getElementById('btnCalculerTerme');
        const ddr = document.getElementById('ddr');
        const termePrevuDisplay = document.getElementById('termePrevuDisplay');
        if (btnCalculerTerme && ddr && termePrevuDisplay) {
            btnCalculerTerme.addEventListener('click', () => {
                if (!ddr.value) {
                    termePrevuDisplay.textContent = 'Saisir la DDR puis cliquer sur Calculer.';
                    return;
                }
                const d = new Date(ddr.value + 'T12:00:00');
                d.setDate(d.getDate() + 281);
                termePrevuDisplay.textContent = 'Terme prévu : ' + d.toLocaleDateString('fr-FR', { day: '2-digit', month: '2-digit', year: 'numeric' });
            });
        }
    }

    function formatDate(d) {
        return d ? new Date(d).toLocaleDateString('fr-FR') : '-';
    }

    return { init, show, remove };
})();
