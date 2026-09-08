# Paso 10 — Frontend: `index.html`, `stores/auth.ts`, `LoginView.vue`, `services/api.ts`

**Fecha:** 2026-09-07
**Depende de:** paso 9 (Client ID real ya en `frontend/.env`, versionado por decisión del proyecto).

---

## Objetivo del paso

Agregar el botón "Continuar con Google" al login existente, integrando Google Identity Services (GIS) con el store de autenticación y el manejo de errores ya existentes, sin rediseñar nada del frontend.

---

## Archivos modificados

### 1. `frontend/index.html`

Se agregó la carga del script de Google Identity Services, justo después del `<title>`:

```diff
  <title>Andina Seguros</title>
+ <script src="https://accounts.google.com/gsi/client" async defer></script>
```

`async defer` evita bloquear el render inicial — el script se ejecuta cuando esté disponible, y `LoginView.vue` verifica su presencia (`window.google`) antes de usarlo (ver más abajo), por si el usuario navega muy rápido a `/login` antes de que el script externo termine de cargar.

### 2. `frontend/src/types/index.ts`

Se agregó un tipo mínimo para el callback de GIS, ya que el proyecto es TypeScript estricto (`vue-tsc -b` corre en `npm run build`):

```ts
export interface GoogleCredentialResponse{credential:string}
```

### 3. `frontend/src/stores/auth.ts`

Se agregó `loginWithGoogle` como acción hermana de `login`, y se extrajo la lógica común de "persistir sesión" a un método `setSession(token, username)` para no duplicar el parseo del JWT y el guardado en `localStorage` entre ambos flujos:

```diff
  actions: {
    async login(username: string, password: string) {
      const { data } = await api.post('/auth/login', { username, password });
-     const payload = parseJwt(data.token);
-
-     this.token = data.token;
-     this.role = (payload.rol || payload.role || payload.roles?.[0]?.replace('ROLE_', '') || '') as Role;
-     this.username = username;
-
-     localStorage.setItem('token', this.token);
-     localStorage.setItem('role', this.role);
-     localStorage.setItem('username', username);
+     this.setSession(data.token, username);
    },
+   async loginWithGoogle(idToken: string) {
+     const { data } = await api.post('/auth/google', { idToken });
+     this.setSession(data.token, parseJwt(data.token).sub || '');
+   },
+   setSession(token: string, username: string) {
+     const payload = parseJwt(token);
+
+     this.token = token;
+     this.role = (payload.rol || payload.role || payload.roles?.[0]?.replace('ROLE_', '') || '') as Role;
+     this.username = username;
+
+     localStorage.setItem('token', this.token);
+     localStorage.setItem('role', this.role);
+     localStorage.setItem('username', username);
+   },
```

**Por qué `parseJwt(data.token).sub` para el username de Google:** el backend firma el JWT de Andina con `subject = usuario.getUsername()` (`JwtTokenAdapter.generar`, sin cambios desde el paso 1). Para un usuario creado por `AutenticarConGoogleUseCase`, `username = email` (decisión del paso 5). Es decir, el claim `sub` del JWT de Andina **ya es el email/username correcto** — no hace falta pedirle nada extra al backend ni decodificar el `idToken` de Google en el frontend (que, como advierte el análisis original §32, nunca debe usarse como prueba de identidad ni fuente de datos de negocio).

### 4. `frontend/src/views/LoginView.vue`

Se agregó, dentro del mismo archivo de una sola línea (siguiendo el estilo ya establecido en el proyecto para las vistas):

- Import de `onMounted` (además del `ref` ya usado) y del tipo `GoogleCredentialResponse`.
- `onGoogleCredential(response)`: mismo patrón que `go()` (loading/error/redirect), pero llamando a `a.loginWithGoogle(response.credential)`.
- `onMounted(...)`: inicializa `google.accounts.id` con el `client_id` leído de `import.meta.env.VITE_GOOGLE_CLIENT_ID` (paso 9) y renderiza el botón oficial de Google en un contenedor `#google-btn`. Se verifica `if(!google)return` por si el script externo (`index.html`) todavía no cargó.
- En el `<template>`: un separador visual (`<div class="google-divider"><span>o</span></div>`) entre el botón de login local y el contenedor `<div id="google-btn"></div>` del botón de Google.

**Decisión de tipado:** se usa `(window as any).google` porque el proyecto no tiene instalados los tipos oficiales de Google Identity Services (`@types/google.accounts` no es un paquete que Google mantenga de forma oficial y estable) y agregar una dependencia nueva solo para tipos de un objeto global de terceros no se justifica para el alcance de este proyecto. Es el patrón más común en integraciones de GIS con Vue/React sin SDK propio.

