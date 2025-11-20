package com.app.demo.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.demo.DTO.Request.CrearClaseRequest;
import com.app.demo.DTO.Response.AsistenciaResponse;
import com.app.demo.DTO.Response.ClaseResponse;
import com.app.demo.Service.AsistenciaService;
import com.app.demo.Service.ClaseService;
import com.app.demo.Service.UserService;

@RestController
@RequestMapping("/api/agenda")
public class AsistenciaController {

    private final AsistenciaService asistenciaService;
    private final ClaseService claseService;
    private final UserService userService;

    public AsistenciaController(AsistenciaService asistenciaService, ClaseService claseService, UserService userService) {
        this.asistenciaService = asistenciaService;
        this.claseService = claseService;
        this.userService = userService;
    }

    @GetMapping("/{id}/asistencia")
    public List<AsistenciaResponse> obtenerAsistencia(@PathVariable("id") Long claseId) {
        return asistenciaService.obtenerAsistenciaPorClase(claseId);
    }

    @GetMapping("/clases")
    public ResponseEntity<List<ClaseResponse>> obtenerTodasLasClases() {
        List<ClaseResponse> clases = claseService.obtenerTodasLasClases();
        return ResponseEntity.ok(clases);
    }

    @GetMapping("/coach/{coachId}/clases")
    public ResponseEntity<List<ClaseResponse>> obtenerClasesPorCoach(@PathVariable("coachId") Long coachId) {
        List<ClaseResponse> clases = claseService.obtenerClasesPorCoach(coachId);
        return ResponseEntity.ok(clases);
    }

    @PostMapping("/clases")
    public ResponseEntity<?> crearClase(@RequestBody CrearClaseRequest request, Authentication authentication) {
        try {
            // Log para debug
            System.out.println("🔍 POST /api/agenda/clases - Authentication: " + (authentication != null ? authentication.getName() : "null"));
            if (authentication != null) {
                System.out.println("🔍 Authorities: " + authentication.getAuthorities());
                System.out.println("🔍 IsAuthenticated: " + authentication.isAuthenticated());
            }
            
            // Verificar autenticación
            if (authentication == null) {
                System.out.println("❌ Authentication es null");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized", "message", "Authentication es null"));
            }
            
            if (!authentication.isAuthenticated()) {
                System.out.println("❌ Usuario no autenticado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized", "message", "Usuario no autenticado"));
            }

            // Debug: Obtener authorities para logging
            String username = authentication.getName();
            var authorities = authentication.getAuthorities();
            var authorityNames = authorities.stream()
                .map(a -> a.getAuthority())
                .toList();

            // Verificar que el usuario tiene rol COACH
            boolean isCoach = authorities.stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_COACH"));
            
            if (!isCoach) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                        "error", "Access Denied", 
                        "message", "Solo los coaches pueden crear clases",
                        "username", username != null ? username : "null",
                        "authorities", authorityNames.toString()
                    ));
            }

            // Obtener el ID del coach desde el token JWT (username ya obtenido arriba)
            if (username == null || username.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized", "message", "Usuario no autenticado"));
            }
            
            com.app.demo.Entity.User coach = userService.getUserByUsername(username);
            
            // Verificar que el usuario existe
            if (coach == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Access Denied", "message", "Usuario no encontrado"));
            }

            // Crear la clase
            ClaseResponse claseCreada = claseService.crearClase(request, coach.getUserId());
            
            return ResponseEntity.ok(claseCreada);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Bad Request", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal Server Error", "message", "Error al crear la clase: " + e.getMessage()));
        }
    }
}

