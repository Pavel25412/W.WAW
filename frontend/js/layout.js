const PAGES = [
  { href: 'index.html', label: 'Start' },
  { href: 'tickets.html', label: 'Kup bilet' },
  { href: 'my-tickets.html', label: 'Moje bilety' },
  { href: 'delays.html', label: 'Opóźnienia' },
  { href: 'lines.html', label: 'Linie' },
];

const current = location.pathname.split('/').pop() || 'index.html';

const header = document.getElementById('site-header');
if (header) {
  header.innerHTML = `
    <div class="bar">
      <a class="brand" href="index.html" aria-label="W.WAW - strona główna">W<span>.</span>WAW</a>
      <button class="nav-toggle" type="button" aria-expanded="false" aria-controls="main-nav">Menu</button>
      <nav id="main-nav" aria-label="Główna nawigacja">
        <ul>
          ${PAGES.map((p) => `<li><a href="${p.href}"${p.href === current ? ' aria-current="page"' : ''}>${p.label}</a></li>`).join('')}
        </ul>
      </nav>
    </div>`;

  const toggle = header.querySelector('.nav-toggle');
  const nav = header.querySelector('nav');
  toggle.addEventListener('click', () => {
    const open = nav.classList.toggle('open');
    toggle.setAttribute('aria-expanded', String(open));
  });
}

const footer = document.getElementById('site-footer');
if (footer) {
  footer.innerHTML = `
    <div class="inner">
      <div>
        <h2>Mapa serwisu</h2>
        <ul>${PAGES.map((p) => `<li><a href="${p.href}">${p.label}</a></li>`).join('')}</ul>
      </div>
      <div>
        <h2>W.WAW</h2>
        <p>Bilety komunikacji miejskiej w Warszawie i dziennik opóźnień. Zdążysz.</p>
      </div>
      <div>
        <h2>O projekcie</h2>
        <p>Projekt rekrutacyjny. Płatności są symulowane, a linie i opóźnienia to dane demonstracyjne.</p>
      </div>
    </div>
    <div class="legal">© 2026 W.WAW. Ceny biletów wg taryfy ZTM Warszawa.</div>`;
}
