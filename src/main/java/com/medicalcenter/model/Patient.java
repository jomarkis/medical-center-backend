package com.medicalcenter.model;

import jakarta.persistence.*;

@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int patientID; // Patient ID

    @Column(nullable = false)
    private String patientName; // Patient name

    private String patientPhone; // Patient phone
    private String email; // Patient email

    public Patient() {
    }

    // Constructor of the patients
    public Patient(String patientName, String patientPhone, String email) {
        // The rest are given by the user during the creation of the object.
        this.patientName = patientName;
        this.patientPhone = patientPhone;
        this.email = email;
    }

    // Getters and Setters

    public int getPatientID() {
        return patientID;
    }

    public void setPatientID(int patientID) {
        this.patientID = patientID;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}