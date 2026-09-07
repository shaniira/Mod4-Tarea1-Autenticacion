# Paso 2 — Persistencia Mongo (`UsuarioDocument`, `SpringDataUsuarioMongoRepository`, `UsuarioMongoRepositoryAdapter`, `UsuarioMongoMapper`)

**Fecha:** 2026-09-07
**Depende de:** [01-entity-usuario.md](01-entity-usuario.md) (requiere que `Usuario` ya tenga `email`/`googleSubject`).

---

## Objetivo del paso

Que Mongo pueda persistir y consultar por `googleSubject`, y que el mapper deje de inferir `email` desde `username` (bug detectado en el análisis previo) para mapear `email`/`googleSubject` directamente desde/hacia el dominio.

---

## Archivos modificados

### 1. `interfaceadapters/out/persistence/mongodb/document/UsuarioDocument.java`

Se agregó el campo `googleSubject`, indexado igual que `email` (único y disperso, para no romper documentos de usuarios locales que nunca tendrán este campo):

```diff
  public String passwordHash;
+
+ @Indexed(unique = true, sparse = true)
+ public String googleSubject;
+
  public RolUsuario rol;
  public boolean activo;
```

### 2. `usecases/port/out/repository/UsuarioRepository.java` (puerto de salida)

**Nota:** este archivo no estaba listado de forma explícita en el paso 2 del plan, pero es un requisito de Clean Architecture: el adaptador Mongo no puede exponer métodos nuevos si el puerto que implementa (la abstracción que conocen los casos de uso) no los declara primero. Se agregó:

```diff
  Optional<Usuario> buscarPorUsername(String username);
+
+ Optional<Usuario> buscarPorEmail(String email);
+
+ Optional<Usuario> buscarPorGoogleSubject(String googleSubject);
```

Sigue siendo una interfaz pura de `usecases`, sin ningún import de Mongo/Spring.

### 3. `interfaceadapters/out/persistence/mongodb/repository/SpringDataUsuarioMongoRepository.java`

Se agregaron los métodos derivados de Spring Data (naming convention, sin necesidad de `@Query`):

```diff
  Optional<UsuarioDocument> findByUsername(String username);
+
+ Optional<UsuarioDocument> findByEmail(String email);
+
+ Optional<UsuarioDocument> findByGoogleSubject(String googleSubject);
```

### 4. `interfaceadapters/out/persistence/mongodb/adapter/UsuarioMongoRepositoryAdapter.java`

Se implementaron los dos métodos nuevos del puerto, delegando al repositorio Spring Data y reutilizando el mapper existente, igual que `buscarPorUsername`:

```java
public Optional<Usuario> buscarPorEmail(String x) {
    return repo.findByEmail(x).map(mapper::toDomain);
}

public Optional<Usuario> buscarPorGoogleSubject(String x) {
    return repo.findByGoogleSubject(x).map(mapper::toDomain);
}
```

### 5. `interfaceadapters/out/persistence/mongodb/mapper/UsuarioMongoMapper.java` — corrección del bug documentado

**Antes** (`toDocument`):
```java
d.email = x.getUsername().contains("@") ? x.getUsername() : null;
```
Esto **ignoraba** cualquier valor real de `email` del dominio y lo adivinaba a partir de `username`. Ahora que `Usuario` tiene su propio campo `email` (paso 1), esa inferencia ya no tiene sentido y quedaba inconsistente.

**Después:**
```java
d.email = x.getEmail();
d.googleSubject = x.getGoogleSubject();
```

**`toDomain`** ahora lee `d.googleSubject` en vez del `null` fijo que se dejó temporalmente en el paso 1:
```diff
  return new Usuario(
          UUID.fromString(d.id),
          d.username,
          d.email,
-         d.passwordHash, null, d.rol, d.activo);
+         d.passwordHash,
+         d.googleSubject,
+         d.rol,
+         d.activo);
```

