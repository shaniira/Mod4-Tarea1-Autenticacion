# Paso 9 — Configuración en Google Cloud Console + preparación de `frontend/.env`

**Fecha:** 2026-09-07
**Depende de:** nada del código (es 100% configuración externa en Google Cloud), pero es prerrequisito del paso 10 (frontend) y del paso 11 (prueba end-to-end real).

---

## Objetivo del paso

Obtener un `Client ID` de Google real, configurado correctamente para el flujo elegido (Google Identity Services + `POST /api/auth/google`), y dejarlo disponible de forma segura para el frontend sin comprometerlo en el control de versiones.

---

## Configuración realizada en Google Cloud Console (por el usuario)

| Campo | Valor elegido | Justificación |
|---|---|---|
| Tipo de usuario (Audience) | **Usuarios externos** | El proyecto usa una cuenta Gmail personal, no Google Workspace — "Interno" no es una opción viable. "Externo" permite loguear con cualquier Cuenta de Google, quedando en modo de pruebas hasta agregar explícitamente los correos de prueba (paso pendiente del propio usuario: agregar su Gmail a la lista de test users). |
| Tipo de aplicación (Client) | Web application | Es el único tipo compatible con Google Identity Services + JavaScript en el navegador. |
| Authorized JavaScript origins | `http://localhost:5173` | Coincide exactamente con el puerto donde corre el frontend, tanto en `npm run dev` (Vite) como en `frontend/docker-compose.yml` (`5173:80`), y con `CORS_ALLOWED_ORIGINS` ya configurado en el backend. Sin ruta ni barra final. |
| Authorized redirect URIs | (vacío, no configurado) | No aplica al flujo elegido (callback JS + `POST /api/auth/google`) — solo sería necesario si se migrara a Authorization Code Flow gestionado por el backend (ver `IMPLEMENTACION-LOGIN-GOOGLE.md §55`). |
| Client ID obtenido | `1070285094051-hu34h00hmfc1fhuj8dg4oc55f4h3satq.apps.googleusercontent.com` | Es un identificador público (no un secreto) — se distribuye intencionalmente dentro del JavaScript del navegador. No se generó ni se usó ningún `Client Secret`, tal como estaba previsto (`IMPLEMENTACION-LOGIN-GOOGLE.md §29`: el flujo de ID Token no lo necesita). |

---

## Decisión sobre `frontend/.env`: se versiona (no se ignora)

Se evaluó inicialmente agregar un `frontend/.gitignore` para excluir `.env` del control de versiones (siguiendo la práctica más común para archivos de entorno). El usuario indicó explícitamente **no hacerlo**: el objetivo del proyecto es que cualquier compañero pueda clonar el repo y levantar todo sin pasos extra de configuración manual — por lo tanto `frontend/.env` **se mantiene versionado, con el `Client ID` real incluido**.

Esto es una decisión de proyecto válida en este contexto porque, a diferencia de un `Client Secret` o una contraseña, el **`Client ID` de Google no es un secreto** — está diseñado para exponerse públicamente (termina de todas formas visible en el bundle de JavaScript servido al navegador de cualquier usuario final). No hay ninguna credencial sensible en `frontend/.env` que journalice un riesgo real al versionarla.

No se creó ningún `frontend/.gitignore` (se descartó la versión inicial que sí se había creado).

### Archivos creados/modificados

1. **`frontend/.env`** (nuevo, **versionado**) — valores reales para desarrollo, compartidos con el equipo:
   ```
   VITE_API_URL=http://localhost:8080/api
   VITE_GOOGLE_CLIENT_ID=1070285094051-hu34h00hmfc1fhuj8dg4oc55f4h3satq.apps.googleusercontent.com
   ```

2. **`frontend/.env.example`** (modificado) — se mantiene igual como plantilla de referencia (aunque con `.env` ya versionado pierde algo de su propósito original, se deja por si en el futuro se decide dejar de versionar `.env`, p. ej. al pasar a producción con un `Client ID` distinto):
   ```diff
     VITE_API_URL=http://localhost:8080/api
   + VITE_GOOGLE_CLIENT_ID=tu-client-id.apps.googleusercontent.com
   ```

---

## Pendiente del lado del backend para la prueba end-to-end (paso 11)

El backend (`Arquitectura-Clean/docker-compose.yml`) ya está preparado desde el paso 6 para leer `GOOGLE_CLIENT_ID` desde una variable de entorno del host (`GOOGLE_CLIENT_ID: ${GOOGLE_CLIENT_ID:-}`). Para que la verificación del ID Token funcione contra este Client ID real, habrá que exportar la variable **en el entorno donde se levante el backend** antes de `docker compose up`, por ejemplo:

```bash
export GOOGLE_CLIENT_ID=1070285094051-hu34h00hmfc1fhuj8dg4oc55f4h3satq.apps.googleusercontent.com
```

Esto no se ejecutó todavía en este paso — queda para cuando se haga la prueba real end-to-end (paso 11), junto con levantar el backend completo (Mongo + API).

---

## Impacto

| Área | Impacto |
|---|---|
| Google Cloud (externo al repo) | Proyecto OAuth configurado, en modo de pruebas, con un `Client ID` funcional. |
| `frontend/.env` | Nuevo, **versionado** (decisión explícita del usuario) — contiene el `Client ID` real; cualquiera que clone el repo puede levantar el frontend sin configurar nada manualmente. |
| `frontend/.env.example` | Se mantiene como plantilla de referencia, aunque `frontend/.env` ya está versionado con el valor real. |
| Backend | Sin cambios de código en este paso — solo queda pendiente exportar `GOOGLE_CLIENT_ID` al levantar el contenedor para la prueba real (paso 11). |

---

## Siguiente paso

Paso 10 — Frontend: `index.html` (script de Google Identity Services), `src/types/index.ts`, `src/stores/auth.ts` (`loginWithGoogle`), `src/views/LoginView.vue` (botón de Google), y mensajes de error nuevos en `src/services/api.ts`.
