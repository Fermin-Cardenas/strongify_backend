package com.app.demo.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.demo.DTO.Request.MetodoPagoRequest;
import com.app.demo.DTO.Request.UpdateMetodoPagoRequest;
import com.app.demo.DTO.Response.MetodoPagoResponse;
import com.app.demo.Entity.MetodoPago;
import com.app.demo.Entity.User;
import com.app.demo.Repository.MetodoPagoRepository;
import com.app.demo.Repository.UserRepository;

@Service
public class MetodoPagoService {

    private final MetodoPagoRepository metodoPagoRepository;
    private final UserRepository userRepository;

    public MetodoPagoService(MetodoPagoRepository metodoPagoRepository, UserRepository userRepository) {
        this.metodoPagoRepository = metodoPagoRepository;
        this.userRepository = userRepository;
    }

    public List<MetodoPagoResponse> obtenerMetodosPagoPorUsuario(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));
        
        List<MetodoPago> metodos = metodoPagoRepository.findByUserOrderByEsPredeterminadaDesc(user);
        
        return metodos.stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public MetodoPagoResponse agregarMetodoPago(Long userId, MetodoPagoRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));

        // Extraer últimos 4 dígitos
        String numeroTarjeta = request.getNumeroTarjeta();
        if (numeroTarjeta == null || numeroTarjeta.length() < 4) {
            throw new RuntimeException("Número de tarjeta inválido");
        }
        String ultimos4 = numeroTarjeta.substring(numeroTarjeta.length() - 4);

        // Si es predeterminada, marcar las demás como false
        if (Boolean.TRUE.equals(request.getEsPredeterminada())) {
            Optional<MetodoPago> predeterminadoActual = metodoPagoRepository.findByUserAndEsPredeterminadaTrue(user);
            if (predeterminadoActual.isPresent()) {
                MetodoPago actual = predeterminadoActual.get();
                actual.setEsPredeterminada(false);
                metodoPagoRepository.save(actual);
            }
        }

        MetodoPago nuevoMetodo = new MetodoPago();
        nuevoMetodo.setUser(user);
        nuevoMetodo.setTipo(request.getTipo());
        nuevoMetodo.setUltimos4(ultimos4);
        nuevoMetodo.setNombreTitular(request.getNombreTitular());
        nuevoMetodo.setFechaVencimiento(request.getFechaVencimiento());
        nuevoMetodo.setMarca(request.getMarca());
        nuevoMetodo.setEsPredeterminada(request.getEsPredeterminada() != null ? request.getEsPredeterminada() : false);

        MetodoPago guardado = metodoPagoRepository.save(nuevoMetodo);
        return convertirAResponse(guardado);
    }

    @Transactional
    public MetodoPagoResponse actualizarMetodoPago(Long userId, Long metodoPagoId, UpdateMetodoPagoRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));

        MetodoPago metodo = metodoPagoRepository.findByMetodoPagoIdAndUser(metodoPagoId, user)
                .orElseThrow(() -> new RuntimeException("Método de pago no encontrado"));

        if (request.getNombreTitular() != null) {
            metodo.setNombreTitular(request.getNombreTitular());
        }
        if (request.getFechaVencimiento() != null) {
            metodo.setFechaVencimiento(request.getFechaVencimiento());
        }
        if (request.getEsPredeterminada() != null) {
            // Si se establece como predeterminada, marcar las demás como false
            if (Boolean.TRUE.equals(request.getEsPredeterminada())) {
                Optional<MetodoPago> predeterminadoActual = metodoPagoRepository.findByUserAndEsPredeterminadaTrue(user);
                if (predeterminadoActual.isPresent() && !predeterminadoActual.get().getMetodoPagoId().equals(metodoPagoId)) {
                    MetodoPago actual = predeterminadoActual.get();
                    actual.setEsPredeterminada(false);
                    metodoPagoRepository.save(actual);
                }
            }
            metodo.setEsPredeterminada(request.getEsPredeterminada());
        }

        MetodoPago actualizado = metodoPagoRepository.save(metodo);
        return convertirAResponse(actualizado);
    }

    @Transactional
    public void eliminarMetodoPago(Long userId, Long metodoPagoId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));

        MetodoPago metodo = metodoPagoRepository.findByMetodoPagoIdAndUser(metodoPagoId, user)
                .orElseThrow(() -> new RuntimeException("Método de pago no encontrado"));

        // Verificar que no sea el único método de pago
        long totalMetodos = metodoPagoRepository.countByUser(user);
        if (totalMetodos <= 1) {
            throw new RuntimeException("No puedes eliminar tu único método de pago");
        }

        metodoPagoRepository.delete(metodo);
    }

    @Transactional
    public MetodoPagoResponse establecerPredeterminado(Long userId, Long metodoPagoId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));

        MetodoPago metodo = metodoPagoRepository.findByMetodoPagoIdAndUser(metodoPagoId, user)
                .orElseThrow(() -> new RuntimeException("Método de pago no encontrado"));

        // Marcar las demás como false
        Optional<MetodoPago> predeterminadoActual = metodoPagoRepository.findByUserAndEsPredeterminadaTrue(user);
        if (predeterminadoActual.isPresent() && !predeterminadoActual.get().getMetodoPagoId().equals(metodoPagoId)) {
            MetodoPago actual = predeterminadoActual.get();
            actual.setEsPredeterminada(false);
            metodoPagoRepository.save(actual);
        }

        metodo.setEsPredeterminada(true);
        MetodoPago actualizado = metodoPagoRepository.save(metodo);
        return convertirAResponse(actualizado);
    }

    private MetodoPagoResponse convertirAResponse(MetodoPago metodo) {
        return new MetodoPagoResponse(
            metodo.getMetodoPagoId(),
            metodo.getTipo(),
            metodo.getUltimos4(),
            metodo.getNombreTitular(),
            metodo.getFechaVencimiento(),
            metodo.getEsPredeterminada(),
            metodo.getMarca()
        );
    }
}

