package com.medicalcenter.repository;

import com.medicalcenter.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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

    @Query("""
    SELECT a.patientID AS patientID, SUM(a.cost) AS totalIncome, COUNT(a) AS totalAppointments
    FROM Appointment a
    WHERE (:patientID IS NULL OR a.patientID = :patientID)
    GROUP BY a.patientID
    HAVING (:minIncome IS NULL OR SUM(a.cost) >= :minIncome)
       AND (:maxIncome IS NULL OR SUM(a.cost) <= :maxIncome)
    ORDER BY SUM(a.cost) DESC
""")
    List<Map<String, Object>> getPatientIncomeStats(
            @Param("patientID") Integer patientID,
            @Param("minIncome") Double minIncome,
            @Param("maxIncome") Double maxIncome
    );

    @Query("""
    SELECT a.examID as examID, SUM(a.cost) as totalIncome, COUNT(a) as totalAppointments
    FROM Appointment a
    WHERE (:examID IS NULL OR a.examID = :examID)
    GROUP BY a.examID
    HAVING (:minIncome IS NULL OR SUM(a.cost) >= :minIncome)
       AND (:maxIncome IS NULL OR SUM(a.cost) <= :maxIncome)
    ORDER BY SUM(a.cost) DESC
""")
    List<Map<String, Object>> getExamIncomeStats(
            @Param("examID") Integer examID,
            @Param("minIncome") Double minIncome,
            @Param("maxIncome") Double maxIncome
    );

    @Query("""
    SELECT e.examCategory as category, SUM(a.cost) as totalIncome, COUNT(a) as totalAppointments
    FROM Appointment a
    JOIN Exam e ON e.examID = a.examID
    WHERE (:category IS NULL OR e.examCategory = :category)
    GROUP BY e.examCategory
    HAVING (:minIncome IS NULL OR SUM(a.cost) >= :minIncome)
       AND (:maxIncome IS NULL OR SUM(a.cost) <= :maxIncome)
    ORDER BY SUM(a.cost) DESC
""")
    List<Map<String, Object>> getCategoryIncomeStats(
            @Param("category") String category,
            @Param("minIncome") Double minIncome,
            @Param("maxIncome") Double maxIncome
    );
}