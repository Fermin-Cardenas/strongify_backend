# Documentación de API - GymApp Backend

## Información General

- **Base URL**: `http://localhost:8080`
- **Puerto**: 8080
- **Autenticación**: JWT (JSON Web Token)
- **Formato de Respuesta**: JSON
- **Despliegue**: Docker Compose

## Configuración con Docker

Esta API está configurada para ejecutarse con Docker Compose. Los servicios incluyen:
- **java_app**: Aplicación Spring Boot (puerto 8080)
- **java_db**: Base de datos PostgreSQL (puerto 5432)

### Verificar que Docker está corriendo

Antes de probar los endpoints, asegúrate de que los contenedores estén activos:

```bash
docker ps
```

Deberías ver los contenedores `java_app` y `java_db` en la lista.

### Iniciar los servicios

Si los contenedores no están corriendo:

```bash
# Construir la imagen (solo la primera vez o después de cambios)
docker compose build java_app

# Iniciar los servicios
docker compose up -d
```

### Detener los servicios

```bash
docker compose down
```

### Ver logs

Para ver los logs de la aplicación:

```bash
docker logs java_app -f
```

Para ver los logs de la base de datos:

```bash
docker logs java_db -f
```

## Autenticación

La mayoría de los endpoints requieren autenticación mediante JWT. El token debe enviarse en el header `Authorization` con el formato:
```
Authorization: Bearer <token>
```

Los únicos endpoints públicos (sin autenticación) son:
- `/auth/login`
- `/auth/register`
- `/auth/forgot-password`

---

## Endpoints Disponibles

### 1. Autenticación (`/auth`)

#### 1.1. Login
- **Método**: `POST`
- **Ruta**: `/auth/login`
- **Autenticación**: No requerida
- **Descripción**: Inicia sesión y obtiene un token JWT

