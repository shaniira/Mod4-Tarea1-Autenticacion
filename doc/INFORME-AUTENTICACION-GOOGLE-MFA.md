# Informe didáctico: autenticación con Google y MFA

## 1. Propósito

Este documento explica cómo funciona la autenticación del proyecto Andina Seguros. Está dirigido a personas que recién comienzan con seguridad de aplicaciones, pero también introduce los términos técnicos necesarios para comprender y mantener la solución.

El sistema ofrece dos formas de ingreso:

1. **Login social con Google:** el usuario demuestra su identidad mediante una cuenta de Google.
2. **Login local con usuario y contraseña:** si el usuario activó MFA, debe ingresar además un código de Google Authenticator.

> Decisión del proyecto: el MFA propio se solicita únicamente en el login local. El login con Google entrega la sesión directamente y no solicita el código TOTP de la aplicación.

---

## 2. Conceptos fundamentales

### 2.1 Autenticación y autorización

Aunque suelen confundirse, son procesos distintos:

- **Autenticación:** comprueba quién es el usuario. Ejemplos: contraseña, cuenta de Google o código TOTP.
- **Autorización:** determina qué acciones puede realizar el usuario autenticado según su rol.

```mermaid
flowchart LR
    A[Usuario] --> B[Autenticación]
    B --> C{Identidad válida?}
    C -- No --> D[Acceso rechazado]
    C -- Sí --> E[Autorización por rol]
    E --> F[Recursos permitidos]
```

Los roles actuales son:

| Rol | Responsabilidad principal |
|---|---|
| `ADMIN` | Administración general |
| `AGENTE` | Gestión de clientes, cotizaciones y pólizas |
| `ACTUARIO` | Gestión de tarifas |
| `CLIENTE` | Consulta de su propia cuenta |

### 2.2 Factores de autenticación

Un factor es una prueba utilizada para demostrar la identidad:

- **Algo que sabes:** contraseña o PIN.
- **Algo que tienes:** teléfono con Google Authenticator.
- **Algo que eres:** huella digital o reconocimiento facial.

El login local con MFA combina dos factores:

```text
Contraseña                    Google Authenticator
(algo que sabes)       +      (algo que tienes)
       Factor 1                       Factor 2
```

### 2.3 OAuth 2.0 y OpenID Connect

**OAuth 2.0** es un estándar de autorización. **OpenID Connect (OIDC)** agrega una capa de identidad sobre OAuth 2.0.

Google Identity Services utiliza estos estándares para que la aplicación pueda recibir una identidad verificada sin conocer la contraseña de Google del usuario.

En este proyecto:

- El frontend obtiene un **ID Token** de Google.
- El backend valida ese token.
- El backend crea su propio JWT de sesión.

La aplicación no utiliza el ID Token de Google como sesión interna.

### 2.4 JWT

JWT significa **JSON Web Token**. Es un token firmado que representa la sesión del usuario.

Contiene información o *claims*, por ejemplo:

```json
{
  "sub": "admin",
  "rol": "ADMIN",
  "exp": 1780000000
}
```

- `sub`: identidad o nombre del usuario.
- `rol`: rol utilizado para autorizar operaciones.
- `exp`: momento de expiración.

El frontend envía el JWT en cada petición protegida:

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### 2.5 MFA, TOTP y Google Authenticator

**MFA** significa autenticación multifactor. **TOTP** significa contraseña temporal de un solo uso basada en tiempo.

Google Authenticator y el backend comparten una clave secreta. Ambos calculan el mismo código utilizando:

- La clave secreta.
- La hora actual dividida en periodos de 30 segundos.
- El algoritmo HMAC-SHA1.
- Una salida de seis dígitos.

```mermaid
flowchart LR
    S[Secreto compartido] --> G[Google Authenticator]
    S --> B[Backend]
    T[Periodo actual de 30 s] --> G
    T --> B
    G --> C1[Código 123456]
    B --> C2[Código esperado 123456]
    C1 --> V{Comparación}
    C2 --> V
    V -->|Coinciden| OK[Segundo factor válido]
```

El teléfono no necesita conexión a Internet para generar el código. Sí necesita tener su reloj correctamente sincronizado.

---

## 3. Vista general de los flujos

```mermaid
flowchart TD
    I[Pantalla de login] --> M{Método elegido}
    M -->|Google| G[Validar ID Token con Google]
    G --> J1[Emitir JWT de Andina]
    M -->|Usuario y contraseña| L[Validar credenciales locales]
    L --> Q{MFA habilitado?}
    Q -->|No| J2[Emitir JWT de Andina]
    Q -->|Sí| D[Crear desafío temporal]
    D --> T[Solicitar código TOTP]
    T --> V[Validar Google Authenticator]
    V -->|Correcto| J3[Emitir JWT de Andina]
    V -->|Incorrecto o vencido| R[Rechazar acceso]
```

