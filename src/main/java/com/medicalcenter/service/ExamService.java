package com.medicalcenter.service;

import com.medicalcenter.model.Exam;
import com.medicalcenter.repository.DoctorRepository;
import com.medicalcenter.repository.ExamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamService {
    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    public List<Exam> getExamsByDoctor(int doctorID) {

        if (!doctorRepository.existsById(doctorID)) {
            throw new IllegalArgumentException("Doctor not found with ID: " + doctorID);
        }

        return examRepository.findByDoctorID(doctorID);
    }

    public Exam createExam(Exam exam) {
        if (!doctorRepository.existsById(exam.getDoctorID())) {
            throw new IllegalArgumentException("Cannot create exam. Doctor not found with ID: " + exam.getDoctorID());
        }
        return examRepository.save(exam);
    }
}
