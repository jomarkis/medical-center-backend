package com.medicalcenter.dto;

public class LoginResponse {
    private String token;
    private String role;
    private Integer identifier;

    public LoginResponse() {}

    public LoginResponse(String token, String role, Integer identifier) {
        this.token = token;
        this.role = role;
        this.identifier = identifier;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Integer identifier) {
        this.identifier = identifier;
    }
}