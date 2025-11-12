package com.app.demo.Service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.demo.Entity.Reserva;
import com.app.demo.Repository.ReservaRepository;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;

    public ReservaService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
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
}

