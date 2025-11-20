package com.app.demo.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.demo.Entity.MembresiaActiva;
import com.app.demo.Entity.User;

@Repository
public interface MembresiaActivaRepository extends JpaRepository<MembresiaActiva, Long> {
    
    // Obtener membresía activa (solo debe haber una por usuario)
    Optional<MembresiaActiva> findByUsuarioAndActivaTrue(User usuario);
}

