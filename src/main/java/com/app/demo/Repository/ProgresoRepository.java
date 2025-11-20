package com.app.demo.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.demo.Entity.Progreso;
import com.app.demo.Entity.User;

public interface ProgresoRepository extends JpaRepository<Progreso, Long> {
    Optional<Progreso> findByUser(User user);
    List<Progreso> findByUserOrderByFechaRegistroDesc(User user);
    
    @Query("SELECT p FROM Progreso p WHERE p.user = :user ORDER BY p.fechaRegistro DESC")
    List<Progreso> findHistorialByUser(@Param("user") User user);
}

