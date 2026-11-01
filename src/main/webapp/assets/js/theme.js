(function () {
    const root = document.documentElement;
    let saved = null;
    try { saved = localStorage.getItem('studystack-theme'); } catch (_) { /* Storage may be disabled. */ }
    const preferred = window.matchMedia && window.matchMedia('(prefers-color-scheme: light)').matches ? 'light' : 'dark';
    const theme = saved === 'light' || saved === 'dark' ? saved : preferred;
    root.setAttribute('data-theme', theme);
    root.style.colorScheme = theme;
})();

document.addEventListener('DOMContentLoaded', function () {
    const toggle = document.querySelector('[data-theme-toggle]');
    if (!toggle) return;
    const sync = function () {
        const light = document.documentElement.getAttribute('data-theme') === 'light';
        toggle.textContent = light ? '☀' : '☾';
        toggle.setAttribute('aria-label', light ? 'Switch to dark mode' : 'Switch to light mode');
        toggle.setAttribute('title', light ? 'Switch to dark mode' : 'Switch to light mode');
    };
    sync();
    toggle.addEventListener('click', function () {
        const next = document.documentElement.getAttribute('data-theme') === 'dark' ? 'light' : 'dark';
        document.documentElement.setAttribute('data-theme', next);
        document.documentElement.style.colorScheme = next;
        try { localStorage.setItem('studystack-theme', next); } catch (_) { /* Keep this visit's theme. */ }
        sync();
    });
});
