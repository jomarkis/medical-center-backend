package com.medicalcenter.service;

import com.medicalcenter.dto.*;
import com.medicalcenter.repository.DoctorRepository;
import com.medicalcenter.repository.PatientRepository;
import com.medicalcenter.security.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthenticationService {

    private final JwtUtils jwtUtils;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    @Value("${admin.pin:1234}")
    private String adminPin;

    public AuthenticationService(JwtUtils jwtUtils, PatientRepository patientRepository, DoctorRepository doctorRepository) {
        this.jwtUtils = jwtUtils;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    public LoginResponse login(LoginRequest request) {
        if (request instanceof AdminLoginRequest adminReq) {
            return authenticateAdmin(adminReq.getPin());
        } else if (request instanceof DoctorLoginRequest docReq) {
            return authenticateDoctor(docReq.getDoctorID());
        } else if (request instanceof PatientLoginRequest patReq) {
            return authenticatePatient(patReq.getPatientID());
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown login request type");
    }

    private LoginResponse authenticateAdmin(String pin) {
        if (pin == null || !pin.equals(adminPin)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Admin PIN");
        }
        String token = jwtUtils.generateToken("ROLE_ADMIN", 0);
        return new LoginResponse(token, "ROLE_ADMIN", 0);
    }

    private LoginResponse authenticateDoctor(Integer doctorID) {
        if (doctorID == null || doctorID <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Doctor ID");
        }
        if (!doctorRepository.existsById(doctorID)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor with ID " + doctorID + " not found");
        }
        String token = jwtUtils.generateToken("ROLE_DOCTOR", doctorID);
        return new LoginResponse(token, "ROLE_DOCTOR", doctorID);
    }

    private LoginResponse authenticatePatient(Integer patientID) {
        if (patientID == null || patientID <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Patient ID");
        }
        if (!patientRepository.existsById(patientID)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient with ID " + patientID + " not found");
        }
        String token = jwtUtils.generateToken("ROLE_PATIENT", patientID);
        return new LoginResponse(token, "ROLE_PATIENT", patientID);
    }
}