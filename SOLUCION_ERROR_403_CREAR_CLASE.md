# 🔧 Solución Error 403 al Crear Clase

## 🎯 Problema

El frontend recibe un error **403 Forbidden** al intentar crear una clase, aunque el endpoint funciona correctamente desde el backend.

## ✅ Verificación del Backend

El endpoint `POST /api/agenda/clases` está funcionando correctamente. Se probó exitosamente con:
- Usuario: `coach_juan`
- Token JWT válido
- Response: 200 OK

## 🔍 Posibles Causas del Error 403

### 1. **Token no se está enviando correctamente**

El header `Authorization` debe tener el formato exacto:
```
Authorization: Bearer <token>
```

**Importante:**
- Debe incluir la palabra "Bearer" seguida de un espacio
- El token debe estar completo (sin espacios adicionales)
- El header debe llamarse exactamente "Authorization" (case-sensitive en algunos casos)

### 2. **Token expirado**

Los tokens JWT expiran después de 24 horas. Si el token expiró, el frontend debe:
1. Detectar el error 401/403
2. Hacer logout automático
3. Redirigir al login

### 3. **Token de un usuario sin rol COACH**

Solo usuarios con rol `COACH` pueden crear clases. Verificar que:
- El usuario que inició sesión tiene rol COACH
- El token corresponde a un usuario coach (username debe comenzar con `coach_`)

### 4. **CORS o Headers incorrectos**

Aunque CORS está configurado para permitir todos los orígenes, verificar que:
- El header `Content-Type: application/json` esté presente
- No haya headers adicionales que puedan causar problemas

## 🛠️ Solución en el Frontend

### Verificación del Token

```typescript
// Verificar que el token existe y no está vacío
const token = await AsyncStorage.getItem('token');
if (!token || token.trim() === '') {
  console.error('Token no encontrado');
  // Redirigir al login
  return;
}
```

### Formato Correcto del Header

```typescript
const headers = {
  'Authorization': `Bearer ${token}`,  // ⚠️ IMPORTANTE: "Bearer " con espacio
  'Content-Type': 'application/json'
};
```

### Ejemplo Completo de Petición

```typescript
const crearClase = async (claseData: CrearClaseRequest) => {
  try {
    // 1. Obtener token
    const token = await AsyncStorage.getItem('token');
    if (!token) {
      throw new Error('No hay token de autenticación');
    }

    // 2. Verificar que el usuario es coach
    const userRole = await AsyncStorage.getItem('userRole');
    if (userRole !== 'COACH') {
      throw new Error('Solo los coaches pueden crear clases');
    }

    // 3. Preparar headers
    const headers = {
      'Authorization': `Bearer ${token.trim()}`,  // ⚠️ Espacio después de "Bearer"
      'Content-Type': 'application/json'
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

    // 5. Hacer petición
    const response = await fetch(`${API_URL}/api/agenda/clases`, {
      method: 'POST',
      headers: headers,
      body: body
    });

    // 6. Manejar respuesta
    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      console.error('Error response:', errorData);
      
      if (response.status === 403) {
        throw new Error('No tienes permisos para crear clases. Verifica que seas un coach.');
      } else if (response.status === 401) {
        throw new Error('Token expirado. Por favor, inicia sesión nuevamente.');
      } else {
        throw new Error(errorData.message || `Error ${response.status}: ${response.statusText}`);
      }
    }

    const data = await response.json();
    return data;

  } catch (error) {
    console.error('Error al crear clase:', error);
    throw error;
  }
};
```

### Usando Axios (si aplica)

```typescript
import axios from 'axios';

const crearClase = async (claseData: CrearClaseRequest) => {
  const token = await AsyncStorage.getItem('token');
  
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
};
```

## 🧪 Debugging

### 1. Verificar el Token

Agregar logs para verificar el token:

```typescript
const token = await AsyncStorage.getItem('token');
console.log('Token length:', token?.length);
console.log('Token preview:', token?.substring(0, 50) + '...');
console.log('Token completo:', token);
```

### 2. Verificar Headers

Agregar logs antes de la petición:

```typescript
console.log('Headers:', {
  'Authorization': `Bearer ${token?.substring(0, 20)}...`,
  'Content-Type': 'application/json'
});
```

### 3. Verificar Response

```typescript
console.log('Response status:', response.status);
console.log('Response headers:', response.headers);
const responseData = await response.json();
console.log('Response data:', responseData);
```

### 4. Probar con curl/Postman

Para verificar que el endpoint funciona, probar con:

```bash
# 1. Obtener token
TOKEN=$(curl -X POST http://192.168.0.178:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"coach_juan","password":"coach123"}' \
  | jq -r '.token')

# 2. Crear clase
curl -X POST http://192.168.0.178:8080/api/agenda/clases \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nombreClase": "Test Clase",
    "descripcion": "Test",
    "fechaHoraInicio": "2025-11-25T10:00:00+00:00",
    "duracionMinutos": 60,
    "cupoMaximo": 20
  }'
```

## ✅ Checklist de Verificación

- [ ] El token se está obteniendo correctamente del almacenamiento
- [ ] El token no está vacío o null
- [ ] El header Authorization tiene el formato: `Bearer <token>` (con espacio)
- [ ] El token no ha expirado (verificar fecha de expiración)
- [ ] El usuario que inició sesión tiene rol COACH
- [ ] El Content-Type está configurado como `application/json`
- [ ] La URL del endpoint es correcta: `${API_URL}/api/agenda/clases`
- [ ] El método HTTP es POST
- [ ] Los datos del body están en formato JSON válido

## 🔄 Si el Problema Persiste

1. **Verificar logs del backend:**
   ```bash
   docker logs java_app --tail 50
   ```

2. **Verificar que el usuario tiene rol COACH:**
   ```sql
   SELECT u.user_id, a.username, r.name as rol 
   FROM user_info u 
   JOIN auth_info a ON u.user_id = a.user_id 
   JOIN role r ON a.rol_id = r.rol_id 
   WHERE a.username = 'coach_juan';
   ```

3. **Probar con un nuevo token:**
   - Hacer logout
   - Hacer login nuevamente
   - Intentar crear la clase con el nuevo token

4. **Verificar la IP del servidor:**
   - Asegurarse de usar la IP correcta: `http://192.168.0.178:8080`
   - Verificar que el firewall permite conexiones

## 📝 Notas Importantes

- El endpoint **SÍ funciona** desde el backend (probado exitosamente)
- El problema está en cómo el frontend está enviando la petición
- El formato del header Authorization es crítico: debe ser exactamente `Bearer <token>`
- Los tokens expiran después de 24 horas

---

**Última actualización**: 18 de Noviembre, 2025

