package com.medicalcenter.dto;

import java.time.LocalDate;

public class AppointmentRequest {
    private int patientID;
    private int examID;
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