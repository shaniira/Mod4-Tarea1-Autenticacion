# Paso 6 — `GoogleLoginRequest`, `RestRequestMapper`, `AuthController`, `UseCaseConfig`, `application.yml`

**Fecha:** 2026-09-07
**Depende de:** paso 4 (`GoogleIdentityVerifierAdapter`), paso 5 (`AutenticarConGoogleUseCase`, `GoogleLoginRequestModel`).

---

## Objetivo del paso

Exponer `AutenticarConGoogleUseCase` como `POST /api/auth/google`, cablear sus dependencias en `UseCaseConfig`, y agregar la configuración externa (`GOOGLE_CLIENT_ID`). Con este paso el flujo de login con Google queda **alcanzable de punta a punta vía HTTP**.

---

## Archivos nuevos

### `interfaceadapters/in/rest/request/GoogleLoginRequest.java`

```java
public record GoogleLoginRequest(@NotBlank String idToken) {}
```

Mismo estilo que `LoginRequest`.

---

## Archivos modificados

### 1. `interfaceadapters/in/rest/mapper/RestRequestMapper.java`

Se agregó, junto a los demás `toCore(...)`:

```java
public static GoogleLoginRequestModel toCore(GoogleLoginRequest value) {
    return new GoogleLoginRequestModel(value.idToken());
}
```

No hizo falta agregar imports: el archivo ya usa `import ...request.*;` y `import ...usecases.dto.*;` con wildcard.

### 2. `interfaceadapters/in/rest/controller/AuthController.java`

Se agregó la tercera dependencia (`AutenticarConGoogleUseCase`) al constructor y el endpoint:

```java
@PostMapping("/google")
public TokenResponse google(@Valid @RequestBody GoogleLoginRequest solicitud) {
    return autenticarConGoogle.execute(toCore(solicitud));
}
```

Sin lógica adicional — igual patrón que `login`/`register` (recibe HTTP, convierte DTO, invoca caso de uso, devuelve respuesta).

**Efecto colateral de compilación:** al cambiar el constructor de `AuthController` de 2 a 3 parámetros, `AuthControllerTest` dejaba de compilar. Se actualizó agregando `mock(AutenticarConGoogleUseCase.class)` como tercer argumento, y se aprovechó para agregar aserciones sobre el nuevo endpoint (`controller.google(...)`) en el mismo test existente, siguiendo su propio estilo (un solo método de test que ejercita los tres endpoints).

### 3. `frameworksdrivers/configuration/spring/UseCaseConfig.java`

Se agregaron 2 beans nuevos, siguiendo exactamente el patrón ya usado para `autenticarUsuario(...)`:

```java
@Bean
GoogleIdentityVerifierPort googleIdentityVerifierPort(
        @Value("${app.google.client-id}") String clientId,
        @Value("${app.google.issuer:https://accounts.google.com}") String issuer) {
    JwtDecoder jwtDecoder =
            NimbusJwtDecoder.withJwkSetUri(GoogleIdentityVerifierAdapter.GOOGLE_JWK_SET_URI)
                    .build();
    return new GoogleIdentityVerifierAdapter(jwtDecoder, clientId, issuer);
}

@Bean
AutenticarConGoogleUseCase autenticarConGoogle(
        UsuarioRepository usuarios,
        GoogleIdentityVerifierPort googleIdentityVerifier,
        TokenGeneratorPort tokenGenerator,
        IdGeneratorPort ids) {
    return new AutenticarConGoogleUseCase(usuarios, googleIdentityVerifier, tokenGenerator, ids);
}
```

Aquí es donde efectivamente se construye el `NimbusJwtDecoder` real apuntando al JWKS de Google (`GoogleIdentityVerifierAdapter.GOOGLE_JWK_SET_URI`, definido en el paso 4) — es la única clase de toda la solución que sabe que existe una URL de Google de por medio; el resto de capas solo conocen el puerto. `NimbusJwtDecoder.withJwkSetUri(...).build()` **no hace ninguna llamada de red al construirse** (la resolución de claves JWKS de Nimbus es perezosa y cacheada, solo ocurre en el primer `decode()`), por lo que este bean no puede romper el arranque de la aplicación por falta de conectividad.

Se agregaron los imports necesarios: `AutenticarConGoogleUseCase`, `GoogleIdentityVerifierAdapter` (de `interfaceadapters.out.security.google`, que **no** queda cubierto por el `import ...security.*;` ya existente porque los wildcards de Java no son recursivos sobre subpaquetes), `JwtDecoder` y `NimbusJwtDecoder` de Spring Security. `GoogleIdentityVerifierPort` ya estaba cubierto por el wildcard `usecases.port.out.security.*` existente.

