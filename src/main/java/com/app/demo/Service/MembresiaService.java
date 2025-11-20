package com.app.demo.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.demo.DTO.Request.ActivarMembresiaRequest;
import com.app.demo.DTO.Response.MembresiaResponse;
import com.app.demo.Entity.Membresia;
import com.app.demo.Entity.MembresiaActiva;
import com.app.demo.Entity.Pago;
import com.app.demo.Entity.Tipo;
import com.app.demo.Entity.User;
import com.app.demo.Repository.MembresiaActivaRepository;
import com.app.demo.Repository.MembresiaRepository;
import com.app.demo.Repository.PagoRepository;
import com.app.demo.Repository.UserRepository;

@Service
public class MembresiaService {

    private final MembresiaRepository repo;
    private final PagoRepository pagoRepository;
    private final UserRepository userRepository;
    private final MembresiaActivaRepository membresiaActivaRepository;

    public MembresiaService(MembresiaRepository repo, PagoRepository pagoRepository, 
                           UserRepository userRepository, MembresiaActivaRepository membresiaActivaRepository){
        this.repo = repo;
        this.pagoRepository = pagoRepository;
        this.userRepository = userRepository;
        this.membresiaActivaRepository = membresiaActivaRepository;
    }

    @Transactional
    public MembresiaResponse activarMembresia(Long userId, ActivarMembresiaRequest request) {
        // Validar datos
        if (request.getTipo() == null || request.getCosto() == null || request.getDuracion() == null) {
            throw new RuntimeException("Todos los campos son requeridos: tipo, costo, duracion");
        }

        // Obtener usuario
        User usuario = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));

        // Convertir string a enum Tipo
        Tipo tipoEnum;
        try {
            tipoEnum = Tipo.valueOf(request.getTipo().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Tipo de membresía inválido: " + request.getTipo() + 
                    ". Valores permitidos: PAGO_SEMANAL, POR_SESION, MENSUAL, ANUAL");
        }

        // ✅ PASO 1: Buscar si ya existe una membresía activa
        Optional<MembresiaActiva> membresiaExistenteOpt = membresiaActivaRepository
                .findByUsuarioAndActivaTrue(usuario);

        MembresiaActiva membresiaActiva;
        LocalDate fechaInicio;
        LocalDate fechaFin;

        if (membresiaExistenteOpt.isPresent()) {
            // ✅ CASO 2: Ya existe membresía activa → EXTENDER (acumular días)
            membresiaActiva = membresiaExistenteOpt.get();

            // Mantener la fecha de inicio original
            fechaInicio = membresiaActiva.getFechaInicio();

            // ✅ ACUMULAR DÍAS: Extender la fecha de fin
            // Si la fecha de fin es en el futuro, extender desde ahí
            // Si la fecha de fin ya pasó, extender desde hoy
            LocalDate fechaBase = membresiaActiva.getFechaFin().isAfter(LocalDate.now()) 
                    ? membresiaActiva.getFechaFin()  // Extender desde la fecha de fin actual
                    : LocalDate.now();               // O desde hoy si ya expiró

            fechaFin = fechaBase.plusDays(request.getDuracion());

            // Actualizar campos de la membresía existente
            membresiaActiva.setTipo(tipoEnum);  // Actualizar tipo si cambió
            membresiaActiva.setCosto(request.getCosto()); // Actualizar costo si cambió
            membresiaActiva.setDuracion(membresiaActiva.getDuracion() + request.getDuracion()); // Acumular duración total
            membresiaActiva.setFechaFin(fechaFin); // Nueva fecha de fin

            // Guardar cambios
            membresiaActiva = membresiaActivaRepository.save(membresiaActiva);

        } else {
            // ✅ CASO 1: No hay membresía activa → CREAR NUEVA
            membresiaActiva = new MembresiaActiva();
            membresiaActiva.setUsuario(usuario);
            membresiaActiva.setTipo(tipoEnum);
            membresiaActiva.setCosto(request.getCosto());
            membresiaActiva.setDuracion(request.getDuracion());
            membresiaActiva.setFechaInicio(LocalDate.now());
            membresiaActiva.setFechaFin(LocalDate.now().plusDays(request.getDuracion()));
            membresiaActiva.setActiva(true);

            fechaInicio = membresiaActiva.getFechaInicio();
            fechaFin = membresiaActiva.getFechaFin();

            // Guardar nueva membresía
            membresiaActiva = membresiaActivaRepository.save(membresiaActiva);
        }

        // Crear membresía tipo para el historial (opcional, para mantener compatibilidad)
        Membresia membresia = new Membresia();
        membresia.setTipo(tipoEnum);
        membresia.setCosto(request.getCosto());
        membresia.setDuracion(request.getDuracion());
        membresia = repo.save(membresia);

        // Crear pago asociado al usuario y la membresía (para historial)
        Pago pago = new Pago();
        pago.setUsuario(usuario);
        pago.setMembresia(membresia);
        pago.setMonto(BigDecimal.valueOf(request.getCosto()));
        pago.setFechaPago(OffsetDateTime.now());
        pago.setMetodoPago("TARJETA"); // Por defecto, puede cambiarse después
        pagoRepository.save(pago);

        // Calcular días restantes
        long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), fechaFin);

        // Convertir LocalDate a OffsetDateTime para la respuesta
        OffsetDateTime fechaInicioOffset = fechaInicio.atStartOfDay()
                .atOffset(java.time.ZoneOffset.UTC);
        OffsetDateTime fechaFinOffset = fechaFin.atStartOfDay()
                .atOffset(java.time.ZoneOffset.UTC);

        // Crear respuesta
        MembresiaResponse response = new MembresiaResponse(
            membresiaActiva.getMembresiaId(),
            membresiaActiva.getTipo().name(),
            membresiaActiva.getCosto(),
            membresiaActiva.getDuracion(),
            fechaInicioOffset,
            fechaFinOffset,
            diasRestantes > 0 ? diasRestantes : 0L,
            true // Siempre activa si está en la tabla membresia_activa
        );

        return response;
    }

    public Membresia guardarMembresia(Membresia m){
        return repo.save(m);
    }

    public Optional<MembresiaResponse> obtenerMembresiaPorUsuario(User usuario) {
        // ✅ Con la nueva lógica, siempre habrá máximo 1 membresía activa
        Optional<MembresiaActiva> membresiaOpt = membresiaActivaRepository
                .findByUsuarioAndActivaTrue(usuario);

        if (membresiaOpt.isEmpty()) {
            return Optional.empty();
        }

        MembresiaActiva membresia = membresiaOpt.get();

        // Calcular días restantes
        long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), membresia.getFechaFin());
        boolean activa = diasRestantes > 0 && LocalDate.now().isBefore(membresia.getFechaFin()) 
                && membresia.isActiva();

        // Convertir LocalDate a OffsetDateTime para la respuesta
        OffsetDateTime fechaInicio = membresia.getFechaInicio().atStartOfDay()
                .atOffset(java.time.ZoneOffset.UTC);
        OffsetDateTime fechaFin = membresia.getFechaFin().atStartOfDay()
                .atOffset(java.time.ZoneOffset.UTC);

        String tipoString = membresia.getTipo() != null ? membresia.getTipo().name() : "MENSUAL";

        MembresiaResponse response = new MembresiaResponse(
            membresia.getMembresiaId(),
            tipoString,
            membresia.getCosto(),
            membresia.getDuracion(),
            fechaInicio,
            fechaFin,
            diasRestantes > 0 ? diasRestantes : 0L,
            activa
        );

        return Optional.of(response);
    }
}
