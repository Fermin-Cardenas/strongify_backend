package com.app.demo.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.demo.Entity.Pago;
import com.app.demo.Entity.User;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByUsuario(User usuario);
    
    // Método seguro que retorna List y toma el primero
    @Query("SELECT p FROM Pago p WHERE p.usuario = :usuario ORDER BY p.fechaPago DESC")
    List<Pago> findLatestByUsuarioOrdered(@Param("usuario") User usuario);
    
    // Método usando Pageable para obtener solo el más reciente
    @Query("SELECT p FROM Pago p WHERE p.usuario = :usuario ORDER BY p.fechaPago DESC")
    Page<Pago> findLatestByUsuarioPageable(@Param("usuario") User usuario, Pageable pageable);
    
    // Método legacy (mantener para compatibilidad, pero usar los nuevos)
    @Deprecated
    @Query("SELECT p FROM Pago p WHERE p.usuario = :usuario ORDER BY p.fechaPago DESC")
    Optional<Pago> findLatestByUsuario(@Param("usuario") User usuario);
}

