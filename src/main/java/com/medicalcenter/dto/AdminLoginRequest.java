package com.medicalcenter.dto;

public class AdminLoginRequest extends LoginRequest {
    private String pin;

    public AdminLoginRequest() {}

    public AdminLoginRequest(String pin) {
        this.pin = pin;
    }

    public String getPin() { return pin; }
    public void setPin(String pin) { this.pin = pin; }
}