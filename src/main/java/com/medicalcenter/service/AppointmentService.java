package com.medicalcenter.service;

import com.medicalcenter.model.Appointment;
import com.medicalcenter.model.Exam;
import com.medicalcenter.repository.AppointmentRepository;
import com.medicalcenter.repository.ExamRepository;
import com.medicalcenter.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private PatientRepository patientRepository;

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> getAppointmentsByExam(int examID) {
        return appointmentRepository.findByExamID(examID);
    }

    public List<Appointment> getAppointmentsByPatient(int patientID) {
        return appointmentRepository.findByPatientID(patientID);
    }

    public Appointment bookAppointment(int patientID, int examID, LocalDate date, boolean fastResults) {
        if (!patientRepository.existsById(patientID)) {
            throw new IllegalArgumentException("Patient not found with ID: " + patientID);
        }

        Exam exam = examRepository.findById(examID)
                .orElseThrow(() -> new IllegalArgumentException("Exam not found with ID: " + examID));

        long bookedSlots = appointmentRepository.countByExamIDAndAppointmentDate(examID, date);
        if (bookedSlots >= exam.getMaxSlotsPerDay()) {
            throw new IllegalStateException("No available slots for this exam on date: " + date);
        }

        float finalCost = exam.getCost(fastResults);

        Appointment appointment = new Appointment(patientID, examID, date, fastResults, finalCost);
        return appointmentRepository.save(appointment);
    }
}