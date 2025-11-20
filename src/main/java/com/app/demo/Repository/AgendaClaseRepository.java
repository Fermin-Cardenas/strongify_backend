package com.app.demo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.demo.Entity.AgendaClase;
import com.app.demo.Entity.User;

public interface AgendaClaseRepository extends JpaRepository<AgendaClase, Long> {
    List<AgendaClase> findByCoach(User coach);
    List<AgendaClase> findByCoach_UserId(Long coachId);
} 
