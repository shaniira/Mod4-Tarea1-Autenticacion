# Paso 8 (adaptado) — Verificación completa vía Docker (sin Maven/JDK local)

**Fecha:** 2026-09-07
**Depende de:** pasos 1 a 7 (todo el backend del login con Google).

---

## Cambio respecto al plan original

El plan original (`IMPLEMENTACION-LOGIN-GOOGLE.md`, orden recomendado, paso 8) decía "Backend: `mvn test` completo (incluye `CleanArchitectureTest`)". Este entorno de trabajo **no tiene Maven ni JDK instalados**, solo Docker. `IMPLEMENTACION-LOGIN-GOOGLE.md` se actualizó en 4 puntos para reflejarlo:

- Sección "Orden recomendado de implementación", paso 8.
- Nota sobre verificación de compilación (constructor de `Usuario`).
- Sección "Plan de pruebas" (regresión backend).
- Sección "Criterios de aceptación" (`mvn test` / `CleanArchitectureTest`).

En los cuatro casos se dejó explícito que la verificación real en este proyecto se hace con `docker build` sobre `Arquitectura-Clean/Dockerfile`, no con `mvn` directamente en el host.

---

## Por qué `docker build` es equivalente a `mvn test` aquí

`Arquitectura-Clean/Dockerfile` ya tenía, desde antes de esta funcionalidad, una etapa de build que ejecuta:

```dockerfile
RUN mvn clean package \
      --no-transfer-progress \
      -Dstyle.color=never \
      > /tmp/maven-build.log 2>&1 \
    && grep -E "Tests run:|BUILD SUCCESS" /tmp/maven-build.log \
    || (cat /tmp/maven-build.log && exit 1)
```

Es decir: **las pruebas y las reglas ArchUnit ya eran parte obligatoria del build de la imagen** (comentario textual del propio Dockerfile: "Las pruebas y reglas ArchUnit forman parte obligatoria del build de la imagen"). `mvn clean package` ejecuta el ciclo de vida de Maven hasta la fase `package`, lo que incluye `compile` y `test` — exactamente lo que pedía el paso 8 original. Si cualquier test falla, `grep` no encuentra `BUILD SUCCESS`, se hace `cat` del log completo y el build de Docker termina con `exit 1` (falla visible, no silenciosa).

**Lo único que `mvn clean package` no ejecuta es el chequeo de formato de Spotless** (`spotless-maven-plugin`, bindeado a la fase `verify`, posterior a `package`) — pero eso es una verificación de estilo de código, no de correctitud funcional ni arquitectónica, y no forma parte de lo que el paso 8 buscaba confirmar.

---

## Comando ejecutado

```bash
cd Arquitectura-Clean
docker build -f Dockerfile -t andina-seguros-clean:test-paso8 .
```

## Resultado

```
BUILD SUCCESS
Tests run: 49, Failures: 0, Errors: 0, Skipped: 0
```

Desglose relevante de la salida (`Tests run:` por clase):

| Clase de test | Resultado |
|---|---|
| `DemoCredentialsTest` | 1/1 ✅ |
| `GoogleIdentityVerifierAdapterTest` (paso 4) | 6/6 ✅ |
| `PolizaEmitidaNotificationHandlerTest` | 2/2 ✅ |
| `VehicleInformationControllerTest` | 1/1 ✅ |
| `AuthControllerTest` (paso 6, con el endpoint `/google`) | 1/1 ✅ |
| `GlobalExceptionHandlerTest` (paso 6, con los 2 casos nuevos) | 3/3 ✅ |
| `MotorDeTarificacionTest` | 1/1 ✅ |
| `EvaluadorRenovacionTest` | 1/1 ✅ |
| `PropuestaRenovacionTest` | 3/3 ✅ |
| `SiniestroTest` | 1/1 ✅ |
| `ConsultarInformacionVehiculoServiceTest` | 4/4 ✅ |
| `AutenticarConGoogleUseCaseTest` (paso 5) | 6/6 ✅ |
| `AutenticarUsuarioUseCaseTest` (paso 7, con el caso `passwordHash == null`) | 5/5 ✅ |
| `ListarCotizacionesPendientesEmisionUseCaseTest` | 1/1 ✅ |
| `ListarCotizacionesUseCaseTest` | 3/3 ✅ |
| `EmitirPolizaUseCaseTest` | 2/2 ✅ |
| **`CleanArchitectureTest`** (ArchUnit) | **8/8 ✅** |

El resultado de `CleanArchitectureTest` es la confirmación empírica (no solo revisión manual) de que:

- `GoogleIdentity`/`GoogleIdentityVerifierPort` (paso 3) cumplen `output_ports_are_interfaces`.
- `GoogleIdentityVerifierAdapter` (paso 4, que sí depende de Spring Security/Nimbus) **no** viola `interface_adapters_do_not_know_frameworks` (esa regla solo prohíbe depender de `frameworksdrivers`, no de Spring en general).
- `AutenticarConGoogleUseCase` (paso 5) **no** viola `use_cases_have_no_framework_dependencies` ni `use_cases_only_point_inward` — es decir, ninguna clase de Google SDK/Nimbus/Spring Security se filtró a la capa `usecases`, que era el riesgo principal que este análisis buscaba prevenir desde el diseño original.

Esto también confirma retroactivamente que las revisiones manuales de los pasos 1 a 7 (hechas sin poder compilar) fueron correctas: **cero errores de compilación** en los 8 archivos nuevos y los ~14 archivos modificados hasta este punto.

---

## Limpieza

La imagen `andina-seguros-clean:test-paso8` se eliminó (`docker rmi`) al terminar la verificación — era solo para confirmar el build, no forma parte del flujo de despliegue normal (para eso se usa `docker compose up --build` sobre `docker-compose.yml`, con la etiqueta `andina-seguros-clean:1.0.0` ya definida ahí).

---

## Impacto

| Área | Impacto |
|---|---|
| `IMPLEMENTACION-LOGIN-GOOGLE.md` | 4 secciones actualizadas para reflejar Docker como mecanismo de verificación real en este entorno, en vez de `mvn` local. |
| Confianza en pasos 1-7 | Alta: no quedan pendientes de "revisión manual sin compilar" — todo el código de backend del login con Google (pasos 1 a 7) está confirmado compilando y pasando tests. |
| Frontend | Sin impacto — este paso es exclusivamente backend. |

---

## Siguiente paso

Paso 9: configuración en Google Cloud Console (proyecto, branding, audience, cliente OAuth Web, `Authorized JavaScript origin` `http://localhost:5173`, obtener el `Client ID` real) — es manual, fuera del código, y es un prerrequisito para poder probar el flujo end-to-end real en el paso 11.
