package com.app.demo.Service;

import org.springframework.stereotype.Service;

import com.app.demo.Entity.Membresia;
import com.app.demo.Repository.MembresiaRepository;

@Service
public class MembresiaService {

    private final MembresiaRepository repo;

    public MembresiaService(MembresiaRepository repo){
        this.repo = repo;
    }

    public Membresia guardarMembresia(Membresia m){
        return repo.save(m);
    }
}
