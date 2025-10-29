package com.app.demo.DTO.Response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserResponse {
	private Long id;
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String photoUrl;
	private String username;
	private LocalDate birthday;
	private LocalDateTime lastLogin;

	public UserResponse() {
	}

	public UserResponse(Long id, String firstName, String lastName, String phoneNumber, String photoUrl,
			String username, LocalDate birthday, LocalDateTime lastLogin) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNumber = phoneNumber;
		this.photoUrl = photoUrl;
		this.username = username;
		this.birthday = birthday;
		this.lastLogin = lastLogin;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public LocalDate getBirthday() {
		return birthday;
	}

	public void setBirthday(LocalDate birthday) {
		this.birthday = birthday;
	}

	public LocalDateTime getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(LocalDateTime lastLogin) {
		this.lastLogin = lastLogin;
	}
}