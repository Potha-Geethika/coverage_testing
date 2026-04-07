package com.carbo.job.model.proposal;

import com.carbo.job.model.Chemical;

import java.util.List;

public class ProposalDataResponse {

    private List<Chemical> additives;

    private List<PumpScheduleStage> pumpSchedules;

    public List<Chemical> getAdditives() {
        return additives;
    }

    public void setAdditives(List<Chemical> additives) {
        this.additives = additives;
    }

    public List<PumpScheduleStage> getPumpSchedules() {
        return pumpSchedules;
    }

    public void setPumpSchedules(List<PumpScheduleStage> pumpSchedules) {
        this.pumpSchedules = pumpSchedules;
    }
}
