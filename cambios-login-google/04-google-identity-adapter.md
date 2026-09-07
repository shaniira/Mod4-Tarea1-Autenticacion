# Paso 4 — Dependencia Maven + `GoogleIdentityVerifierAdapter` + tests con claves controladas

**Fecha:** 2026-09-07
**Depende de:** [03-google-identity-port.md](03-google-identity-port.md) (`GoogleIdentity`, `GoogleIdentityVerifierPort`).

---

## Objetivo del paso

Implementar la verificación criptográfica real del ID Token de Google (firma, `iss`, `aud`, `exp`, `sub`), aislada por completo detrás de `GoogleIdentityVerifierPort`, y probarla sin depender de red ni de Google real — usando un par de claves RSA generado localmente en el propio test.

---

## Archivos modificados/nuevos

### 1. `Arquitectura-Clean/pom.xml` — MODIFICADO

Se agregó, junto a `spring-boot-starter-security` (ya existente):

```xml
<dependency>
  <groupId>org.springframework.security</groupId>
  <artifactId>spring-security-oauth2-jose</artifactId>
</dependency>
```

Sin versión explícita: la gestiona el BOM de `spring-boot-starter-parent` (3.3.5), igual que el resto de dependencias de Spring del proyecto. Esta librería trae transitivamente `com.nimbusds:nimbus-jose-jwt`, que se usa tanto en el adaptador (`NimbusJwtDecoder`) como en el test (`SignedJWT`, `JWTClaimsSet`, `RSASSASigner`) — **no hizo falta agregar ninguna dependencia extra para los tests**.

### 2. `Arquitectura-Clean/src/main/java/com/andinaseguros/interfaceadapters/out/security/google/GoogleIdentityVerifierAdapter.java` — NUEVO

Implementa `GoogleIdentityVerifierPort`. Responsabilidades y diseño:

- Recibe por constructor un `org.springframework.security.oauth2.jwt.JwtDecoder` ya construido (no construye él mismo el `NimbusJwtDecoder.withJwkSetUri(...)`) — así el adaptador es agnóstico de si el decoder apunta al JWKS real de Google o a una clave de prueba, lo cual es lo que permite testearlo sin red. La construcción del decoder real (contra `GOOGLE_JWK_SET_URI`) queda para el paso 6 (`UseCaseConfig`), igual que hoy `UseCaseConfig` construye `JwtTokenAdapter` a partir de valores de configuración.
- `jwtDecoder.decode(idToken)` valida **firma** (contra las claves públicas que use el decoder) y, por defecto de Spring Security (`JwtValidators.createDefault()`), también valida **vigencia** (`exp`/`nbf`) automáticamente — no hubo que escribir ese chequeo a mano.
- Después del `decode`, el adaptador valida manualmente lo que el validador por defecto de Spring **no** cubre:
  - `iss` debe ser exactamente el configurado (`https://accounts.google.com`).
  - `aud` debe contener el `clientId` configurado (`GOOGLE_CLIENT_ID`).
  - `sub` debe existir y no estar en blanco.
- Cualquier fallo (firma inválida, `iss`/`aud` incorrectos, vencido, sin `sub`) se señaliza lanzando `org.springframework.security.oauth2.jwt.JwtException` (unchecked). **Decisión importante:** el adaptador no traduce esto a una excepción de dominio (`ReglaNegocioException`) — esa traducción es responsabilidad del caso de uso (paso 5), que puede capturar `RuntimeException` de forma genérica sin necesitar importar ninguna clase de Spring (así `usecases` sigue sin depender de frameworks).
- **No valida `email_verified` aquí.** Por diseño (ver `IMPLEMENTACION-LOGIN-GOOGLE.md` §4.7 y el análisis original §40 "Caso 6"), esa decisión de negocio (rechazar o no un email no verificado) vive en `AutenticarConGoogleUseCase`, no en el adaptador — el adaptador solo *reporta* el valor de `email_verified` dentro del `GoogleIdentity`.
- Se agregó la constante pública `GOOGLE_JWK_SET_URI = "https://www.googleapis.com/oauth2/v3/certs"` en la propia clase del adaptador (y no en `UseCaseConfig`) para mantener este detalle específico de Google localizado junto al resto del conocimiento sobre Google, en vez de esparcido en la configuración genérica de Spring.

### 3. `Arquitectura-Clean/src/test/java/com/andinaseguros/interfaceadapters/out/security/google/GoogleIdentityVerifierAdapterTest.java` — NUEVO

6 casos, todos con **claves RSA generadas localmente en el `@BeforeEach`** (`KeyPairGenerator.getInstance("RSA")`) y tokens firmados a mano con Nimbus (`SignedJWT` + `RSASSASigner`) — **cero llamadas de red a Google**, tal como exige el análisis (§41: "No debemos depender de llamadas reales a Google para todas las pruebas unitarias").

