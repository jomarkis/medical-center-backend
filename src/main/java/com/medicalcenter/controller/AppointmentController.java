package com.medicalcenter.controller;

import com.medicalcenter.dto.AppointmentRequest;
import com.medicalcenter.model.Appointment;
import com.medicalcenter.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    // GET /api/appointments ή φιλτράρισμα με ?examID=.. ή ?patientID=..
    @GetMapping
    public List<Appointment> getAppointments(
            @RequestParam(required = false) Integer examID,
            @RequestParam(required = false) Integer patientID) {

        if (examID != null) {
            return appointmentService.getAppointmentsByExam(examID);
        }
        if (patientID != null) {
            return appointmentService.getAppointmentsByPatient(patientID);
        }
        return appointmentService.getAllAppointments();
    }

    // POST /api/appointments -> Κλείσιμο νέου ραντεβού
    @PostMapping
    public Appointment createAppointment(@RequestBody AppointmentRequest request) {
        return appointmentService.bookAppointment(
                request.getPatientID(),
                request.getExamID(),
                request.getAppointmentDate(),
                request.isFastResults()
        );
    }
}