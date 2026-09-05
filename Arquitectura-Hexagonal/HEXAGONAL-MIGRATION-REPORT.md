# Reporte de reorganización hexagonal

## Estructura final

```text
com/andinaseguros/
├── core/
│   ├── domain/{model,valueobject,enums,event,exception,service}
│   ├── application/{service,dto,mapper,exception}
│   └── ports/{in,out}
├── adapters/
│   ├── inbound/rest/{controller,request,response,mapper,exception}
│   └── outbound/{persistence,security,event,notification,external,clock,id}
└── bootstrap
```

## Cambios aplicados

- `domain` se movió a `core/domain`.
- Los servicios, DTO, mappers y excepciones de aplicación se movieron a `core/application`.
- `application/port/in` se movió a `core/ports/in`.
- `application/port/out` se movió a `core/ports/out`.
- `configuration` se movió a `bootstrap`.
- Los requests REST con Jakarta Validation quedaron en `adapters/inbound/rest/request`.
- Los DTO internos del core quedaron libres de anotaciones de frameworks.
- Las pruebas de dominio y aplicación se movieron a los paquetes equivalentes bajo `core`.

## Reglas verificadas

- No quedan paquetes Java heredados `com.andinaseguros.domain`, `com.andinaseguros.application` ni `com.andinaseguros.configuration`.
- No se encontraron imports de Spring, MongoDB, Jakarta, JWT, Jackson o Springdoc dentro de `core`.
- Los imports internos apuntan a clases existentes.
- ArchUnit comprueba la independencia del core, del dominio y de los puertos.

La ejecución automática de Maven queda pendiente en este entorno porque el comando `mvn` no está instalado.
