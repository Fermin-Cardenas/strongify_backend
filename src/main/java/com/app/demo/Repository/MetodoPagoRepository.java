package com.app.demo.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.demo.Entity.MetodoPago;
import com.app.demo.Entity.User;

public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Long> {
    List<MetodoPago> findByUserOrderByEsPredeterminadaDesc(User user);
    Optional<MetodoPago> findByMetodoPagoIdAndUser(Long metodoPagoId, User user);
    Optional<MetodoPago> findByUserAndEsPredeterminadaTrue(User user);
    long countByUser(User user);
}

