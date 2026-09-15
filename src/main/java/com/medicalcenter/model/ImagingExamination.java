package com.medicalcenter.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("IMAGING")
public class ImagingExamination extends Exam {

    private String machineType;
    private static final float COST_INCREASE_RATE = 0.10f;

    public ImagingExamination() {
    }

    public ImagingExamination(String examName, String examCategory, int maxSlotsPerDay, float cost, int doctorID, String machineType) {
        super(examName, examCategory, maxSlotsPerDay, cost, doctorID);
        this.machineType = machineType;
    }

    public String getMachineType() {
        return machineType;
    }

    public void setMachineType(String machineType) {
        this.machineType = machineType;
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