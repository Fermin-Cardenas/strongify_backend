package com.app.demo.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.app.demo.DTO.Response.AsistenciaResponse;
import com.app.demo.Entity.Reserva;
import com.app.demo.Repository.ReservaRepository;

@Service
public class AsistenciaService {

    private final ReservaRepository reservaRepository;

    public AsistenciaService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    public List<AsistenciaResponse> obtenerAsistenciaPorClase(Long claseId) {
        List<Reserva> reservas = reservaRepository.findByClaseAgendada_Id(claseId);

        return reservas.stream()
                .map(r -> new AsistenciaResponse(
                        r.getId(),
                        r.getCliente().getFirstName() + " " + r.getCliente().getLastName(),
                        r.getCliente().getPhoneNumber(),
                        r.getAsistencia()))
                .collect(Collectors.toList());
    }
}

