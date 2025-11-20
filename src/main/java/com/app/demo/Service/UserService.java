package com.app.demo.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.app.demo.DTO.Request.ChangePasswordRequest;
import com.app.demo.DTO.Request.UpdateRolRequest;
import com.app.demo.DTO.Request.UpdateUserRequest;
import com.app.demo.DTO.Response.ReservaResponse;
import com.app.demo.DTO.Response.UserResponse;
import com.app.demo.Entity.AuthUser;
import com.app.demo.Entity.Reserva;
import com.app.demo.Entity.Role;
import com.app.demo.Entity.User;
import com.app.demo.Repository.AuthUserRepository;
import com.app.demo.Repository.ReservaRepository;
import com.app.demo.Repository.RoleRepository;
import com.app.demo.Repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {
	private final UserRepository repository;
	private final AuthService authService;
	private final AuthUserRepository authUserRepository;
	private final PasswordEncoder passwordEncoder;
	private final RoleRepository roleRepository;
	private final ReservaRepository reservaRepository;

	
	public User getUserByUsername(String username) {
		return authService.findByGmail(username)
				.orElseThrow(() -> new RuntimeException("User not found: " + username)).getUser();
	}

	public UserService(UserRepository repository, AuthService authService, AuthUserRepository authUserRepository,
			PasswordEncoder passwordEncoder, RoleRepository roleRepository, ReservaRepository reservaRepository) {
		this.repository = repository;
		this.authService = authService;
		this.authUserRepository = authUserRepository;
		this.passwordEncoder = passwordEncoder;
		this.roleRepository = roleRepository;
		this.reservaRepository = reservaRepository;
	}

	private AuthUser getAuthUser(String username) {
		return authUserRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("User not found" + username));
	}

	@Transactional
	public Optional<UserResponse> findByUsername(String username) {
		return authUserRepository.findByUsernameWithRoleAndUser(username).map(authUser -> {
			User user = authUser.getUser();

			LocalDate birthday = user.getBirthday();

			return new UserResponse(user.getUserId(), user.getFirstName(), user.getLastName(), user.getPhoneNumber(),
					user.getPhoto_url(), authUser.getUsername(),
					birthday, authUser.getLastLogin(), user.getAltura());
		});
	}

	@Transactional
	public Optional<UserResponse> updateUserInfo(Long user_id, UpdateUserRequest request, String username) {
		User user = getUserByUsername(username);

		return repository.findById(user_id).map(existingUser -> {
			if (request.getFirstName() != null)
				existingUser.setFirstName(request.getFirstName());
			if (request.getLastName() != null)
				existingUser.setLastName(request.getLastName());
			if (request.getBirthday() != null)
				existingUser.setBirthday(request.getBirthday());
			if (request.getPhoneNumber() != null)
				existingUser.setPhoneNumber(request.getPhoneNumber());
			if (request.getPhotoUrl() != null)
				existingUser.setPhoto_url(request.getPhotoUrl());
			if (request.getAltura() != null) {
				// Validar altura (entre 0.5 y 3.0 metros)
				Double altura = request.getAltura();
				if (altura < 0.5 || altura > 3.0) {
					throw new RuntimeException("La altura debe estar entre 0.5 y 3.0 metros");
				}
				existingUser.setAltura(altura);
			}

			AuthUser authUser = authUserRepository.findByUser(existingUser)
					.orElseThrow(() -> new RuntimeException("User not found"));
			authUser.setUpdatedAt(LocalDateTime.now());
			authUser = authUserRepository.save(authUser);

			User savedUser = repository.save(existingUser);

			return new UserResponse(savedUser.getUserId(), savedUser.getFirstName(), savedUser.getLastName(),
					savedUser.getPhoneNumber(), savedUser.getPhoto_url(),
					authUserRepository.findByUser(savedUser).map(AuthUser::getUsername).orElse(""),
					savedUser.getBirthday(),
					savedUser.getAuthUser().getLastLogin(),
					savedUser.getAltura());
		});
	}

	@Transactional
	public void changePassword(String username, ChangePasswordRequest request) {
		// 1. Find the user
		AuthUser user = getAuthUser(username);

		// 2. Validate the actual password is equal to current password
		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new RuntimeException("The actual password is incorrect. ");
		}

		// 3. Validate the new password match
		if (!request.getNewPassword().equals(request.getConfirmPassword())) {
			throw new RuntimeException("The password not match.");
		}

		// 4. Validate that new password be different to the actual pass.
		if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
			throw new RuntimeException("The new password be different to the old password.");
		}

		// 5. Encrypt and set the new password.
		String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
		user.setPassword(encodedNewPassword);

		// 6. Update the date of updated
		user.setUpdatedAt(LocalDateTime.now());

		// 7. Save the password
		authUserRepository.save(user);
	}

	@Transactional
	public Map<String, String> uploadImage(String username, MultipartFile file) throws IOException {
		User user = getUserByUsername(username);

		if (file.isEmpty()) {
			throw new RuntimeException("File empty");
		}

		String contentType = file.getContentType();
		if (contentType == null || !contentType.startsWith("image/")) {
			throw new RuntimeException("Solo se permiten imágenes");
		}

		String getPrevius = user.getPhoto_url();
		if (getPrevius != null) {
			Path previousPath = Paths.get("uploads/", getPrevius);
			if (Files.exists(previousPath)) {
				Files.delete(previousPath);
			}
		}

		// Save file
		String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
		Path imagePath = Paths.get("uploads/", fileName);
		Files.createDirectories(imagePath.getParent());
		Files.copy(file.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);

		// Update user
		user.setPhoto_url(fileName);
		repository.save(user);

		return Map.of("fileName", fileName, "message", "Imagen subida con éxito");
	}

	// Método para cambiar el rol del usuario
	public Optional<UserResponse> updateRol(Long user_id, UpdateRolRequest request) {

		return repository.findById(user_id).map(user -> {
			AuthUser authUser = authUserRepository.findByUser(user)
					.orElseThrow(() -> new RuntimeException("User not found"));

			Role role = roleRepository.findById(request.getRol_id())
					.orElseThrow(() -> new RuntimeException("Role not found"));

			authUser.setRole(role);

			authUser.setRole(role);
			authUser.setUpdatedAt(LocalDateTime.now());
			authUserRepository.save(authUser);

			User updatedUser = repository.save(user);

			return new UserResponse(
					updatedUser.getUserId(),
					updatedUser.getFirstName(),
					updatedUser.getLastName(),
					updatedUser.getPhoneNumber(),
					updatedUser.getPhoto_url(),
					authUser.getUsername(),
					updatedUser.getBirthday(),
					authUser.getLastLogin(),
					updatedUser.getAltura());
		});
	}

    @Transactional
    public List<ReservaResponse> listarReservasPorUsuario(String username) {
        User user = getUserByUsername(username);
        List<Reserva> reservas = reservaRepository.findByCliente(user);
        
        return reservas.stream()
                .map(this::convertirAReservaResponse)
                .collect(Collectors.toList());
    }

    private ReservaResponse convertirAReservaResponse(Reserva reserva) {
        // Obtener username del cliente
        String clienteUsername = null;
        Optional<AuthUser> authUserOpt = authUserRepository.findByUser(reserva.getCliente());
        if (authUserOpt.isPresent()) {
            clienteUsername = authUserOpt.get().getUsername();
        }

        // Crear ClienteInfo
        ReservaResponse.ClienteInfo clienteInfo = new ReservaResponse.ClienteInfo(
            reserva.getCliente().getUserId(),
            clienteUsername
        );

        // Obtener nombre de la clase desde el catálogo
        String nombreClase = null;
        if (reserva.getClaseAgendada() != null && 
            reserva.getClaseAgendada().getCatalogo() != null) {
            nombreClase = reserva.getClaseAgendada().getCatalogo().getNombre();
        }

        // Crear ClaseAgendadaInfo
        ReservaResponse.ClaseAgendadaInfo claseInfo = new ReservaResponse.ClaseAgendadaInfo(
            reserva.getClaseAgendada() != null ? reserva.getClaseAgendada().getId() : null,
            nombreClase
        );

        // Crear y retornar ReservaResponse
        return new ReservaResponse(
            reserva.getId(),
            clienteInfo,
            claseInfo,
            reserva.getFechaReserva(),
            reserva.getEstado(),
            reserva.getAsistencia()
        );
    }
}
