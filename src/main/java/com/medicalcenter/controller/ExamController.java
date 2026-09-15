package com.medicalcenter.controller;

import com.medicalcenter.model.Exam;
import com.medicalcenter.repository.ExamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
@CrossOrigin(origins = "*")
public class ExamController {

    @Autowired
    private ExamRepository examRepository;


    @GetMapping
    public List<Exam> getExams(@RequestParam(required = false) Integer doctorID) {
        if (doctorID != null) {
            return examRepository.findByDoctorID(doctorID);
        }
        return examRepository.findAll();
    }

    @PostMapping
    public Exam createExam(@RequestBody Exam exam) {
        return examRepository.save(exam);
    }
}