**Request Body:**
```json
{
  "username": "usuario123",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

#### 1.2. Registro
- **Método**: `POST`
- **Ruta**: `/auth/register`
- **Autenticación**: No requerida
- **Descripción**: Registra un nuevo usuario

**Request Body:**
```json
{
  "username": "nuevo_usuario",
  "password": "password123",
  "confirmPassword": "password123",
  "first_name": "Juan",
  "last_name": "Pérez",
  "birthday": "1990-05-15",
  "phone": "1234567890"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

#### 1.3. Recuperar Contraseña
- **Método**: `POST`
- **Ruta**: `/auth/forgot-password`
- **Autenticación**: No requerida
- **Descripción**: Solicita recuperación de contraseña (requiere configuración de email para producción)

**Request Body:**
```json
{
  "username": "usuario123",
  "email": "usuario@example.com"
}
```

**Response (200 OK):**
```
"Si el usuario existe, se enviará un email con instrucciones para recuperar la contraseña"
```

**Nota**: Este endpoint está implementado pero requiere configuración de servicio de email para funcionar completamente. Actualmente devuelve un mensaje genérico.

---

### 2. Perfil de Usuario (`/api/profile`)

#### 2.1. Obtener Información del Perfil
- **Método**: `GET`
- **Ruta**: `/api/profile/get-profile-info`
- **Autenticación**: Requerida (JWT)
- **Descripción**: Obtiene la información del perfil del usuario autenticado

**Headers:**
```
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
{
  "id": 1,
  "firstName": "Juan",
  "lastName": "Pérez",
  "phoneNumber": "1234567890",
  "photoUrl": "http://localhost:8080/images/usuario.jpg",
  "username": "usuario123",
  "birthday": "1990-05-15",
  "lastLogin": "2024-01-15T10:30:00"
}
```

---

#### 2.2. Actualizar Información del Perfil
- **Método**: `PUT`
- **Ruta**: `/api/profile/update-profile-info/{user_id}`
- **Autenticación**: Requerida (JWT)
- **Descripción**: Actualiza la información del perfil del usuario

**Headers:**
```
Authorization: Bearer <token>
```

**Path Parameters:**
- `user_id` (Long): ID del usuario a actualizar

**Request Body:**
```json
{
  "firstName": "Juan",
  "lastName": "Pérez",
  "phoneNumber": "9876543210",
  "photoUrl": "http://localhost:8080/images/nueva_foto.jpg",
  "birthday": "1990-05-15"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "firstName": "Juan",
  "lastName": "Pérez",
  "phoneNumber": "9876543210",
  "photoUrl": "http://localhost:8080/images/nueva_foto.jpg",
  "username": "usuario123",
  "birthday": "1990-05-15",
  "lastLogin": "2024-01-15T10:30:00"
}
```

---

#### 2.3. Obtener Membresía
- **Método**: `GET`
- **Ruta**: `/api/profile/membresia`
- **Autenticación**: Requerida (JWT)
- **Descripción**: Obtiene la membresía activa del usuario autenticado

**Headers:**
```
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
{
  "membresiaId": 1,
  "tipo": "MENSUAL",
  "costo": 500.00,
  "duracionDias": 30,
  "fechaInicio": "2024-01-15T10:30:00Z",
  "fechaFin": "2024-02-14T10:30:00Z",
  "diasRestantes": 15,
  "activa": true
}
```

**Response (404 Not Found):**
```json
{
  "message": "No tienes una membresía activa"
}
```

---

#### 2.4. Guardar Progreso (Peso/IMC)
- **Método**: `POST`
- **Ruta**: `/api/profile/progreso`
- **Autenticación**: Requerida (JWT)
- **Descripción**: Guarda un nuevo registro de progreso (peso/IMC) en el historial del usuario

**Headers:**
```
Authorization: Bearer <token>
```

**Request Body:**
```json
{
  "peso": 75.5
}
```

**Response (200 OK):**
```json
{
  "message": "Progreso guardado exitosamente",
  "progresoId": 1,
  "peso": 75.5,
  "imc": 24.5,
  "fechaRegistro": "2024-01-15T10:30:00"
}
```

**Nota**: Cada vez que se guarda un progreso, se crea un nuevo registro en el historial (no se actualiza el anterior). El IMC se calcula automáticamente si el usuario tiene altura registrada usando la fórmula: `IMC = peso (kg) / (altura (m))²`. Si el usuario no tiene altura, el IMC será `null`.

---

#### 2.5. Obtener Historial de Progreso
- **Método**: `GET`
- **Ruta**: `/api/profile/progreso`
- **Autenticación**: Requerida (JWT)
- **Descripción**: Obtiene el historial completo de progreso (peso/IMC) del usuario autenticado

**Headers:**
```
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "peso": 75.5,
    "imc": 24.5,
    "fecha": "2024-01-15T10:30:00"
  },
  {
    "id": 2,
    "peso": 76.0,
    "imc": 24.7,
    "fecha": "2024-01-20T10:30:00"
  }
]
```

**Response (200 OK) - Sin historial:**
```json
[]
```

**Nota**: Los registros están ordenados por fecha descendente (más reciente primero). El IMC será `null` si el usuario no tiene altura registrada.

---

#### 2.6. Obtener Métodos de Pago
- **Método**: `GET`
- **Ruta**: `/api/profile/metodos-pago`
- **Autenticación**: Requerida (JWT)
- **Descripción**: Obtiene todos los métodos de pago (tarjetas) del usuario autenticado

**Headers:**
```
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "tipo": "credito",
    "ultimos4": "4242",
    "nombreTitular": "Juan Pérez",
    "fechaVencimiento": "12/25",
    "esPredeterminada": true,
    "marca": "visa"
  },
  {
    "id": 2,
    "tipo": "debito",
    "ultimos4": "8888",
    "nombreTitular": "Juan Pérez",
    "fechaVencimiento": "06/26",
    "esPredeterminada": false,
    "marca": "mastercard"
  }
]
```

**Response (200 OK) - Sin métodos de pago:**
```json
[]
```

---

#### 2.7. Agregar Método de Pago
- **Método**: `POST`
- **Ruta**: `/api/profile/metodos-pago`
- **Autenticación**: Requerida (JWT)
- **Descripción**: Agrega un nuevo método de pago (tarjeta) para el usuario autenticado

**Headers:**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "tipo": "credito",
  "numeroTarjeta": "4242424242424242",
  "nombreTitular": "Juan Pérez",
  "fechaVencimiento": "12/25",
  "cvv": "123",
  "marca": "visa",
  "esPredeterminada": true
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "tipo": "credito",
  "ultimos4": "4242",
  "nombreTitular": "Juan Pérez",
  "fechaVencimiento": "12/25",
  "esPredeterminada": true,
  "marca": "visa"
}
```

**Notas**:
- Solo se guardan los últimos 4 dígitos de la tarjeta (nunca el número completo)
- El CVV no se guarda por seguridad
- Si `esPredeterminada` es `true`, se marca automáticamente las demás tarjetas como `false`

---

#### 2.8. Actualizar Método de Pago
- **Método**: `PUT`
- **Ruta**: `/api/profile/metodos-pago/{id}`
- **Autenticación**: Requerida (JWT)
- **Descripción**: Actualiza un método de pago existente

**Headers:**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**Path Parameters:**
- `id` (Long): ID del método de pago

**Request Body** (todos los campos son opcionales):
```json
{
  "nombreTitular": "Juan Pérez",
  "fechaVencimiento": "12/26",
  "esPredeterminada": false
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "tipo": "credito",
  "ultimos4": "4242",
  "nombreTitular": "Juan Pérez",
  "fechaVencimiento": "12/26",
  "esPredeterminada": false,
  "marca": "visa"
}
```

---

#### 2.9. Eliminar Método de Pago
- **Método**: `DELETE`
- **Ruta**: `/api/profile/metodos-pago/{id}`
- **Autenticación**: Requerida (JWT)
- **Descripción**: Elimina un método de pago del usuario autenticado

**Headers:**
```
Authorization: Bearer <token>
```

**Path Parameters:**
- `id` (Long): ID del método de pago

**Response (200 OK):**
```
"Método de pago eliminado exitosamente"
```

**Response (400 Bad Request):**
```
"No puedes eliminar tu único método de pago"
```

---

#### 2.10. Establecer Método de Pago Predeterminado
- **Método**: `PUT`
- **Ruta**: `/api/profile/metodos-pago/{id}/predeterminado`
- **Autenticación**: Requerida (JWT)
- **Descripción**: Establece un método de pago como predeterminado

**Headers:**
```
Authorization: Bearer <token>
```

**Path Parameters:**
- `id` (Long): ID del método de pago

**Request Body**: Vacío `{}` o sin body

**Response (200 OK):**
```json
{
  "id": 1,
  "tipo": "credito",
  "ultimos4": "4242",
  "nombreTitular": "Juan Pérez",
  "fechaVencimiento": "12/25",
  "esPredeterminada": true,
  "marca": "visa"
}
```

**Nota**: Al establecer uno como predeterminado, automáticamente se marca los demás como `false`.

---

#### 2.11. Cambiar Contraseña
- **Método**: `PUT`
- **Ruta**: `/api/profile/change-password`
- **Autenticación**: Requerida (JWT)
- **Descripción**: Cambia la contraseña del usuario autenticado

**Headers:**
```
Authorization: Bearer <token>
```

**Request Body:**
```json
{
  "password": "password_actual",
  "newPassword": "nueva_password",
  "confirmPassword": "nueva_password"
}
```

**Response (200 OK):**
```
"Contraseña actualizada correctamente"
```

**Response (400 Bad Request):**
```
"Mensaje de error descriptivo"
```

---

#### 2.4. Subir Imagen de Perfil
- **Método**: `POST`
- **Ruta**: `/api/profile/upload-image`
- **Autenticación**: Requerida (JWT)
- **Descripción**: Sube una imagen de perfil para el usuario autenticado

**Headers:**
```
Authorization: Bearer <token>
Content-Type: multipart/form-data
```

**Body (form-data):**
- `file` (File): Archivo de imagen

**Response (200 OK):**
```json
{
  "message": "Imagen subida correctamente",
  "url": "http://localhost:8080/images/usuario.jpg"
}
```

---

#### 2.5. Actualizar Rol de Usuario (Solo Admin)
- **Método**: `PUT`
- **Ruta**: `/api/profile/update-user-rol/{user_id}`
- **Autenticación**: Requerida (JWT) + Rol ADMIN
- **Descripción**: Actualiza el rol de un usuario (solo administradores)

**Headers:**
```
Authorization: Bearer <token>
```

**Path Parameters:**
- `user_id` (Long): ID del usuario a actualizar

**Request Body:**
```json
{
  "rol_id": 2
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "firstName": "Juan",
  "lastName": "Pérez",
  "phoneNumber": "1234567890",
  "photoUrl": "http://localhost:8080/images/usuario.jpg",
  "username": "usuario123",
  "birthday": "1990-05-15",
  "lastLogin": "2024-01-15T10:30:00"
}
```

---

#### 2.6. Obtener Mis Reservas
- **Método**: `GET`
- **Ruta**: `/api/profile/me/reservas`
- **Autenticación**: Requerida (JWT)
- **Descripción**: Obtiene todas las reservas del usuario autenticado

**Headers:**
```
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "cliente": {
      "id": 1,
      "username": "usuario123"
    },
    "claseAgendada": {
      "id": 1,
      "nombre": "Yoga Matutino"
    },
    "fechaReserva": "2024-01-15T10:00:00Z",
    "estado": "CONFIRMADA",
    "asistencia": false
  }
]
```

---

### 3. Roles (`/api/role`) - Solo Admin

#### 3.1. Crear Rol
- **Método**: `POST`
- **Ruta**: `/api/role/create-role`
- **Autenticación**: Requerida (JWT) + Rol ADMIN
- **Descripción**: Crea un nuevo rol en el sistema

**Headers:**
```
Authorization: Bearer <token>
```

**Request Body:**
```json
{
  "name": "ENTRENADOR",
  "description": "Rol para entrenadores del gimnasio"
}
```

**Response (200 OK):**
```
"Role creado"
```

---

#### 3.2. Obtener Todos los Roles
- **Método**: `GET`
- **Ruta**: `/api/role/get-roles`
- **Autenticación**: Requerida (JWT) + Rol ADMIN
- **Descripción**: Obtiene todos los roles disponibles

**Headers:**
```
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
[
  {
    "name": "ADMIN",
    "description": "Administrador del sistema"
  },
  {
    "name": "USER",
    "description": "Usuario regular"
  },
  {
    "name": "ENTRENADOR",
    "description": "Rol para entrenadores del gimnasio"
  }
]
```

---

#### 3.3. Obtener Rol por ID
- **Método**: `GET`
- **Ruta**: `/api/role/get-role/{rol_id}`
- **Autenticación**: Requerida (JWT) + Rol ADMIN
- **Descripción**: Obtiene un rol específico por su ID

**Headers:**
```
Authorization: Bearer <token>
```

**Path Parameters:**
- `rol_id` (Long): ID del rol

**Response (200 OK):**
```json
{
  "name": "ENTRENADOR",
  "description": "Rol para entrenadores del gimnasio"
}
```

---

#### 3.4. Actualizar Rol
- **Método**: `PUT`
- **Ruta**: `/api/role/update-role/{rol_id}`
- **Autenticación**: Requerida (JWT) + Rol ADMIN
- **Descripción**: Actualiza la información de un rol

**Headers:**
```
Authorization: Bearer <token>
```

**Path Parameters:**
- `rol_id` (Long): ID del rol a actualizar

**Request Body:**
```json
{
  "name": "ENTRENADOR",
  "description": "Descripción actualizada del rol de entrenador"
}
```

**Response (200 OK):**
```json
{
  "name": "ENTRENADOR",
  "description": "Descripción actualizada del rol de entrenador"
}
```

---

#### 3.5. Eliminar Rol
- **Método**: `DELETE`
- **Ruta**: `/api/role/delete-role/{rol_id}`
- **Autenticación**: Requerida (JWT) + Rol ADMIN
- **Descripción**: Elimina un rol del sistema

**Headers:**
```
Authorization: Bearer <token>
```

**Path Parameters:**
- `rol_id` (Long): ID del rol a eliminar

**Response (200 OK):**
```
"Role eliminado"
```

---

### 4. Reservas (`/api/reservas`)

#### 4.1. Reservar Clase
- **Método**: `POST`
- **Ruta**: `/api/reservas/clase/{claseId}`
- **Autenticación**: Requerida (JWT)
- **Descripción**: Crea una nueva reserva para una clase

**Headers:**
```
Authorization: Bearer <token>
```

**Path Parameters:**
- `claseId` (Long): ID de la clase a reservar

**Response (200 OK):**
```json
{
  "id": 1,
  "cliente": {
    "id": 1,
    "username": "usuario123"
  },
  "claseAgendada": {
    "id": 5,
    "nombre": "Yoga Matutino"
  },
  "fechaReserva": "2024-01-15T10:00:00Z",
  "estado": "CONFIRMADA",
  "asistencia": false
}
```

**Response (400 Bad Request):**
```json
"Mensaje de error descriptivo"
```

---

#### 4.2. Cancelar Reserva
- **Método**: `DELETE`
- **Ruta**: `/api/reservas/{id}`
- **Autenticación**: Requerida (JWT)
- **Descripción**: Cancela una reserva del usuario autenticado

**Headers:**
```
Authorization: Bearer <token>
```

**Path Parameters:**
- `id` (Long): ID de la reserva a cancelar

**Response (200 OK):**
```
"Reserva cancelada exitosamente"
```

**Response (400 Bad Request):**
```
"No tienes permiso para cancelar esta reserva"
```

o

```
"La reserva ya está cancelada"
```

**Nota**: Al cancelar una reserva, se libera el cupo de la clase automáticamente.

---

#### 4.3. Marcar Asistencia
- **Método**: `POST`
- **Ruta**: `/api/reservas/{id}/marcar-asistencia`
- **Autenticación**: Requerida (JWT)
- **Descripción**: Marca la asistencia de una reserva (presente o ausente)

**Headers:**
```
Authorization: Bearer <token>
```

**Path Parameters:**
- `id` (Long): ID de la reserva

**Request Body:**
```json
{
  "asistencia": true
}
```

**Response (200 OK):**
```
"Asistencia marcada como PRESENTE para la reserva ID 1"
```

o

```
"Asistencia marcada como AUSENTE para la reserva ID 1"
```

---

### 5. Agenda/Asistencia (`/api/agenda`)

#### 5.1. Listar Todas las Clases Disponibles
- **Método**: `GET`
- **Ruta**: `/api/agenda/clases`
- **Autenticación**: Requerida (JWT)
- **Descripción**: Obtiene todas las clases disponibles en el sistema

**Headers:**
```
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "nombreClase": "Yoga Matutino",
    "descripcion": "Clase de yoga para principiantes",
    "coachId": 2,
    "coachNombre": "María García",
    "sucursalId": 1,
    "sucursalNombre": "Sucursal Centro",
    "fechaHoraInicio": "2024-01-20T08:00:00Z",
    "fechaHoraFin": "2024-01-20T09:00:00Z",
    "cupoActual": 15,
    "cupoMaximo": 20,
    "duracionMinutos": 60
  },
  {
    "id": 2,
    "nombreClase": "CrossFit",
    "descripcion": "Entrenamiento de alta intensidad",
    "coachId": 3,
    "coachNombre": "Juan Pérez",
    "sucursalId": 1,
    "sucursalNombre": "Sucursal Centro",
    "fechaHoraInicio": "2024-01-20T10:00:00Z",
    "fechaHoraFin": "2024-01-20T11:00:00Z",
    "cupoActual": 8,
    "cupoMaximo": 15,
    "duracionMinutos": 60
  }
]
```

---

#### 5.2. Listar Clases por Coach
- **Método**: `GET`
- **Ruta**: `/api/agenda/coach/{coachId}/clases`
- **Autenticación**: Requerida (JWT)
- **Descripción**: Obtiene todas las clases asignadas a un coach específico

**Headers:**
```
Authorization: Bearer <token>
```

**Path Parameters:**
- `coachId` (Long): ID del coach

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "nombreClase": "Yoga Matutino",
    "descripcion": "Clase de yoga para principiantes",
    "coachId": 2,
    "coachNombre": "María García",
    "sucursalId": 1,
    "sucursalNombre": "Sucursal Centro",
    "fechaHoraInicio": "2024-01-20T08:00:00Z",
    "fechaHoraFin": "2024-01-20T09:00:00Z",
    "cupoActual": 15,
    "cupoMaximo": 20,
    "duracionMinutos": 60
  }
]
```

