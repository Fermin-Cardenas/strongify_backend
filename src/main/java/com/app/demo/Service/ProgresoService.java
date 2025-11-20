package com.app.demo.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.demo.DTO.Request.ProgresoRequest;
import com.app.demo.DTO.Response.ProgresoResponse;
import com.app.demo.Entity.Progreso;
import com.app.demo.Entity.User;
import com.app.demo.Repository.ProgresoRepository;
import com.app.demo.Repository.UserRepository;

@Service
public class ProgresoService {

    private final ProgresoRepository progresoRepository;
    private final UserRepository userRepository;

    public ProgresoService(ProgresoRepository progresoRepository, UserRepository userRepository) {
        this.progresoRepository = progresoRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Progreso guardarProgreso(Long userId, ProgresoRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));

        // Crear nuevo registro de progreso (historial)
        Progreso progreso = new Progreso();
        progreso.setUser(user);
        progreso.setPeso(request.getPeso());
        
        // Calcular IMC si el usuario tiene altura
        Double imc = null;
        if (user.getAltura() != null && user.getAltura() > 0) {
            imc = request.getPeso() / (user.getAltura() * user.getAltura());
            imc = Math.round(imc * 100.0) / 100.0; // Redondear a 2 decimales
        }
        progreso.setImc(imc);
        progreso.setFechaRegistro(LocalDateTime.now());

        return progresoRepository.save(progreso);
    }

    public List<ProgresoResponse> obtenerHistorialProgreso(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));
        
        List<Progreso> historial = progresoRepository.findByUserOrderByFechaRegistroDesc(user);
        
        return historial.stream()
                .map(p -> new ProgresoResponse(
                    p.getProgresoId(),
                    p.getPeso(),
                    p.getImc(),
                    p.getFechaRegistro()
                ))
                .collect(Collectors.toList());
    }

    public Optional<Progreso> obtenerProgresoPorUsuario(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));
        
        return progresoRepository.findByUser(user);
    }
}

