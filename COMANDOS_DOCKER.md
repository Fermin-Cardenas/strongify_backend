# Comandos Útiles de Docker

## Comandos Básicos

### Verificar Estado
```bash
# Ver contenedores corriendo
docker ps

# Ver todos los contenedores (incluyendo detenidos)
docker ps -a
```

### Iniciar Servicios
```bash
# Construir la imagen de la aplicación (primera vez o después de cambios)
docker compose build java_app

# Iniciar todos los servicios en segundo plano
docker compose up -d

# Iniciar y ver logs en tiempo real
docker compose up
```

### Detener Servicios
```bash
# Detener los servicios (mantiene los contenedores)
docker compose stop

# Detener y eliminar contenedores
docker compose down

# Detener, eliminar contenedores y volúmenes (⚠️ elimina datos de BD)
docker compose down -v
```

## Logs

### Ver Logs
```bash
# Logs de la aplicación Java
docker logs java_app

# Logs en tiempo real (seguimiento)
docker logs java_app -f

# Últimas 100 líneas de logs
docker logs java_app --tail 100

# Logs de la base de datos
docker logs java_db -f
```

## Acceso a Contenedores

### Entrar al Contenedor de la Aplicación
```bash
docker exec -it java_app sh
```

### Entrar al Contenedor de la Base de Datos
```bash
docker exec -it java_db psql -U root -d postgres
```

### Ejecutar Comandos en el Contenedor
```bash
# Ejemplo: Ver variables de entorno
docker exec java_app env

# Ejemplo: Ver procesos
docker exec java_app ps aux
```

## Base de Datos

### Conectarse a PostgreSQL
```bash
# Desde el host (si tienes psql instalado)
psql -h localhost -p 5432 -U root -d postgres

# Desde dentro del contenedor
docker exec -it java_db psql -U root -d postgres
```

### Comandos SQL Útiles
```sql
-- Listar todas las bases de datos
\l

-- Conectarse a una base de datos
\c gymappdb

-- Listar todas las tablas
\dt

-- Ver estructura de una tabla
\d nombre_tabla

-- Salir
\q
```

## Reiniciar Servicios

### Reiniciar un Contenedor Específico
```bash
docker restart java_app
docker restart java_db
```

### Reiniciar Todos los Servicios
```bash
docker compose restart
```

## Limpieza

### Limpiar Recursos No Utilizados
```bash
# Eliminar contenedores detenidos
docker container prune

# Eliminar imágenes no utilizadas
docker image prune

# Limpieza completa (⚠️ elimina todo lo no utilizado)
docker system prune -a
```

## Verificar Salud de los Servicios

### Verificar que la API responde
```bash
# Desde la terminal (Windows PowerShell)
curl http://localhost:8080/auth/login

# O desde el navegador
# Abre: http://localhost:8080
```

### Verificar uso de recursos
```bash
# Estadísticas de uso de recursos
docker stats

# Estadísticas de un contenedor específico
docker stats java_app
```

## Solución de Problemas

### El contenedor no inicia
```bash
# Ver logs detallados
docker logs java_app

# Verificar configuración
docker compose config

# Reconstruir sin caché
docker compose build --no-cache java_app
```

### Puerto ya en uso
```bash
# Ver qué proceso usa el puerto 8080 (Windows)
netstat -ano | findstr :8080

# Ver qué proceso usa el puerto 5432 (Windows)
netstat -ano | findstr :5432
```

### Reiniciar desde cero
```bash
# Detener todo
docker compose down -v

# Eliminar imágenes
docker rmi gym-java-app:1.0.0

# Reconstruir todo
docker compose build --no-cache
docker compose up -d
```

## Variables de Entorno

### Ver variables de entorno del contenedor
```bash
docker exec java_app env
```

### Crear archivo .env (si no existe)
Crea un archivo `.env` en la raíz del proyecto con:
```
DATABASE_URL=jdbc:postgresql://java_db:5432/postgres
DATABASE_USERNAME=root
DATABASE_PASSWORD=61945
```

## Backup y Restore de Base de Datos

### Backup
```bash
docker exec java_db pg_dump -U root postgres > backup.sql
```

### Restore
```bash
cat backup.sql | docker exec -i java_db psql -U root postgres
```

## Monitoreo Continuo

### Ver logs y estadísticas simultáneamente
```bash
# Terminal 1: Logs
docker logs java_app -f

# Terminal 2: Estadísticas
docker stats java_app
```

