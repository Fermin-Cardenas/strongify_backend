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

        // Obtener el cupo máximo del catálogo
        Integer cupoMaximo = null;
        if (clase.getCatalogo() != null && clase.getCatalogo().getCupo() != null) {
            cupoMaximo = clase.getCatalogo().getCupo();
        }

        // Verificar si hay cupo disponible
        // cupoActual representa el número de personas registradas (ocupadas)
        Integer cupoActual = clase.getCupoActual() != null ? clase.getCupoActual() : 0;
        if (cupoMaximo != null && cupoActual >= cupoMaximo) {
            throw new RuntimeException("No hay cupos disponibles para esta clase.");
        }

        // Crear nueva reserva
        Reserva nuevaReserva = new Reserva();
        nuevaReserva.setCliente(user);
        nuevaReserva.setClaseAgendada(clase);
        nuevaReserva.setEstado("CONFIRMADA");
        nuevaReserva.setAsistencia(false);
        nuevaReserva.setFechaReserva(java.time.OffsetDateTime.now());

        // Actualizar el cupo de la clase (incrementar personas registradas)
        clase.setCupoActual(cupoActual + 1);
        agendaClaseRepository.save(clase);

        // Guardar la reserva
        return reservaRepository.save(nuevaReserva);

    }

    @Transactional
    public void cancelarReserva(Long reservaId, String username) {
        User user = getUserByUsername(username);
        
        Optional<Reserva> reservaOptional = reservaRepository.findById(reservaId);
        
        if (reservaOptional.isEmpty()) {
            throw new RuntimeException("Reserva no encontrada con ID: " + reservaId);
        }

        Reserva reserva = reservaOptional.get();
        
        // Verificar que la reserva pertenece al usuario
        if (!reserva.getCliente().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("No tienes permiso para cancelar esta reserva");
        }

        // Verificar que la reserva no esté cancelada ya
        if ("CANCELADA".equals(reserva.getEstado())) {
            throw new RuntimeException("La reserva ya está cancelada");
        }

        // Actualizar cupo de la clase (decrementar personas registradas)
        AgendaClase clase = reserva.getClaseAgendada();
        if (clase != null) {
            Integer cupoActual = clase.getCupoActual() != null ? clase.getCupoActual() : 0;
            // Solo decrementar si es mayor a 0 para evitar valores negativos
            if (cupoActual > 0) {
                clase.setCupoActual(cupoActual - 1);
            } else {
                clase.setCupoActual(0);
            }
            agendaClaseRepository.save(clase);
        }

        // Cancelar la reserva
        reserva.setEstado("CANCELADA");
        reservaRepository.save(reserva);
    }
}
