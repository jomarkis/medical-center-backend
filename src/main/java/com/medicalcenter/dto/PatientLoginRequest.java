package com.medicalcenter.dto;

public class PatientLoginRequest extends LoginRequest {
    private Integer patientID;

    public PatientLoginRequest() {}

    public PatientLoginRequest(Integer patientID) {
        this.patientID = patientID;
    }

    public Integer getPatientID() { return patientID; }
    public void setPatientID(Integer patientID) { this.patientID = patientID; }
}