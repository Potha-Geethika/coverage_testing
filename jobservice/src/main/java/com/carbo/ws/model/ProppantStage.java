package com.carbo.ws.model;

import com.carbo.proppantstage.model.ProppantContainer;
import com.carbo.proppantstage.model.Silo;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotNull;
import java.util.*;

@Document(collection = "proppant-stages")
public class ProppantStage {
    @Id
    private String id;

    @Field("jobId")
    @NotNull
    @Indexed(unique = false)
    private String jobId;

    @Field("wellId")
    @NotNull
    @Indexed(unique = false)
    private String wellId;

    @Field("date")
    private Date date;

    @Field("well")
    private String well;

    @Field("stage")
    @NotNull
    private Float stage;

    @Field("silos")
    private Map<String, Silo> silos = new HashMap<>();

    @Field("runOrders")
    private List<ProppantContainer> runOrders = new ArrayList<>();

    @Field("blender")
    private String blender;

    @Field("diverter")
    private String diverter;

    @Field("diverterAmount")
    private Float diverterAmount;

    @Field("currentInSilos")
    private Map<String, Float> currentInSilos = new HashMap<>();

    @Field("organizationId")
    @NotNull
    @Indexed(unique = false)
    private String organizationId;

    @Field("ts")
    private Long ts;

    @Field("created")
    private Long created = new Date().getTime();

    @Field("modified")
    private Long modified  = new Date().getTime();

    @Field("lastModifiedBy")
    private String lastModifiedBy;

    @Field("isMigrated")
    private Boolean isMigrated;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getWell() {
        return well;
    }

    public void setWell(String well) {
        this.well = well;
    }

    public Float getStage() {
        return stage;
    }

    public void setStage(Float stage) {
        this.stage = stage;
    }

    public Map<String, Silo> getSilos() {
        return silos;
    }

    public void setSilos(Map<String, Silo> silos) {
        this.silos = silos;
    }

    public String getBlender() {
        return blender;
    }

    public void setBlender(String blender) {
        this.blender = blender;
    }

    public String getDiverter() {
        return diverter;
    }

    public void setDiverter(String diverter) {
        this.diverter = diverter;
    }

    public Float getDiverterAmount() {
        return diverterAmount;
    }

    public void setDiverterAmount(Float diverterAmount) {
        this.diverterAmount = diverterAmount;
    }

    public Map<String, Float> getCurrentInSilos() {
        return currentInSilos;
    }

    public void setCurrentInSilos(Map<String, Float> currentInSilos) {
        this.currentInSilos = currentInSilos;
    }

    public List<ProppantContainer> getRunOrders() {
        return runOrders;
    }

    public void setRunOrders(List<ProppantContainer> runOrders) {
        this.runOrders = runOrders;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getWellId() {
        return wellId;
    }

    public void setWellId(String wellId) {
        this.wellId = wellId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public Long getModified() {
        return modified;
    }

    public void setModified(Long modified) {
        this.modified = modified;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public void updateModified() {
        this.modified = new Date().getTime();
    }

    public Boolean getMigrated() {
        return isMigrated;
    }

    public void setMigrated(Boolean migrated) {
        isMigrated = migrated;
    }

    public List<ProppantContainer> getAllSubmittedContainer(Boolean isBox) {
        List<ProppantContainer> result = new ArrayList<>();
        if (isBox) {
            if (this.runOrders != null && !this.runOrders.isEmpty()) {
                result.addAll(this.runOrders);
            }
        }
        else {
            if (this.silos != null && !this.silos.isEmpty()) {
                result.addAll(this.silos.values());
            }
        }

        return result;
    }
}
