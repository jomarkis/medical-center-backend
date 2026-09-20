package com.medicalcenter.service;

import com.medicalcenter.model.Appointment;
import com.medicalcenter.model.Exam;
import com.medicalcenter.repository.AppointmentRepository;
import com.medicalcenter.repository.DoctorRepository;
import com.medicalcenter.repository.ExamRepository;
import com.medicalcenter.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ExamRepository examRepository;
    private final PatientRepository patientRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              ExamRepository examRepository,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.examRepository = examRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    @Autowired
    private DoctorRepository doctorRepository;

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> getAppointments(Integer examID, Integer patientID, Integer doctorID, LocalDate date) {
        // Ελέγχουμε μόνο όσα πεδία έχουν δοθεί πραγματικά από το frontend
        if (examID != null && !examRepository.existsById(examID)) {
            throw new IllegalArgumentException("Exam not found with ID: " + examID);
        }
        if (patientID != null && !patientRepository.existsById(patientID)) {
            throw new IllegalArgumentException("Patient not found with ID: " + patientID);
        }
        if (doctorID != null && !doctorRepository.existsById(doctorID)) {
            throw new IllegalArgumentException("Doctor not found with ID: " + doctorID);
        }

        // Αν όλα τα δοθέντα IDs είναι έγκυρα, εκτελείται το συνδυαστικό query
        return appointmentRepository.findWithFilters(examID, patientID, doctorID, date);
    }

    @Transactional
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

    public List<Map<String, Object>> getPatientIncomeStats(Integer patientID, Double minIncome, Double maxIncome) {
        if (patientID != null && !patientRepository.existsById(patientID)) {
            throw new IllegalArgumentException("Patient not found with ID: " + patientID);
        }

        if (minIncome != null && maxIncome != null && minIncome > maxIncome) {
            throw new IllegalArgumentException("minIncome cannot be greater than maxIncome.");
        }

        return appointmentRepository.getPatientIncomeStats(patientID, minIncome, maxIncome);
    }

    @Transactional
    public Map<String, String> deleteAppointment(Integer id) {
        if (!appointmentRepository.existsById(id)) {
            throw new IllegalArgumentException("Appointment not found with ID: " + id);
        }

        appointmentRepository.deleteById(id);

        return Map.of(
                "status", "success",
                "message", "Appointment with ID " + id + " was successfully deleted."
        );
    }
}