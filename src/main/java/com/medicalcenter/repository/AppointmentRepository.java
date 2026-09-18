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

    // Μετράει πόσα ραντεβού έχουν ήδη κλειστεί για συγκεκριμένη εξέταση σε συγκεκριμένη ημέρα
    long countByExamIDAndAppointmentDate(int examID, LocalDate appointmentDate);

    @Query("""
    SELECT a FROM Appointment a
    JOIN Exam e ON a.examID = e.examID
    WHERE (:examID IS NULL OR a.examID = :examID)
      AND (:patientID IS NULL OR a.patientID = :patientID)
      AND (:doctorID IS NULL OR e.doctorID = :doctorID)
      AND (:date IS NULL OR a.appointmentDate = :date)
""")
    List<Appointment> findWithFilters(
            @Param("examID") Integer examID,
            @Param("patientID") Integer patientID,
            @Param("doctorID") Integer doctorID,
            @Param("date") LocalDate date
    );
}