---

#### 5.3. Obtener Asistencia por Clase
- **Método**: `GET`
- **Ruta**: `/api/agenda/{id}/asistencia`
- **Autenticación**: Requerida (JWT)
- **Descripción**: Obtiene la lista de asistencias para una clase específica

**Headers:**
```
Authorization: Bearer <token>
```

**Path Parameters:**
- `id` (Long): ID de la clase

**Response (200 OK):**
```json
[
  {
    "reservaId": 1,
    "nombreCliente": "Juan Pérez",
    "telefono": "1234567890",
    "asistencia": true
  },
  {
    "reservaId": 2,
    "nombreCliente": "María García",
    "telefono": "9876543210",
    "asistencia": false
  }
]
```

---

### 6. Membresías (`/api/admin/membresias`)

#### 6.1. Activar Membresía
- **Método**: `POST`
- **Ruta**: `/api/admin/membresias/activar`
- **Autenticación**: Requerida (JWT)
- **Descripción**: Activa una membresía para el usuario autenticado. Crea la membresía y un registro de pago asociado.

**Headers:**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "tipo": "MENSUAL",
  "costo": 1299.00,
  "duracion": 90
}
```

**Nota**: Los tipos de membresía disponibles son:
- `PAGO_SEMANAL`
- `POR_SESION`
- `MENSUAL`
- `ANUAL`

**Response (200 OK):**
```json
{
  "membresiaId": 10,
  "tipo": "MENSUAL",
  "costo": 1299.00,
  "duracionDias": 90,
  "fechaInicio": "2024-01-15T10:30:00Z",
  "fechaFin": "2024-04-15T10:30:00Z",
  "diasRestantes": 89,
  "activa": true
}
```

**Response (400 Bad Request):**
```json
{
  "error": "Todos los campos son requeridos: tipo, costo, duracion"
}
```

o

```json
{
  "error": "Tipo de membresía inválido: INVALIDO. Valores permitidos: PAGO_SEMANAL, POR_SESION, MENSUAL, ANUAL"
}
```

**Notas importantes**:
- El endpoint obtiene automáticamente el usuario del token JWT
- Se crea un registro de pago asociado al usuario y la membresía
- La fecha de inicio es la fecha actual
- La fecha de fin se calcula sumando la duración en días a la fecha de inicio

---

## Guía de Pruebas en Postman

### Configuración Inicial

**Importante**: Asegúrate de que los contenedores Docker estén corriendo antes de probar los endpoints.

1. **Verificar Docker está corriendo**
   ```bash
   docker ps
   ```
   Deberías ver los contenedores `java_app` y `java_db` activos.

2. **Crear un Environment en Postman**
   - Abre Postman
   - Haz clic en "Environments" en el menú lateral
   - Crea un nuevo environment llamado "GymApp Local"
   - Agrega las siguientes variables:
     - `base_url`: `http://localhost:8080`
     - `token`: (dejar vacío, se llenará automáticamente)
   
   **O importa el environment preconfigurado**: `GymApp_Local.postman_environment.json`

