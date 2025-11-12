package com.app.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.demo.Entity.Membresia;

public interface MembresiaRepository extends JpaRepository<Membresia, Long> {
    
}
