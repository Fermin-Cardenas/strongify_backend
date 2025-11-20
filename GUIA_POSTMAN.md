# Guía Rápida - Pruebas en Postman

## ⚠️ Requisito Previo: Docker

**Antes de comenzar**, asegúrate de que los contenedores Docker estén corriendo:

```bash
# Verificar que los contenedores estén activos
docker ps

# Si no están corriendo, iniciarlos:
docker compose build java_app
docker compose up -d
```

Deberías ver los contenedores `java_app` y `java_db` en la lista.

## Importar Colección y Environment

### Paso 1: Importar Environment
1. Abre Postman
2. Haz clic en "Environments" en el menú lateral izquierdo
3. Haz clic en "Import" (botón en la esquina superior izquierda)
4. Selecciona el archivo `GymApp_Local.postman_environment.json`
5. El environment "GymApp Local" aparecerá en tu lista

### Paso 2: Importar Colección
1. Haz clic en "Collections" en el menú lateral izquierdo
2. Haz clic en "Import" (botón en la esquina superior izquierda)
3. Selecciona el archivo `GymApp_API.postman_collection.json`
4. La colección "GymApp API" aparecerá en tu lista

### Paso 3: Seleccionar Environment
1. En la esquina superior derecha de Postman, verás un dropdown de environments
2. Selecciona "GymApp Local"

## Flujo de Pruebas Recomendado

### 1. Primero: Autenticación
1. Ve a la carpeta "Autenticación" en la colección
2. Ejecuta **"Registro"** o **"Login"**
3. El token se guardará automáticamente en la variable `token` del environment
4. Verifica en la pestaña "Tests" que el token se guardó correctamente

### 2. Segundo: Probar Endpoints de Perfil
1. Ve a la carpeta "Perfil de Usuario"
2. Prueba "Obtener Perfil" - debería funcionar con el token guardado
3. Prueba "Actualizar Perfil" - ajusta el `user_id` en la URL si es necesario
4. Prueba "Cambiar Contraseña" - usa la contraseña actual
5. Prueba "Subir Imagen" - selecciona un archivo de imagen

### 3. Tercero: Probar Reservas
1. Ve a la carpeta "Reservas"
2. Prueba "Reservar Clase" - ajusta el `claseId` en la URL
3. Prueba "Marcar Asistencia" - ajusta el `id` de la reserva en la URL

### 4. Cuarto: Probar Agenda
1. Ve a la carpeta "Agenda/Asistencia"
2. Prueba "Obtener Asistencia por Clase" - ajusta el `id` de la clase

### 5. Quinto: Probar Roles (Solo Admin)
**Nota**: Necesitas un usuario con rol ADMIN para estos endpoints
1. Ve a la carpeta "Roles (Solo Admin)"
2. Prueba los diferentes endpoints de roles

### 6. Sexto: Probar Membresías
1. Ve a la carpeta "Membresías"
2. Prueba "Activar Membresía"

## Variables Disponibles

- `{{base_url}}`: URL base de la API (por defecto: `http://localhost:8080`)
- `{{token}}`: Token JWT (se llena automáticamente después de login/registro)

## Personalizar Valores

### Cambiar Base URL
1. Selecciona el environment "GymApp Local"
2. Edita el valor de `base_url` si tu servidor está en otro puerto o dirección

### Cambiar IDs en URLs
1. Abre cualquier petición
2. En la URL, reemplaza los valores numéricos (como `1`, `2`, etc.) con los IDs reales de tu base de datos
3. Ejemplo: Cambiar `/api/profile/update-profile-info/1` a `/api/profile/update-profile-info/5`

## Solución de Problemas

### Error 401 (Unauthorized)
- El token ha expirado o es inválido
- Solución: Ejecuta nuevamente "Login" o "Registro" para obtener un nuevo token

### Error 403 (Forbidden)
- No tienes el rol necesario (ej: ADMIN)
- Solución: Asegúrate de usar un usuario con los permisos adecuados

### Error de Conexión
- El servidor no está corriendo o los contenedores Docker están detenidos
- Solución: 
  1. Verifica que los contenedores estén corriendo: `docker ps`
  2. Si no están corriendo, inícialos: `docker compose up -d`
  3. Verifica los logs si hay problemas: `docker logs java_app -f`

### Token no se guarda automáticamente
- Verifica que el environment "GymApp Local" esté seleccionado
- Verifica que la respuesta del login/registro tenga el formato correcto con el campo `token`

## Tips Adicionales

1. **Guardar Respuestas**: Puedes guardar ejemplos de respuestas haciendo clic derecho en una petición → "Save Response" → "Save as Example"

2. **Pre-request Scripts**: Puedes agregar scripts que se ejecuten antes de cada petición (útil para refrescar tokens automáticamente)

3. **Tests Automáticos**: Cada petición puede tener tests que verifiquen automáticamente las respuestas

4. **Organizar en Folders**: La colección ya está organizada por funcionalidad, pero puedes crear más carpetas si lo necesitas

5. **Variables Locales**: Puedes crear variables locales en cada petición si necesitas valores específicos

## Próximos Pasos

Una vez que hayas probado todos los endpoints en Postman, puedes:
1. Usar la documentación completa en `API_DOCUMENTATION.md` para integrar la API en React Native
2. Revisar los ejemplos de código para React Native en la sección "Consejos para React Native" de la documentación