### 5. `frontend/src/assets/main.css`

Se agregaron 3 reglas nuevas al final del archivo (que ya está minificado en una sola línea, se respetó ese formato):

```css
.google-divider{display:flex;align-items:center;gap:10px;color:var(--muted);font-size:11px;text-transform:uppercase;letter-spacing:.08em}
.google-divider::before,.google-divider::after{content:'';flex:1;height:1px;background:var(--line)}
#google-btn{display:flex;justify-content:center}
```

Reutiliza las variables de color ya definidas (`--muted`, `--line`) en vez de inventar colores nuevos, para que el separador visual sea consistente con el resto del sistema de diseño existente.

### 6. `frontend/src/services/api.ts`

Se agregaron los 4 códigos de error nuevos al mapa `messagesByCode`, siguiendo el mismo patrón que los códigos ya existentes (`CREDENCIALES_INVALIDAS`, `USUARIO_DUPLICADO`, etc.):

```ts
GOOGLE_TOKEN_INVALIDO: 'No se pudo validar tu cuenta de Google. Intenta nuevamente.',
GOOGLE_EMAIL_NO_VERIFICADO: 'Tu correo de Google no está verificado.',
USUARIO_INACTIVO: 'Tu cuenta está inactiva. Contacta al administrador.',
CUENTA_EXISTENTE_REQUIERE_VINCULACION: 'Ya existe una cuenta con este correo. Inicia sesión con tu contraseña para vincular Google.',
```

**Nota sobre por qué esto es (parcialmente) redundante y aun así correcto:** `errorMessage(...)` prioriza `data.mensaje` (el mensaje que ya envía `GlobalExceptionHandler` desde el backend, que ya está en español y es amigable) por encima de `messagesByCode`. Es decir, en el flujo normal el mensaje que se muestra viene directo del backend, no de este mapa. Sin embargo, el proyecto **ya tenía este mismo patrón de redundancia** para los códigos existentes (p. ej. `CREDENCIALES_INVALIDAS` ya está tanto en el backend como en este mapa) — es un fallback defensivo intencional del proyecto, no una improvisación de este cambio, así que se mantuvo la consistencia agregando los 4 códigos nuevos de la misma forma.

---

## Impacto

| Área | Impacto |
|---|---|
| `index.html` | Una línea nueva (script externo `async defer`), no afecta el render de ninguna otra vista. |
| `stores/auth.ts` | Refactor interno (`setSession`) + 1 acción nueva. `login(...)` mantiene exactamente el mismo comportamiento observable (mismo test esperado si existiera uno). |
| `LoginView.vue` | Único archivo con cambios visuales; el formulario local no se modificó, solo se le agregó contenido debajo. |
| `main.css` | 3 reglas nuevas, ninguna existente se tocó. |
| `services/api.ts` | Solo se agregaron entradas a un objeto ya existente; `errorMessage(...)` no cambió de lógica. |
| Backend | Sin impacto — este paso es exclusivamente frontend. |
| Otras vistas (`DashboardView`, `ClientesView`, etc.) | Sin impacto — no consumen `auth.ts` más allá de `isAuthenticated`/`role`, que no cambiaron de forma. |

---

## Verificación

Este entorno **sí tiene Node.js (v24.19.0) y npm (11.17.0)** instalados — a diferencia del backend, aquí no hizo falta recurrir a Docker. Se ejecutó `npm ci` para instalar dependencias exactas desde `package-lock.json`, y se dejó pendiente correr:

- `npm run build` (ejecuta `vue-tsc -b && vite build`) — para confirmar que los tipos nuevos (`GoogleCredentialResponse`, `(window as any).google`) no rompen el chequeo estricto de TypeScript.
- `npm test` (`vitest run`) — para confirmar que `api.test.ts` sigue pasando con las nuevas entradas en `messagesByCode`.

*(Resultados de estos dos comandos se documentan en la actualización de este mismo archivo una vez completada la instalación, más abajo o en un apéndice, según corresponda al momento de cerrar el paso.)*

---

## Siguiente paso

Paso 11: prueba end-to-end manual real — levantar backend (`docker compose up` con `GOOGLE_CLIENT_ID` exportado) y frontend (`npm run dev`), completar el login con una cuenta Gmail de prueba agregada en Google Cloud Console (paso 9), confirmar que el JWT de Andina resultante funciona contra un endpoint protegido, y confirmar que el login local no sufrió ninguna regresión.
