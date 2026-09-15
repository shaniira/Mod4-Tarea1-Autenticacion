---
marp: true
theme: default
_class: lead
paginate: true
---

# Autenticacion con Facebook

## Integracion OAuth 2.0 en Andina Seguros

Validacion de identidad, proteccion de tokens y emision de sesion propia.

---

# Objetivo de la integracion

- Permitir el ingreso con una cuenta de Facebook.
- Delegar autenticacion y consentimiento en Facebook.
- Validar la respuesta en el backend antes de crear una sesion local.
- Crear o actualizar el usuario de Andina Seguros.
- Entregar un JWT propio, nunca un JWT emitido por Facebook.

---

# Principio del flujo

## OAuth 2.0 Authorization Code

- El navegador se redirige al dialogo de autorizacion de Facebook.
- Facebook devuelve un `code` al callback registrado.
- Solo el backend canjea el `code` usando el secreto de la aplicacion.
- El navegador no envia directamente un token de Facebook al backend.

---

# Componentes principales

- `AuthController`: expone los endpoints de inicio, callback y sesion.
- `AutenticarConFacebookUseCase`: coordina las validaciones y la sesion.
- `FacebookOAuthPort`: contrato que aisla al caso de uso del proveedor externo.
- `FacebookOAuthAdapter`: implementa la comunicacion con Facebook Graph API.
- `FacebookProperties`: concentra URLs, permisos, TTL y secretos configurables.

---

# Control del state OAuth

- `OAuthStatePort` define la creacion y consumo del parametro `state`.
- `MongoDBOAuthStateAdapter` es la implementacion usada por la aplicacion.
- Genera 32 bytes aleatorios con `SecureRandom`.
- El valor tiene tiempo de vida configurable.
- Se elimina en el primer uso: no puede reutilizarse.

**Resultado:** se bloquean callbacks falsificados, vencidos o repetidos.

---

# Validacion con Facebook

1. Se construye la URL OAuth con `client_id`, `redirect_uri`, `state` y permisos.
2. Facebook autentica al usuario y solicita consentimiento.
3. El callback recibe `code` y `state`.
4. El backend consume y valida el `state`.
5. Se canjea el `code` en `/oauth/access_token`.
6. Se consultan `/me` y `/me/permissions` en Graph API.

---

# Diagrama de interaccion

```text
Usuario -> Backend: GET /api/auth/facebook
Backend -> MongoDB: crear state con TTL
Backend -> Facebook: redireccion OAuth
Facebook -> Usuario: autenticacion y consentimiento
Facebook -> Backend: callback con code y state
Backend -> MongoDB: consumir state una sola vez
Backend -> Facebook Graph API: token, perfil y permisos
Backend -> Base de datos: crear o actualizar usuario
Backend -> Frontend: redireccion con ticket temporal
Frontend -> Backend: canjear ticket por JWT
```

---

# Usuario y sesion local

- Se busca al usuario por proveedor `FACEBOOK` e identificador de Facebook.
- Si no existe, se crea la cuenta local `facebook_{id}`.
- Se actualizan email, nombre, permisos y vencimiento cuando corresponda.
- El `JwtTokenAdapter` genera el JWT de Andina Seguros.
- El JWT contiene usuario, rol, fecha de emision y expiracion.

---

# Proteccion del access token

## AES-256-GCM

- El token de Facebook se cifra antes de persistirse.
- Se usa una llave de 32 bytes desde `FACEBOOK_TOKEN_ENCRYPTION_KEY`.
- Cada cifrado emplea un IV aleatorio de 96 bits.
- GCM incorpora una etiqueta de autenticacion de 128 bits.
- La etiqueta permite detectar alteraciones del dato cifrado.

---

# Entrega segura de la sesion

- El JWT no se incluye en la URL de redireccion al frontend.
- `LoginTicketPort` crea un ticket temporal de 32 bytes aleatorios.
- El ticket se codifica en Base64 URL-safe y tiene TTL configurable.
- `InMemoryLoginTicketAdapter` lo elimina al canjearlo.
- El frontend usa `POST /api/auth/facebook/session` para obtener el JWT.

**Base64 codifica datos; no los cifra.**

---

# Seguridad y configuracion

- HTTPS protege el trafico con Facebook y debe mantenerse en produccion.
- `FACEBOOK_APP_SECRET` se usa solo en el backend y debe ser un secreto de entorno.
- `FACEBOOK_REDIRECT_URI` debe coincidir exactamente con la registrada en Facebook.
- Permisos minimos solicitados: `public_profile,email`.
- `JWT_SECRET` debe ser independiente de la llave de cifrado del token Facebook.
- Los errores controlados cubren rechazo, callback invalido, permisos, configuracion y ticket vencido.

---

# Mensajes clave

- Facebook confirma identidad y consentimiento; el backend decide si crea la sesion.
- El `state` de un solo uso protege el retorno OAuth.
- El token de Facebook se protege en reposo con AES-256-GCM.
- El ticket temporal evita filtrar el JWT en la URL.
- La aplicacion mantiene control sobre sus usuarios, roles y JWT.