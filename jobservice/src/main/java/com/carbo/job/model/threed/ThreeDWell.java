package com.carbo.job.model.threed;

import org.apache.commons.collections.map.HashedMap;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ThreeDWell {

    @Field("fileName")
    private String fileName;

    @Field("tvd")
    private List<Float> tvd = new ArrayList<>();

    @Field("md")
    private List<Float> md = new ArrayList<>();

    @Field("easting")
    private List<Float> easting = new ArrayList<>();

    @Field("northing")
    private List<Float> northing = new ArrayList<>();

    @Field("perfs")
    private List<Perf> perfs = new ArrayList<>();

    @Field("profiles")
    private List<Profile> profiles = new ArrayList<>();

    @Field("profileSet")
    private Map<String, List<Profile>> profileSet = new HashedMap();

    @Field("rockTypes")
    private List<RockType> rockTypes = new ArrayList<>();

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<Float> getTvd() {
        return tvd;
    }

    public void setTvd(List<Float> tvd) {
        this.tvd = tvd;
    }

    public List<Float> getMd() {
        return md;
    }

    public void setMd(List<Float> md) {
        this.md = md;
    }

    public List<Float> getEasting() {
        return easting;
    }

    public void setEasting(List<Float> easting) {
        this.easting = easting;
    }

    public List<Float> getNorthing() {
        return northing;
    }

    public void setNorthing(List<Float> northing) {
        this.northing = northing;
    }

    public List<Perf> getPerfs() {
        return perfs;
    }

    public void setPerfs(List<Perf> perfs) {
        this.perfs = perfs;
    }

    public List<Profile> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<Profile> profiles) {
        this.profiles = profiles;
    }

    public List<RockType> getRockTypes() {
        return rockTypes;
    }

    public void setRockTypes(List<RockType> rockTypes) {
        this.rockTypes = rockTypes;
    }

    public Map<String, List<Profile>> getProfileSet() {
        return profileSet;
    }

    public void setProfileSet(Map<String, List<Profile>> profileSet) {
        this.profileSet = profileSet;
    }
}
