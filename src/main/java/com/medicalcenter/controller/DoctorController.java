package com.medicalcenter.controller;

import com.medicalcenter.model.Doctor;
import com.medicalcenter.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "*")
public class DoctorController {

    @Autowired
    private DoctorRepository doctorRepository;

    // GET /api/doctors -> Επιστρέφει όλους τους γιατρούς σε JSON
    @GetMapping
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    // POST /api/doctors -> Αποθηκεύει έναν νέο γιατρό στη βάση
    @PostMapping
    public Doctor createDoctor(@RequestBody Doctor doctor) {
        return doctorRepository.save(doctor);
    }
}