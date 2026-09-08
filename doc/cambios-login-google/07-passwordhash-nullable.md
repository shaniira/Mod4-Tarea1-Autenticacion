# Paso 7 — Ajustar `AutenticarUsuarioUseCase` para `passwordHash == null`

**Fecha:** 2026-09-07
**Depende de:** paso 1 (`Usuario.passwordHash` ahora puede ser `null` para usuarios 100% Google).

---

## Objetivo del paso

Desde el paso 1, un `Usuario` puede existir sin `passwordHash` (usuario creado únicamente por `AutenticarConGoogleUseCase`, paso 5). Si esa misma persona intentara loguearse por el flujo **local** (`POST /api/auth/login`, con cualquier contraseña), había que asegurarse de que el sistema la rechace con un error de negocio controlado — no con una excepción técnica sin manejar.

---

## El bug que se estaba previniendo

`BCryptPasswordEncoderAdapter.coincide(textoPlano, hash)` delega en `BCryptPasswordEncoder.matches(raw, encoded)` de Spring Security. Esa implementación **lanza `IllegalArgumentException("encodedPassword cannot be null")`** cuando `encoded` es `null` — no devuelve `false`.

Antes de este paso, `AutenticarUsuarioUseCase.execute(...)` hacía:

```java
if (!usuario.isActivo()
        || !passwordEncoder.coincide(solicitud.password(), usuario.getPasswordHash())) {
    throw credencialesInvalidas();
}
```

Si `usuario.getPasswordHash()` era `null` (usuario solo-Google), esta línea **no lanzaba `ReglaNegocioException("CREDENCIALES_INVALIDAS", ...)`** como se esperaría — lanzaba una `IllegalArgumentException` no capturada, que `GlobalExceptionHandler.illegal(...)` traduce a `400 ARGUMENTO_INVALIDO` filtrando además el mensaje interno de Spring (`"encodedPassword cannot be null"`) directamente al cliente. Dos problemas a la vez: status HTTP incorrecto (400 en vez de 401) y fuga de un detalle de implementación interno — justo lo que el análisis original (§44) pide evitar ("nunca devolver detalles internos como stacktraces/mensajes técnicos").

---

## Archivo modificado

### `usecases/service/auth/AutenticarUsuarioUseCase.java`

```diff
  if (!usuario.isActivo()
+         || usuario.getPasswordHash() == null
          || !passwordEncoder.coincide(solicitud.password(), usuario.getPasswordHash())) {
      throw credencialesInvalidas();
  }
```

Un único `||` adicional. Gracias al cortocircuito de `||` en Java, si `passwordHash` es `null` la llamada a `passwordEncoder.coincide(...)` **nunca se ejecuta**, y el flujo cae directamente en `throw credencialesInvalidas()` — el mismo error genérico (`CREDENCIALES_INVALIDAS`, 401) que ya se usa para "usuario no existe" o "contraseña incorrecta". Se decidió deliberadamente **no** crear un código de error distinto (p.ej. `USUARIO_SOLO_GOOGLE`) para este caso: revelar que una cuenta existe pero "solo tiene Google configurado" es información que ayuda a un atacante a enumerar cuentas y su método de autenticación — mismo principio de seguridad que ya aplica el proyecto al unificar "no existe" y "password incorrecta" bajo un solo mensaje.

---

## Archivo de test nuevo

### `usecases/service/auth/AutenticarUsuarioUseCaseTest.java`

No existía ningún test dedicado a este caso de uso (se confirmó por búsqueda antes de este paso). Se creó uno con 5 casos, cubriendo tanto el caso nuevo como regresión del comportamiento existente:

| Test | Qué cubre |
|---|---|
| `autenticaUnUsuarioLocalConCredencialesCorrectas` | Camino feliz existente (regresión) — confirma que el cambio no afecta el login local normal. |
| `rechazaUnUsuarioInexistente` | Regresión del `orElseThrow(this::credencialesInvalidas)` ya existente. |
| `rechazaUnUsuarioInactivo` | Regresión de la condición `!usuario.isActivo()` ya existente. |
| `rechazaUnaContrasenaIncorrecta` | Regresión de `!passwordEncoder.coincide(...)` ya existente. |
| `rechazaUnUsuarioSoloGoogleSinLanzarNullPointerException` | **El caso nuevo de este paso**: usuario con `passwordHash = null` (creado por Google) intenta loguearse localmente → se verifica que lanza `ReglaNegocioException` con código `CREDENCIALES_INVALIDAS`, y con `verifyNoInteractions(passwordEncoder, ...)` se confirma que `passwordEncoder.coincide(...)` **nunca llega a invocarse** (es la prueba de que el cortocircuito funciona, no solo de que no explota). |

Estilo: JUnit 5 + AssertJ + Mockito, igual que `AutenticarConGoogleUseCaseTest` del paso 5 y el resto de la suite de `usecases`.

---

## Impacto

| Área | Impacto |
|---|---|
| `AutenticarUsuarioUseCase` | Un `||` agregado; comportamiento idéntico para todos los casos ya existentes (usuario inexistente, inactivo, contraseña incorrecta) — solo cambia el resultado para el caso nuevo (`passwordHash == null`), que antes no era alcanzable porque hasta el paso 1 `Usuario` no permitía esa combinación en la práctica. |
| Seguridad | Cierra una fuga de información (mensaje interno de Spring) y corrige un status HTTP incorrecto para un caso que solo existe **a partir de** la introducción de usuarios Google. |
| `AutenticarConGoogleUseCase`, `RegistrarUsuarioUseCase` | Sin cambios — este ajuste es exclusivo del flujo de login local. |
| Tests | Suite nueva y autocontenida; no modifica ningún test existente. |

---

## Verificación

⚠️ Sin Maven/Java en este entorno, no se pudo ejecutar `mvn test`. Revisión manual:

- Confirmé en la documentación de Spring Security (`BCryptPasswordEncoder.matches`) que un `encodedPassword` nulo lanza `IllegalArgumentException`, no devuelve `false` — de ahí la necesidad real de este cambio (no era una precaución especulativa).
- Tracé el cortocircuito de `||`: con `passwordHash == null`, la tercera subexpresión no se evalúa, así que `passwordEncoder.coincide(...)` nunca se invoca con un hash nulo, sin importar qué implementación real de `PasswordEncoderPort` esté configurada.

**Pendiente:** correr `mvn -q test -Dtest=AutenticarUsuarioUseCaseTest` en un entorno con JDK/Maven.

---

## Siguiente paso

Paso 8: `mvn test` completo (incluye `CleanArchitectureTest`) — cerrar la parte de backend antes de pasar a la configuración de Google Cloud Console (paso 9) y al frontend (paso 10).
