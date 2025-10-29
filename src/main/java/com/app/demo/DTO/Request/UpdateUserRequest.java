package com.app.demo.DTO.Request;

import java.time.LocalDate;

public class UpdateUserRequest {

	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String photoUrl;
	private LocalDate birthday;

	public UpdateUserRequest() {

	}

	public UpdateUserRequest(String firstName, String lastName, String phoneNumber, String photoUrl, LocalDate birthday) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNumber = phoneNumber;
		this.photoUrl = photoUrl;
		this.birthday = birthday;
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

	public LocalDate getBirthday() {
		return birthday;
	}

	public void setBirthday(LocalDate birthday) {
		this.birthday = birthday;
	}

}
