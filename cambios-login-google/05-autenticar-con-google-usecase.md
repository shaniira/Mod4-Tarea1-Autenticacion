# Paso 5 — `GoogleLoginRequestModel` + `AutenticarConGoogleUseCase` + tests

**Fecha:** 2026-09-07
**Depende de:** paso 2 (`UsuarioRepository.buscarPorEmail`/`buscarPorGoogleSubject`), paso 3 (`GoogleIdentity`, `GoogleIdentityVerifierPort`).

---

## Objetivo del paso

Implementar el caso de uso central del login federado: orquestar verificación de identidad, búsqueda/alta de usuario y emisión del JWT propio, con las políticas de seguridad decididas en `IMPLEMENTACION-LOGIN-GOOGLE.md` (§4.7).

---

## Archivos nuevos

### 1. `usecases/dto/GoogleLoginRequestModel.java`

```java
public record GoogleLoginRequestModel(String idToken) {}
```

Mismo estilo que `LoginRequestModel(String username, String password)`.

### 2. `usecases/service/auth/AutenticarConGoogleUseCase.java`

Dependencias inyectadas por constructor: `UsuarioRepository`, `GoogleIdentityVerifierPort`, `TokenGeneratorPort`, `IdGeneratorPort` — exactamente las 4 previstas en el plan original.

**Flujo implementado en `execute(GoogleLoginRequestModel)`:**

1. `verificar(idToken)` — delega en `GoogleIdentityVerifierPort.verificar(...)`. Si esta llamada lanza cualquier `RuntimeException` (firma inválida, `aud`/`iss` incorrectos, vencido — todo lo que ya cubre `GoogleIdentityVerifierAdapter` del paso 4), se traduce a `ReglaNegocioException("GOOGLE_TOKEN_INVALIDO", ...)`. Importante: el `catch (RuntimeException e)` no importa ningún tipo de Spring/Nimbus — solo usa `java.lang.RuntimeException` — por eso `usecases` sigue sin depender de frameworks pese a que el adaptador que implementa el puerto sí los use.
2. Si `!identidad.emailVerified()` → `ReglaNegocioException("GOOGLE_EMAIL_NO_VERIFICADO", ...)`. Esta verificación se hace **antes** de cualquier búsqueda en el repositorio, aplicándose igual a usuarios nuevos y existentes (decisión: nunca confiar en un email no verificado de Google, sin importar si es primer login o no).
3. Busca por `googleSubject` (`usuarios.buscarPorGoogleSubject(identidad.subject())`).
   - Si existe: continúa al paso 5 directamente (usuario ya vinculado).
   - Si no existe: `orElseGet(() -> vincularOCrearUsuario(identidad))`:
     - Busca por `email`. Si **ya existe una cuenta local con ese email** (sin `googleSubject` vinculado, porque si lo tuviera se habría encontrado en el paso anterior) → `ReglaNegocioException("CUENTA_EXISTENTE_REQUIERE_VINCULACION", ...)` — política segura elegida en `IMPLEMENTACION-LOGIN-GOOGLE.md §6` (no vincular cuentas automáticamente solo por coincidencia de email).
     - Si no existe ninguna cuenta con ese email: crea un `Usuario` nuevo — `username = email`, `email = email`, `passwordHash = null`, `googleSubject = subject`, `rol = RolUsuario.CLIENTE` (fijo, decidido por el backend, **nunca** tomado del request), `activo = true` — y lo guarda.
4. Verifica `usuario.isActivo()`; si no, `ReglaNegocioException("USUARIO_INACTIVO", ...)`.
5. Construye `AuthenticatedUser(usuario.getUsername(), usuario.getRol().name())` y llama `tokenGenerator.generar(...)` — **exactamente el mismo objeto y el mismo puerto que usa `AutenticarUsuarioUseCase`** para el login local; no se tocó `JwtTokenAdapter` ni `TokenGeneratorPort`.
6. Retorna `Responses.TokenResponse(token, "Bearer", tokenGenerator.expirationSeconds())` — mismo contrato que `/login`.

Los 4 códigos de error (`GOOGLE_TOKEN_INVALIDO`, `GOOGLE_EMAIL_NO_VERIFICADO`, `USUARIO_INACTIVO`, `CUENTA_EXISTENTE_REQUIERE_VINCULACION`) se generan con métodos privados dedicados (`tokenInvalido()`, `emailNoVerificado()`, `usuarioInactivo()`, `cuentaRequiereVinculacion()`), replicando el patrón ya usado en `AutenticarUsuarioUseCase.credencialesInvalidas()`.

---

## Archivo de test nuevo

### `usecases/service/auth/AutenticarConGoogleUseCaseTest.java`

6 casos, con mocks de los 4 puertos (Mockito) — igual estilo que `EmitirPolizaUseCaseTest`:

