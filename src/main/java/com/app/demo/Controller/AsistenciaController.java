package com.app.demo.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.demo.DTO.Response.AsistenciaResponse;
import com.app.demo.Service.AsistenciaService;

@RestController
@RequestMapping("/api/agenda")
public class AsistenciaController {

    private final AsistenciaService asistenciaService;

    public AsistenciaController(AsistenciaService asistenciaService) {
        this.asistenciaService = asistenciaService;
    }

    @GetMapping("/{id}/asistencia")
    public List<AsistenciaResponse> obtenerAsistencia(@PathVariable("id") Long claseId) {
        return asistenciaService.obtenerAsistenciaPorClase(claseId);
    }
}

