package com.medicalcenter.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SPECIALIZED")
public class SpecializedExamination extends Exam {

    private String examSpecialty;
    private static final float COST_INCREASE_RATE = 0.30f;

    public SpecializedExamination() {
    }

    public SpecializedExamination(String examName, String examCategory, int maxSlotsPerDay, float cost, int doctorID, String examSpecialty) {
        super(examName, examCategory, maxSlotsPerDay, cost, doctorID);
        this.examSpecialty = examSpecialty;
    }

    public String getExamSpecialty() {
        return examSpecialty;
    }

    public void setExamSpecialty(String examSpecialty) {
        this.examSpecialty = examSpecialty;
    }

    @Override
    public float getCost(Boolean fastresults) {
        float baseCost = getCost();
        if (Boolean.TRUE.equals(fastresults)) {
            return baseCost + (baseCost * COST_INCREASE_RATE);
        }
        return baseCost;
    }
}