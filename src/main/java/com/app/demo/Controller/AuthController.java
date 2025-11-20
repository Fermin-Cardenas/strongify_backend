package com.app.demo.Controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.demo.DTO.Request.ForgotPasswordRequest;
import com.app.demo.DTO.Request.LoginRequest;
import com.app.demo.DTO.Request.RegisterRequest;
import com.app.demo.DTO.Response.AuthResponse;
import com.app.demo.Service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {
	
	private final AuthService authService;
	
	public AuthController(AuthService authService) {
		this.authService = authService;
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest request){
		try {
			AuthResponse response = authService.login(request);
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
	
	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody RegisterRequest request){
		try {
			AuthResponse response = authService.register(request);
			return ResponseEntity.ok(response);
		} catch (DataIntegrityViolationException e) {
			String errorMessage = "Error al registrar usuario";
			if (e.getMessage() != null) {
				if (e.getMessage().contains("phone_number")) {
					errorMessage = "El número de teléfono ya está registrado";
				} else if (e.getMessage().contains("username")) {
					errorMessage = "El nombre de usuario ya está en uso";
				} else {
					errorMessage = "Los datos proporcionados ya existen en el sistema";
				}
			}
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error interno del servidor: " + e.getMessage());
		}
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request){
		try {
			authService.forgotPassword(request.getUsername());
			return ResponseEntity.ok("Si el usuario existe, se enviará un email con instrucciones para recuperar la contraseña");
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error interno del servidor: " + e.getMessage());
		}
	}

}
