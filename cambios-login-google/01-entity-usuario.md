# Paso 1 — Evolucionar `Usuario` (entity) y actualizar call sites

**Fecha:** 2026-09-07
**Rama/estado:** cambios directos sobre working tree, sin commit todavía (pendiente de tu revisión).

---

## Objetivo del paso

Preparar la entidad de dominio `Usuario` para soportar identidad federada (Google), agregando `email` y `googleSubject`, sin romper ninguno de los usos existentes del constructor.

---

## Archivos modificados

### 1. `Arquitectura-Clean/src/main/java/com/andinaseguros/entities/model/Usuario.java`

**Antes:** 5 campos — `id, username, passwordHash, rol, activo`.

**Después:** 7 campos — `id, username, email, passwordHash, googleSubject, rol, activo`.

Cambios concretos:

- Nuevos campos `private final String email;` y `private final String googleSubject;`.
- Constructor ampliado a 7 parámetros, en el orden: `(UUID id, String username, String email, String passwordHash, String googleSubject, RolUsuario rol, boolean activo)`.
- Nuevos getters `getEmail()` y `getGoogleSubject()`.
- `passwordHash` sigue siendo un `String` normal (ya podía ser cualquier valor); a partir de ahora se admite semánticamente `null` para usuarios que solo se autentican con Google. La clase no valida nulidad en el constructor (igual que antes), esa validación vive en los casos de uso (ver paso 7).
- Se mantiene inmutable: sin setters, todo `final`, mismo estilo que el resto de `entities/model/*`.

No se agregó ninguna dependencia externa ni import nuevo — sigue sin conocer Spring, Mongo ni Google (cumple la regla de ArchUnit "entities no dependen de frameworks").

### 2. `Arquitectura-Clean/src/main/java/com/andinaseguros/usecases/service/auth/RegistrarUsuarioUseCase.java`

Único cambio: la construcción de `Usuario` en `execute(...)` ahora pasa `null` en `email` y `null` en `googleSubject`, ya que el registro local actual no captura email por separado (sigue igual que antes del cambio) y nunca setea `googleSubject`.

```diff
  usuarios.guardar(
          new Usuario(
                  ids.generar(),
                  solicitud.username(),
+                 null,
                  passwordEncoder.codificar(solicitud.password()),
+                 null,
                  solicitud.rol(),
                  true));
```

### 3. `Arquitectura-Clean/src/main/java/com/andinaseguros/frameworksdrivers/configuration/spring/MongoDemoDataInitializer.java`

Único cambio: el usuario demo `admin` creado en `seedAdmin()` ahora pasa `null`/`null` en las mismas dos posiciones nuevas. Sigue sin `email` ni `googleSubject` — es un usuario 100% local.

### 4. `Arquitectura-Clean/src/main/java/com/andinaseguros/interfaceadapters/out/persistence/mongodb/mapper/UsuarioMongoMapper.java`

Único cambio en `toDomain(...)`: se agregó `d.email` (el campo **ya existía** en `UsuarioDocument`, solo faltaba pasarlo al dominio) y `null` como placeholder de `googleSubject` (el campo `googleSubject` **todavía no existe** en `UsuarioDocument` — se agrega recién en el paso 2, junto con el resto de la persistencia Mongo).

```diff
  public Usuario toDomain(UsuarioDocument d) {
-     return new Usuario(UUID.fromString(d.id), d.username, d.passwordHash, d.rol, d.activo);
+     return new Usuario(
+             UUID.fromString(d.id), d.username, d.email, d.passwordHash, null, d.rol, d.activo);
  }
```

**Nota importante:** `toDocument(...)` de este mismo mapper **no se tocó** en este paso. Sigue con el bug ya documentado (`d.email = x.getUsername().contains("@") ? x.getUsername() : null`) porque su corrección definitiva (mapear `email`/`googleSubject` directo desde el dominio en ambas direcciones) está planificada explícitamente para el **paso 2** (persistencia Mongo), para no mezclar la evolución del dominio con la evolución de la persistencia en un mismo commit.

---

## Búsqueda de impacto realizada

Se buscó exhaustivamente cualquier construcción `new Usuario(...)` en todo `Arquitectura-Clean/` (main y test):

```
Arquitectura-Clean/.../MongoDemoDataInitializer.java   → actualizado
Arquitectura-Clean/.../RegistrarUsuarioUseCase.java    → actualizado
Arquitectura-Clean/.../UsuarioMongoMapper.java          → actualizado
```

No se encontró ninguna otra construcción del constructor en `src/test/**` (incluidos `AuthControllerTest.java` y `DemoCredentialsTest.java`, que usan `Usuario` solo indirectamente vía mocks de `RegistrarUsuarioUseCase`/`AutenticarUsuarioUseCase`, no construyen la entidad directamente).

---

## Impacto

| Área                                                                                | Impacto                                                                                                                                                                                          |
| ------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `entities`                                                                         | Cambio de forma del agregado`Usuario`; sigue sin conocer frameworks.                                                                                                                           |
| `usecases` (`RegistrarUsuarioUseCase`)                                           | Cambio mecánico de 2 parámetros extra en`null`; comportamiento funcional idéntico al de antes del cambio.                                                                                   |
| `frameworksdrivers` (`MongoDemoDataInitializer`)                                 | Igual, cambio mecánico, comportamiento idéntico.                                                                                                                                               |
| `interfaceadapters` (`UsuarioMongoMapper.toDomain`)                              | Ahora el dominio recibe el`email` real que ya estaba en Mongo (antes se perdía silenciosamente al reconstruir el objeto de dominio). `googleSubject` queda en `null` hasta el paso 2.     |
| Otros casos de uso (`AutenticarUsuarioUseCase`, `JwtAuthenticationFilter`, etc.) | **Sin cambios ni impacto** — no construyen `Usuario`, solo llaman a sus getters existentes (`getUsername`, `getPasswordHash`, `getRol`, `isActivo`), que no cambiaron de firma. |
| Tests existentes                                                                     | No requieren cambios: ninguno construye`Usuario` directamente.                                                                                                                                 |
| ArchUnit / Clean Architecture                                                        | Sin impacto: no se introdujo ningún import nuevo hacia frameworks en`entities` ni en `usecases`.                                                                                            |

## Siguiente paso

Paso 2: evolucionar `UsuarioDocument`, `SpringDataUsuarioMongoRepository`, `UsuarioMongoRepositoryAdapter` y corregir `UsuarioMongoMapper.toDocument(...)` para persistir `googleSubject` y dejar de inferir `email` desde `username`.