2. **Configurar Variables Globales (Opcional)**
   - Puedes crear una variable global `token` para que se use en todas las peticiones

### Paso 1: Registro de Usuario

1. **Crear una nueva petición**
   - Método: `POST`
   - URL: `{{base_url}}/auth/register`
   - Headers: 
     - `Content-Type: application/json`
   - Body (raw JSON):
   ```json
   {
     "username": "test_user",
     "password": "password123",
     "confirmPassword": "password123",
     "first_name": "Test",
     "last_name": "User",
     "birthday": "1990-01-01",
     "phone": "1234567890"
   }
   ```

2. **Guardar el token**
   - En la respuesta, copia el valor del campo `token`
   - En el environment, actualiza la variable `token` con este valor
   - O crea un script de prueba para guardarlo automáticamente:
   ```javascript
   if (pm.response.code === 200) {
       var jsonData = pm.response.json();
       pm.environment.set("token", jsonData.token);
   }
   ```

### Paso 2: Login (Alternativa al Registro)

1. **Crear una nueva petición**
   - Método: `POST`
   - URL: `{{base_url}}/auth/login`
   - Headers:
     - `Content-Type: application/json`
   - Body (raw JSON):
   ```json
   {
     "username": "test_user",
     "password": "password123"
   }
   ```

