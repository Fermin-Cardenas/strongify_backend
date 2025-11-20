# Análisis: API vs Requisitos de Strongify

## ✅ Endpoints Disponibles en la API Actual

### Autenticación
- ✅ `POST /auth/login` - Login con username/password
- ✅ `POST /auth/register` - Registro básico
- ❌ `POST /auth/forgot-password` - **FALTA** Recuperación de contraseña
- ❌ `POST /auth/facebook` - **FALTA** Login con Facebook

### Perfil de Usuario
- ✅ `GET /api/profile/get-profile-info` - Obtener perfil
- ✅ `PUT /api/profile/update-profile-info/{user_id}` - Actualizar perfil
- ✅ `POST /api/profile/upload-image` - Subir imagen
- ❌ `GET /api/profile/membresia` - **FALTA** Obtener membresía del usuario
- ❌ `POST /api/profile/progreso` - **FALTA** Guardar peso/IMC

### Clases y Agenda
- ✅ `GET /api/profile/me/reservas` - Obtener mis reservas (para ScheduleScreen)
- ❌ `GET /api/agenda/clases` - **FALTA** Obtener todas las clases disponibles
- ❌ `GET /api/agenda/clases/{coachId}` - **FALTA** Obtener clases de un coach específico
- ✅ `GET /api/agenda/{id}/asistencia` - Obtener asistencia por clase

### Reservas
- ✅ `POST /api/reservas/clase/{claseId}` - Reservar clase
- ❌ `DELETE /api/reservas/{id}` - **FALTA** Cancelar reserva
- ✅ `POST /api/reservas/{id}/marcar-asistencia` - Marcar asistencia (para Coach)

### Membresías
- ✅ `POST /api/admin/membresias/activar` - Activar membresía
- ❌ `GET /api/profile/membresia` - **FALTA** Obtener membresía del usuario actual

## 📋 Resumen: Lo que Funciona vs Lo que Falta

### ✅ Funcionalidades Completas
1. **Autenticación básica** (username/password)
2. **Registro de usuarios**
3. **Gestión de perfil**
4. **Reservar clases**
5. **Ver mis reservas**
6. **Marcar asistencia** (para coaches)

### ⚠️ Funcionalidades Parciales
1. **Membresías** - Solo activación, falta obtener membresía del usuario
2. **Agenda** - Solo asistencia por clase, falta listar todas las clases

### ❌ Funcionalidades Faltantes
1. **Login con Facebook** (OAuth)
2. **Validación por SMS** en registro
3. **Recuperación de contraseña** (ForgotPassword)
4. **Cancelar reserva**
5. **Obtener todas las clases disponibles** (para ScheduleScreen)
6. **Obtener clases del coach** (para MyScheduleScreen del Coach)
7. **Guardar peso/IMC** (Progreso)
8. **Obtener membresía del usuario** (días restantes, tipo, etc.)

## 🔧 Recomendaciones

### Para Desarrollo Inicial (MVP)
La API actual puede soportar un MVP básico si:
1. Se implementan los endpoints faltantes críticos:
   - `GET /api/agenda/clases` - Listar clases disponibles
   - `DELETE /api/reservas/{id}` - Cancelar reserva
   - `GET /api/profile/membresia` - Obtener membresía

2. Se adaptan las funcionalidades:
   - **Login con Facebook**: Puede implementarse en el frontend y enviar token a un endpoint personalizado
   - **Validación SMS**: Puede implementarse como servicio externo (Twilio, etc.) y solo validar en frontend
   - **ForgotPassword**: Endpoint crítico que debe agregarse

### Prioridad de Implementación
1. **Alta**: `GET /api/agenda/clases`, `DELETE /api/reservas/{id}`, `GET /api/profile/membresia`
2. **Media**: `POST /auth/forgot-password`, `POST /api/profile/progreso`
3. **Baja**: `POST /auth/facebook`, Validación SMS (puede ser frontend)

## 💡 Solución Temporal para Desarrollo

Mientras se implementan los endpoints faltantes, puedes:
1. **ScheduleScreen**: Usar `GET /api/profile/me/reservas` para mostrar las reservas del usuario
2. **BookingScreen**: Usar `POST /api/reservas/clase/{claseId}` (necesitas el endpoint de listar clases)
3. **MyScheduleScreen (Coach)**: Necesita endpoint específico para obtener clases del coach
4. **PaymentScreen**: Usar `POST /api/admin/membresias/activar` (necesita endpoint para obtener membresía actual)