Los dos métodos terminan generando el mismo tipo de JWT interno. Por ello, el resto de la aplicación no necesita saber cómo ingresó el usuario.

---

## 4. Flujo del login con Google

### 4.1 Secuencia

```mermaid
sequenceDiagram
    actor U as Usuario
    participant F as Frontend Vue
    participant G as Google Identity Services
    participant B as Backend Clean
    participant DB as MongoDB

    U->>F: Presiona Continuar con Google
    F->>G: Solicita autenticación
    G->>U: Muestra selector o acceso de Google
    U->>G: Confirma su cuenta
    G-->>F: Devuelve ID Token
    F->>B: POST /api/auth/google con idToken
    B->>B: Valida firma, issuer, audience y expiración
    B->>DB: Busca usuario por googleSubject o email
    DB-->>B: Usuario existente o resultado vacío
    B->>DB: Crea o vincula usuario cuando corresponde
    B-->>F: JWT propio de Andina Seguros
    F->>F: Guarda sesión y redirige según el rol
```

### 4.2 ¿Qué valida el backend?

El backend no debe confiar en datos personales enviados directamente por el navegador. Solo recibe el `idToken` y valida:

- **Firma:** demuestra que Google emitió el token.
- **Issuer (`iss`):** debe corresponder a Google.
- **Audience (`aud`):** debe coincidir con el Client ID de la aplicación.
- **Expiración (`exp`):** impide aceptar tokens vencidos.
- **Subject (`sub`):** identificador estable de la cuenta Google.
- **Email verificado:** evita aceptar correos que Google no confirmó.

### 4.3 ¿Por qué se usa `googleSubject`?

El correo puede cambiar. El claim `sub`, guardado como `googleSubject`, es el identificador estable asignado por Google a la cuenta para esta aplicación.

### 4.4 Resultado del login social

Si la identidad es válida y el usuario está activo, el backend devuelve:

```json
{
  "token": "jwt-de-andina-seguros",
  "tipo": "Bearer",
  "expiraEnSegundos": 28800
}
```

En este flujo no se solicita el TOTP propio del sistema.

---

## 5. Activación de Google Authenticator

Antes de exigir MFA, el usuario debe vincular su aplicación Google Authenticator.

```mermaid
sequenceDiagram
    actor U as Usuario autenticado
    participant F as Frontend Vue
    participant B as Backend Clean
    participant DB as MongoDB
    participant A as Google Authenticator

    U->>F: Abre Seguridad MFA
    F->>B: GET /api/mfa/estado
    B-->>F: habilitado = false
    U->>F: Presiona Activar MFA
    F->>B: POST /api/mfa/configurar + JWT
    B->>B: Genera secreto Base32 y URI otpauth
    B->>B: Genera imagen QR
    B->>DB: Guarda secreto con mfaHabilitado=false
    B-->>F: Secreto, URI y QR
    F-->>U: Muestra el QR
    U->>A: Escanea el QR
    A-->>U: Muestra código de 6 dígitos
    U->>F: Ingresa el código
    F->>B: POST /api/mfa/activar + JWT + código
    B->>B: Valida el TOTP
    B->>DB: Guarda mfaHabilitado=true
    B-->>F: 204 No Content
    F-->>U: MFA habilitado
```

La activación tiene dos fases por seguridad:

1. Se genera y guarda el secreto como pendiente.
2. Solo se habilita MFA cuando el usuario demuestra que escaneó correctamente el QR.

---

## 6. Login local con MFA

### 6.1 Primer factor

El usuario envía su nombre y contraseña:

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "Admin123*"
}
```

Si MFA no está habilitado, el backend devuelve el JWT normalmente. Si está habilitado, no entrega el JWT y devuelve un desafío:

```json
{
  "requiresMfa": true,
  "token": null,
  "challengeToken": "desafio-temporal",
  "challengeExpiresIn": 300
}
```

### 6.2 Segundo factor

```mermaid
sequenceDiagram
    actor U as Usuario
    participant F as Frontend Vue
    participant B as Backend Clean
    participant DB as MongoDB

    U->>F: Envía usuario y contraseña
    F->>B: POST /api/auth/login
    B->>DB: Busca usuario
    B->>B: Comprueba contraseña y estado
    B->>B: Detecta mfaHabilitado=true
    B-->>F: challengeToken, sin JWT
    F-->>U: Solicita código de 6 dígitos
    U->>F: Ingresa código TOTP
    F->>B: POST /api/auth/mfa/verificar
    B->>B: Consume desafío y valida expiración
    B->>DB: Comprueba que el usuario siga activo
    B->>B: Verifica el código TOTP
    B-->>F: JWT definitivo
    F->>F: Guarda sesión y redirige
