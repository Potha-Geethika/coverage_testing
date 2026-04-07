package com.carbo.job.model.threed;

import org.springframework.data.mongodb.core.mapping.Field;

public class Perf {

    @Field("mdTop")
    private Float mdTop;

    @Field("mdBottom")
    private Float mdBottom;

    @Field("tvdTop")
    private Float tvdTop;

    @Field("tvdBottom")
    private Float tvdBottom;

    @Field("stage")
    private Float stage;

    public Float getMdTop() {
        return mdTop;
    }

    public void setMdTop(Float mdTop) {
        this.mdTop = mdTop;
    }

    public Float getMdBottom() {
        return mdBottom;
    }

    public void setMdBottom(Float mdBottom) {
        this.mdBottom = mdBottom;
    }

    public Float getTvdTop() {
        return tvdTop;
    }

    public void setTvdTop(Float tvdTop) {
        this.tvdTop = tvdTop;
    }

    public Float getTvdBottom() {
        return tvdBottom;
    }

    public void setTvdBottom(Float tvdBottom) {
        this.tvdBottom = tvdBottom;
    }

    public Float getStage() {
        return stage;
    }

    public void setStage(Float stage) {
        this.stage = stage;
    }
}
