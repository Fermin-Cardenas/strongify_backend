# 🔧 Solución Definitiva Error 403 - Crear Clase

## ✅ Estado del Backend

El endpoint **SÍ funciona correctamente**. Se probó exitosamente desde PowerShell con el mismo token que el frontend está usando.

**Prueba exitosa:**
- Token: `eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjb2FjaF9qdWFuIiwiaWF0IjoxNzYzNDgwNzA2LCJleHAiOjE3NjM1NjcxMDZ9...`
- Response: **200 OK**
- Clase creada correctamente

## 🔍 Diagnóstico del Problema

El frontend está recibiendo un **403 Forbidden con body vacío**, lo que indica que **Spring Security está rechazando la petición ANTES de que llegue al controller**.

### Cambios Realizados en el Backend

1. ✅ **Corregido el principal del AuthenticationToken**: Ahora usa el objeto `UserDetails` completo en lugar de solo el username
2. ✅ **Agregado logging detallado**: Para diagnosticar problemas
3. ✅ **Mejorado manejo de OPTIONS**: Para CORS preflight
4. ✅ **Endpoint de debug**: `/api/profile/debug-auth` para verificar el rol

## 🧪 Verificar Logs del Backend en Tiempo Real

Cuando el frontend haga la petición, ejecuta este comando para ver los logs:

```bash
docker logs -f java_app | grep -i "POST.*clases\|403\|forbidden\|authentication\|authority"
```

O en PowerShell:
```powershell
docker logs -f java_app | Select-String -Pattern "POST.*clases|403|Forbidden|Authentication|authority" -CaseSensitive:$false
```

### Logs Esperados

Si todo funciona correctamente, deberías ver:
```
✅ Authentication establecida para: coach_juan
✅ Authorities: [ROLE_COACH]
🔍 POST /api/agenda/clases - Authentication: coach_juan
🔍 Authorities: [ROLE_COACH]
🔍 IsAuthenticated: true
```

Si hay un problema, verás:
```
❌ Token inválido para usuario: ...
❌ Error al cargar UserDetails para: ...
⚠️ Token presente pero username es null
❌ Authentication es null
```

## 🔍 Posibles Causas del 403

### 1. El Token No Se Está Leyendo Correctamente

**Síntoma**: Los logs muestran "Token presente pero username es null" o no hay logs de autenticación.

**Solución**: Verificar que el header `Authorization` tenga exactamente el formato: `Bearer <token>` (con espacio).

### 2. El SecurityContext No Se Está Estableciendo

**Síntoma**: Los logs muestran "Authentication es null" en el controller.

**Solución**: Ya corregido - el principal ahora usa el objeto `UserDetails` completo.

### 3. CORS Preflight Falla

**Síntoma**: La petición OPTIONS falla antes de la petición POST.

**Solución**: Ya implementado - el JWT filter permite OPTIONS sin validar token.

### 4. El Frontend Está Usando una URL Diferente

**Síntoma**: Los logs no muestran ninguna petición POST a `/api/agenda/clases`.

**Solución**: Verificar que la URL sea exactamente: `http://192.168.0.178:8080/api/agenda/clases`

## ✅ Verificación Paso a Paso

### Paso 1: Verificar que el Endpoint Funciona

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
    "fechaHoraInicio": "2025-11-18T16:00:00",
    "fechaHoraFin": "2025-11-18T17:00:00",
    "duracionMinutos": 60,
    "cupoMaximo": 20
  }'
```

**Si esto funciona**, el backend está bien y el problema está en el frontend.

### Paso 2: Monitorear Logs Mientras el Frontend Hace la Petición

1. Abre una terminal y ejecuta:
   ```bash
   docker logs -f java_app
   ```

2. Desde el frontend, intenta crear una clase

3. Observa los logs en tiempo real

4. Busca estos mensajes:
   - `✅ Authentication establecida para: coach_juan`
   - `🔍 POST /api/agenda/clases`
   - `❌` (cualquier error)

### Paso 3: Verificar el Token del Frontend

El frontend debe verificar que el token sea el mismo que se usa en el endpoint de debug:

```typescript
// En el frontend, después de hacer login
const token = await AsyncStorage.getItem('token');
console.log('Token completo:', token);

// Usar este mismo token en la petición de crear clase
```

## 🛠️ Solución Temporal: Probar con curl desde el Frontend

Si el problema persiste, el frontend puede probar hacer la petición exactamente como curl:

```typescript
const crearClase = async (claseData) => {
  const token = await AsyncStorage.getItem('token');
  const API_URL = 'http://192.168.0.178:8080';
  
  // Preparar exactamente como curl
  const url = `${API_URL}/api/agenda/clases`;
  const headers = {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  };
  const body = JSON.stringify(claseData);
  
  console.log('🔍 URL:', url);
  console.log('🔍 Headers:', headers);
  console.log('🔍 Body:', body);
  
  try {
    const response = await fetch(url, {
      method: 'POST',
      headers: headers,
      body: body
    });
    
    console.log('📡 Status:', response.status);
    console.log('📡 Status Text:', response.statusText);
    
    const text = await response.text();
    console.log('📡 Response Text:', text);
    
    if (!response.ok) {
      let errorData;
      try {
        errorData = JSON.parse(text);
      } catch {
        errorData = { message: text || `Error ${response.status}` };
      }
      throw new Error(errorData.message || `Error ${response.status}`);
    }
    
    return JSON.parse(text);
  } catch (error) {
    console.error('❌ Error:', error);
    throw error;
  }
};
```

## 📋 Checklist Final

- [ ] El endpoint funciona con curl/Postman
- [ ] Los logs del backend muestran la petición POST
- [ ] Los logs muestran "✅ Authentication establecida"
- [ ] Los logs muestran "🔍 POST /api/agenda/clases"
- [ ] El token del frontend es el mismo que funciona en curl
- [ ] La URL es exactamente `http://192.168.0.178:8080/api/agenda/clases`
- [ ] El header Authorization tiene el formato: `Bearer <token>`
- [ ] El Content-Type es `application/json`

## 🚨 Si el Problema Persiste

1. **Compartir los logs del backend** cuando el frontend hace la petición
2. **Verificar la IP del servidor** (puede haber cambiado)
3. **Probar con un token completamente nuevo** (logout/login)
4. **Verificar que no haya un proxy o interceptor** en el frontend que modifique la petición

---

**Última actualización**: 18 de Noviembre, 2025

