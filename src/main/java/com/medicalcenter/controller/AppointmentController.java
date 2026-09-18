package com.medicalcenter.controller;

import com.medicalcenter.dto.AppointmentRequest;
import com.medicalcenter.model.Appointment;
import com.medicalcenter.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return appointmentService.getAppointments(examID, patientID, doctorID, date);
    }

    // POST /api/appointments -> Κλείσιμο νέου ραντεβού
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Appointment createAppointment(@RequestBody AppointmentRequest request) {
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