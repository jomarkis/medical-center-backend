package com.medicalcenter.controller;

import com.medicalcenter.model.Exam;
import com.medicalcenter.repository.ExamRepository;
import com.medicalcenter.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
@CrossOrigin(origins = "*")
public class ExamController {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private ExamService examService;

    @GetMapping
    public List<Exam> getExams(@RequestParam(required = false) Integer doctorID, Authentication authentication) {
        Integer loggedInId = Integer.valueOf(authentication.getName());
        boolean isDoctor = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"));
        if (isDoctor) {
            doctorID = loggedInId;
        }
        if (doctorID != null) {
            return examService.getExamsByDoctor(doctorID);
        }
        return examRepository.findAll();
    }

    @PostMapping
    public Exam createExam(@RequestBody Exam exam) {
        return examService.createExam(exam);
    }
}