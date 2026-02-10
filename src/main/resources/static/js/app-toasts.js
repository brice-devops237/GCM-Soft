/**
 * Toasts applicatifs (Notyf) - Messages personnalisés avec couleurs selon le type.
 * Uniquement des toasts (aucun alert). AppToasts.success(), .error(), .warning(), .info().
 */
(function () {
  'use strict';

  var defaultColors = {
    success: '#71dd37',
    danger: '#ff4d49',
    warning: '#ffab00',
    info: '#03c3ec'
  };

  function getColors() {
    if (typeof window.config !== 'undefined' && window.config.colors) {
      return {
        success: window.config.colors.success || defaultColors.success,
        danger: window.config.colors.danger || defaultColors.danger,
        warning: window.config.colors.warning || defaultColors.warning,
        info: window.config.colors.info || defaultColors.info
      };
    }
    return defaultColors;
  }

  function createNotyf() {
    var colors = getColors();
    return new Notyf({
      duration: 4000,
      ripple: true,
      dismissible: true,
      position: { x: 'right', y: 'top' },
      types: [
        {
          type: 'success',
          background: colors.success,
          icon: { className: 'bx bx-check-circle', tagName: 'i' }
        },
        {
          type: 'error',
          background: colors.danger,
          icon: { className: 'bx bx-x-circle', tagName: 'i' }
        },
        {
          type: 'warning',
          background: colors.warning,
          icon: { className: 'bx bx-error', tagName: 'i' }
        },
        {
          type: 'info',
          background: colors.info,
          icon: { className: 'bx bx-info-circle', tagName: 'i' }
        }
      ]
    });
  }

  var notyf = null;

  function getNotyf() {
    if (!notyf) {
      if (typeof Notyf === 'undefined') {
        console.warn('AppToasts: Notyf non chargé.');
        return null;
      }
      notyf = createNotyf();
    }
    return notyf;
  }

  window.AppToasts = {
    success: function (message) {
      var n = getNotyf();
      if (n) n.success(message || 'Opération réussie.');
    },
    error: function (message) {
      var n = getNotyf();
      if (n) n.error(message || 'Une erreur est survenue.');
    },
    warning: function (message) {
      var n = getNotyf();
      if (n) n.open({ type: 'warning', message: message || 'Attention.' });
    },
    info: function (message) {
      var n = getNotyf();
      if (n) n.open({ type: 'info', message: message || 'Information.' });
    }
  };
})();
