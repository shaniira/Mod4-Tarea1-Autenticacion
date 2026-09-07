# Paso 3 — `GoogleIdentity` + `GoogleIdentityVerifierPort`

**Fecha:** 2026-09-07
**Depende de:** nada de los pasos anteriores en términos de compilación (son clases nuevas e independientes); conceptualmente es la pieza que usará el caso de uso del paso 5.

---

## Objetivo del paso

Definir, dentro de la capa `usecases`, el contrato con el que el core del sistema hablará de "identidad verificada por Google", sin que el core conozca nada de Google SDK, Nimbus, JWK o HTTP. Esta es la pieza central que permite que la verificación técnica del ID Token quede aislada en un adaptador (paso 4) y sea reemplazable sin tocar casos de uso.

---

## Archivos nuevos

### 1. `Arquitectura-Clean/src/main/java/com/andinaseguros/usecases/port/out/security/GoogleIdentity.java`

```java
package com.andinaseguros.usecases.port.out.security;

public record GoogleIdentity(
        String subject, String email, boolean emailVerified, String name, String picture) {}
```

Modelo de datos puro (record), sin lógica ni dependencias externas. Representa el resultado de una verificación exitosa de un ID Token de Google: `subject` es el claim `sub` (identificador estable de la cuenta Google), `email`/`emailVerified`/`name`/`picture` son los demás claims relevantes del ID Token.

Se ubicó en el mismo paquete que `AuthenticatedUser` y `TokenClaims` (`usecases/port/out/security`) porque cumple el mismo rol: un DTO de salida de un puerto de seguridad, siguiendo el estilo exacto de esas dos clases (record de una línea, sin comentarios, sin getters explícitos).

### 2. `Arquitectura-Clean/src/main/java/com/andinaseguros/usecases/port/out/security/GoogleIdentityVerifierPort.java`

```java
package com.andinaseguros.usecases.port.out.security;

public interface GoogleIdentityVerifierPort {
    GoogleIdentity verificar(String idToken);
}
```

Puerto de salida con un único método. El caso de uso (paso 5) recibirá un `idToken` (String) y obtendrá de vuelta un `GoogleIdentity` ya verificado, sin saber cómo se validó la firma/`aud`/`iss`/`exp`.

---

## Por qué estas clases y no otra forma

- Se evitó modelar `GoogleIdentity` como una clase con setters o builder: el resto de records de este paquete (`AuthenticatedUser`, `TokenClaims`) son inmutables de una línea, y no hay ninguna razón para romper esa consistencia aquí.
- El método se llama `verificar` (no `validar`, `decode`, `parse`) para dejar explícito que su responsabilidad incluye una verificación criptográfica real, no solo una decodificación de Base64 — esto es justamente la distinción de seguridad que se enfatiza en el análisis (`§14` del documento de análisis original): nunca confiar en un token solo por poder leerlo.
- No se lanzó una excepción de dominio propia (`GoogleTokenInvalidoException` o similar) en esta interfaz porque el contrato del puerto no debe imponer un tipo de excepción concreto todavía; se decidirá al implementar el caso de uso (paso 5) qué excepción de `entities.exception` se usa para traducir un fallo de verificación en un error de negocio (`GOOGLE_TOKEN_INVALIDO`), igual que hoy `AutenticarUsuarioUseCase` traduce credenciales inválidas a `ReglaNegocioException`.

---

## Verificación contra las reglas ArchUnit existentes

Se revisó `CleanArchitectureTest.java` para confirmar que el nuevo puerto cumple la regla `output_ports_are_interfaces`:

```java
classes().that(... clazz.getPackageName().contains(".usecases.port.out")
                && (clazz.getSimpleName().endsWith("Port") || clazz.getSimpleName().endsWith("Repository")))
        .should().beInterfaces();
```

`GoogleIdentityVerifierPort` está en `usecases.port.out.security`, termina en `Port` y **es una interfaz** → cumple. `GoogleIdentity` no termina en `Port`/`Repository`, así que la regla no le aplica (correcto, es un DTO, no un puerto).

También se confirmó que ninguna de las dos clases importa nada fuera de `java.lang`/el propio paquete, por lo que tampoco puede violar `use_cases_have_no_framework_dependencies` ni `use_cases_only_point_inward`.

---

## Impacto

| Área                                                                             | Impacto                                                                                         |
| --------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------- |
| `usecases`                                                                      | Dos clases nuevas, sin modificar ninguna existente. Ningún archivo previo cambia en este paso. |
| Casos de uso actuales (`AutenticarUsuarioUseCase`, `RegistrarUsuarioUseCase`) | Ninguno — no referencian estas clases.                                                         |
| ArchUnit                                                                          | Cumple`output_ports_are_interfaces` (verificado manualmente contra la regla).                 |
| Compilación                                                                      | Bajo riesgo: son archivos nuevos y autocontenidos, no tocan código existente.                  |


---

## Siguiente paso

Paso 4: agregar la dependencia Maven (`spring-security-oauth2-jose`) y crear `GoogleIdentityVerifierAdapter` en `interfaceadapters/out/security/google/`, con tests usando claves controladas (no llamadas reales a Google).
