package com.medicalcenter.dto;


import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public class AppointmentRequest {
    @NotNull(message = "Patient ID is required")
    @Positive(message = "Patient ID must be a positive number")
    private Integer patientID;

    @NotNull(message = "Exam ID is required")
    @Positive(message = "Exam ID must be a positive number")
    private Integer examID;

    @NotNull(message = "Appointment date is required")
    @FutureOrPresent(message = "Appointment date cannot be in the past")
    private LocalDate appointmentDate;

    private boolean fastResults;

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
}