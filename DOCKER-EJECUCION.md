# Ejecución Docker — tres arquitecturas

Las tres aplicaciones utilizan el puerto interno `8080`, pero Docker publica un puerto distinto para cada arquitectura. Cada una tiene MongoDB, base de datos, volumen y red independientes.

| Arquitectura | API | Swagger | MongoDB host | Base de datos |
|---|---|---|---|---|
| Onion | http://localhost:8081 | http://localhost:8081/swagger-ui.html | localhost:27018 | andina_seguros_onion |
| Hexagonal | http://localhost:8082 | http://localhost:8082/swagger-ui.html | localhost:27019 | andina_seguros_hexagonal |
| Clean | http://localhost:8083 | http://localhost:8083/swagger-ui.html | localhost:27020 | andina_seguros_clean |

## Levantar las tres juntas

Ejecutar desde la carpeta `Arqutiecturas`:

```bash
docker compose up -d --build
docker compose ps
```

Ver logs:

```bash
docker compose logs -f onion-backend hexagonal-backend clean-backend
```

Detener sin borrar datos:

```bash
docker compose down
```

## Levantar una arquitectura individual

Entrar en `Arquitectura-Onion`, `Arquitectura-Hexagonal` o `Arquitectura-Clean` y ejecutar:

```bash
docker compose up -d --build
```

Los Compose individuales conservan los mismos puertos de la tabla.

## Aislamiento

- Onion: `andina_onion_network` y `andina_onion_mongo_data`.
- Hexagonal: `andina_hexagonal_network` y `andina_hexagonal_mongo_data`.
- Clean: `andina_clean_network` y `andina_clean_mongo_data`.

No se debe ejecutar al mismo tiempo el Compose global y un Compose individual de la misma arquitectura porque comparten nombres y puertos deliberadamente.