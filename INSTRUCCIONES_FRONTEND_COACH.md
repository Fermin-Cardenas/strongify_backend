# 📋 Instrucciones Frontend - Integración de Usuarios Coach

## 🎯 Resumen de Cambios

Se ha implementado el **rol COACH** en el backend y se han creado 3 usuarios de prueba con este rol para desarrollo del frontend del coach.

---

## ✅ Cambios Realizados en el Backend

### 1. Nuevo Rol: COACH
- **ID del Rol**: `2`
- **Nombre**: `COACH`
- **Descripción**: "Entrenador/Coach del gimnasio"
- **Authority en Spring Security**: `ROLE_COACH`

### 2. Usuarios Coach Creados

Se han creado 3 usuarios coach de prueba con las siguientes credenciales:

| Username | Contraseña | Nombre Completo | Teléfono |
|----------|------------|------------------|----------|
| `coach_juan` | `coach123` | Juan Pérez | 5512345678 |
| `coach_maria` | `coach123` | María González | 5598765432 |
| `coach_carlos` | `coach123` | Carlos Rodríguez | 5555555555 |

**Nota**: Todos los usuarios coach tienen la misma contraseña: `coach123`

---

## 🔐 Cómo Identificar el Rol del Usuario

### Opción 1: Decodificar el JWT (Recomendado)

El JWT contiene el `username` del usuario. Los **authorities** (roles) están en el token pero necesitas decodificarlo.

**Estructura del JWT:**
```json
{
  "sub": "coach_juan",  // username
  "iat": 1234567890,    // issued at
  "exp": 1234654290     // expiration
}
```

**Authorities en Spring Security:**
- Usuario normal: `ROLE_USER`
- Coach: `ROLE_COACH`
- Admin: `ROLE_ADMIN` (si existe)

### Opción 2: Endpoint para Obtener el Rol (Si se implementa)

Actualmente el endpoint `GET /api/profile/get-profile-info` **NO incluye el rol** en la respuesta. 

**Respuesta actual:**
```json
{
  "id": 3,
  "firstName": "Juan",
  "lastName": "Pérez",
  "phoneNumber": "5512345678",
  "photoUrl": null,
  "username": "coach_juan",
  "birthday": "1990-05-15",
  "lastLogin": null,
  "altura": null
}
```

**Solución temporal**: Puedes identificar usuarios coach por su `username` que comienza con `"coach_"` o hacer una petición adicional al backend para obtener el rol.

### Opción 3: Verificar el Rol desde el Backend

Si necesitas verificar el rol del usuario, puedes:

1. **Hacer una petición al endpoint de perfil** y verificar el `username`
2. **Decodificar el JWT** para obtener el username y verificar si comienza con `"coach."`
3. **Implementar un endpoint adicional** que devuelva el rol del usuario (requiere modificación del backend)

---

## 🚀 Flujo de Autenticación para Coach

### 1. Login

**Endpoint**: `POST /api/auth/login`

**Request:**
```json
{
  "username": "coach_juan",
  "password": "coach123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### 2. Guardar el Token

Guarda el token JWT en el almacenamiento local/sesión del dispositivo.

### 3. Verificar el Rol

**Método recomendado**: Decodificar el JWT o verificar el `username`:

```javascript
// Ejemplo en JavaScript/TypeScript
const token = localStorage.getItem('token');
const decoded = jwt_decode(token); // Usa una librería como jwt-decode
const username = decoded.sub;

// Verificar si es coach
const isCoach = username.startsWith('coach_') || 
                username.includes('coach');

// O verificar el authority del token si está disponible
```

### 4. Redirigir según el Rol

```javascript
if (isCoach) {
  // Redirigir al dashboard del coach
  navigation.navigate('CoachDashboard');
} else {
  // Redirigir al dashboard del usuario
  navigation.navigate('UserDashboard');
}
```

---

## 📡 Endpoints Disponibles para Coach

Todos los endpoints requieren autenticación JWT. El backend verificará automáticamente los permisos según el rol.

### Endpoints que un Coach puede usar:

1. **Perfil de Usuario**
   - `GET /api/profile/get-profile-info` - Obtener perfil
   - `PUT /api/profile/update-profile-info/{user_id}` - Actualizar perfil

2. **Clases** (si están implementados)
   - `GET /api/agenda/clases` - Listar todas las clases
   - `GET /api/agenda/coach/{coachId}/clases` - Clases de un coach específico

3. **Reservas** (si el coach puede ver reservas de sus clases)
   - `GET /api/profile/me/reservas` - Ver reservas del usuario

**Nota**: Los endpoints específicos para coach (como gestionar clases, ver alumnos, etc.) deben ser implementados según las necesidades del frontend.

---

## 🔒 Seguridad y Permisos

### Verificación de Rol en el Backend

El backend usa Spring Security con `@PreAuthorize` para proteger endpoints:

```java
@PreAuthorize("hasRole('COACH')")  // Solo coaches
@PreAuthorize("hasRole('ADMIN')")  // Solo admins
@PreAuthorize("hasRole('USER')")   // Solo usuarios normales
```

### Manejo de Errores

Si un usuario sin el rol adecuado intenta acceder a un endpoint protegido:

**Response (403 Forbidden):**
```json
{
  "error": "Access Denied",
  "message": "No tienes permisos para acceder a este recurso"
}
```

---

## 🧪 Pruebas Recomendadas

### 1. Probar Login de Coach

```bash
curl -X POST http://192.168.0.178:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "coach_juan",
    "password": "coach123"
  }'
