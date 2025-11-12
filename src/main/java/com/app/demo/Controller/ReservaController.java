package com.app.demo.Controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.demo.DTO.Request.MarcarAsistenciaRequest;
import com.app.demo.Entity.Reserva;
import com.app.demo.Service.ReservaService;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping("/{id}/marcar-asistencia")
    public ResponseEntity<String> marcarAsistencia(
            @PathVariable Long id,
            @RequestBody MarcarAsistenciaRequest request) {

        Reserva reservaActualizada = reservaService.marcarAsistencia(id, request.getAsistencia());
        String mensaje = request.getAsistencia()
                ? "Asistencia marcada como PRESENTE para la reserva ID " + reservaActualizada.getId()
                : "Asistencia marcada como AUSENTE para la reserva ID " + reservaActualizada.getId();

        return ResponseEntity.ok(mensaje);
    }

    @PostMapping("/clase/{claseId}")
    public ResponseEntity<?> reservarClase(@PathVariable Long claseId, Principal principal) {
        try {
            String username = principal.getName(); // viene del token JWT
            Reserva nuevaReserva = reservaService.reservar(username, claseId);
            return ResponseEntity.ok(nuevaReserva);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
