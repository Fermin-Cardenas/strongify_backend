package com.app.demo.DTO.Request;

public class ForgotPasswordRequest {
    private String username;
    private String email; // Opcional, para enviar email de recuperación

    public ForgotPasswordRequest() {}

    public ForgotPasswordRequest(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

