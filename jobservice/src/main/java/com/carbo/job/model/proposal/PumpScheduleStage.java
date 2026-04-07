package com.carbo.job.model.proposal;

import java.util.List;

import com.carbo.job.model.PumpSchedule;

public class PumpScheduleStage {
    private Float totalCleanVolume;
    private int numberOfRows;
    private int currentStage;
    private String wellId;
    private List<PumpSchedule> pump;

    public PumpScheduleStage() {
    }

    public PumpScheduleStage(Float totalCleanVolume, int numberOfRows, int currentStage, String wellId, List<PumpSchedule> pump) {
        this.totalCleanVolume = totalCleanVolume;
        this.numberOfRows = numberOfRows;
        this.pump = pump;
        this.currentStage = currentStage;
        this.wellId = wellId;
    }

    public Float getTotalCleanVolume() {
        return totalCleanVolume;
    }
    public void setTotalCleanVolume(Float totalCleanVolume) {
        this.totalCleanVolume = totalCleanVolume;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public void setNumberOfRows(int numberOfRows) {
        this.numberOfRows = numberOfRows;
    }

    public List<PumpSchedule> getPump() {
        return pump;
    }

    public void setPump(List<PumpSchedule> pump) {
        this.pump = pump;
    }
    
    public String generateUniqueKey() {
        return totalCleanVolume + numberOfRows + pump.get(0).getWellId();
    }

    public int getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(int currentStage) {
        this.currentStage = currentStage;
    }

    public String getWellId() {
        return wellId;
    }

    public void setWellId(String wellId) {
        this.wellId = wellId;
    }
    
}
