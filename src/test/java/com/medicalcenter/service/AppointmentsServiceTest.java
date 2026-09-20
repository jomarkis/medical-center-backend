package com.medicalcenter.service;

import com.medicalcenter.model.Appointment;
import com.medicalcenter.model.Exam;
import com.medicalcenter.model.ImagingExamination;
import com.medicalcenter.model.Patient;
import com.medicalcenter.repository.AppointmentRepository;
import com.medicalcenter.repository.DoctorRepository;
import com.medicalcenter.repository.ExamRepository;
import com.medicalcenter.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentsServiceTest {

    // 1. Πρέπει να υπάρχουν και τα 4 mocks που ζητάει ο constructor:
    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private ExamRepository examRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository; // Μην το παραλείψεις!

    // 2. Εδώ γίνεται η έγχυση των 4 mocks:
    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    void appointmentBooking() {
        // Arrange
        LocalDate date = LocalDate.now();

        Patient mockPatient = new Patient();
        mockPatient.setPatientID(1);

        Exam mockExam = new ImagingExamination();
        mockExam.setExamID(10);
        mockExam.setCost(50.0f);
        mockExam.setMaxSlotsPerDay(5);

        Appointment mockSaved = new Appointment();
        mockSaved.setAppointmentID(100);
        mockSaved.setCost(50.0f);

        // Χρησιμοποίησε any() για να αποφύγεις mismatch σε types (int vs Integer)
        when(patientRepository.existsById(1)).thenReturn(true);
        when(examRepository.findById(10)).thenReturn(Optional.of(mockExam));
        when(appointmentRepository.countByExamIDAndAppointmentDate(10, date)).thenReturn(0L);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(mockSaved);

        // Act
        Appointment result = appointmentService.bookAppointment(1, 10, date, false);

        // Assert
        assertNotNull(result);
        assertEquals(50.0, result.getCost());
        verify(appointmentRepository).save(any(Appointment.class));
    }
}