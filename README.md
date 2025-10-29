## 🚀 Despliegue en producción

Para correr tu proyecto en un servidor limpio (solo con Docker):

1. **Clonar el repositorio en el servidor**
```bash
git clone https://github.com/EmmanuelDeveloper-19/gymapp-backend.git
cd gymapp-backend

2. **Construir y levantar contenedores**
docker compose build java_app
docker compose up

3. **Verificar que los contenedores esten corriendo**
docker ps
