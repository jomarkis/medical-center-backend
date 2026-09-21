package com.medicalcenter.service;

import com.medicalcenter.model.*;
import com.medicalcenter.repository.AppointmentRepository;
import com.medicalcenter.repository.DoctorRepository;
import com.medicalcenter.repository.ExamRepository;
import com.medicalcenter.repository.PatientRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExamServiceTest {
    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private ExamRepository examRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    @InjectMocks
    private ExamService examService;

    @Test
    @DisplayName("Get exams with valid DoctorID")
    public void testGetExamsByDoctorWithValidDoctorID() {
        Doctor mockDoctor = new Doctor();
        mockDoctor.setDoctorID(1);

        Exam mockExam = new ImagingExamination();
        mockExam.setDoctorID(1);

        when(doctorRepository.existsById(1)).thenReturn(true);
        when(examRepository.findByDoctorID(1)).thenReturn(List.of(mockExam));

        List<Exam> exams = examService.getExamsByDoctor(1);

        assertFalse(exams.isEmpty());
        assertTrue(exams.contains(mockExam));
        verify(examRepository, times(1)).findByDoctorID(1);
    }

    @Test
    @DisplayName("Get exams with invalid DoctorID")
    public void testGetExamsByDoctorWithInvalidDoctorID() {
        Doctor mockDoctor = new Doctor();
        mockDoctor.setDoctorID(1);

        Exam mockExam = new ImagingExamination();
        mockExam.setDoctorID(1);

        when(doctorRepository.existsById(100)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> examService.getExamsByDoctor(100));

        assertEquals("Doctor not found with ID: 100", exception.getMessage());
        verify(examRepository, never()).findByDoctorID(100);
    }

    @Test
    @DisplayName("Create exam with valid DoctorID")
    public void testCreateExamWithValidDoctorID() {
        Doctor mockDoctor = new Doctor();
        mockDoctor.setDoctorID(1);

        Exam mockExam = new ImagingExamination();
        mockExam.setDoctorID(1);

        when(doctorRepository.existsById(mockExam.getDoctorID())).thenReturn(true);
        when(examRepository.save(any(Exam.class))).thenReturn(mockExam);

        Exam result = examService.createExam(mockExam);
        assertNotNull(result);
        assertEquals(result.getDoctorID(), mockExam.getDoctorID());
        verify(examRepository, times(1)).save(mockExam);
    }

    @Test
    @DisplayName("Create exam with invalid DoctorID")
    public void testCreateExamWithInvalidDoctorID() {
        Doctor mockDoctor = new Doctor();
        mockDoctor.setDoctorID(1);

        Exam mockExam = new ImagingExamination();
        mockExam.setDoctorID(100);

        when(doctorRepository.existsById(mockExam.getDoctorID())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> examService.createExam(mockExam));

        assertEquals("Cannot create exam. Doctor not found with ID: 100", exception.getMessage());
        verify(examRepository, never()).save(any(Exam.class));
    }
}
