package com.app.demo.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.demo.Entity.AgendaClase;
import com.app.demo.Entity.Reserva;
import com.app.demo.Entity.User;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByClaseAgendada_Id(Long claseAgendadaId);

    Optional<Reserva> findByClienteAndClaseAgendada(User cliente, AgendaClase claseAgendada);
    List<Reserva> findByCliente(User cliente);

}
