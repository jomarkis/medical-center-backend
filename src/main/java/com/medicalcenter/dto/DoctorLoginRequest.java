package com.medicalcenter.dto;

public class DoctorLoginRequest extends LoginRequest {
    private Integer doctorID;

    public DoctorLoginRequest() {}

    public DoctorLoginRequest(Integer doctorID) {
        this.doctorID = doctorID;
    }

    public Integer getDoctorID() { return doctorID; }
    public void setDoctorID(Integer doctorID) { this.doctorID = doctorID; }
}