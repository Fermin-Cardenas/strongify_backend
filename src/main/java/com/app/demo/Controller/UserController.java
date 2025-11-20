package com.app.demo.Controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.app.demo.DTO.Request.ChangePasswordRequest;
import com.app.demo.DTO.Request.MetodoPagoRequest;
import com.app.demo.DTO.Request.ProgresoRequest;
import com.app.demo.DTO.Request.UpdateMetodoPagoRequest;
import com.app.demo.DTO.Request.UpdateRolRequest;
import com.app.demo.DTO.Request.UpdateUserRequest;
import com.app.demo.DTO.Response.MembresiaResponse;
import com.app.demo.DTO.Response.MetodoPagoResponse;
import com.app.demo.DTO.Response.ProgresoResponse;
import com.app.demo.DTO.Response.ReservaResponse;
import com.app.demo.DTO.Response.UserResponse;
import com.app.demo.Entity.Progreso;
import com.app.demo.Entity.User;
import com.app.demo.Service.MembresiaService;
import com.app.demo.Service.MetodoPagoService;
import com.app.demo.Service.ProgresoService;
import com.app.demo.Service.UserService;

@RestController
@RequestMapping("/api/profile")
public class UserController {
	private final UserService userService;
	private final MembresiaService membresiaService;
	private final ProgresoService progresoService;
	private final MetodoPagoService metodoPagoService;

	public UserController(UserService userService, MembresiaService membresiaService, 
	                     ProgresoService progresoService, MetodoPagoService metodoPagoService) {
		this.userService = userService;
		this.membresiaService = membresiaService;
		this.progresoService = progresoService;
		this.metodoPagoService = metodoPagoService;
	}

