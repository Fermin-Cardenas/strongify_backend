# 🔧 Solución Final Error 403 - Crear Clase

## ✅ Verificación Backend

El endpoint **SÍ funciona correctamente** desde el backend. Se probó exitosamente con:
- Token válido de `coach_juan`
- Response: **200 OK**
- Clase creada correctamente

## 🔍 Diagnóstico del Problema

El frontend está recibiendo un **403 Forbidden** con body vacío, lo que indica que **Spring Security está rechazando la petición antes de que llegue al controller**.

### Posibles Causas:

1. **URL relativa vs absoluta**: El frontend está usando `/api/agenda/clases` (relativa) en lugar de la URL completa
2. **CORS Preflight**: El navegador/envío está haciendo un OPTIONS request que falla
3. **Token no se está enviando correctamente** en la petición POST (aunque se ve en los logs)
4. **El frontend no está leyendo el body del error** correctamente

## ✅ Solución: Verificar Configuración del Frontend

### 1. Verificar la URL Base

Asegúrate de que la URL base esté configurada correctamente:

```typescript
// ❌ INCORRECTO - URL relativa
const url = '/api/agenda/clases';

// ✅ CORRECTO - URL completa
const API_URL = 'http://192.168.0.178:8080'; // O la IP correcta
const url = `${API_URL}/api/agenda/clases`;
```

### 2. Verificar el Formato del Header Authorization

```typescript
// ✅ CORRECTO
const headers = {
  'Authorization': `Bearer ${token}`,  // ⚠️ Espacio después de "Bearer"
  'Content-Type': 'application/json'
};

// ❌ INCORRECTO
const headers = {
  'Authorization': `Bearer${token}`,  // Sin espacio
  // o
  'authorization': `Bearer ${token}`,  // Minúscula (puede causar problemas)
};
```

### 3. Verificar que el Body sea JSON Válido

```typescript
// ✅ CORRECTO
const body = JSON.stringify({
  nombreClase: "Yoga Matutino",
  descripcion: "Clase de yoga",
  fechaHoraInicio: "2025-11-25T10:00:00+00:00",
  duracionMinutos: 60,
  cupoMaximo: 20
});

// ❌ INCORRECTO
const body = {
  nombreClase: "Yoga Matutino",
  // ... sin JSON.stringify()
};
```

### 4. Ejemplo Completo de Petición

```typescript
const crearClase = async (claseData: CrearClaseRequest) => {
  try {
    // 1. Obtener token
    const token = await AsyncStorage.getItem('token');
    if (!token) {
      throw new Error('No hay token de autenticación');
    }

    // 2. Configurar URL completa
    const API_URL = 'http://192.168.0.178:8080'; // ⚠️ Verificar IP actual
    const url = `${API_URL}/api/agenda/clases`;

    // 3. Preparar headers
    const headers = {
      'Authorization': `Bearer ${token.trim()}`,  // ⚠️ Espacio después de "Bearer"
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    };

    // 4. Preparar body
    const body = JSON.stringify({
      nombreClase: claseData.nombreClase,
      descripcion: claseData.descripcion || null,
      fechaHoraInicio: claseData.fechaHoraInicio,  // Formato ISO 8601
      fechaHoraFin: claseData.fechaHoraFin || null,
      duracionMinutos: claseData.duracionMinutos || null,
      cupoMaximo: claseData.cupoMaximo,
      sucursalId: claseData.sucursalId || null,
      catalogoId: claseData.catalogoId || null
    });

    console.log('🔍 URL:', url);
    console.log('🔍 Headers:', { ...headers, Authorization: `Bearer ${token.substring(0, 20)}...` });
    console.log('🔍 Body:', body);

    // 5. Hacer petición
    const response = await fetch(url, {
      method: 'POST',
      headers: headers,
      body: body
    });

    console.log('📡 Response Status:', response.status);
    console.log('📡 Response Headers:', Object.fromEntries(response.headers.entries()));

    // 6. Leer respuesta
    const responseText = await response.text();
    console.log('📡 Response Body (text):', responseText);

    if (!response.ok) {
      let errorData;
      try {
        errorData = JSON.parse(responseText);
      } catch {
        errorData = { message: responseText || `Error ${response.status}` };
      }
      
      console.error('❌ Error Response:', errorData);
      
      if (response.status === 403) {
        throw new Error(errorData.message || 'No tienes permisos para crear clases');
      } else if (response.status === 401) {
        throw new Error('Token expirado. Por favor, inicia sesión nuevamente.');
      } else {
        throw new Error(errorData.message || `Error ${response.status}: ${response.statusText}`);
      }
    }

    const data = JSON.parse(responseText);
    console.log('✅ Clase creada:', data);
    return data;

  } catch (error) {
    console.error('❌ Error al crear clase:', error);
    throw error;
  }
};
```