2. **Guardar el token** (igual que en el registro)

### Paso 3: Configurar Autenticación para Peticiones Protegidas

Para todas las peticiones que requieren autenticación:

1. **En la pestaña "Authorization"**
   - Type: `Bearer Token`
   - Token: `{{token}}`

   O manualmente en Headers:
   - Key: `Authorization`
   - Value: `Bearer {{token}}`

### Paso 4: Probar Endpoints de Perfil

#### Obtener Perfil
- Método: `GET`
- URL: `{{base_url}}/api/profile/get-profile-info`
- Authorization: Bearer Token `{{token}}`

#### Actualizar Perfil
- Método: `PUT`
- URL: `{{base_url}}/api/profile/update-profile-info/1`
- Authorization: Bearer Token `{{token}}`
- Body (raw JSON):
```json
{
  "firstName": "Juan",
  "lastName": "Pérez",
  "phoneNumber": "9876543210",
  "photoUrl": "http://localhost:8080/images/foto.jpg",
  "birthday": "1990-05-15"
}
```

#### Cambiar Contraseña
- Método: `PUT`
- URL: `{{base_url}}/api/profile/change-password`
- Authorization: Bearer Token `{{token}}`
- Body (raw JSON):
```json
{
  "password": "password123",
  "newPassword": "newpassword456",
  "confirmPassword": "newpassword456"
}
```

