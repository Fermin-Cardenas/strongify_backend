package com.app.demo.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.app.demo.DTO.Request.ChangePasswordRequest;
import com.app.demo.DTO.Request.UpdateUserRequest;
import com.app.demo.DTO.Response.UserResponse;
import com.app.demo.Entity.AuthUser;
import com.app.demo.Entity.User;
import com.app.demo.Repository.AuthUserRepository;
import com.app.demo.Repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {
	private final UserRepository repository;
	private final AuthService authService;
	private final AuthUserRepository authUserRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository repository, AuthService authService, AuthUserRepository authUserRepository, PasswordEncoder passwordEncoder) {
		this.repository = repository;
		this.authService = authService;
		this.authUserRepository = authUserRepository;
		this.passwordEncoder = passwordEncoder;
	}

	private User getUserByUsername(String username) {
		return authService.findByGmail(username)
				.orElseThrow(() -> new RuntimeException("User not found: " + username)).getUser();
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
					birthday, authUser.getLastLogin());
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

			AuthUser authUser = authUserRepository.findByUser(existingUser)
					.orElseThrow(() -> new RuntimeException("User not found"));
			authUser.setUpdatedAt(LocalDateTime.now());
			authUser = authUserRepository.save(authUser);

			User savedUser = repository.save(existingUser);

			return new UserResponse(savedUser.getUserId(), savedUser.getFirstName(), savedUser.getLastName(),
					savedUser.getPhoneNumber(), savedUser.getPhoto_url(),
					authUserRepository.findByUser(savedUser).map(AuthUser::getUsername).orElse(""),
					savedUser.getBirthday(),
					savedUser.getAuthUser().getLastLogin());
		});
	}
	
	@Transactional 
	public void changePassword(String username, ChangePasswordRequest request) {
		// 1. Find the user
		AuthUser user = getAuthUser(username);
		
		// 2. Validate the actual password is equal to current password
		if(!passwordEncoder.matches(request.getPassword(),user.getPassword())) {
			throw new RuntimeException("The actual password is incorrect. ");
		}
		
		// 3. Validate the new password match
		if(!request.getNewPassword().equals(request.getConfirmPassword())) {
			throw new RuntimeException("The password not match.");
		}
		
		// 4. Validate that new password be different to the actual pass.
		if(passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
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
		
		if(file.isEmpty()) {
			throw new RuntimeException("File empty");
		}
		
		String contentType = file.getContentType();
	    if (contentType == null || !contentType.startsWith("image/")) {
	        throw new RuntimeException("Solo se permiten imágenes");
	    }
	    
	    String getPrevius = user.getPhoto_url();
	    if(getPrevius != null) {
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

}
