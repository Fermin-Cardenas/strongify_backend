# 🔍 Debug Error 403 - Crear Clase

## ❓ Pregunta: ¿Solo el admin puede crear clases?

**NO**, tanto los **coaches** como los **admins** pueden crear clases. El endpoint está configurado para permitir usuarios con rol `ROLE_COACH`.

## 🔍 Diagnóstico del Problema

El error 403 indica que el usuario autenticado **NO tiene el rol COACH**. Esto puede deberse a:

1. **El token es de un usuario normal (USER)**, no de un coach
2. **El token expiró** y los authorities no se cargaron correctamente
3. **El usuario no tiene rol COACH** en la base de datos

## ✅ Solución: Endpoint de Debug

He agregado un endpoint de debug para que el frontend pueda verificar el rol del usuario:

### **GET /api/profile/debug-auth**

Este endpoint muestra información sobre la autenticación actual del usuario.

**Headers:**
```
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
{
  "authenticated": true,
  "username": "coach_juan",
  "authorities": ["ROLE_COACH"],
  "isCoach": true,
  "hasRoleCOACH": true
}
```

**Si el usuario NO es coach:**
```json
{
  "authenticated": true,
  "username": "usuario_normal",
  "authorities": ["ROLE_USER"],
  "isCoach": false,
  "hasRoleCOACH": false
}
```

## 🛠️ Cómo Usar el Endpoint de Debug

### 1. Verificar el Rol del Usuario

```typescript
const verificarRol = async () => {
  const token = await AsyncStorage.getItem('token');
  
  const response = await fetch(`${API_URL}/api/profile/debug-auth`, {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  });
  
  const data = await response.json();
  console.log('Debug Auth:', data);
  
  if (!data.isCoach) {
    console.error('❌ El usuario NO es coach');
    console.log('Username:', data.username);
    console.log('Authorities:', data.authorities);
  } else {
    console.log('✅ El usuario ES coach');
  }
  
  return data;
};
```

### 2. Verificar Antes de Crear Clase

```typescript
const crearClase = async (claseData) => {
  // 1. Verificar rol primero
  const authInfo = await verificarRol();
  
  if (!authInfo.isCoach) {
    throw new Error(`Usuario ${authInfo.username} no tiene rol COACH. Authorities: ${authInfo.authorities.join(', ')}`);
  }
  
  // 2. Si es coach, proceder a crear la clase
  const token = await AsyncStorage.getItem('token');
  const response = await fetch(`${API_URL}/api/agenda/clases`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(claseData)
  });
  
  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}));
    console.error('Error al crear clase:', errorData);
    throw new Error(errorData.message || 'Error al crear clase');
  }
  
  return await response.json();
};
```

## 📋 Checklist de Verificación

Antes de intentar crear una clase, verificar:

- [ ] El usuario inició sesión con un usuario **coach** (username debe comenzar con `coach_`)
- [ ] El token no ha expirado (los tokens expiran después de 24 horas)
- [ ] El endpoint `/api/profile/debug-auth` muestra `"isCoach": true`
- [ ] El header Authorization tiene el formato correcto: `Bearer <token>`

## 🔄 Solución Rápida

Si el usuario NO es coach:

1. **Hacer logout**
2. **Hacer login con un usuario coach:**
   - Username: `coach_juan`
   - Password: `coach123`
3. **Verificar con el endpoint de debug**
4. **Intentar crear la clase nuevamente**

## 📝 Mejoras Implementadas

1. **Endpoint de debug** (`/api/profile/debug-auth`) para verificar el rol
2. **Mensajes de error mejorados** que incluyen el username y authorities cuando hay un 403
3. **Mejor logging** en el backend para diagnosticar problemas

## 🧪 Probar el Endpoint de Debug

```bash
# 1. Obtener token de un coach
TOKEN=$(curl -X POST http://192.168.0.178:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"coach_juan","password":"coach123"}' \
  | jq -r '.token')

# 2. Verificar rol
curl -X GET http://192.168.0.178:8080/api/profile/debug-auth \
  -H "Authorization: Bearer $TOKEN"
```

**Respuesta esperada:**
```json
{
  "authenticated": true,
  "username": "coach_juan",
  "authorities": ["ROLE_COACH"],
  "isCoach": true,
  "hasRoleCOACH": true
}
```

---

**Última actualización**: 18 de Noviembre, 2025

