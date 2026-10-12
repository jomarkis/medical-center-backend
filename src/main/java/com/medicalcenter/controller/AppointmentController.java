package com.medicalcenter.controller;

import com.medicalcenter.dto.AppointmentRequest;
import com.medicalcenter.model.Appointment;
import com.medicalcenter.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
            @RequestParam(required = false) Integer patientID,
            @RequestParam(required = false) Integer doctorID,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, Authentication authentication) {
        Integer loggedInId = Integer.valueOf(authentication.getName());
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isDoctor = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"));
        boolean isPatient = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PATIENT"));

        if (isDoctor) {
            doctorID = loggedInId;
        } else if (isPatient) {
            patientID = loggedInId;
        }
        return appointmentService.getAppointments(examID, patientID, doctorID, date);
    }

    // POST /api/appointments -> Κλείσιμο νέου ραντεβού
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Appointment createAppointment(@Valid @RequestBody AppointmentRequest request) {
        return appointmentService.bookAppointment(
                request.getPatientID(),
                request.getExamID(),
                request.getAppointmentDate(),
                request.isFastResults()
        );
    }


    // DELETE /api/appointments/delete/{id}
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteAppointment(@PathVariable Integer id) {
        Map<String, String> response = appointmentService.deleteAppointment(id);
        return ResponseEntity.ok(response);
    }
}