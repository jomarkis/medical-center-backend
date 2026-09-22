package com.medicalcenter.repository;

import com.medicalcenter.model.Appointment;
import com.medicalcenter.model.Exam;
import com.medicalcenter.model.ImagingExamination;
import com.medicalcenter.model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AppointmentRepositoryTest {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TestEntityManager entityManager;

    // ## findWithFileters Tests ##

    @Test
    @DisplayName("findWithFilters returns appointments matching doctor and date")
    void testFindWithFilters_DoctorAndDateFilter() {
        LocalDate targetDate = LocalDate.of(2026, 9, 25);
        LocalDate otherDate = LocalDate.of(2026, 9, 26);

        ImagingExamination exam1 = new ImagingExamination("Exam 1", "IMAGING", 1, 3, 10, "XRAY");
        entityManager.persist(exam1);

        ImagingExamination exam2 = new ImagingExamination("Exam 2", "IMAGING", 1, 3, 20, "XRAY");
        entityManager.persist(exam2);

        Appointment appMatching = new Appointment();
        appMatching.setExamID(exam1.getExamID());
        appMatching.setPatientID(1);
        appMatching.setAppointmentDate(targetDate);
        appMatching.setCost(40.0f);
        entityManager.persist(appMatching);

        Appointment appWrongDate = new Appointment();
        appWrongDate.setExamID(exam1.getExamID());
        appWrongDate.setPatientID(2);
        appWrongDate.setAppointmentDate(otherDate);
        appWrongDate.setCost(40.0f);
        entityManager.persist(appWrongDate);

        Appointment appWrongDoctor = new Appointment();
        appWrongDoctor.setExamID(exam2.getExamID());
        appWrongDoctor.setPatientID(3);
        appWrongDoctor.setAppointmentDate(targetDate);
        appWrongDoctor.setCost(60.0f);
        entityManager.persist(appWrongDoctor);

        entityManager.flush();
        entityManager.clear();

        List<Appointment> results = appointmentRepository.findWithFilters(
                null,
                null,
                10,
                targetDate
        );

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(appMatching.getAppointmentID(), results.get(0).getAppointmentID());
        assertEquals(1, results.get(0).getPatientID());
    }

    @Test
    @DisplayName("findWithFilters returns all appointments when all parameters are null")
    void testFindWithFilters_AllNullParameters_ReturnsAll() {
        ImagingExamination exam = new ImagingExamination("Exam 1", "IMAGING", 1, 3, 10, "XRAY");
        entityManager.persist(exam);

        Appointment a1 = new Appointment();
        a1.setExamID(exam.getExamID());
        a1.setPatientID(1);
        a1.setAppointmentDate(LocalDate.now());
        a1.setCost(30.0f);
        entityManager.persist(a1);

        Appointment a2 = new Appointment();
        a2.setExamID(exam.getExamID());
        a2.setPatientID(2);
        a2.setAppointmentDate(LocalDate.now().plusDays(1));
        a2.setCost(30.0f);
        entityManager.persist(a2);

        entityManager.flush();
        entityManager.clear();

        List<Appointment> results = appointmentRepository.findWithFilters(null, null, null, null);

        assertEquals(2, results.size());
    }

    // ## getPatientIncomeStats Tests ##
    @Nested
    @DisplayName("Tests for getPatientIncomeStats")
    class PatientIncomeStatsTests {

        private Patient patient1;
        private Patient patient2;

        @BeforeEach
        void setUp() {
            patient1 = new Patient("Patient 1", "123", "patient1@gmail.com");
            entityManager.persist(patient1);

            patient2 = new Patient("Patient 2", "123", "patient2@gmail.com");
            entityManager.persist(patient2);


            ImagingExamination exam1 = new ImagingExamination("Exam 1", "IMAGING", 1, 3, 10, "XRAY");
            entityManager.persist(exam1);

            ImagingExamination exam2 = new ImagingExamination("Exam 2", "IMAGING", 1, 5, 10, "XRAY");
            entityManager.persist(exam2);

            Appointment patient1_app1 = new Appointment();
            patient1_app1.setExamID(exam1.getExamID());
            patient1_app1.setPatientID(patient1.getPatientID());
            patient1_app1.setAppointmentDate(LocalDate.now());
            patient1_app1.setCost(100.0f);
            entityManager.persist(patient1_app1);

            Appointment patient1_app2 = new Appointment();
            patient1_app2.setExamID(exam2.getExamID());
            patient1_app2.setPatientID(patient1.getPatientID());
            patient1_app2.setAppointmentDate(LocalDate.now());
            patient1_app2.setCost(200.0f);
            entityManager.persist(patient1_app2);

            Appointment patient2_app = new Appointment();
            patient2_app.setExamID(exam1.getExamID());
            patient2_app.setPatientID(patient2.getPatientID());
            patient2_app.setAppointmentDate(LocalDate.now());
            patient2_app.setCost(100.0f);
            entityManager.persist(patient2_app);

            entityManager.flush();
            entityManager.clear();
        }

        @Test
        @DisplayName("getPatientIncomeStats test without filters")
        void testGetPatientIncomeStats() {
            List<Map<String, Object>> results = appointmentRepository.getPatientIncomeStats(null, null, null);

            assertEquals(2, results.size());
            assertEquals(300.0, results.get(0).get("totalIncome"));
            assertEquals(2L, results.get(0).get("totalAppointments"));
            assertEquals(100.0, results.get(1).get("totalIncome"));
            assertEquals(1L, results.get(1).get("totalAppointments"));
        }

        @Test
        @DisplayName("getPatientIncomeStats test with patient filter")
        void testGetPatientIncomeStats_PatientFilter() {

            List<Map<String, Object>> results = appointmentRepository.getPatientIncomeStats(patient1.getPatientID(), null, null);

            assertEquals(1, results.size());
            assertEquals(300.0, results.get(0).get("totalIncome"));
            assertEquals(2L, results.get(0).get("totalAppointments"));

            List<Map<String, Object>> results2 = appointmentRepository.getPatientIncomeStats(patient2.getPatientID(), null, null);

            assertEquals(1, results2.size());
            assertEquals(100.0, results2.get(0).get("totalIncome"));
            assertEquals(1L, results2.get(0).get("totalAppointments"));
        }

        @Test
        @DisplayName("getPatientIncomeStats test with income range filter")
        void testGetPatientIncomeStats_IncomeRangeFilter() {
            List<Map<String, Object>> results = appointmentRepository.getPatientIncomeStats(null, 150.0, null);

            assertEquals(1, results.size());
            assertEquals(300.0, results.get(0).get("totalIncome"));
            assertEquals(2L, results.get(0).get("totalAppointments"));

            List<Map<String, Object>> results2 = appointmentRepository.getPatientIncomeStats(null, null, 150.0);

            assertEquals(1, results2.size());
            assertEquals(100.0, results2.get(0).get("totalIncome"));
            assertEquals(1L, results2.get(0).get("totalAppointments"));

            List<Map<String, Object>> results3 = appointmentRepository.getPatientIncomeStats(null, 99.0, 301.0);

            assertEquals(2, results3.size());

            assertEquals(2, results.size());
            assertEquals(300.0, results.get(0).get("totalIncome"));
            assertEquals(2L, results.get(0).get("totalAppointments"));
            assertEquals(100.0, results.get(1).get("totalIncome"));
            assertEquals(1L, results.get(1).get("totalAppointments"));

            List<Map<String, Object>> results4 = appointmentRepository.getPatientIncomeStats(null, 400.0, null);
            assertTrue(results4.isEmpty());

        }
    }
}