package com.medicalcenter.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;

@Entity
@Table(name = "exams")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "exam_type", discriminatorType = DiscriminatorType.STRING)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "examType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ImagingExamination.class, name = "IMAGING"),
        @JsonSubTypes.Type(value = MicrobiologicalExamination.class, name = "MICROBIOLOGICAL"),
        @JsonSubTypes.Type(value = SpecializedExamination.class, name = "SPECIALIZED")
})
public abstract class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int examID;

    @Column(nullable = false)
    private String examName;

    private String examCategory;
    private int maxSlotsPerDay;
    private float cost;
    private int doctorID;

    public Exam() {
    }

    public Exam(String examName, String examCategory, int maxSlotsPerDay, float cost, int doctorID) {
        this.examName = examName;
        this.examCategory = examCategory;
        this.maxSlotsPerDay = maxSlotsPerDay;
        this.cost = cost;
        this.doctorID = doctorID;
    }

    public abstract float getCost(Boolean fastresults);

    // Getters and Setters
    public int getExamID() {
        return examID;
    }

    public void setExamID(int examID) {
        this.examID = examID;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public String getExamCategory() {
        return examCategory;
    }

    public void setExamCategory(String examCategory) {
        this.examCategory = examCategory;
    }

    public int getMaxSlotsPerDay() {
        return maxSlotsPerDay;
    }

    public void setMaxSlotsPerDay(int maxSlotsPerDay) {
        this.maxSlotsPerDay = maxSlotsPerDay;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public int getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(int doctorID) {
        this.doctorID = doctorID;
    }
}