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
import org.springframework.data.repository.query.Param;
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
public class StatisticsServiceTest {
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

    @InjectMocks
    private StatisticsService statisticsService;

    // ## Get Patient Income Stats Tests ##
    @Test
    @DisplayName("Test getPatientIncomeStats with valid patientID and income range")
    public void testGetPatientIncomeStats_ValidInput() {
        Patient mockPatient = new Patient();
        mockPatient.setPatientID(1);

        Exam mockExam = new ImagingExamination();
        mockExam.setExamID(1);
        mockExam.setExamCategory("Imaging");

        Appointment mockAppointment = new Appointment();
        mockAppointment.setPatientID(1);

        List<Map<String, Object>> mockStats = new ArrayList<>();
        mockStats.add(Map.of("patientID", 1, "totalIncome", 300.0, "totalAppointments", 3));

        when(patientRepository.existsById(1)).thenReturn(true);
        when(appointmentRepository.getPatientIncomeStats(1, 100.0, 500.0)).thenReturn(mockStats);

        List<Map<String, Object>> result = statisticsService.getPatientIncomeStats(1, 100.0, 500.0);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).get("patientID"));
        assertEquals(300.0, result.get(0).get("totalIncome"));
        assertEquals(3, result.get(0).get("totalAppointments"));
        verify(appointmentRepository, times(1)).getPatientIncomeStats(1, 100.0, 500.0);
    }

    @Test
    @DisplayName("Test getPatientIncomeStats with invalid patientID")
    public void testGetPatientIncomeStats_InvalidPatientID() {
        Patient mockPatient = new Patient();
        mockPatient.setPatientID(1);

        Exam mockExam = new ImagingExamination();
        mockExam.setExamID(1);
        mockExam.setExamCategory("Imaging");

        Appointment mockAppointment = new Appointment();
        mockAppointment.setPatientID(1);

        when(patientRepository.existsById(999)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->statisticsService.getPatientIncomeStats(999, 100.0, 500.0));

        assertEquals(exception.getMessage(), "Patient not found with ID: 999");
        verify(appointmentRepository, never()).getPatientIncomeStats(999, 100.0, 500.0);
    }

    @Test
    @DisplayName("Test getPatientIncomeStats with Invalid income range")
    public void testGetPatientIncomeStats_InvalidIncomeRange() {
        Patient mockPatient = new Patient();
        mockPatient.setPatientID(1);

        Exam mockExam = new ImagingExamination();
        mockExam.setExamID(1);
        mockExam.setExamCategory("Imaging");

        Appointment mockAppointment = new Appointment();
        mockAppointment.setPatientID(1);

        when(patientRepository.existsById(1)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->statisticsService.getPatientIncomeStats(1, 100.0, 50.0));

        assertEquals(exception.getMessage(), "minIncome cannot be greater than maxIncome.");
        verify(appointmentRepository, never()).getPatientIncomeStats(1, 100.0, 500.0);
    }

    // ## getExamIncomeStats Tests ##

    @Test
    @DisplayName("Test getExamIncomeStats with valid examID and income range")
    public void testGetExamIncomeStats_ValidInput() {
        Patient mockPatient = new Patient();
        mockPatient.setPatientID(1);

        Exam mockExam = new ImagingExamination();
        mockExam.setExamID(1);
        mockExam.setExamCategory("Imaging");

        Appointment mockAppointment = new Appointment();
        mockAppointment.setPatientID(1);

        List<Map<String, Object>> mockStats = new ArrayList<>();
        mockStats.add(Map.of("examID", 1, "totalIncome", 300.0, "totalAppointments", 3));

        when(examRepository.existsById(1)).thenReturn(true);
        when(appointmentRepository.getExamIncomeStats(1, 100.0, 500.0)).thenReturn(mockStats);

        List<Map<String, Object>> result = statisticsService.getExamIncomeStats(1, 100.0, 500.0);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).get("examID"));
        assertEquals(300.0, result.get(0).get("totalIncome"));
        assertEquals(3, result.get(0).get("totalAppointments"));
        verify(appointmentRepository, times(1)).getExamIncomeStats(1, 100.0, 500.0);

    }

    @Test
    @DisplayName("Test getExamIncomeStats with invalid examID")
    public void testGetExamIncomeStats_InvalidExamID() {
        Patient mockPatient = new Patient();
        mockPatient.setPatientID(1);

        Exam mockExam = new ImagingExamination();
        mockExam.setExamID(1);
        mockExam.setExamCategory("Imaging");

        Appointment mockAppointment = new Appointment();
        mockAppointment.setPatientID(1);

        when(examRepository.existsById(999)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->statisticsService.getExamIncomeStats(999, 100.0, 500.0));

        assertEquals(exception.getMessage(), "Exam not found with ID: 999");
        verify(appointmentRepository, never()).getExamIncomeStats(999, 100.0, 500.0);
    }

    @Test
    @DisplayName("Test getExamIncomeStats with Invalid income range")
    public void testGetExamIncomeStats_InvalidIncomeRange() {
        Patient mockPatient = new Patient();
        mockPatient.setPatientID(1);

        Exam mockExam = new ImagingExamination();
        mockExam.setExamID(1);
        mockExam.setExamCategory("Imaging");

        Appointment mockAppointment = new Appointment();
        mockAppointment.setPatientID(1);

        when(examRepository.existsById(1)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->statisticsService.getExamIncomeStats(1, 100.0, 50.0));

        assertEquals(exception.getMessage(), "minIncome cannot be greater than maxIncome.");
        verify(appointmentRepository, never()).getExamIncomeStats(1, 100.0, 500.0);
    }

    // ## getExamCategoryIncomeStats Tests ##

    @Test
    @DisplayName("Test getExamCategoryIncomeStats with valid income range")
    public void testGetExamCategoryIncomeStats_ValidInput() {
        Patient mockPatient = new Patient();
        mockPatient.setPatientID(1);

        Exam mockExam = new ImagingExamination();
        mockExam.setExamID(1);
        mockExam.setExamCategory("Imaging");

        Appointment mockAppointment = new Appointment();
        mockAppointment.setPatientID(1);

        List<Map<String, Object>> mockStats = new ArrayList<>();
        mockStats.add(Map.of("category", "Imaging", "totalIncome", 300.0, "totalAppointments", 3));

        when(appointmentRepository.getCategoryIncomeStats("Imaging", 100.0, 500.0)).thenReturn(mockStats);

        List<Map<String, Object>> result = statisticsService.getExamCategoryIncomeStats("Imaging", 100.0, 500.0);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Imaging", result.get(0).get("category"));
        assertEquals(300.0, result.get(0).get("totalIncome"));
        assertEquals(3, result.get(0).get("totalAppointments"));
        verify(appointmentRepository, times(1)).getCategoryIncomeStats("Imaging", 100.0, 500.0);
    }

    @Test
    @DisplayName("Test getExamCategoryIncomeStats with Invalid income range")
    public void testGetExamCategoryIncomeStats_InvalidIncomeRange() {
        Patient mockPatient = new Patient();
        mockPatient.setPatientID(1);

        Exam mockExam = new ImagingExamination();
        mockExam.setExamID(1);
        mockExam.setExamCategory("Imaging");

        Appointment mockAppointment = new Appointment();
        mockAppointment.setPatientID(1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->statisticsService.getExamCategoryIncomeStats("Imaging", 100.0, 50.0));

        assertEquals(exception.getMessage(), "minIncome cannot be greater than maxIncome.");
        verify(appointmentRepository, never()).getCategoryIncomeStats("Imaging", 100.0, 50.0);
    }
}
