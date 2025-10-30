# Despliegue en Producción

Guía rápida para correr tu proyecto en un servidor limpio utilizando Docker.

## 1. Clonar el repositorio
Ejecuta los siguientes comandos en tu servidor:
```bash
git clone https://github.com/EmmanuelDeveloper-19/gymapp-backend.git
```

```bash
cd gymapp-backend
```
## 2. Construir y levantar los contenedores
Primero, construye la imagen de la aplicación Java:
```bash
docker compose build java_app
```
Luego, levanta todos los servicios definidos en tu docker-compose.yml:
```bash
docker compose up -d
```
Consejo: Usa -d para ejecutar los contenedores en segundo plano.

## 3. Verificar que los contenedores estén corriendo
Comprueba el estado de los contenedores activos:
```bash
docker ps
```
Deberías ver tus servicios (por ejemplo: java_app, db, etc.) en la lista.

### Listo
Tu proyecto debería estar corriendo correctamente en producción. 🎉

Si necesitas detener los contenedores:
```bash
docker compose down
```
