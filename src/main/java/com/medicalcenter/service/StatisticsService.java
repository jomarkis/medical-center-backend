package com.medicalcenter.service;

import com.medicalcenter.repository.AppointmentRepository;
import com.medicalcenter.repository.ExamRepository;
import com.medicalcenter.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Transactional(readOnly = true)
@Service
public class StatisticsService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ExamRepository examRepository;

    public List<Map<String, Object>> getPatientIncomeStats(Integer patientID, Double minIncome, Double maxIncome) {
        if (patientID != null && !patientRepository.existsById(patientID)) {
            throw new IllegalArgumentException("Patient not found with ID: " + patientID);
        }

        if (minIncome != null && maxIncome != null && minIncome > maxIncome) {
            throw new IllegalArgumentException("minIncome cannot be greater than maxIncome.");
        }

        return appointmentRepository.getPatientIncomeStats(patientID, minIncome, maxIncome);
    }

    public List<Map<String, Object>> getExamIncomeStats(Integer examID, Double minIncome, Double maxIncome) {
        if (examID != null && !examRepository.existsById(examID)) {
            throw new IllegalArgumentException("Exam not found with ID: " + examID);
        }
        if (minIncome != null && maxIncome != null && minIncome > maxIncome) {
            throw new IllegalArgumentException("minIncome cannot be greater than maxIncome.");
        }
        return appointmentRepository.getExamIncomeStats(examID, minIncome, maxIncome);
    }

    public List<Map<String, Object>> getExamCategoryIncomeStats(String category, Double minIncome, Double maxIncome) {
        if (minIncome != null && maxIncome != null && minIncome > maxIncome) {
            throw new IllegalArgumentException("minIncome cannot be greater than maxIncome.");
        }
        return appointmentRepository.getCategoryIncomeStats(category, minIncome, maxIncome);
    }
}
