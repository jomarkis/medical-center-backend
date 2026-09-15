package com.medicalcenter.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int appointmentID;

    private int patientID;
    private int examID;
    private LocalDate appointmentDate;
    private boolean fastResults;
    private float cost;

    public Appointment() {
    }

    public Appointment(int patientID, int examID, LocalDate appointmentDate, boolean fastResults, float cost) {
        this.patientID = patientID;
        this.examID = examID;
        this.appointmentDate = appointmentDate;
        this.fastResults = fastResults;
        this.cost = cost;
    }

    // Getters and Setters
    public int getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(int appointmentID) {
        this.appointmentID = appointmentID;
    }

    public int getPatientID() {
        return patientID;
    }

    public void setPatientID(int patientID) {
        this.patientID = patientID;
    }

    public int getExamID() {
        return examID;
    }

    public void setExamID(int examID) {
        this.examID = examID;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public boolean isFastResults() {
        return fastResults;
    }

    public void setFastResults(boolean fastResults) {
        this.fastResults = fastResults;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }
}