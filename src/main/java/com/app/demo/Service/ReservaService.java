package com.app.demo.Service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.demo.Entity.AgendaClase;
import com.app.demo.Entity.AuthUser;
import com.app.demo.Entity.Reserva;
import com.app.demo.Entity.User;
import com.app.demo.Repository.AgendaClaseRepository;
import com.app.demo.Repository.AuthUserRepository;
import com.app.demo.Repository.ReservaRepository;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final AuthService authService;
    private final AuthUserRepository authUserRepository;
    private final AgendaClaseRepository agendaClaseRepository;

    public ReservaService(
            ReservaRepository reservaRepository,
            AuthService authService,
            AuthUserRepository authUserRepository,
            AgendaClaseRepository agendaClaseRepository) {
        this.reservaRepository = reservaRepository;
        this.authService = authService;
        this.authUserRepository = authUserRepository;
        this.agendaClaseRepository = agendaClaseRepository;
    }

    private User getUserByUsername(String username) {
        return authService.findByGmail(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username))
                .getUser();
    }

    private AuthUser getAuthUser(String username) {
        return authUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    @Transactional
    public Reserva marcarAsistencia(Long id, Boolean asistencia) {
        Optional<Reserva> optionalReserva = reservaRepository.findById(id);

        if (optionalReserva.isEmpty()) {
            throw new RuntimeException("Reserva no encontrada con ID: " + id);
        }

        Reserva reserva = optionalReserva.get();
        reserva.setAsistencia(asistencia);
        return reservaRepository.save(reserva);
    }

    @Transactional
    public Reserva reservar(String username, Long claseId) {

        // Obtener usuario
        User user = getUserByUsername(username);

        // Obtener clase
        AgendaClase clase = agendaClaseRepository.findById(claseId)
                .orElseThrow(() -> new RuntimeException("Clase no encontrada con ID: " + claseId));

        // Verificar si ya existe una reserva igual
        Optional<Reserva> reservaExistente = reservaRepository.findByClienteAndClaseAgendada(user, clase);
        if (reservaExistente.isPresent()) {
            throw new RuntimeException("Ya tienes una reserva para esta clase.");
        }

        // Verificar si hay cupo disponible (opcional)
        if (clase.getCupoActual() != null && clase.getCupoActual() <= 0) {
            throw new RuntimeException("No hay cupos disponibles para esta clase.");
        }

        // Crear nueva reserva
        Reserva nuevaReserva = new Reserva();
        nuevaReserva.setCliente(user);
        nuevaReserva.setClaseAgendada(clase);
        nuevaReserva.setEstado("CONFIRMADA");
        nuevaReserva.setAsistencia(false);
        nuevaReserva.setFechaReserva(java.time.OffsetDateTime.now());

        // Actualizar el cupo de la clase si aplica
        if (clase.getCupoActual() != null) {
            clase.setCupoActual(clase.getCupoActual() - 1);
            agendaClaseRepository.save(clase);
        }

        // Guardar la reserva
        return reservaRepository.save(nuevaReserva);

    }
}
