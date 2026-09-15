package com.medicalcenter.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("MICROBIOLOGICAL")
public class MicrobiologicalExamination extends Exam {

    private String sampleType;
    private static final float COST_INCREASE_RATE = 0.20f;

    public MicrobiologicalExamination() {
    }

    public MicrobiologicalExamination(String examName, String examCategory, int maxSlotsPerDay, float cost, int doctorID, String sampleType) {
        super(examName, examCategory, maxSlotsPerDay, cost, doctorID);
        this.sampleType = sampleType;
    }

    public String getSampleType() {
        return sampleType;
    }

    public void setSampleType(String sampleType) {
        this.sampleType = sampleType;
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