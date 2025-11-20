# Comparación: API Backend vs Requisitos App Móvil

## 📊 Resumen Ejecutivo

**Estado General**: ⚠️ **PARCIALMENTE COMPLETA** - La API tiene aproximadamente **70% de los endpoints necesarios**.

La API actual puede soportar las funcionalidades básicas de la app móvil, pero faltan algunos endpoints críticos para una experiencia completa.

---

## ✅ Endpoints que SÍ existen en la API

### Autenticación
- ✅ `POST /auth/login` - Login con username/password
- ✅ `POST /auth/register` - Registro de usuarios

### Perfil de Usuario
- ✅ `GET /api/profile/get-profile-info` - Obtener perfil del usuario autenticado
- ✅ `PUT /api/profile/update-profile-info/{user_id}` - Actualizar perfil
- ✅ `PUT /api/profile/change-password` - Cambiar contraseña
- ✅ `POST /api/profile/upload-image` - Subir imagen de perfil

### Reservas
- ✅ `GET /api/profile/me/reservas` - Obtener mis reservas (para ScheduleScreen)
- ✅ `POST /api/reservas/clase/{claseId}` - Reservar una clase
- ✅ `POST /api/reservas/{id}/marcar-asistencia` - Marcar asistencia (para Coach)

### Agenda/Asistencia
- ✅ `GET /api/agenda/{id}/asistencia` - Obtener asistencia por clase (para Coach)

### Membresías
- ✅ `POST /api/admin/membresias/activar` - Activar membresía

---

## ❌ Endpoints que NO existen (Faltantes)

### 🔴 Alta Prioridad (Críticos para funcionalidad básica)

1. **`GET /api/agenda/clases`** 
   - **Uso en app**: `app/(tabs)/agenda/index.tsx` - Listar todas las clases disponibles para reservar
   - **Estado actual**: ⚠️ La app usa mocks como fallback
   - **Impacto**: Sin este endpoint, los usuarios no pueden ver las clases disponibles para reservar

2. **`GET /api/agenda/coach/{coachId}/clases`**
   - **Uso en app**: `app/(coach)/agenda/index.tsx` - Agenda del coach
   - **Estado actual**: ⚠️ La app usa mocks como fallback
   - **Impacto**: Los coaches no pueden ver sus clases asignadas

3. **`DELETE /api/reservas/{id}`**
   - **Uso en app**: `app/(tabs)/perfil/mis-reservas.tsx` - Cancelar reserva
   - **Estado actual**: ⚠️ La app usa mocks como fallback
   - **Impacto**: Los usuarios no pueden cancelar sus reservas

### 🟡 Media Prioridad (Importantes para UX completa)

4. **`GET /api/profile/membresia`**
   - **Uso en app**: `app/(tabs)/payment.tsx` - Mostrar membresía actual del usuario
   - **Estado actual**: ❌ Solo UI, sin integración
   - **Impacto**: Los usuarios no pueden ver su membresía activa, días restantes, etc.

5. **`POST /auth/forgot-password`**
   - **Uso en app**: `app/(auth)/forgot-password.tsx` - Recuperar contraseña
   - **Estado actual**: ❌ Solo UI, sin integración
   - **Impacto**: Los usuarios no pueden recuperar su contraseña si la olvidan

6. **`POST /api/profile/progreso`**
   - **Uso en app**: `app/(tabs)/perfil/registrar-peso.tsx` - Guardar peso/IMC
   - **Estado actual**: ❌ Solo UI, sin integración
   - **Impacto**: Los usuarios no pueden registrar su progreso físico

### 🟢 Baja Prioridad (Funcionalidades adicionales)

7. **Endpoints de métodos de pago**
   - **Uso en app**: `app/(tabs)/perfil/metodos-pago.tsx` - CRUD de tarjetas
   - **Estado actual**: ❌ Solo UI, sin integración
   - **Impacto**: Funcionalidad de pago limitada

8. **Login con Facebook**
   - **Uso en app**: `app/(auth)/login.tsx` - Opción de login con Facebook
   - **Estado actual**: ❌ No implementado
   - **Impacto**: Solo login con username/password disponible

---

## 📋 Matriz de Cobertura por Funcionalidad