```

### 2. Probar Obtener Perfil

```bash
curl -X GET http://192.168.0.178:8080/api/profile/get-profile-info \
  -H "Authorization: Bearer <token>"
```

### 3. Verificar que el Usuario es Coach

- El `username` debe comenzar con `"coach."`
- O decodificar el JWT para verificar los authorities

---

## 📝 Notas Importantes

1. **El rol no está en UserResponse**: Actualmente el endpoint de perfil no devuelve el rol. Si necesitas el rol explícitamente, considera:
   - Decodificar el JWT
   - Verificar el patrón del username (debe comenzar con `coach_`)
   - Solicitar al backend que agregue el rol a `UserResponse`

2. **Contraseñas de Prueba**: Todos los usuarios coach tienen la contraseña `coach123`. En producción, cada usuario debe tener su propia contraseña segura.

3. **Formato de Username**: Los nombres de usuario solo pueden contener letras, números y guiones bajos (`_`). Los usuarios coach siguen el patrón `coach_<nombre>`.

4. **IP del Servidor**: Asegúrate de usar la IP correcta del servidor:
   - Desarrollo local: `http://localhost:8080`
   - Red local: `http://192.168.0.178:8080` (verificar IP actual)

5. **Firewall**: Asegúrate de que el firewall de Windows permita conexiones entrantes en el puerto 8080.

---

## 🛠️ Implementación Sugerida en el Frontend

### 1. Servicio de Autenticación

```typescript
// authService.ts
export const login = async (username: string, password: string) => {
  const response = await fetch(`${API_URL}/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password })
  });
  
  const data = await response.json();
  const token = data.token;
  
  // Guardar token
  await AsyncStorage.setItem('token', token);
  
  // Decodificar para obtener username
  const decoded = jwt_decode(token);
  const userRole = determineUserRole(decoded.sub);
  
  // Guardar rol
  await AsyncStorage.setItem('userRole', userRole);
  
  return { token, role: userRole };
};

const determineUserRole = (username: string): string => {
  if (username.startsWith('coach_')) {
    return 'COACH';
  }
  return 'USER';
};
```

### 2. Hook de Autenticación

```typescript
// useAuth.ts
export const useAuth = () => {
  const [userRole, setUserRole] = useState<string | null>(null);
  
  useEffect(() => {
    const loadUserRole = async () => {
      const role = await AsyncStorage.getItem('userRole');
      setUserRole(role);
    };
    loadUserRole();
  }, []);
  
  const isCoach = userRole === 'COACH';
  const isUser = userRole === 'USER';
  
  return { userRole, isCoach, isUser };
};
```

### 3. Navegación Condicional

```typescript
// App.tsx o Navigation.tsx
const AppNavigator = () => {
  const { isCoach } = useAuth();
  
  return (
    <NavigationContainer>
      {isCoach ? (
        <CoachStackNavigator />
      ) : (
        <UserStackNavigator />
      )}
    </NavigationContainer>
  );
};
```

---

## 📞 Soporte

Si necesitas:
- Agregar más usuarios coach
- Modificar el endpoint de perfil para incluir el rol
- Implementar endpoints específicos para coach
- Cualquier otra modificación

Contacta al desarrollador del backend.

---

## ✅ Checklist de Integración

- [ ] Probar login con usuarios coach
- [ ] Implementar decodificación de JWT o verificación de username
- [ ] Crear lógica de redirección según rol
- [ ] Implementar navegación específica para coach
- [ ] Probar endpoints protegidos con rol COACH
- [ ] Manejar errores 403 (Forbidden)
- [ ] Actualizar UI para mostrar información del coach
- [ ] Probar en dispositivos físicos (verificar IP del servidor)

---

**Última actualización**: 18 de Noviembre, 2025