### 5. Usando Axios (si aplica)

```typescript
import axios from 'axios';

const crearClase = async (claseData: CrearClaseRequest) => {
  const token = await AsyncStorage.getItem('token');
  const API_URL = 'http://192.168.0.178:8080';
  
  try {
    const response = await axios.post(
      `${API_URL}/api/agenda/clases`,
      claseData,
      {
        headers: {
          'Authorization': `Bearer ${token}`,  // ⚠️ Formato correcto
          'Content-Type': 'application/json'
        }
      }
    );
    
    return response.data;
  } catch (error) {
    if (error.response) {
      console.error('Error response:', error.response.data);
      console.error('Error status:', error.response.status);
      throw new Error(error.response.data?.message || 'Error al crear clase');
    }
    throw error;
  }
};
```

## 🧪 Prueba de Diagnóstico

Antes de crear la clase, prueba estos endpoints para verificar la configuración:

### 1. Probar el Endpoint de Debug

```typescript
const verificarAuth = async () => {
  const token = await AsyncStorage.getItem('token');
  const API_URL = 'http://192.168.0.178:8080';
  
  const response = await fetch(`${API_URL}/api/profile/debug-auth`, {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  });
  
  const data = await response.json();
  console.log('🔍 Debug Auth:', data);
  
  if (!data.isCoach) {
    console.error('❌ El usuario NO es coach');
    console.log('Username:', data.username);
    console.log('Authorities:', data.authorities);
  }
  
  return data;
};
```

### 2. Probar con curl (para verificar)

```bash
# Obtener token
TOKEN=$(curl -X POST http://192.168.0.178:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"coach_juan","password":"coach123"}' \
  | jq -r '.token')

# Crear clase
curl -v -X POST http://192.168.0.178:8080/api/agenda/clases \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nombreClase": "Test",
    "descripcion": "Test",
    "fechaHoraInicio": "2025-11-25T10:00:00+00:00",
    "duracionMinutos": 60,
    "cupoMaximo": 20
  }'
```

## 🔍 Debugging Adicional

### Agregar Logs en el Frontend

```typescript
// Antes de la petición
console.log('🔍 URL completa:', url);
console.log('🔍 Token length:', token?.length);
console.log('🔍 Token preview:', token?.substring(0, 50) + '...');
console.log('🔍 Headers:', {
  ...headers,
  Authorization: `Bearer ${token?.substring(0, 20)}...`
});
console.log('🔍 Body:', body);

// Después de la petición
console.log('📡 Status:', response.status);
console.log('📡 Status Text:', response.statusText);
console.log('📡 Headers:', Object.fromEntries(response.headers.entries()));

// Leer el body como texto primero
const text = await response.text();
console.log('📡 Response Text:', text);

// Luego parsear si es JSON
try {
  const json = JSON.parse(text);
  console.log('📡 Response JSON:', json);
} catch {
  console.log('📡 Response no es JSON válido');
}
```

## ✅ Checklist de Verificación

- [ ] La URL es **absoluta** (no relativa): `http://192.168.0.178:8080/api/agenda/clases`
- [ ] El header Authorization tiene el formato: `Bearer <token>` (con espacio)
- [ ] El token no está vacío o null
- [ ] El Content-Type es `application/json`
- [ ] El body está en formato JSON válido (usar `JSON.stringify()`)
- [ ] El método HTTP es `POST`
- [ ] El endpoint de debug (`/api/profile/debug-auth`) muestra `isCoach: true`
- [ ] La IP del servidor es correcta (`192.168.0.178` o la IP actual)

## 🚨 Si el Problema Persiste

1. **Verificar logs del backend:**
   ```bash
   docker logs java_app --tail 100 | grep -i "403\|forbidden\|post.*clases"
   ```

2. **Probar con Postman o curl** para verificar que el endpoint funciona

3. **Verificar la IP del servidor:**
   ```bash
   ipconfig | findstr "IPv4"
   ```

4. **Verificar que el firewall permite conexiones** en el puerto 8080

5. **Probar con un token fresco:**
   - Hacer logout
   - Hacer login nuevamente
   - Intentar crear la clase con el nuevo token

---

**Última actualización**: 18 de Noviembre, 2025