```

Petición del segundo factor:

```http
POST /api/auth/mfa/verificar
Content-Type: application/json

{
  "challengeToken": "desafio-temporal",
  "codigo": "123456"
}
```

El desafío:

- Tiene una duración de cinco minutos.
- Es aleatorio.
- No funciona como JWT de sesión.
- Se elimina al consumirse para impedir su reutilización.

---

## 7. Desactivación de MFA

Desactivar MFA requiere una sesión válida y un código TOTP vigente:

```http
DELETE /api/mfa
Authorization: Bearer jwt-de-sesion
Content-Type: application/json

{
  "codigo": "123456"
}
```

Solicitar nuevamente el código evita que una persona que robó solamente el JWT pueda desactivar la protección sin tener acceso al teléfono.

---

## 8. Endpoints principales

| Método | Endpoint | Autenticación | Propósito |
|---|---|---|---|
| `POST` | `/api/auth/login` | Público | Login con usuario y contraseña |
| `POST` | `/api/auth/google` | Público | Login social mediante ID Token de Google |
| `POST` | `/api/auth/mfa/verificar` | Desafío MFA | Validar segundo factor y obtener JWT |
| `GET` | `/api/mfa/estado` | JWT | Consultar si MFA está habilitado |
| `POST` | `/api/mfa/configurar` | JWT | Generar secreto y QR |
| `POST` | `/api/mfa/activar` | JWT + TOTP | Confirmar y activar MFA |
| `DELETE` | `/api/mfa` | JWT + TOTP | Desactivar MFA |

---

## 9. Aplicación de Clean Architecture

La lógica se divide para que el negocio no dependa directamente de Spring, MongoDB, Google o ZXing.

```mermaid
flowchart TB
    subgraph FD[Frameworks & Drivers]
        CFG[UseCaseConfig / SecurityConfig]
    end
    subgraph IA[Interface Adapters]
        REST[AuthController / MfaController]
        SEC[Adaptadores Google, TOTP, QR y desafío]
        MONGO[Repositorios y mappers MongoDB]
    end
    subgraph UC[Use Cases]
        AUTH[AutenticarUsuario / AutenticarConGoogle]
        MFA[Configurar / Activar / Verificar / Desactivar MFA]
        PORTS[Puertos de seguridad y repositorios]
    end
    subgraph EN[Entities]
        USER[Usuario]
    end

    FD --> IA
    IA --> UC
    UC --> EN
    SEC -. implementa .-> PORTS
    MONGO -. implementa .-> PORTS
```

### Distribución de responsabilidades

- **Entities:** `Usuario` contiene `mfaSecret` y `mfaHabilitado`, sin importar clases de frameworks.
- **Use Cases:** deciden cuándo configurar, activar, exigir y validar MFA.
- **Puertos:** definen qué necesita la aplicación: generar secretos, comprobar TOTP, crear QR o almacenar usuarios.
- **Adaptadores:** implementan los detalles técnicos de TOTP, QR, Google, JWT y MongoDB.
- **Controllers:** traducen HTTP a modelos de entrada y llaman casos de uso.
- **UseCaseConfig:** conecta interfaces con implementaciones mediante inyección de dependencias.

La dirección de dependencias apunta hacia el núcleo. Esto permite cambiar una librería TOTP o una base de datos sin reescribir las reglas de autenticación.

---

## 10. Responsabilidad del frontend Vue

El frontend no valida tokens de Google ni códigos TOTP por su cuenta. Su responsabilidad es coordinar la experiencia del usuario:

```mermaid
stateDiagram-v2
    [*] --> Login
    Login --> Sesion: login local sin MFA
    Login --> Sesion: login con Google
    Login --> EsperandoMFA: login local requiere MFA
    EsperandoMFA --> Sesion: TOTP correcto
    EsperandoMFA --> Login: cancelar o desafío vencido
    Sesion --> ConfiguracionMFA: abrir Seguridad
    ConfiguracionMFA --> Sesion: activar o desactivar
    Sesion --> Login: cerrar sesión
