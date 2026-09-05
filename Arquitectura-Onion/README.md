# Andina Seguros — Arquitectura Onion

Proyecto Maven monomódulo con un solo `src`. La arquitectura se distingue por la propiedad de las abstracciones internas.

```text
com.andinaseguros/
├── domain/
│   ├── model/ valueobject/ enums/ event/ exception/ service/
│   └── repository/              # interfaces de repositorio (contratos de persistencia)
├── application/
│   ├── service/                 # casos de uso
│   ├── dto/                     # un archivo por Dto de entrada (p. ej. CrearClienteDto) + Responses,
│   │                             # sin anotaciones de framework
│   └── gateway/                 # interfaces de servicios externos (Port) y sus modelos de datos
├── infrastructure/              # implementaciones: persistence/adapter, external (JSON.pe), notification,
│                                 # security, id, time, event, config — MongoDB, WhatsApp, JWT, configuración
└── presentation/
    ├── controller/               # controladores REST
    ├── request/                  # un archivo por Request HTTP (p. ej. CrearClienteRequest) con validación (jakarta.validation)
    ├── mapper/                   # RestRequestMapper: *Request -> *Dto
    └── response/                 # DTOs de salida HTTP
```

Regla: las dependencias apuntan hacia `domain`. Onion no utiliza paquetes `port` (a diferencia de Hexagonal, no hay un paquete `ports/in`/`ports/out` separado del dominio/aplicación); sí existe el paquete `infrastructure.persistence.adapter`, ya que "adapter" ahí describe la implementación externa de un contrato, no un concepto propio del vocabulario hexagonal. Los repositorios pertenecen al dominio (`domain.repository`) y los gateways requeridos por la aplicación pertenecen a `application.gateway` (interfaz `*Port` + sus modelos de datos, p. ej. `VehicleInformationPort` y `VehicleInformation`). Las implementaciones de ambos tipos de contrato están siempre en `infrastructure`.

## Validación de entrada HTTP

El dato de entrada de cada caso de uso se llama **Dto** (p. ej. `CrearClienteDto`, `EmitirPolizaDto`) y vive en `application.dto`, un archivo por operación — son POJOs puros, sin anotaciones `jakarta.validation`, porque `application` no debe depender de una API de framework. Se usa el sufijo `Dto` (y no `Command`, tomado de Hexagonal) para mantener el vocabulario propio de esta implementación de Onion.

La validación de las peticiones HTTP vive exclusivamente en `presentation.request`, con un **Request** por operación (p. ej. `CrearClienteRequest`, anotado con `jakarta.validation.constraints`). Los controllers reciben el `*Request` con `@Valid` y lo convierten al `*Dto` correspondiente mediante `RestRequestMapper.toApplication(...)` antes de invocar el caso de uso. Mantener nombres distintos entre ambas capas (`Request` vs `Dto`) evita la ambigüedad de tener el mismo nombre de clase en dos paquetes distintos.

Validación estructural: `OnionArchitectureTest` (ArchUnit) comprueba independencia del dominio, dirección de `application` (incluida la ausencia de dependencias de framework: Spring, JPA, Bean Validation, Mongo), que los contratos de `domain.repository` y `application.gateway.*Port` sean interfaces, que sus implementaciones vivan únicamente en `infrastructure`, ausencia de paquetes `port` y ausencia de dependencias de framework en `domain`.