### 6. `frameworksdrivers/configuration/persistence/MongoIndexConfiguration.java` — cambio adicional no listado en el plan original

Este archivo ya existía y asegura **explícitamente** (vía `ApplicationRunner` + `MongoTemplate`) el índice único+disperso de `email`, además de la anotación `@Indexed` del documento — es el patrón ya establecido en el proyecto para los campos opcionales-únicos de `UsuarioDocument`. Para mantener consistencia, se replicó el mismo patrón para `googleSubject`:

```java
mongo.indexOps(UsuarioDocument.class)
        .ensureIndex(
                new Index()
                        .on("googleSubject", Sort.Direction.ASC)
                        .named("googleSubject")
                        .unique()
                        .sparse());
```

Sin este cambio, el índice de `googleSubject` habría dependido únicamente de `auto-index-creation: true` en `application.yml`; se prefirió no dejarlo solo a esa configuración implícita ya que el propio proyecto no confía en ella para `email` (evidencia: la creación explícita ya existente).

---

## Búsqueda de impacto realizada

- `implements UsuarioRepository` → solo `UsuarioMongoRepositoryAdapter` (ya actualizado). No hay otras implementaciones ni fakes manuales del puerto en `src/test`.
- `UsuarioMongoMapper|UsuarioDocument|UsuarioMongoRepositoryAdapter` en `src/test` → **ninguna coincidencia**; no existen tests unitarios/integración de estas clases hoy, por lo que no hay nada que actualizar en este paso (una oportunidad para el paso 8, al correr la suite completa).

---

## Impacto

| Área | Impacto |
|---|---|
| `usecases/port/out/repository/UsuarioRepository` | Puerto ampliado con 2 métodos nuevos. Cualquier futura implementación (o mock estricto) del puerto deberá cubrirlos. |
| Mongo (`UsuarioDocument`) | Nuevo campo `googleSubject`, índice único+sparse — no afecta documentos existentes (sparse permite ausencia del campo). |
| `UsuarioMongoMapper.toDocument` | **Cambio de comportamiento real:** antes se perdía cualquier `email` explícito del dominio (no existía en el dominio) y se inventaba desde `username`; ahora se persiste el `email` real del dominio. Como en el paso 1 `RegistrarUsuarioUseCase` sigue pasando `email = null`, el comportamiento observable de `/register` **no cambia todavía** (seguirá guardando `email = null` para altas locales) hasta que se decida capturar email en el registro local. |
| Índices Mongo | Se agrega `googleSubject` a la creación explícita de índices, igual patrón que `email`. |
| ArchUnit / Clean Architecture | Sin impacto: los nuevos métodos del puerto son interfaces puras; la implementación queda en `interfaceadapters`. |
| Casos de uso existentes (`AutenticarUsuarioUseCase`, `RegistrarUsuarioUseCase`, filtro JWT) | Sin cambios — no usan los métodos nuevos todavía (se consumirán recién en el paso 5, `AutenticarConGoogleUseCase`). |

---

## Verificación

⚠️ Igual que en el paso 1, **no hay Maven/Java disponibles en este entorno** para correr `mvn compile`/`mvn test`. Verificación hecha manualmente:

- Todos los métodos añadidos al puerto (`UsuarioRepository`) tienen implementación correspondiente en el único adaptador que lo implementa.
- Los nombres de método en `SpringDataUsuarioMongoRepository` (`findByEmail`, `findByGoogleSubject`) siguen la convención de Spring Data y coinciden exactamente con los nombres de campo de `UsuarioDocument` (`email`, `googleSubject`).
- El mapper usa getters que ya existen en `Usuario` desde el paso 1 (`getEmail()`, `getGoogleSubject()`).

**Pendiente:** correr `mvn -q compile` en un entorno con JDK/Maven para confirmar antes de continuar.

---

## Siguiente paso

Paso 3: crear `GoogleIdentity` (record) y `GoogleIdentityVerifierPort` en `usecases/port/out/security/`.
