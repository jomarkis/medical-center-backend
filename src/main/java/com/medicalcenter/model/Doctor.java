package com.medicalcenter.model;
import jakarta.persistence.*;

@Entity
@Table(name = "doctors")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int doctorID;

    @Column(nullable = false)
    private String doctorName;

    private String doctorPhone;
    private String specialty;
    private int years;

    // Default constructor (υποχρεωτικός για το JPA/Hibernate)
    public Doctor() {
    }

    public Doctor(String doctorName, String doctorPhone, String specialty, int years) {
        this.doctorName = doctorName;
        this.doctorPhone = doctorPhone;
        this.specialty = specialty;
        this.years = years;
    }

    // Getters & Setters
    public int getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(int doctorID) {
        this.doctorID = doctorID;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorPhone() {
        return doctorPhone;
    }

    public void setDoctorPhone(String doctorPhone) {
        this.doctorPhone = doctorPhone;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public int getYears() {
        return years;
    }

    public void setYears(int years) {
        this.years = years;
    }
}