#### Subir Imagen
- Método: `POST`
- URL: `{{base_url}}/api/profile/upload-image`
- Authorization: Bearer Token `{{token}}`
- Body (form-data):
  - Key: `file` (tipo: File)
  - Value: Selecciona un archivo de imagen

### Paso 5: Probar Endpoints de Reservas

#### Reservar Clase
- Método: `POST`
- URL: `{{base_url}}/api/reservas/clase/1`
- Authorization: Bearer Token `{{token}}`

#### Marcar Asistencia
- Método: `POST`
- URL: `{{base_url}}/api/reservas/1/marcar-asistencia`
- Authorization: Bearer Token `{{token}}`
- Body (raw JSON):
```json
{
  "asistencia": true
}
```

#### Obtener Mis Reservas
- Método: `GET`
- URL: `{{base_url}}/api/profile/me/reservas`
- Authorization: Bearer Token `{{token}}`

### Paso 6: Probar Endpoints de Agenda

#### Obtener Asistencia por Clase
- Método: `GET`
- URL: `{{base_url}}/api/agenda/1/asistencia`
- Authorization: Bearer Token `{{token}}`

### Paso 7: Probar Endpoints de Roles (Solo Admin)

**Nota**: Estos endpoints requieren que el usuario tenga el rol `ADMIN`. Asegúrate de tener un usuario con este rol.

