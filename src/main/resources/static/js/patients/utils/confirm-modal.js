window.ConfirmModal = (function () {

    function show(title, message, onConfirm, confirmBtnClass = 'btn-danger') {
        const modalEl = document.getElementById('modalConfirm');
        if (!modalEl) return;

        document.getElementById('confirmModalTitle').textContent = title;
        document.getElementById('confirmModalMessage').innerHTML = message;

        const oldBtn = document.getElementById('confirmModalBtn');
        const newBtn = oldBtn.cloneNode(true);

        newBtn.className = `btn ${confirmBtnClass}`;
        oldBtn.parentNode.replaceChild(newBtn, oldBtn);

        newBtn.addEventListener('click', () => {
            bootstrap.Modal.getInstance(modalEl)?.hide();
            onConfirm?.();
        });

        new bootstrap.Modal(modalEl).show();
    }

    return { show };
})();