### 4. `src/main/resources/application.yml`

```yaml
app:
  google:
    client-id: ${GOOGLE_CLIENT_ID:}
    issuer: https://accounts.google.com
```

`client-id` usa el mismo patrón que `JWT_SECRET`/`CORS_ALLOWED_ORIGINS` (variable de entorno con default vacío) — así `@Value("${app.google.client-id}")` siempre resuelve a algo (aunque sea `""`) y **no puede impedir el arranque** de la aplicación por una propiedad faltante, ni siquiera en entornos donde `GOOGLE_CLIENT_ID` todavía no está configurado (el endpoint simplemente rechazará cualquier token real porque ningún `aud` coincidirá con una cadena vacía).

### 5. `Arquitectura-Clean/docker-compose.yml`

Se agregó `GOOGLE_CLIENT_ID: ${GOOGLE_CLIENT_ID:-}` al servicio `backend`, junto a `JWT_SECRET`/`CORS_ALLOWED_ORIGINS`, para que la variable de entorno del host se propague al contenedor igual que las demás.

**Nota:** el `docker-compose.yml` de la **raíz** del repositorio (`Arquitecturas/docker-compose.yml`) orquesta las 3 arquitecturas (Clean/Hexagonal/Onion) y repite estas mismas variables 3 veces — **no se tocó**, porque está fuera del alcance de esta tarea (solo `Arquitectura-Clean` + `frontend`). Si más adelante se decide levantar el login de Google también vía ese compose raíz, habrá que replicar la misma variable ahí.

### 6. `interfaceadapters/in/rest/exception/GlobalExceptionHandler.java` — cambio adicional no listado explícitamente en el plan original, pero necesario para "buenas prácticas"

Sin este cambio, los 4 códigos de error nuevos (`GOOGLE_TOKEN_INVALIDO`, `GOOGLE_EMAIL_NO_VERIFICADO`, `USUARIO_INACTIVO`, `CUENTA_EXISTENTE_REQUIERE_VINCULACION`) habrían caído todos en el `default -> HttpStatus.UNPROCESSABLE_ENTITY` (422) del switch existente — semánticamente incorrecto para errores de autenticación. Se agregó:

```java
case "CREDENCIALES_INVALIDAS",
        "GOOGLE_TOKEN_INVALIDO",
        "GOOGLE_EMAIL_NO_VERIFICADO",
        "USUARIO_INACTIVO" -> HttpStatus.UNAUTHORIZED;
case "CUENTA_EXISTENTE_REQUIERE_VINCULACION" -> HttpStatus.CONFLICT;
```

Razonamiento: los primeros tres son variantes de "no pudimos autenticarte" → 401, igual que `CREDENCIALES_INVALIDAS` ya existente. `CUENTA_EXISTENTE_REQUIERE_VINCULACION` no es un fallo de autenticación sino un conflicto de estado (la cuenta ya existe por otra vía) → 409, más preciso que el 422 genérico. Se agregaron 2 tests nuevos en `GlobalExceptionHandlerTest` siguiendo el patrón exacto del test ya existente (`invalidCredentialsReturnUnauthorizedInsteadOfUnprocessableEntity`).

---

## Impacto

| Área                                                                | Impacto                                                                                                                                                                          |
| -------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `AuthController`                                                   | Endpoint nuevo`/api/auth/google`. `/api/auth/**` ya era `permitAll()` en `SecurityConfig` — **sin cambios en `SecurityConfig`**, el endpoint ya queda cubierto. |
| `AuthControllerTest`                                               | Actualizado para no romper compilación (constructor de 3 args) + nueva cobertura del endpoint Google.                                                                           |
| `UseCaseConfig`                                                    | Único lugar de todo el backend que sabe que existe una URL JWKS de Google — cumple el aislamiento buscado.                                                                     |
| `application.yml` / `docker-compose.yml` (Clean)                 | Nueva variable`GOOGLE_CLIENT_ID`, con default vacío — no rompe entornos donde aún no está configurada.                                                                     |
| `GlobalExceptionHandler`                                           | Los errores de Google ahora devuelven status HTTP semánticamente correctos (401/409) en vez de caer todos en 422.                                                               |
| `SecurityConfig`, `JwtAuthenticationFilter`, `JwtTokenAdapter` | **Sin cambios**, confirmando lo previsto en el análisis original (§22/23/24).                                                                                            |

---

---

## Siguiente paso

Paso 7: ajustar `AutenticarUsuarioUseCase` para que el login local no falle con `NullPointerException` cuando `passwordHash` sea `null` (usuario que solo tiene autenticación por Google).
