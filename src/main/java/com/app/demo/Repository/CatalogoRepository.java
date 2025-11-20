package com.app.demo.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.demo.Entity.Catalogo;

public interface CatalogoRepository extends JpaRepository<Catalogo, Long> {
    Optional<Catalogo> findByCatalogoId(Long catalogoId);
}

