package com.app.demo.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.demo.DTO.Request.CrearClaseRequest;
import com.app.demo.DTO.Response.ClaseResponse;
import com.app.demo.Entity.AgendaClase;
import com.app.demo.Entity.Catalogo;
import com.app.demo.Entity.Sucursal;
import com.app.demo.Entity.User;
import com.app.demo.Repository.AgendaClaseRepository;
import com.app.demo.Repository.CatalogoRepository;
import com.app.demo.Repository.SucursalRepository;
import com.app.demo.Repository.UserRepository;

@Service
public class ClaseService {

    private final AgendaClaseRepository agendaClaseRepository;
    private final CatalogoRepository catalogoRepository;
    private final SucursalRepository sucursalRepository;
    private final UserRepository userRepository;

    public ClaseService(AgendaClaseRepository agendaClaseRepository, 
                       CatalogoRepository catalogoRepository,
                       SucursalRepository sucursalRepository,
                       UserRepository userRepository) {
        this.agendaClaseRepository = agendaClaseRepository;
        this.catalogoRepository = catalogoRepository;
        this.sucursalRepository = sucursalRepository;
        this.userRepository = userRepository;
    }

    public List<ClaseResponse> obtenerTodasLasClases() {
        List<AgendaClase> clases = agendaClaseRepository.findAll();
        
        return clases.stream()
                .map(this::convertirAClaseResponse)
                .collect(Collectors.toList());
    }

    public List<ClaseResponse> obtenerClasesPorCoach(Long coachId) {
        List<AgendaClase> clases = agendaClaseRepository.findByCoach_UserId(coachId);
        
        return clases.stream()
                .map(this::convertirAClaseResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ClaseResponse crearClase(CrearClaseRequest request, Long coachId) {
        // Validaciones
        if (request.getNombreClase() == null || request.getNombreClase().trim().isEmpty()) {
            throw new RuntimeException("El nombre de la clase es requerido");
        }
        if (request.getNombreClase().length() < 3) {
            throw new RuntimeException("El nombre de la clase debe tener al menos 3 caracteres");
        }
        if (request.getFechaHoraInicio() == null) {
            throw new RuntimeException("La fecha y hora de inicio es requerida");
        }
        if (request.getFechaHoraInicio().isBefore(OffsetDateTime.now())) {
            throw new RuntimeException("La fecha de inicio no puede ser anterior a la fecha actual");
        }
        if (request.getCupoMaximo() == null || request.getCupoMaximo() <= 0) {
            throw new RuntimeException("El cupo máximo debe ser un número positivo");
        }
        if (request.getCupoMaximo() > 100) {
            throw new RuntimeException("El cupo máximo no puede ser mayor a 100");
        }

        // Obtener el coach
        User coach = userRepository.findById(coachId)
                .orElseThrow(() -> new RuntimeException("Coach no encontrado"));

        // Obtener o crear el catálogo
        Catalogo catalogo;
        if (request.getCatalogoId() != null) {
            catalogo = catalogoRepository.findById(request.getCatalogoId())
                    .orElseThrow(() -> new RuntimeException("Catálogo no encontrado"));
        } else {
            // Crear un nuevo catálogo si no se proporciona
            catalogo = new Catalogo();
            catalogo.setNombre(request.getNombreClase());
            catalogo.setDescripcion(request.getDescripcion());
            catalogo.setDuracion(request.getDuracionMinutos());
            catalogo.setCupo(request.getCupoMaximo());
            catalogo = catalogoRepository.save(catalogo);
        }

        // Calcular fechaHoraFin si no se proporciona
        OffsetDateTime fechaHoraFin = request.getFechaHoraFin();
        if (fechaHoraFin == null) {
            if (request.getDuracionMinutos() != null && request.getDuracionMinutos() > 0) {
                fechaHoraFin = request.getFechaHoraInicio().plusMinutes(request.getDuracionMinutos());
            } else if (catalogo.getDuracion() != null && catalogo.getDuracion() > 0) {
                fechaHoraFin = request.getFechaHoraInicio().plusMinutes(catalogo.getDuracion());
            } else {
                throw new RuntimeException("Debe proporcionarse fechaHoraFin o duracionMinutos");
            }
        }

        // Validar que fechaHoraFin sea posterior a fechaHoraInicio
        if (fechaHoraFin.isBefore(request.getFechaHoraInicio()) || fechaHoraFin.isEqual(request.getFechaHoraInicio())) {
            throw new RuntimeException("La fecha de fin debe ser posterior a la fecha de inicio");
        }

        // Obtener sucursal si se proporciona
        Sucursal sucursal = null;
        if (request.getSucursalId() != null) {
            sucursal = sucursalRepository.findById(request.getSucursalId())
                    .orElseThrow(() -> new RuntimeException("Sucursal no encontrada"));
        }

        // Crear la clase
        AgendaClase clase = new AgendaClase();
        clase.setCatalogo(catalogo);
        clase.setCoach(coach);
        clase.setSucursal(sucursal);
        clase.setFechaHoraInicio(request.getFechaHoraInicio());
        clase.setFechaHoraFin(fechaHoraFin);
        clase.setCupoActual(0);

        clase = agendaClaseRepository.save(clase);

        return convertirAClaseResponse(clase);
    }

    private ClaseResponse convertirAClaseResponse(AgendaClase clase) {
        ClaseResponse response = new ClaseResponse();
        response.setId(clase.getId());
        
        if (clase.getCatalogo() != null) {
            response.setNombreClase(clase.getCatalogo().getNombre());
            response.setDescripcion(clase.getCatalogo().getDescripcion());
            response.setDuracionMinutos(clase.getCatalogo().getDuracion());
            response.setCupoMaximo(clase.getCatalogo().getCupo());
            
            // Agregar información del catálogo
            ClaseResponse.CatalogoInfo catalogoInfo = new ClaseResponse.CatalogoInfo(
                clase.getCatalogo().getCatalogoId(),
                clase.getCatalogo().getNombre(),
                clase.getCatalogo().getDescripcion()
            );
            response.setCatalogo(catalogoInfo);
        }
        
        if (clase.getCoach() != null) {
            response.setCoachId(clase.getCoach().getUserId());
            response.setCoachNombre(clase.getCoach().getFirstName() + " " + clase.getCoach().getLastName());
        }
        
        if (clase.getSucursal() != null) {
            response.setSucursalId(clase.getSucursal().getSucursalId());
            response.setSucursalNombre(clase.getSucursal().getNombre());
        }
        
        response.setFechaHoraInicio(clase.getFechaHoraInicio());
        response.setFechaHoraFin(clase.getFechaHoraFin());
        response.setCupoActual(clase.getCupoActual() != null ? clase.getCupoActual() : 0);
        
        return response;
    }
}

