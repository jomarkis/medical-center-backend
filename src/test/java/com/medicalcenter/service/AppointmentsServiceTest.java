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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    // ## Appointment Booking Tests ##

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

        // ## Get Appointment Tests ##

        @Test
        @DisplayName("Get appointments with valid filters")
        void getAppointmentsWithValidFilters() {
            LocalDate date = LocalDate.now();

            Patient mockPatient = new Patient();
            mockPatient.setPatientID(1);

            Exam mockExam = new ImagingExamination();
            mockExam.setExamID(10);

            Doctor mockDoctor = new Doctor();
            mockDoctor.setDoctorID(100);

            Appointment mockAppointment = new Appointment();
            mockAppointment.setAppointmentID(200);

            when(examRepository.existsById(10)).thenReturn(true);
            when(patientRepository.existsById(1)).thenReturn(true);
            when(doctorRepository.existsById(100)).thenReturn(true);
            when(appointmentRepository.findWithFilters(10, 1, 100, date))
                    .thenReturn(List.of(mockAppointment));

            List<Appointment> appointments = appointmentService.getAppointments(10, 1, 100, date);

            assertFalse(appointments.isEmpty());
            assertTrue(appointments.contains(mockAppointment));
            assertEquals(200, mockAppointment.getAppointmentID());
            verify(appointmentRepository, times(1)).findWithFilters(10, 1, 100, date);
        }

        @Test
        @DisplayName("Get appointments when invalid patient ID")
        void getAppointmentsWithInvalidPatientID() {
            LocalDate date = LocalDate.now();

            Patient mockPatient = new Patient();
            mockPatient.setPatientID(1);

            Exam mockExam = new ImagingExamination();
            mockExam.setExamID(10);

            Doctor mockDoctor = new Doctor();
            mockDoctor.setDoctorID(100);

            when(examRepository.existsById(10)).thenReturn(true);
            when(patientRepository.existsById(999)).thenReturn(false);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                appointmentService.getAppointments(10,  999, 100, date)        );

            assertEquals("Patient not found with ID: 999", exception.getMessage());
            verify(appointmentRepository, never()).findWithFilters(any(), any(), any(), any());
        }

        @Test
        @DisplayName("Get appointments when invalid exam ID")
        void getAppointmentsWithInvalidExamID() {
            LocalDate date = LocalDate.now();

            Patient mockPatient = new Patient();
            mockPatient.setPatientID(1);

            Exam mockExam = new ImagingExamination();
            mockExam.setExamID(10);

            Doctor mockDoctor = new Doctor();
            mockDoctor.setDoctorID(100);

            when(examRepository.existsById(999)).thenReturn(false);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                appointmentService.getAppointments(999, 1, 100, date)        );

            assertEquals("Exam not found with ID: 999", exception.getMessage());
            verify(appointmentRepository, never()).findWithFilters(any(), any(), any(), any());
        }

        @Test
        @DisplayName("Get appointments when invalid doctor ID")
        void getAppointmentsWithInvalidDoctorID() {
            LocalDate date = LocalDate.now();

            Patient mockPatient = new Patient();
            mockPatient.setPatientID(1);

            Exam mockExam = new ImagingExamination();
            mockExam.setExamID(10);

            Doctor mockDoctor = new Doctor();
            mockDoctor.setDoctorID(100);

            when(examRepository.existsById(10)).thenReturn(true);
            when(patientRepository.existsById(1)).thenReturn(true);
            when(doctorRepository.existsById(999)).thenReturn(false);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                appointmentService.getAppointments(10, 1, 999, date)        );

            assertEquals("Doctor not found with ID: 999", exception.getMessage());
            verify(appointmentRepository, never()).findWithFilters(any(), any(), any(), any());
        }

        // ## Delete Appointment Test

        @Test
        @DisplayName("Delete appointment successfully")
        void deleteAppointmentSuccessfully() {
            Appointment mockAppointment = new Appointment();
            mockAppointment.setAppointmentID(1);

            when(appointmentRepository.existsById(1)).thenReturn(true);

            appointmentService.deleteAppointment(1);

            verify(appointmentRepository, times(1)).deleteById(1);
        }

        @Test
        @DisplayName("Delete appointment failed")
        void deleteAppointmentFailed() {
            Appointment mockAppointment = new Appointment();
            mockAppointment.setAppointmentID(1);

            when(appointmentRepository.existsById(999)).thenReturn(false);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                appointmentService.deleteAppointment(999)
            );

            assertEquals("Appointment not found with ID: 999", exception.getMessage());
            verify(appointmentRepository, never()).deleteById(999);
        }
    }