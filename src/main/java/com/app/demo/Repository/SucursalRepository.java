package com.app.demo.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.demo.Entity.Sucursal;

public interface SucursalRepository extends JpaRepository<Sucursal, Long> {
    Optional<Sucursal> findBySucursalId(Long sucursalId);
}