#### Crear Rol
- Método: `POST`
- URL: `{{base_url}}/api/role/create-role`
- Authorization: Bearer Token `{{token}}`
- Body (raw JSON):
```json
{
  "name": "ENTRENADOR",
  "description": "Rol para entrenadores"
}
```

#### Obtener Todos los Roles
- Método: `GET`
- URL: `{{base_url}}/api/role/get-roles`
- Authorization: Bearer Token `{{token}}`

#### Obtener Rol por ID
- Método: `GET`
- URL: `{{base_url}}/api/role/get-role/1`
- Authorization: Bearer Token `{{token}}`

#### Actualizar Rol
- Método: `PUT`
- URL: `{{base_url}}/api/role/update-role/1`
- Authorization: Bearer Token `{{token}}`
- Body (raw JSON):
```json
{
  "name": "ENTRENADOR",
  "description": "Descripción actualizada"
}
```

#### Eliminar Rol
- Método: `DELETE`
- URL: `{{base_url}}/api/role/delete-role/1`
- Authorization: Bearer Token `{{token}}`

### Paso 8: Probar Endpoints de Membresías

#### Activar Membresía
- Método: `POST`
- URL: `{{base_url}}/api/admin/membresias/activar`
- Authorization: Bearer Token `{{token}}`
- Body (raw JSON):
```json
{
  "tipo": "MENSUAL",
  "costo": 500.00,
  "duracion": 30
}
```

---

## Consejos para React Native

### 1. Configuración de Base URL

```javascript
// config/api.js
const API_BASE_URL = __DEV__ 
  ? 'http://localhost:8080'  // Desarrollo
  : 'https://tu-api-produccion.com';  // Producción

export default API_BASE_URL;
```

### 2. Manejo de Autenticación

```javascript
// services/authService.js
import AsyncStorage from '@react-native-async-storage/async-storage';
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080';

// Guardar token
export const saveToken = async (token) => {
  await AsyncStorage.setItem('auth_token', token);
};

// Obtener token
export const getToken = async () => {
  return await AsyncStorage.getItem('auth_token');
};

// Configurar axios con interceptor
const api = axios.create({
  baseURL: API_BASE_URL,
});

api.interceptors.request.use(async (config) => {
  const token = await getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default api;
```

### 3. Ejemplo de Login

```javascript
// services/authService.js
export const login = async (username, password) => {
  try {
    const response = await api.post('/auth/login', {
      username,
      password,
    });
    
    if (response.data.token) {
      await saveToken(response.data.token);
      return response.data;
    }
  } catch (error) {
    throw error.response?.data || error.message;
  }
};
```

### 4. Ejemplo de Obtener Perfil

```javascript
// services/userService.js
import api from './authService';

export const getProfile = async () => {
  try {
    const response = await api.get('/api/profile/get-profile-info');
    return response.data;
  } catch (error) {
    throw error.response?.data || error.message;
  }
};
```

### 5. Manejo de Errores

