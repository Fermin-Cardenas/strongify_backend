package com.app.demo.Controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.demo.DTO.Request.ActivarMembresiaRequest;
import com.app.demo.DTO.Response.MembresiaResponse;
import com.app.demo.DTO.Response.UserResponse;
import com.app.demo.Service.MembresiaService;
import com.app.demo.Service.UserService;

@RestController
@RequestMapping("/api/admin/membresias")
public class MembresiaController {

    private final MembresiaService membresiaService;
    private final UserService userService;

    public MembresiaController(MembresiaService membresiaService, UserService userService){
        this.membresiaService = membresiaService;
        this.userService = userService;
    }

    @PostMapping("/activar")
    public ResponseEntity<?> activarMembresia(
            @RequestBody ActivarMembresiaRequest request,
            Authentication authentication) {
        try {
            // Obtener usuario del contexto de seguridad
            String username = authentication.getName();
            Optional<UserResponse> userResponse = userService.findByUsername(username);
            
            if (userResponse.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Usuario no encontrado"));
            }

            Long userId = userResponse.get().getId();

            // Validar request
            if (request.getTipo() == null || request.getCosto() == null || request.getDuracion() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Todos los campos son requeridos: tipo, costo, duracion"));
            }

            // Activar membresía
            MembresiaResponse response = membresiaService.activarMembresia(userId, request);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al activar membresía: " + e.getMessage()));
        }
    }
    
}
