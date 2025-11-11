package com.app.demo.Controller;

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
}
