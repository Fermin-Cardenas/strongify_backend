package com.app.demo.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.app.demo.Entity.Membresia;
import com.app.demo.Service.MembresiaService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@Controller
@RequestMapping("/api/admin/membresias")
public class MembresiaController {

    private final MembresiaService s;

    public MembresiaController(MembresiaService s){
        this.s = s;
    }

    @PostMapping("/activar")
        public ResponseEntity<Membresia> guardarMembresia(@RequestBody Membresia membresia) {
        Membresia nuevaMembresia = s.guardarMembresia(membresia);
        return ResponseEntity.ok(nuevaMembresia);
    }
    
}
