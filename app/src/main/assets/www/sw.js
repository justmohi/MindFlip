const CACHE_NAME = 'mindflip-v2';

const ASSETS = [
  './index.html',
  './manifest.json',
  './assets/index-9iesdlBu.js',
  './assets/index-Bt34V8sJ.css'
].map((path) => new URL(path, self.registration.scope).toString());

self.addEventListener('install', (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME)
      .then((cache) => cache.addAll(ASSETS))
      .then(() => self.skipWaiting())
  );
});

self.addEventListener('activate', (event) => {
  event.waitUntil(
    caches.keys()
      .then((keys) => Promise.all(
        keys.filter((key) => key.startsWith('mindflip-') && key !== CACHE_NAME)
          .map((key) => caches.delete(key))
      ))
      .then(() => self.clients.claim())
  );
});

self.addEventListener('fetch', (event) => {
  const request = event.request;
  if (request.method !== 'GET') return;

  const requestUrl = new URL(request.url);
  const scopeUrl = new URL(self.registration.scope);
  if (requestUrl.origin !== scopeUrl.origin ||
      !requestUrl.pathname.startsWith(scopeUrl.pathname)) {
    return;
  }

  event.respondWith(
    caches.match(request).then((cached) => cached || fetch(request))
  );
});
