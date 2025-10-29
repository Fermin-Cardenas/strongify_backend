package com.app.demo.Controller;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.app.demo.DTO.Request.ChangePasswordRequest;
import com.app.demo.DTO.Request.UpdateUserRequest;
import com.app.demo.DTO.Response.UserResponse;
import com.app.demo.Service.UserService;

@RestController
@RequestMapping("/api/profile")
public class UserController {
	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/get-profile-info")
	public ResponseEntity<UserResponse> getMyUserProfile(Authentication authentication) {
		String username = authentication.getName();

		return userService.findByUsername(username).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
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
	public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request, Authentication authentication) {
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

}