| Test                                                                     | Escenario                                                                          | Resultado esperado                                                                                                                                                                                      |
| ------------------------------------------------------------------------ | ---------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `rechazaUnTokenDeGoogleInvalido`                                       | `verifier.verificar(...)` lanza `RuntimeException`                             | `ReglaNegocioException` código `GOOGLE_TOKEN_INVALIDO`; ni `usuarios` ni `tokenGenerator` se tocan                                                                                             |
| `rechazaUnEmailDeGoogleNoVerificado`                                   | `emailVerified = false`                                                          | `ReglaNegocioException` código `GOOGLE_EMAIL_NO_VERIFICADO`                                                                                                                                        |
| `autenticaUnUsuarioGoogleExistenteYActivo`                             | `buscarPorGoogleSubject` devuelve un usuario activo                              | `TokenResponse` con el JWT generado; **no** se llama a `guardar(...)`                                                                                                                         |
| `rechazaUnUsuarioGoogleExistenteInactivo`                              | usuario encontrado por`googleSubject` con `activo=false`                       | `ReglaNegocioException` código `USUARIO_INACTIVO`; `tokenGenerator` no se toca                                                                                                                   |
| `creaUnUsuarioNuevoConRolClienteCuandoNoExisteNiPorSubNiPorEmail`      | ni`googleSubject` ni `email` existen                                           | se captura el`Usuario` pasado a `guardar(...)` y se verifica: `id` generado, `username=email`, `email`, `googleSubject`, `passwordHash=null`, **`rol=CLIENTE`** y `activo=true` |
| `rechazaCuandoYaExisteUnaCuentaLocalConElMismoEmailSinGoogleVinculado` | `googleSubject` no existe pero `email` sí (cuenta local con `passwordHash`) | `ReglaNegocioException` código `CUENTA_EXISTENTE_REQUIERE_VINCULACION`; no se guarda ni se genera token                                                                                            |

Estos 6 casos son exactamente los previstos en el análisis original (§40, casos 1 a 6), con nombres de test descriptivos en español siguiendo el estilo del repo.

---

## Verificación contra reglas ArchUnit

`AutenticarConGoogleUseCase` (en `usecases.service.auth`) importa únicamente:

- `entities.enums.RolUsuario`, `entities.exception.ReglaNegocioException`, `entities.model.Usuario` (permitido: `usecases` puede depender de `entities`, la capa más interna).
- `usecases.dto.*`, `usecases.port.out.*` (su propia capa).

Ningún import de `org.springframework`, `jakarta.*`, `org.bson`, `com.mongodb`, `interfaceadapters` ni `frameworksdrivers` → cumple `use_cases_have_no_framework_dependencies` y `use_cases_only_point_inward`.

El test usa Mockito/AssertJ, pero `CleanArchitectureTest` está configurado con `@AnalyzeClasses(..., importOptions = ImportOption.DoNotIncludeTests.class)`, por lo que el código de test queda fuera del alcance de estas reglas (como ya ocurre con el resto de tests del proyecto).

---

## Impacto

| Área                                                     | Impacto                                                                                                                                                                                                                                                                                                                                                                                  |
| --------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `usecases`                                              | 2 archivos nuevos (`GoogleLoginRequestModel`, `AutenticarConGoogleUseCase`). Ningún archivo existente se modificó en este paso.                                                                                                                                                                                                                                                    |
| `AutenticarUsuarioUseCase`, `RegistrarUsuarioUseCase` | Sin cambios — el nuevo caso de uso es independiente, no los llama ni es llamado por ellos.                                                                                                                                                                                                                                                                                              |
| `TokenGeneratorPort`/`JwtTokenAdapter`                | Sin cambios — se reutiliza tal cual, confirmando la premisa central del diseño ("Google no reemplaza el JWT").                                                                                                                                                                                                                                                                         |
| Seguridad                                                 | Rol de usuarios nuevos por Google queda**fijo en `CLIENTE`** dentro del propio caso de uso — el `GoogleLoginRequestModel` ni siquiera tiene un campo `rol`, así que es estructuralmente imposible que un cliente lo controle (a diferencia de `CrearUsuarioRequest.rol`, el problema preexistente documentado en el paso 1 / `IMPLEMENTACION-LOGIN-GOOGLE.md §4.17`). |
| Aún no conectado                                         | Este caso de uso**todavía no es alcanzable desde HTTP** — falta el paso 6 (`GoogleLoginRequest`, `RestRequestMapper`, `AuthController`, `UseCaseConfig`) para exponerlo como `/api/auth/google`.                                                                                                                                                                       |

---


## Siguiente paso

Paso 6: `GoogleLoginRequest` (DTO REST), `RestRequestMapper`, `AuthController` (`POST /api/auth/google`), `UseCaseConfig` (bean del adaptador y del caso de uso) y `application.yml` (`app.google.client-id`, `app.google.issuer`).