```javascript
// utils/errorHandler.js
export const handleApiError = (error) => {
  if (error.response) {
    // Error de respuesta del servidor
    switch (error.response.status) {
      case 401:
        return 'No autorizado. Por favor, inicia sesión nuevamente.';
      case 403:
        return 'No tienes permisos para realizar esta acción.';
      case 404:
        return 'Recurso no encontrado.';
      case 500:
        return 'Error del servidor. Por favor, intenta más tarde.';
      default:
        return error.response.data?.message || 'Error desconocido';
    }
  } else if (error.request) {
    // Error de red
    return 'Error de conexión. Verifica tu internet.';
  } else {
    return 'Error inesperado. Por favor, intenta nuevamente.';
  }
};
```

### 6. Nota sobre CORS y Red Local

Para que React Native pueda conectarse a `localhost:8080` (con Docker), necesitas:

**Android Emulator:**
- Usar `http://10.0.2.2:8080` en lugar de `localhost:8080`

**iOS Simulator:**
- Usar `http://localhost:8080` (funciona directamente)

**Dispositivo Físico:**
- Usar la IP local de tu computadora (ej: `http://192.168.1.100:8080`)
- Asegúrate de que el dispositivo esté en la misma red WiFi
- **Importante**: Verifica que el puerto 8080 esté expuesto correctamente en Docker (ya está configurado en `docker-compose.yml`)

---

## Resumen de Endpoints

| Método | Ruta | Autenticación | Rol Requerido |
|--------|------|---------------|---------------|
| POST | `/auth/login` | No | - |
| POST | `/auth/register` | No | - |
| POST | `/auth/forgot-password` | No | - |
| GET | `/api/profile/get-profile-info` | Sí | - |
| PUT | `/api/profile/update-profile-info/{user_id}` | Sí | - |
| GET | `/api/profile/membresia` | Sí | - |
| POST | `/api/profile/progreso` | Sí | - |
| GET | `/api/profile/progreso` | Sí | - |
| GET | `/api/profile/metodos-pago` | Sí | - |
| POST | `/api/profile/metodos-pago` | Sí | - |
| PUT | `/api/profile/metodos-pago/{id}` | Sí | - |
| DELETE | `/api/profile/metodos-pago/{id}` | Sí | - |
| PUT | `/api/profile/metodos-pago/{id}/predeterminado` | Sí | - |
| PUT | `/api/profile/change-password` | Sí | - |
| POST | `/api/profile/upload-image` | Sí | - |
| PUT | `/api/profile/update-user-rol/{user_id}` | Sí | ADMIN |
| GET | `/api/profile/me/reservas` | Sí | - |
| POST | `/api/role/create-role` | Sí | ADMIN |
| GET | `/api/role/get-roles` | Sí | ADMIN |
| GET | `/api/role/get-role/{rol_id}` | Sí | ADMIN |
| PUT | `/api/role/update-role/{rol_id}` | Sí | ADMIN |
| DELETE | `/api/role/delete-role/{rol_id}` | Sí | ADMIN |
| POST | `/api/reservas/clase/{claseId}` | Sí | - |
| DELETE | `/api/reservas/{id}` | Sí | - |
| POST | `/api/reservas/{id}/marcar-asistencia` | Sí | - |
| GET | `/api/agenda/clases` | Sí | - |
| GET | `/api/agenda/coach/{coachId}/clases` | Sí | - |
| GET | `/api/agenda/{id}/asistencia` | Sí | - |
| POST | `/api/admin/membresias/activar` | Sí | - |

---

## Códigos de Estado HTTP

- `200 OK`: Petición exitosa
- `400 Bad Request`: Error en los datos enviados
- `401 Unauthorized`: No autenticado o token inválido
- `403 Forbidden`: No tienes permisos (falta rol requerido)
- `404 Not Found`: Recurso no encontrado
- `500 Internal Server Error`: Error del servidor

---

## Notas Importantes

1. **Tokens JWT**: Los tokens tienen una expiración. Si recibes un 401, necesitas hacer login nuevamente.

2. **CORS**: La configuración actual permite solo `http://localhost:4200`. Para React Native, esto no debería ser un problema, pero si necesitas cambiar los orígenes permitidos, modifica `SecurityConfig.java`.

3. **Formato de Fechas**: Las fechas se envían en formato ISO 8601 (ej: `"1990-05-15"` para fechas y `"2024-01-15T10:30:00"` para fechas con hora).

4. **Subida de Archivos**: El endpoint de subida de imágenes usa `multipart/form-data`. En React Native, usa `FormData` para enviar archivos.

---

¡Listo! Con esta documentación puedes probar todos los endpoints en Postman y comenzar a integrarlos en tu aplicación React Native.