	@GetMapping("/get-profile-info")
	public ResponseEntity<UserResponse> getMyUserProfile(Authentication authentication) {
		String username = authentication.getName();

		return userService.findByUsername(username).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/debug-auth")
	public ResponseEntity<Map<String, Object>> debugAuth(Authentication authentication) {
		if (authentication == null) {
			return ResponseEntity.ok(Map.of(
				"authenticated", false,
				"message", "No hay autenticación"
			));
		}

		var authorities = authentication.getAuthorities().stream()
			.map(a -> a.getAuthority())
			.toList();

		boolean isCoach = authorities.stream()
			.anyMatch(a -> a.equals("ROLE_COACH"));

		return ResponseEntity.ok(Map.of(
			"authenticated", authentication.isAuthenticated(),
			"username", authentication.getName() != null ? authentication.getName() : "null",
			"authorities", authorities,
			"isCoach", isCoach,
			"hasRoleCOACH", isCoach
		));
	}

	@PutMapping("/update-profile-info/{user_id}")
	public ResponseEntity<UserResponse> updateUser(@PathVariable("user_id") Long user_id,
			@RequestBody UpdateUserRequest request, Authentication authentication) {
		String username = authentication.getName();

		Optional<UserResponse> updatedUser = userService.updateUserInfo(user_id, request, username);
		return updatedUser.map(ResponseEntity::ok)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
	}

	@PutMapping("/change-password")
	public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request,
			Authentication authentication) {
		String username = authentication.getName();

		try {
			userService.changePassword(username, request);
			return ResponseEntity.ok("Contraseña actualizada correctamente");
		} catch (RuntimeException ex) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
		}
	}

	@PostMapping("/upload-image")
	public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file,
			Authentication authentication) {
		try {
			String username = authentication.getName();

			Map<String, String> response = userService.uploadImage(username, file);
			return ResponseEntity.ok(response);

		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Error al subir imagen: " + e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(Map.of("error", "Usuario no encontrado"));
		}
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/update-user-rol/{user_id}")
	public ResponseEntity<?> updateUserRol(
			@PathVariable Long user_id,
			@RequestBody UpdateRolRequest request) {

		return userService.updateRol(user_id, request)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@GetMapping("/me/reservas")
	public ResponseEntity<List<ReservaResponse>> getAllReservas(Authentication authentication) {
		String username = authentication.getName();
		List<ReservaResponse> reservas = userService.listarReservasPorUsuario(username);
		return ResponseEntity.ok(reservas);
	}

	@GetMapping("/membresia")
	public ResponseEntity<?> getMiMembresia(Authentication authentication) {
		try {
			String username = authentication.getName();
			Optional<UserResponse> userResponse = userService.findByUsername(username);
			
			if (userResponse.isEmpty()) {
				return ResponseEntity.notFound().build();
			}

			User user = userService.getUserByUsername(username);
			Optional<MembresiaResponse> membresia = membresiaService.obtenerMembresiaPorUsuario(user);
			
			if (membresia.isEmpty()) {
				return ResponseEntity.ok(Map.of("message", "No tienes una membresía activa"));
			}
			
			return ResponseEntity.ok(membresia.get());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", e.getMessage()));
		}
	}

	@PostMapping("/progreso")
	public ResponseEntity<?> guardarProgreso(@RequestBody ProgresoRequest request, Authentication authentication) {
		try {
			String username = authentication.getName();
			Optional<UserResponse> userResponse = userService.findByUsername(username);
			
			if (userResponse.isEmpty()) {
				return ResponseEntity.notFound().build();
			}

			Long userId = userResponse.get().getId();
			Progreso progreso = progresoService.guardarProgreso(userId, request);
			
			return ResponseEntity.ok(Map.of(
				"message", "Progreso guardado exitosamente",
				"progresoId", progreso.getProgresoId(),
				"peso", progreso.getPeso(),
				"imc", progreso.getImc() != null ? progreso.getImc() : "N/A",
				"fechaRegistro", progreso.getFechaRegistro()
			));
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Error al guardar progreso: " + e.getMessage()));
		}
	}

	@GetMapping("/progreso")
	public ResponseEntity<List<ProgresoResponse>> obtenerHistorialProgreso(Authentication authentication) {
		try {
			String username = authentication.getName();
			Optional<UserResponse> userResponse = userService.findByUsername(username);
			
			if (userResponse.isEmpty()) {
				return ResponseEntity.notFound().build();
			}

			Long userId = userResponse.get().getId();
			List<ProgresoResponse> historial = progresoService.obtenerHistorialProgreso(userId);
			
			return ResponseEntity.ok(historial);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@GetMapping("/metodos-pago")
	public ResponseEntity<List<MetodoPagoResponse>> obtenerMetodosPago(Authentication authentication) {
		try {
			String username = authentication.getName();
			Optional<UserResponse> userResponse = userService.findByUsername(username);
			
			if (userResponse.isEmpty()) {
				return ResponseEntity.notFound().build();
			}

			Long userId = userResponse.get().getId();
			List<MetodoPagoResponse> metodos = metodoPagoService.obtenerMetodosPagoPorUsuario(userId);
			
			return ResponseEntity.ok(metodos);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@PostMapping("/metodos-pago")
	public ResponseEntity<?> agregarMetodoPago(@RequestBody MetodoPagoRequest request, Authentication authentication) {
		try {
			String username = authentication.getName();
			Optional<UserResponse> userResponse = userService.findByUsername(username);
			
			if (userResponse.isEmpty()) {
				return ResponseEntity.notFound().build();
			}

			Long userId = userResponse.get().getId();
			MetodoPagoResponse metodo = metodoPagoService.agregarMetodoPago(userId, request);
			
			return ResponseEntity.ok(metodo);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
		}
	}

	@PutMapping("/metodos-pago/{id}")
	public ResponseEntity<?> actualizarMetodoPago(
			@PathVariable Long id,
			@RequestBody UpdateMetodoPagoRequest request,
			Authentication authentication) {
		try {
			String username = authentication.getName();
			Optional<UserResponse> userResponse = userService.findByUsername(username);
			
			if (userResponse.isEmpty()) {
				return ResponseEntity.notFound().build();
			}

			Long userId = userResponse.get().getId();
			MetodoPagoResponse metodo = metodoPagoService.actualizarMetodoPago(userId, id, request);
			
			return ResponseEntity.ok(metodo);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
		}
	}

	@DeleteMapping("/metodos-pago/{id}")
	public ResponseEntity<?> eliminarMetodoPago(@PathVariable Long id, Authentication authentication) {
		try {
			String username = authentication.getName();
			Optional<UserResponse> userResponse = userService.findByUsername(username);
			
			if (userResponse.isEmpty()) {
				return ResponseEntity.notFound().build();
			}

			Long userId = userResponse.get().getId();
			metodoPagoService.eliminarMetodoPago(userId, id);
			
			return ResponseEntity.ok("Método de pago eliminado exitosamente");
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PutMapping("/metodos-pago/{id}/predeterminado")
	public ResponseEntity<?> establecerPredeterminado(@PathVariable Long id, Authentication authentication) {
		try {
			String username = authentication.getName();
			Optional<UserResponse> userResponse = userService.findByUsername(username);
			
			if (userResponse.isEmpty()) {
				return ResponseEntity.notFound().build();
			}

			Long userId = userResponse.get().getId();
			MetodoPagoResponse metodo = metodoPagoService.establecerPredeterminado(userId, id);
			
			return ResponseEntity.ok(metodo);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
		}
	}

}