```

Elementos principales:

- `auth.ts`: guarda la sesión y el desafío pendiente.
- `LoginView.vue`: inicia el login local o Google.
- `MfaVerificationView.vue`: solicita el TOTP después del login local.
- `MfaSetupView.vue`: muestra el QR y permite activar o desactivar MFA.
- `router/index.ts`: impide entrar a la verificación sin un desafío pendiente.
- `api.ts`: adjunta el JWT y presenta mensajes de error comprensibles.

El `challengeToken` se guarda en `sessionStorage`; el JWT definitivo se guarda como sesión después de completar la autenticación.

---

## 11. Controles de seguridad

La implementación aplica las siguientes reglas:

- La contraseña de Google nunca llega a Andina Seguros.
- El backend verifica criptográficamente el ID Token.
- El rol no se toma del frontend.
- El JWT definitivo no se emite antes del segundo factor en el login local con MFA.
- El código TOTP debe tener exactamente seis dígitos.
- Se acepta una pequeña ventana temporal para tolerar diferencias de reloj.
- El desafío MFA expira y es de un solo uso.
- El usuario se vuelve a consultar antes de emitir el JWT.
- Los secretos, códigos TOTP y tokens no deben escribirse en logs.
- La configuración y desactivación MFA requieren una sesión autenticada.

### Consideraciones para producción

Para un entorno real se recomienda además:

- Usar HTTPS obligatoriamente.
- Cifrar `mfaSecret` antes de guardarlo en MongoDB.
- Persistir desafíos en Redis o MongoDB si existen varias instancias del backend.
- Limitar intentos y aplicar protección contra fuerza bruta.
- Incorporar códigos de recuperación.
- Utilizar cookies `HttpOnly` y `Secure` para reducir la exposición del JWT.
- Registrar eventos de seguridad sin registrar secretos.
- Sincronizar la hora del servidor mediante NTP.

La implementación académica conserva los desafíos en memoria. Si el backend se reinicia, los desafíos pendientes se invalidan, lo cual es seguro aunque obliga al usuario a iniciar el login nuevamente.

---

## 12. Errores controlados

| Código | Significado |
|---|---|
| `CREDENCIALES_INVALIDAS` | Usuario o contraseña incorrectos |
| `GOOGLE_TOKEN_INVALIDO` | El ID Token no pudo validarse |
| `GOOGLE_EMAIL_NO_VERIFICADO` | Google no confirmó el correo |
| `USUARIO_INACTIVO` | La cuenta está deshabilitada |
| `MFA_CODIGO_INVALIDO` | El TOTP no coincide |
| `MFA_DESAFIO_INVALIDO` | El desafío no existe o ya fue consumido |
| `MFA_DESAFIO_EXPIRADO` | El desafío superó su duración |
| `MFA_YA_HABILITADO` | Se intentó configurar MFA estando activo |
| `MFA_NO_CONFIGURADO` | No existe una configuración MFA válida |

Los errores no deben revelar si una parte específica de las credenciales era correcta ni mostrar información secreta.

---

## 13. Guía de prueba manual

### 13.1 Activar MFA

1. Abrir `http://localhost:5173`.
2. Iniciar sesión con usuario y contraseña.
3. Entrar a **Seguridad MFA**.
4. Presionar **Activar MFA**.
5. Abrir Google Authenticator en el teléfono.
6. Escanear el QR mostrado.
7. Escribir el código actual de seis dígitos.
8. Confirmar que el estado cambie a **Habilitado**.

### 13.2 Probar login local con MFA

1. Cerrar sesión.
2. Ingresar nuevamente usuario y contraseña.
3. Confirmar que el sistema no abra todavía el panel.
4. Escribir un código incorrecto y comprobar el rechazo.
5. Iniciar nuevamente el login si el desafío fue consumido.
6. Escribir el código correcto.
7. Confirmar que se abra el panel y exista una sesión válida.

### 13.3 Probar login con Google

1. Cerrar sesión.
2. Presionar **Continuar con Google**.
3. Elegir una cuenta autorizada.
4. Confirmar que el sistema ingrese directamente sin solicitar el TOTP propio.
5. Verificar que las opciones visibles correspondan al rol del usuario.

### 13.4 Probar desactivación

1. Abrir **Seguridad MFA** con una sesión válida.
2. Introducir el código actual de Google Authenticator.
3. Presionar **Desactivar MFA**.
4. Cerrar sesión.
5. Confirmar que el próximo login local entregue la sesión sin segundo paso.

---

## 14. Resumen final

La solución separa correctamente tres responsabilidades:

1. **Google verifica la identidad social** y entrega un ID Token.
2. **Google Authenticator genera el segundo factor TOTP** para el login local.
3. **Andina Seguros emite su propio JWT**, aplica roles y protege sus endpoints.

El punto más importante es que un ID Token, un desafío MFA y un JWT de sesión no son intercambiables. Cada elemento tiene una finalidad, duración y nivel de confianza diferente. Clean Architecture mantiene estas decisiones de negocio separadas de los detalles técnicos, haciendo que la solución sea más comprensible, comprobable y fácil de evolucionar.
