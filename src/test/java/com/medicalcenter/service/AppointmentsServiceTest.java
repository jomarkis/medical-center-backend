package com.medicalcenter.service;

import com.medicalcenter.model.Appointment;
import com.medicalcenter.model.Exam;
import com.medicalcenter.model.ImagingExamination;
import com.medicalcenter.model.Patient;
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

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentsServiceTest {

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

    @Test
    @DisplayName("Successful appointment booking")
    void appointmentBooking() {
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

        when(patientRepository.existsById(1)).thenReturn(true);
        when(examRepository.findById(10)).thenReturn(Optional.of(mockExam));
        when(appointmentRepository.countByExamIDAndAppointmentDate(10, date)).thenReturn(0L);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(mockSaved);

        Appointment result = appointmentService.bookAppointment(1, 10, date, false);

        assertNotNull(result);
        assertEquals(50.0, result.getCost());
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Booking fails when no slots are available")
    void appointmentBookingNoSlots() {
        LocalDate date = LocalDate.now();

        Patient mockPatient = new Patient();
        mockPatient.setPatientID(1);

        Exam mockExam = new ImagingExamination();
        mockExam.setExamID(10);
        mockExam.setCost(50.0f);
        mockExam.setMaxSlotsPerDay(5);


        when(patientRepository.existsById(1)).thenReturn(true);
        when(examRepository.findById(10)).thenReturn(Optional.of(mockExam));
        when(appointmentRepository.countByExamIDAndAppointmentDate(10, date)).thenReturn(5L); // No slots available

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                appointmentService.bookAppointment(1, 10, date, false)
        );

        assertEquals("No available slots for this exam on date: " + date, exception.getMessage());
        verify(appointmentRepository, never()).save(any(Appointment.class));
        }

        @Test
        @DisplayName("Booking fails when patient does not exist")
        void appointmentBookingPatientNotFound() {
            LocalDate date = LocalDate.now();

            Patient mockPatient = new Patient();
            mockPatient.setPatientID(1);

            Exam mockExam = new ImagingExamination();
            mockExam.setExamID(10);

            when(patientRepository.existsById(2)).thenReturn(false);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    appointmentService.bookAppointment(2, 10, date, false)
            );

            assertEquals("Patient not found with ID: 2", exception.getMessage());
            verify(appointmentRepository, never()).save(any(Appointment.class));
        }

        @Test
        @DisplayName("Booking fails when exam does not exist")
        void appointmentBookingExamNotFound() {
            LocalDate date = LocalDate.now();

            Patient mockPatient = new Patient();
            mockPatient.setPatientID(1);

            Exam mockExam = new ImagingExamination();
            mockExam.setExamID(10);

            when(patientRepository.existsById(1)).thenReturn(true);
            when(examRepository.findById(11)).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    appointmentService.bookAppointment(1, 11, date, false)
            );

            assertEquals("Exam not found with ID: 11", exception.getMessage());
            verify(appointmentRepository, never()).save(any(Appointment.class));
        }
    }