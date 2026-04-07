package com.carbo.job.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "proppant-delivery-entries")
public class ProppantDeliveryEntry {
    @Id
    private String id;

    @Field("jobId")
    @Indexed(unique = false)
    private String jobId;

    @Field("date")
    @NotEmpty(message = "date can not be empty")
    private Date date;

    @Field("proppant")
    @NotEmpty(message = "proppant can not be empty")
    private String proppant;

    @Field("bol")
    @NotEmpty(message = "bol can not be empty")
    private String bol;

    @Field("po")
    @NotEmpty(message = "po can not be empty")
    private String po;

    @Field("vendor")
    @NotEmpty(message = "vendor can not be empty")
    private String vendor;

    @Field("wtAmount")
    @NotEmpty(message = "wtAmount can not be empty")
    private float wtAmount;

    @Field("bolQuantity")
    private Float bolQuantity;

    @Field("uom")
    private String uom;


    @Field("usedIn")
    private List<ProppantUsed> usedIn = new ArrayList<>();

    @Field("boxNumber")
    private Integer boxNumber;

    @Field("padStage")
    private String padStage;

    @Field("moverNumber")
    private Integer moverNumber;

    @Field("binNumber")
    private Integer binNumber;

    @Field("truckNumber")
    private String truckNumber;


    @Field("source")
    private String source;

    @Field("organizationId")
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

    @Field("returned")
    private Float returned;

    @Field("transferredToJobId")
    private String transferredToJobId;

    @Field("transferredFromJobId")
    private String transferredFromJobId;

    @Field("transferredToJob")
    private Float transferredToJob;

    @Field("transferredToYard")
    private Float transferredToYard;

    @Field("writeOffBalance")
    private Float writeOffBalance;

    @Field("status")
    private String status;

    @Field("automatize")
    private Boolean automatize;

    @Field("orderStatusID")
    private int orderStatusID;

    @Field("autoOrderId")
    private int autoOrderId;

    @Field("silo")
    private String silo;

    @Field("timeLoadout")
    private String timeLoadout;

    @Field("customerID")
    @Size(max = 30)
    private String customerID;

    @Field("eventTimes")
    private EventTimes eventTimes;

    @Field("delivered")
    private boolean delivered;

    @Field("copyGeoFenceData")
    private boolean copyGeoFenceData;

    @Field("acceptedDate")
    private long acceptedDate;

    @Field("isAccepted")
    private boolean isAccepted;

    @Field("box")
    private Integer box;

    @Field("bolScanId")
    private String bolScanId;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public String getProppant() {
        return proppant;
    }

    public void setProppant(String proppant) {
        this.proppant = proppant;
    }

    public String getBol() {
        return bol;
    }

    public void setBol(String bol) {
        this.bol = bol;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public float getWtAmount() {
        return wtAmount;
    }

    public void setWtAmount(float wtAmount) {
        this.wtAmount = wtAmount;
    }



    public List<ProppantUsed> getUsedIn() {
        return usedIn;
    }

    public void setUsedIn(List<ProppantUsed> usedIn) {
        this.usedIn = usedIn;
    }

    public Integer getBoxNumber() {
        return boxNumber;
    }

    public void setBoxNumber(Integer boxNumber) {
        this.boxNumber = boxNumber;
    }

    public String getTruckNumber() {
        return truckNumber;
    }

    public void setTruckNumber(String truckNumber) {
        this.truckNumber = truckNumber;
    }



    public String getPadStage() {
        return padStage;
    }

    public void setPadStage(String padStage) {
        this.padStage = padStage;
    }

    public String getPo() {
        return po;
    }

    public void setPo(String po) {
        this.po = po;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
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

    public Integer getMoverNumber() {
        return moverNumber;
    }

    public void setMoverNumber(Integer moverNumber) {
        this.moverNumber = moverNumber;
    }

    public Integer getBinNumber() {
        return binNumber;
    }

    public void setBinNumber(Integer binNumber) {
        this.binNumber = binNumber;
    }

    public Float getReturned() {
        return returned;
    }

    public void setReturned(Float returned) {
        this.returned = returned;
    }

    public String getTransferredToJobId() {
        return transferredToJobId;
    }

    public void setTransferredToJobId(String transferredToJobId) {
        this.transferredToJobId = transferredToJobId;
    }

    public String getTransferredFromJobId() {
        return transferredFromJobId;
    }

    public void setTransferredFromJobId(String transferredFromJobId) {
        this.transferredFromJobId = transferredFromJobId;
    }

    public Float getTransferredToJob() {
        return transferredToJob;
    }

    public void setTransferredToJob(Float transferredToJob) {
        this.transferredToJob = transferredToJob;
    }

    public Float getTransferredToYard() {
        return transferredToYard;
    }

    public void setTransferredToYard(Float transferredToYard) {
        this.transferredToYard = transferredToYard;
    }

    public Float getWriteOffBalance() {
        return writeOffBalance;
    }

    public void setWriteOffBalance(Float writeOffBalance) {
        this.writeOffBalance = writeOffBalance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Float getBolQuantity() {
        return bolQuantity;
    }

    public void setBolQuantity(Float bolQuantity) {
        this.bolQuantity = bolQuantity;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public Boolean getAutomatize() {
        return automatize;
    }

    public void setAutomatize(Boolean automatize) {
        this.automatize = automatize;
    }

    public int getOrderStatusID() {
        return orderStatusID;
    }

    public void setOrderStatusID(int orderStatusID) {
        this.orderStatusID = orderStatusID;
    }

    public int getAutoOrderId() {
        return autoOrderId;
    }

    public void setAutoOrderId(int autoOrderId) {
        this.autoOrderId = autoOrderId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSilo() {
        return silo;
    }

    public void setSilo(String silo) {
        this.silo = silo;
    }

    public String getTimeLoadout() {
        return timeLoadout;
    }

    public void setTimeLoadout(String timeLoadout) {
        this.timeLoadout = timeLoadout;
    }

    public String getCustomerID() {return customerID;}

    public void setCustomerID(String customerID) {this.customerID = customerID;}

    public EventTimes getEventTimes() {
        return eventTimes;
    }

    public void setEventTimes(EventTimes eventTimes) {
        this.eventTimes = eventTimes;
    }

    public boolean isDelivered() {return delivered;}
    public void setDelivered(boolean delivered) {this.delivered = delivered;}

    public boolean isCopyGeoFenceData() {return copyGeoFenceData;}

    public void setCopyGeoFenceData(boolean copyGeoFenceData) {this.copyGeoFenceData = copyGeoFenceData;}

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }

    public long getAcceptedDate() {
        return acceptedDate;
    }

    public void setAcceptedDate(long acceptedDate) {
        this.acceptedDate = acceptedDate;
    }

    public Integer getBox() {
        return box;
    }

    public void setBox(Integer box) {
        this.box = box;
    }

    public String getBolScanId() {
        return bolScanId;
    }

    public void setBolScanId(String bolScanId) {
        this.bolScanId = bolScanId;
    }
}