| Test | Qué verifica |
|---|---|
| `verificaUnTokenValidoYExtraeLaIdentidad` | Camino feliz: firma correcta, `iss`/`aud`/`exp` válidos → `GoogleIdentity` con todos los campos mapeados correctamente. |
| `rechazaTokenFirmadoConOtraClavePrivada` | El decoder está configurado con la clave pública "de Google" (simulada); un token firmado con otra clave privada (atacante) debe fallar la verificación de firma. |
| `rechazaAudienceQueNoCoincideConNuestroClientId` | Token válido pero para otro `client_id` → rechazado (evita aceptar tokens emitidos para otra aplicación). |
| `rechazaIssuerQueNoEsDeGoogle` | Token con `iss` distinto de `https://accounts.google.com` → rechazado. |
| `rechazaTokenVencido` | Token con `exp` en el pasado → rechazado por el validador de timestamp por defecto de Spring Security. |
| `noRechazaPorSuCuentaUnEmailNoVerificado` | Confirma que el adaptador **no** rechaza `email_verified = false`; solo lo reporta — la decisión de negocio se probará en el caso de uso (paso 5). |

Estilo del test: JUnit 5 + AssertJ (`assertThat`/`assertThatThrownBy`), igual que el resto de la suite (`EmitirPolizaUseCaseTest`, `AuthControllerTest`). No se usó Mockito aquí porque no hace falta mockear nada: el propio `NimbusJwtDecoder.withPublicKey(...)` real, alimentado con una clave de prueba, es suficiente y más realista que un mock.

---

## Verificación contra reglas ArchUnit

`GoogleIdentityVerifierAdapter` vive en `interfaceadapters.out.security.google` e importa clases de `org.springframework.security.oauth2.jwt`. Se revisó la regla `interface_adapters_do_not_know_frameworks`:

```java
noClasses().that().resideInAPackage("..interfaceadapters..")
        .should().dependOnClassesThat().resideInAPackage("..frameworksdrivers..");
```

Esta regla solo prohíbe depender de `frameworksdrivers`, **no** de Spring en general — de hecho `JwtAuthenticationFilter` y `BCryptPasswordEncoderAdapter` (ya existentes en el mismo paquete `interfaceadapters.out.security`) también dependen de clases de Spring Security. El nuevo adaptador es consistente con ese patrón ya establecido y no viola ninguna regla.

`GoogleIdentityVerifierAdapter` implementa `GoogleIdentityVerifierPort` (que sí vive en `usecases.port.out` y debe ser interfaz — y lo es, sin cambios en este paso).

---

## Impacto

| Área | Impacto |
|---|---|
| `pom.xml` | Nueva dependencia productiva (no solo de test). Aumenta el árbol de dependencias con Nimbus/OAuth2 JOSE, ya gestionado por el BOM de Spring Boot — sin conflictos de versión esperados. |
| `interfaceadapters` | Un archivo nuevo, no modifica ninguno existente. |
| `usecases`/`entities` | Sin impacto — el adaptador no es referenciado todavía por ningún caso de uso (eso ocurre en el paso 5) ni por `UseCaseConfig` (paso 6). |
| Tests | Un archivo de test nuevo, autocontenido, sin tocar la suite existente. |
| Seguridad | Aquí se materializan las validaciones obligatorias del §14 del análisis (firma, `aud`, `iss`, `exp`, `sub`) — la única que falta materializar es `email_verified`, que por diseño se decide en el caso de uso. |

---

## Verificación

⚠️ Sin Maven/Java en este entorno, no se pudo ejecutar `mvn test` para correr estos 6 tests. Se revisó manualmente:

- Todas las clases de Nimbus usadas en el test (`SignedJWT`, `JWTClaimsSet`, `JWSHeader`, `JWSAlgorithm`, `RSASSASigner`) pertenecen a `com.nimbusds:nimbus-jose-jwt`, dependencia transitiva confirmada de `spring-security-oauth2-jose`.
- Las firmas de métodos usadas de Spring Security (`NimbusJwtDecoder.withPublicKey(RSAPublicKey)`, `Jwt.getClaimAsString`, `Jwt.getClaimAsBoolean`, `Jwt.getAudience`, `Jwt.getSubject`) corresponden a la API pública estable de `spring-security-oauth2-jose` en la versión gestionada por Spring Boot 3.3.5.
- Comportamiento por defecto de `NimbusJwtDecoder` (validación automática de `exp`/`nbf` vía `JwtValidators.createDefault()`) confirmado contra la documentación de Spring Security — de ahí que el test de token vencido no necesite ninguna validación adicional en el adaptador.

**Pendiente real y no reemplazable por esta revisión estática:** correr `mvn -q test -Dtest=GoogleIdentityVerifierAdapterTest` en un entorno con JDK 21 + Maven para confirmar que los 6 casos pasan.

---

## Siguiente paso

Paso 5: `GoogleLoginRequestModel` + `AutenticarConGoogleUseCase` (con sus propios tests unitarios), que consumirá `GoogleIdentityVerifierPort` y `UsuarioRepository.buscarPorGoogleSubject`/`buscarPorEmail` del paso 2.
