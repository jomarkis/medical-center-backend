package com.medicalcenter.controller;

import com.medicalcenter.service.AppointmentService;
import com.medicalcenter.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    // GET /api/stats/patient-income?patientID=...&minIncome=...&maxIncome=...
    @GetMapping("/patient-income")
    public List<Map<String, Object>> getPatientIncomeStats(
            @RequestParam(required = false) Integer patientID,
            @RequestParam(required = false) Double minIncome,
            @RequestParam(required = false) Double maxIncome) {

        return statisticsService.getPatientIncomeStats(patientID, minIncome, maxIncome);
    }

    @GetMapping("/exam-income")
    public List<Map<String, Object>> getExamIncomeStats(
            @RequestParam(required = false) Integer examID,
            @RequestParam(required = false) Double minIncome,
            @RequestParam(required = false) Double maxIncome) {

        return statisticsService.getExamIncomeStats(examID, minIncome, maxIncome);
    }

    // GET /api/stats/exam-category-income?category=...&minIncome=...&maxIncome=...
    @GetMapping("/exam-category-income")
    public List<Map<String, Object>> getExamCategoryIncomeStats(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minIncome,
            @RequestParam(required = false) Double maxIncome) {

        return statisticsService.getExamCategoryIncomeStats(category, minIncome, maxIncome);
    }
}