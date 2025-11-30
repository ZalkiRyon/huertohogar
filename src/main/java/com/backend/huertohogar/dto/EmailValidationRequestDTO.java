package com.backend.huertohogar.dto;

public class EmailValidationRequestDTO {
    private String email;

    public EmailValidationRequestDTO() {}

    public EmailValidationRequestDTO(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
