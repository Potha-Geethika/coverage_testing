package com.carbo.job.model.threed;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Profile {

    @Field("upperDiameter")
    private Float upperDiameter;

    @Field("lowerDiameter")
    private Float lowerDiameter;

    @Field("horzLength")
    private Float horzLength;

    @Field("vertCenter")
    private Float vertCenter;

    @Field("upperHeight")
    private Float upperHeight;

    @Field("lowerHeight")
    private Float lowerHeight;

    @Field("centerDepth")
    private Float centerDepth;

    @Field("centerDepthMD")
    private Float centerDepthMD;

    @Field("ellLength")
    private Float ellLength;

    @Field("padLength")
    private Float padLength;

    @Field("padUpperHeight")
    private Float padUpperHeight;

    @Field("padLowerHeight")
    private Float padLowerHeight;

    @Field("padCenterDepth")
    private Float padCenterDepth;

    @JsonProperty("xOrigin")
    @Field("xOrigin")
    private Float xOrigin;

    @JsonProperty("yOrigin")
    @Field("yOrigin")
    private Float yOrigin;

    @JsonProperty ("xScale")
    @Field("xScale")
    private Float xScale;
   
    @JsonProperty("yScale")
    @Field("yScale")
    private Float yScale;

    @Field("shift")
    private Float shift;

    @Field("profilePoints")
    private List<Float> profilePoints = new ArrayList<>();

    @Field("lenProfilePoints")
    private List<Float> lenProfilePoints = new ArrayList<>();

    @Field("modelRows")
    private List<List<Float>> modelRows = new ArrayList<>();

    public Float getUpperDiameter() {
        return upperDiameter;
    }

    public void setUpperDiameter(Float upperDiameter) {
        this.upperDiameter = upperDiameter;
    }

    public Float getLowerDiameter() {
        return lowerDiameter;
    }

    public void setLowerDiameter(Float lowerDiameter) {
        this.lowerDiameter = lowerDiameter;
    }

    public Float getHorzLength() {
        return horzLength;
    }

    public void setHorzLength(Float horzLength) {
        this.horzLength = horzLength;
    }

    public Float getVertCenter() {
        return vertCenter;
    }

    public void setVertCenter(Float vertCenter) {
        this.vertCenter = vertCenter;
    }

    public Float getUpperHeight() {
        return upperHeight;
    }

    public void setUpperHeight(Float upperHeight) {
        this.upperHeight = upperHeight;
    }

    public Float getLowerHeight() {
        return lowerHeight;
    }

    public void setLowerHeight(Float lowerHeight) {
        this.lowerHeight = lowerHeight;
    }

    public Float getCenterDepth() {
        return centerDepth;
    }

    public void setCenterDepth(Float centerDepth) {
        this.centerDepth = centerDepth;
    }

    public Float getCenterDepthMD() {
        return centerDepthMD;
    }

    public void setCenterDepthMD(Float centerDepthMD) {
        this.centerDepthMD = centerDepthMD;
    }

    public Float getEllLength() {
        return ellLength;
    }

    public void setEllLength(Float ellLength) {
        this.ellLength = ellLength;
    }

    public Float getPadLength() {
        return padLength;
    }

    public void setPadLength(Float padLength) {
        this.padLength = padLength;
    }

    public Float getPadUpperHeight() {
        return padUpperHeight;
    }

    public void setPadUpperHeight(Float padUpperHeight) {
        this.padUpperHeight = padUpperHeight;
    }

    public Float getPadLowerHeight() {
        return padLowerHeight;
    }

    public void setPadLowerHeight(Float padLowerHeight) {
        this.padLowerHeight = padLowerHeight;
    }

    public Float getPadCenterDepth() {
        return padCenterDepth;
    }

    public void setPadCenterDepth(Float padCenterDepth) {
        this.padCenterDepth = padCenterDepth;
    }

    public Float getxOrigin() {
        return xOrigin;
    }

    public void setxOrigin(Float xOrigin) {
        this.xOrigin = xOrigin;
    }

    public Float getyOrigin() {
        return yOrigin;
    }

    public void setyOrigin(Float yOrigin) {
        this.yOrigin = yOrigin;
    }

    public Float getxScale() {
        return xScale;
    }

    public void setxScale(Float xScale) {
        this.xScale = xScale;
    }

    public Float getyScale() {
        return yScale;
    }

    public void setyScale(Float yScale) {
        this.yScale = yScale;
    }

    public Float getShift() {
        return shift;
    }

    public void setShift(Float shift) {
        this.shift = shift;
    }

    public List<Float> getProfilePoints() {
        return profilePoints;
    }

    public void setProfilePoints(List<Float> profilePoints) {
        this.profilePoints = profilePoints;
    }

    public List<Float> getLenProfilePoints() {
        return lenProfilePoints;
    }

    public void setLenProfilePoints(List<Float> lenProfilePoints) {
        this.lenProfilePoints = lenProfilePoints;
    }

    public List<List<Float>> getModelRows() {
        return modelRows;
    }

    public void setModelRows(List<List<Float>> modelRows) {
        this.modelRows = modelRows;
    }
}
