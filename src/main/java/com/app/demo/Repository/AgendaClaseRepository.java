package com.app.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.demo.Entity.AgendaClase;

public interface AgendaClaseRepository extends JpaRepository<AgendaClase, Long> {
    
} 
