package com.medicalcenter.repository;

import com.medicalcenter.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    // Βρίσκει τα ραντεβού μιας εξέτασης
    List<Appointment> findByExamID(int examID);

    // Βρίσκει τα ραντεβού ενός ασθενή
    List<Appointment> findByPatientID(int patientID);


    // Μετράει πόσα ραντεβού έχουν ήδη κλειστεί για συγκεκριμένη εξέταση σε συγκεκριμένη ημέρα
    long countByExamIDAndAppointmentDate(int examID, LocalDate appointmentDate);

    @Query("""
    SELECT a FROM Appointment a
    JOIN Exam e ON a.examID = e.examID 
    WHERE e.doctorID = :doctorID
""")
    List<Appointment> findAppointmentsByDoctorID(@Param("doctorID") int doctorID);
}