package com.medicalcenter.repository;

import com.medicalcenter.model.*;
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

    @Nested
    @DisplayName("Tests for getPatientIncomeStats and getExamIncomeStats")
    class ExamAndPatientIncomeStatsTests {

        private Patient patient1;
        private Patient patient2;

        private ImagingExamination exam1;
        private ImagingExamination exam2;

        @BeforeEach
        void setUp() {
            patient1 = new Patient("Patient 1", "123", "patient1@gmail.com");
            entityManager.persist(patient1);

            patient2 = new Patient("Patient 2", "123", "patient2@gmail.com");
            entityManager.persist(patient2);


            exam1 = new ImagingExamination("Exam 1", "IMAGING", 1, 100, 10, "XRAY");
            entityManager.persist(exam1);

            exam2 = new ImagingExamination("Exam 2", "IMAGING", 1, 200, 10, "XRAY");
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

        // ## getPatientIncomeStats Tests ##

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
            assertEquals(300.0, results3.get(0).get("totalIncome"));
            assertEquals(2L, results3.get(0).get("totalAppointments"));
            assertEquals(100.0, results3.get(1).get("totalIncome"));
            assertEquals(1L, results3.get(1).get("totalAppointments"));

            List<Map<String, Object>> results4 = appointmentRepository.getPatientIncomeStats(null, 400.0, null);
            assertTrue(results4.isEmpty());

        }

        @Test
        @DisplayName("getPatientIncomeStats test with income range and patientID filter")
        void testGetPatientIncomeStats_IncomeRangeAndPatientIDFilter() {
            List<Map<String, Object>> results = appointmentRepository.getPatientIncomeStats(patient1.getPatientID(), 150.0, null);
            assertEquals(1, results.size());
            assertEquals(300.0, results.get(0).get("totalIncome"));
            assertEquals(2L, results.get(0).get("totalAppointments"));

            List<Map<String, Object>> results2 = appointmentRepository.getPatientIncomeStats(patient1.getPatientID(), 150.0, 250.0);
            assertTrue(results2.isEmpty());

            List<Map<String, Object>> results3 = appointmentRepository.getPatientIncomeStats(patient2.getPatientID(), 99.0, null);
            assertEquals(1, results3.size());
            assertEquals(100.0, results3.get(0).get("totalIncome"));
            assertEquals(1L, results3.get(0).get("totalAppointments"));

            List<Map<String, Object>> results4 = appointmentRepository.getPatientIncomeStats(patient2.getPatientID(), 150.0, null);
            assertTrue(results4.isEmpty());
        }

        // ## Tests for getExamIncomeStats ##

        @Test
        @DisplayName("getExamIncomeStats test without filters")
        void testGetExamIncomeStats() {
            List<Map<String, Object>> results = appointmentRepository.getExamIncomeStats(null, null, null);

            assertEquals(2, results.size());
            assertEquals(200.0, results.get(0).get("totalIncome"));
            assertEquals(2L, results.get(0).get("totalAppointments"));
            assertEquals(200.0, results.get(1).get("totalIncome"));
            assertEquals(1L, results.get(1).get("totalAppointments"));
        }

        @Test
        @DisplayName("getExamIncomeStats test with exam filter")
        void testGetExamIncomeStats_ExamFilter() {
            List<Map<String, Object>> results = appointmentRepository.getExamIncomeStats(exam1.getExamID(), null, null);
            assertEquals(1, results.size());
            assertEquals(200.0, results.get(0).get("totalIncome"));
            assertEquals(2L, results.get(0).get("totalAppointments"));

            List<Map<String, Object>> results2 = appointmentRepository.getExamIncomeStats(exam2.getExamID(), null, null);
            assertEquals(1, results2.size());
            assertEquals(200.0, results2.get(0).get("totalIncome"));
            assertEquals(1L, results2.get(0).get("totalAppointments"));
        }

        @Test
        @DisplayName("getExamIncomeStats test with income range filter")
        void testGetExamIncomeStats_IncomeRangeFilter() {
            List<Map<String, Object>> results = appointmentRepository.getExamIncomeStats(null, 150.0, null);
            assertEquals(2, results.size());
            assertEquals(200.0, results.get(0).get("totalIncome"));
            assertEquals(2L, results.get(0).get("totalAppointments"));
            assertEquals(200.0, results.get(1).get("totalIncome"));
            assertEquals(1L, results.get(1).get("totalAppointments"));

            List<Map<String, Object>> results2 = appointmentRepository.getExamIncomeStats(null, null, 150.0);
            assertTrue(results2.isEmpty());

            List<Map<String, Object>> results3 = appointmentRepository.getExamIncomeStats(null, 250.0, null);
            assertTrue(results3.isEmpty());
        }

        @Test
        @DisplayName("getExamIncomeStats test with income range and examID filter")
        void testGetExamIncomeStats_IncomeRangeAndExamIDFilter() {
            List<Map<String, Object>> results = appointmentRepository.getExamIncomeStats(exam1.getExamID(), 150.0, null);
            assertEquals(1, results.size());
            assertEquals(200.0, results.get(0).get("totalIncome"));
            assertEquals(2L, results.get(0).get("totalAppointments"));

            List<Map<String, Object>> results2 = appointmentRepository.getExamIncomeStats(exam1.getExamID(), 250.0, null);
            assertTrue(results2.isEmpty());

            List<Map<String, Object>> results3 = appointmentRepository.getExamIncomeStats(exam2.getExamID(), 150.0, null);
            assertEquals(1, results3.size());
            assertEquals(200.0, results3.get(0).get("totalIncome"));
            assertEquals(1L, results3.get(0).get("totalAppointments"));

            List<Map<String, Object>> results4 = appointmentRepository.getExamIncomeStats(exam2.getExamID(), 250.0, null);
            assertTrue(results4.isEmpty());
        }

        @Nested
        @DisplayName("Tests for Exam Category Income")
        class CategoryIncomeStatsTests {

            private SpecializedExamination exam3;

            @BeforeEach
            void setUp() {
                exam3 = new SpecializedExamination("Exam 3", "SPECIALIZED", 1, 300, 10, "SPECIALIZED");
                entityManager.persist(exam3);

                Appointment patient3_app = new Appointment();
                patient3_app.setExamID(exam3.getExamID());
                patient3_app.setPatientID(patient2.getPatientID());
                patient3_app.setAppointmentDate(LocalDate.now());
                patient3_app.setCost(300.0f);
                entityManager.persist(patient3_app);

                entityManager.flush();
                entityManager.clear();
            }

            @Test
            @DisplayName("getCategoryIncomeStats test with no filter")
            void testGetCategoryIncomeStats() {
                List<Map<String, Object>> results = appointmentRepository.getCategoryIncomeStats(null, null, null);
                assertEquals(2, results.size());
                assertEquals(400.0, results.get(0).get("totalIncome"));
                assertEquals(3L, results.get(0).get("totalAppointments"));
                assertEquals(300.0, results.get(1).get("totalIncome"));
                assertEquals(1L, results.get(1).get("totalAppointments"));
            }

            @Test
            @DisplayName("getCategoryIncomeStats test with specific category filter")
            void testGetCategoryIncomeStats_SpecificCategoryFilter() {
                List<Map<String, Object>> results = appointmentRepository.getCategoryIncomeStats("IMAGING", null, null);
                assertEquals(1, results.size());
                assertEquals(400.0, results.get(0).get("totalIncome"));
                assertEquals(3L, results.get(0).get("totalAppointments"));

                List<Map<String, Object>> results2 = appointmentRepository.getCategoryIncomeStats("SPECIALIZED", null, null);
                assertEquals(1, results2.size());
                assertEquals(300.0, results2.get(0).get("totalIncome"));
                assertEquals(1L, results2.get(0).get("totalAppointments"));
            }

            @Test
            @DisplayName("getCategoryIncomeStats test with income range filter")
            void testGetCategoryIncomeStats_IncomeRangeFilter() {
                List<Map<String, Object>> results = appointmentRepository.getCategoryIncomeStats(null, 150.0, null);
                assertEquals(2, results.size());
                assertEquals(400.0, results.get(0).get("totalIncome"));
                assertEquals(3L, results.get(0).get("totalAppointments"));
                assertEquals(300.0, results.get(1).get("totalIncome"));
                assertEquals(1L, results.get(1).get("totalAppointments"));

                List<Map<String, Object>> results2 = appointmentRepository.getCategoryIncomeStats(null, null, 150.0);
                assertTrue(results2.isEmpty());

                List<Map<String, Object>> results3 = appointmentRepository.getCategoryIncomeStats(null, 350.0, null);
                assertEquals(1, results3.size());
                assertEquals(400.0, results3.get(0).get("totalIncome"));
                assertEquals(3L, results3.get(0).get("totalAppointments"));

                List<Map<String, Object>> results4 = appointmentRepository.getCategoryIncomeStats(null, null, 350.0);
                assertEquals(1, results4.size());
                assertEquals(300.0, results4.get(0).get("totalIncome"));
                assertEquals(1L, results4.get(0).get("totalAppointments"));

                List<Map<String, Object>> results5 = appointmentRepository.getCategoryIncomeStats(null, 450.0, 500.0);
                assertTrue(results5.isEmpty());

            }

            @Test
            @DisplayName("getCategoryIncomeStats test with specific category and income range filter")
            void testGetCategoryIncomeStats_SpecificCategoryAndIncomeRangeFilter() {
                List<Map<String, Object>> results = appointmentRepository.getCategoryIncomeStats("IMAGING", 150.0, null);
                assertEquals(1, results.size());
                assertEquals(400.0, results.get(0).get("totalIncome"));
                assertEquals(3L, results.get(0).get("totalAppointments"));

                List<Map<String, Object>> results2 = appointmentRepository.getCategoryIncomeStats("IMAGING", 150.0, 350.0);
                assertTrue(results2.isEmpty());

                List<Map<String, Object>> results3 = appointmentRepository.getCategoryIncomeStats("SPECIALIZED", 250.0, null);
                assertEquals(1, results3.size());
                assertEquals(300.0, results3.get(0).get("totalIncome"));
                assertEquals(1L, results3.get(0).get("totalAppointments"));

                List<Map<String, Object>> results4 = appointmentRepository.getCategoryIncomeStats("SPECIALIZED", 350.0, null);
                assertTrue(results4.isEmpty());
            }
        }
    }
}