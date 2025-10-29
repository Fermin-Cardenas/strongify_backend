package com.app.demo.Service;

import java.time.LocalDateTime;
import java.util.Optional;

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
        User user = new User();
        user.setFirstName(request.getFirst_name());
        user.setLastName(request.getLast_name());
        user.setBirthday(request.getBirthday());
        user.setPhoneNumber(request.getPhone());
        user = userRepository.save(user);

        Role role = roleRepository.findById(1L).orElseThrow(() -> new RuntimeException("Default role not found"));

        AuthUser authUser = new AuthUser();
        authUser.setUsername(request.getUsername());
        authUser.setPassword(passwordEncoder.encode(request.getPassword()));
        authUser.setUser(user);
        authUser.setRole(role);
        authUser = authUserRepository.save(authUser);

        AuthResponse response = new AuthResponse();
        response.setToken(jwtService.getToken(authUser));

        return response;
    }
    
    public Optional<AuthUser> findByGmail(String username) {
        return authUserRepository.findByUsername(username);
    }
}
