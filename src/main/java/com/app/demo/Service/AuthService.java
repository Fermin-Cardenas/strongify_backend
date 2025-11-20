package com.app.demo.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.demo.DTO.Request.LoginRequest;
import com.app.demo.DTO.Request.RegisterRequest;
import com.app.demo.DTO.Response.AuthResponse;
import com.app.demo.Entity.AuthUser;
import com.app.demo.Entity.Role;
import com.app.demo.Entity.User;
import com.app.demo.Jwt.JwtService;
import com.app.demo.Repository.AuthUserRepository;
import com.app.demo.Repository.RoleRepository;
import com.app.demo.Repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class AuthService {
	private final AuthUserRepository authUserRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthUserRepository authUserRepository, UserRepository userRepository,
                       RoleRepository roleRepository, JwtService jwtService, AuthenticationManager authenticationManager,
                       PasswordEncoder passwordEncoder) {
        this.authUserRepository = authUserRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        AuthUser authUser = authUserRepository.findByUsernameWithRoleAndUser(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        authUser.setLastLogin(LocalDateTime.now());
        authUser = authUserRepository.save(authUser);
        String token = jwtService.getToken(authUser);

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        return response;
    }
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Validar que el username no exista
        if (authUserRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }

        // Validar que el teléfono no exista
        if (userRepository.findByPhoneNumber(request.getPhone()).isPresent()) {
            throw new RuntimeException("El número de teléfono ya está registrado");
        }

        // Validar que las contraseñas coincidan
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Las contraseñas no coinciden");
        }

        try {
            User user = new User();
            user.setFirstName(request.getFirst_name());
            user.setLastName(request.getLast_name());
            user.setBirthday(request.getBirthday());
            user.setPhoneNumber(request.getPhone());
            user = userRepository.save(user);

            // Buscar rol "USER" por nombre, si no existe, crearlo
            Role role = roleRepository.findByName("USER")
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setName("USER");
                        newRole.setDescription("Usuario regular");
                        return roleRepository.save(newRole);
                    });

            AuthUser authUser = new AuthUser();
            authUser.setUsername(request.getUsername());
            authUser.setPassword(passwordEncoder.encode(request.getPassword()));
            authUser.setUser(user);
            authUser.setRole(role);
            authUser = authUserRepository.save(authUser);

            AuthResponse response = new AuthResponse();
            response.setToken(jwtService.getToken(authUser));

            return response;
        } catch (DataIntegrityViolationException e) {
            String errorMessage = "Error al registrar usuario";
            if (e.getMessage() != null) {
                if (e.getMessage().contains("phone_number")) {
                    errorMessage = "El número de teléfono ya está registrado";
                } else if (e.getMessage().contains("username")) {
                    errorMessage = "El nombre de usuario ya está en uso";
                }
            }
            throw new RuntimeException(errorMessage, e);
        }
    }
    
    public Optional<AuthUser> findByGmail(String username) {
        return authUserRepository.findByUsername(username);
    }

    public void forgotPassword(String username) {
        Optional<AuthUser> authUserOptional = authUserRepository.findByUsername(username);
        
        if (authUserOptional.isEmpty()) {
            // Por seguridad, no revelamos si el usuario existe o no
            throw new RuntimeException("Si el usuario existe, se enviará un email con instrucciones para recuperar la contraseña");
        }

        AuthUser authUser = authUserOptional.get();
        
        // TODO: Aquí deberías implementar el envío de email con un token de recuperación
        // Por ahora, solo lanzamos una excepción genérica
        // En producción, deberías:
        // 1. Generar un token de recuperación
        // 2. Guardarlo en la base de datos con expiración
        // 3. Enviar un email al usuario con el link de recuperación
        
        throw new RuntimeException("Funcionalidad de recuperación de contraseña aún no implementada. Por favor, contacta al administrador.");
    }
}
