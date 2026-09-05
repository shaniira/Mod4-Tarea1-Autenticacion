# Andina Seguros — Arquitectura Hexagonal

Proyecto Java/Spring Boot de módulo único. El core contiene el dominio, los casos de uso y sus puertos; adapters y bootstrap quedan fuera del hexágono.

```text
com.andinaseguros/
├── core/
│   ├── domain/{model,valueobject,enums,event,exception,service}/
│   ├── application/{service,dto,mapper,exception}/
│   └── ports/
│       ├── in/{auth,cliente,vehiculo,cotizacion,poliza,siniestro,renovacion,tarifa}/
│       └── out/{persistence,security,external,notification,event,clock,id}/
├── adapters/
│   ├── inbound/rest/{controller,request,response,mapper,exception}/
│   └── outbound/{persistence/mongo,external,notification,security,event,clock,id}/
└── bootstrap/
    ├── AndinaSegurosApplication.java
    ├── UseCaseConfiguration.java
    ├── SecurityConfig.java
    ├── MongoConfiguration.java
    └── configuración técnica adicional
```

## Reglas de dependencia

- `core.domain` contiene las entidades, objetos de valor y reglas de negocio.
- `core.application` implementa los casos de uso sin conocer adapters ni frameworks.
- `core.ports.in` declara las operaciones ofrecidas por el sistema.
- `core.ports.out` declara las necesidades externas del core.
- `adapters.inbound` implementa entradas como REST.
- `adapters.outbound` implementa persistencia, seguridad, eventos y servicios externos.
- `bootstrap` ensambla las implementaciones mediante inyección de dependencias y puede depender de todas las capas.
- Ninguna clase de `core` depende de Spring, MongoDB, HTTP, JWT, Jakarta, adapters o bootstrap.

## Validación de entrada HTTP

Siguiendo el vocabulario habitual de Ports & Adapters, el dato de entrada de cada caso de uso se llama **Command** (p. ej. `CrearClienteCommand`, `EmitirPolizaCommand`) y vive en `core.application.dto`, un archivo por operación — son objetos Java puros, sin anotaciones `jakarta.validation`, porque el core no debe depender de una API de framework.

La validación de las peticiones HTTP vive exclusivamente en `adapters.inbound.rest.request`, con un **Request** por operación (p. ej. `CrearClienteRequest`, anotado con `jakarta.validation.constraints`). Los controllers reciben el `*Request` con `@Valid` y lo convierten al `*Command` correspondiente mediante `RestRequestMapper.toCore(...)` antes de invocar el puerto de entrada. Mantener nombres distintos entre ambas capas (`Request` vs `Command`) evita la ambigüedad de tener el mismo nombre de clase en dos paquetes distintos.

## Validación estructural

`HexagonalArchitectureTest` (ArchUnit) comprueba: independencia de framework de `core`; que `core.domain` no dependa de `core.application`/`core.ports`; ausencia de un paquete `core.domain.repository`; que todo `*RepositoryPort` y, en general, todo puerto (`*Port`) de `core.ports.out` sea una interfaz; que todo puerto de `core.ports.in` sea una interfaz; que las implementaciones de los puertos de salida (`*Port`) vivan únicamente en `adapters`; que las implementaciones de los puertos de entrada (casos de uso) vivan únicamente en `core.application`; y que toda clase `*Controller` resida en `adapters.inbound.rest.controller`.