| Funcionalidad | Estado | Endpoints Disponibles | Endpoints Faltantes |
|--------------|--------|---------------------|-------------------|
| **Autenticación** | ⚠️ 66% | Login, Register | Forgot Password, Facebook |
| **Perfil** | ✅ 80% | Get, Update, Change Password, Upload Image | Progreso (peso/IMC) |
| **Reservas** | ⚠️ 66% | Listar, Reservar, Marcar Asistencia | Cancelar |
| **Agenda/Clases** | ⚠️ 33% | Asistencia por clase | Listar todas, Listar por coach |
| **Membresías** | ⚠️ 50% | Activar | Obtener membresía actual |
| **Pagos** | ❌ 0% | - | CRUD tarjetas |

---

## 🎯 ¿Puede la app móvil funcionar con la API actual?

### ✅ **SÍ, pero con limitaciones:**

**Funcionalidades que SÍ funcionan completamente:**
1. ✅ Login y registro de usuarios
2. ✅ Ver y editar perfil
3. ✅ Ver mis reservas
4. ✅ Reservar clases (si se implementa el endpoint de listar clases)
5. ✅ Marcar asistencia (para coaches)
6. ✅ Activar membresía

**Funcionalidades que NO funcionan:**
1. ❌ Ver clases disponibles para reservar (falta `GET /api/agenda/clases`)
2. ❌ Ver agenda del coach (falta `GET /api/agenda/coach/{coachId}/clases`)
3. ❌ Cancelar reservas (falta `DELETE /api/reservas/{id}`)
4. ❌ Ver membresía actual (falta `GET /api/profile/membresia`)
5. ❌ Recuperar contraseña (falta `POST /auth/forgot-password`)
6. ❌ Registrar peso/IMC (falta `POST /api/profile/progreso`)

---

## 🔧 Recomendaciones

### Para un MVP Funcional (Prioridad Alta)

**Implementar estos 3 endpoints primero:**
1. `GET /api/agenda/clases` - **CRÍTICO** para que los usuarios puedan ver y reservar clases
2. `DELETE /api/reservas/{id}` - **IMPORTANTE** para permitir cancelar reservas
3. `GET /api/profile/membresia` - **IMPORTANTE** para mostrar estado de membresía

Con estos 3 endpoints, la app móvil tendría aproximadamente **85% de funcionalidad completa**.

### Para Funcionalidad Completa (Prioridad Media)

**Implementar estos 3 endpoints adicionales:**
4. `GET /api/agenda/coach/{coachId}/clases` - Para agenda del coach
5. `POST /auth/forgot-password` - Para recuperación de contraseña
6. `POST /api/profile/progreso` - Para registro de progreso físico

### Para Funcionalidades Avanzadas (Prioridad Baja)

7. Endpoints de métodos de pago (CRUD tarjetas)
8. Login con Facebook (OAuth)

---

## 📝 Notas Técnicas

### Endpoints que requieren autenticación JWT:
- Todos los endpoints bajo `/api/` requieren token JWT en el header `Authorization: Bearer <token>`
- Los únicos endpoints públicos son `/auth/login` y `/auth/register`

### Formato de datos:
- **Fechas**: Formato ISO 8601 (`"YYYY-MM-DD"` para fechas simples, `"YYYY-MM-DDTHH:mm:ssZ"` para fechas con hora)
- **Booleanos**: `true` o `false` (no strings)
- **Enums**: Valores exactos requeridos (ej: `"MENSUAL"`, `"PAGO_SEMANAL"`, etc.)

### CORS:
- La API está configurada para permitir CORS desde cualquier origen en desarrollo
- Para React Native:
  - Android Emulator: `http://10.0.2.2:8080`
  - iOS Simulator: `http://localhost:8080`
  - Dispositivo físico: IP local de la computadora

---

## ✅ Conclusión

**Respuesta directa**: La API **NO está completamente lista** para la app móvil, pero tiene **suficiente funcionalidad base** para un MVP.

**Cobertura actual**: ~70%
**Cobertura necesaria para MVP**: ~85% (faltan 3 endpoints críticos)
**Cobertura necesaria para funcionalidad completa**: ~95% (faltan 6 endpoints)

**Recomendación**: Implementar los 3 endpoints de alta prioridad antes de lanzar el MVP, y los 3 de media prioridad para una experiencia completa.

