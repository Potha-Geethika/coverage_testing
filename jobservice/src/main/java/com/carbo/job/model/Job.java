package com.carbo.job.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.*;
import java.util.stream.Collectors;

@Document(collection = "jobs")
@JsonIgnoreProperties(ignoreUnknown = true)
@CompoundIndex(def = "{'_id': 1, 'users._id': 1}", name = "job_id_user_id_index", unique = true)
@CompoundIndex(def = "{'organizationId': 1, 'jobNumber': 1}", name = "unique_organizationid_jobnumber_index", unique = true)
@Data
public class Job {
    @Id
    private String id;

    @Field("name")
    @NotEmpty(message = "name can not be empty")
    @Size(max = 100, message = "name can not be more than 100 characters.")
    private String name;

    @Field("jobNumber")
    @NotEmpty(message = "jobNumber can not be empty")
    @Size(max = 14, message = "jobNumber can not be more than 14 characters.")
    private String jobNumber;

    @Field("fleet")
    private String fleet;

    @Field("operator")
    private String operator;

    @Field("pad")
    private String pad;

    @Field("location")
    private String location;

    @Field("zipper")
    private Boolean zipper;

    @Field("refrac")
    private boolean refrac;

    @Field("wells")
    private List<Well> wells = new ArrayList<>();

    @Field("targetStagesPerDay")
    private int targetStagesPerDay;

    @Field("targetDailyPumpTime")
    private float targetDailyPumpTime;

    @Field("chemicalDeliveries")
    private List<ChemicalDelivery> chemicalDeliveries = new ArrayList<>();

    @Field("totesOnStandby")
    private List<Standby> totesOnStandby = new ArrayList<>();

    @Field("bucketsOnStandby")
    private List<Standby> bucketsOnStandby = new ArrayList<>();

    @Field("bucketTest")
    private BucketTest bucketTest = new BucketTest();

    @Field("proppantSchematics")
    private Map<String, SetupContainer> proppantSchematics = new HashMap<>();

    @Field("proppantDeliveries")
    private List<ProppantDeliveryEntry> proppantDeliveries = new ArrayList<>();

    @Field("proppantSchematicType")
    private String proppantSchematicType = "silos";

    @Field("numberOfUnits")
    private Integer numberOfUnits = 3;

    @Field("coneLbs")
    private Float coneLbs = 1400.0f;

    @Field("vendors")
    private List<Vendor> vendors = new ArrayList<>();

    @Field("blenders")
    private List<OnSiteEquipment> blenders = new ArrayList<>();

    @Field("ePumps")
    @JsonProperty("ePumps")
    private List<OnSiteEquipment> ePumps = new ArrayList<>();

    @Field("auxTrailers")
    private List<OnSiteEquipment> auxTrailers = new ArrayList<>();
    @Field("boostPumps")
    private List<OnSiteEquipment> boostPumps = new ArrayList<>();
    @Field("cables")
    private List<OnSiteEquipment> cables = new ArrayList<>();
    @Field("chemicalFloats")
    private List<OnSiteEquipment> chemicalFloats = new ArrayList<>();
    @Field("frackLocks")
    private List<OnSiteEquipment> frackLocks = new ArrayList<>();
    @Field("ironFloats")
    private List<OnSiteEquipment> ironFloats = new ArrayList<>();
    @Field("monoLines")
    private List<OnSiteEquipment> monoLines = new ArrayList<>();
    @Field("naturalGasTrailers")
    private List<OnSiteEquipment> naturalGasTrailers = new ArrayList<>();
    @Field("switchGears")
    private List<OnSiteEquipment> switchGears = new ArrayList<>();
    @Field("tractors")
    private List<OnSiteEquipment> tractors = new ArrayList<>();
    @Field("acidTitration")
    private List<AcidTitration> acidTitration = new ArrayList<>();

    @Field("sandSieve")
    private List<ProppantSandSieve> sandSieve = new ArrayList<>();

    @Field("hydrationUnits")
    private List<OnSiteEquipment> hydrationUnits = new ArrayList<>();

    @Field("pumps")
    private List<OnSiteEquipment> pumps = new ArrayList<>();

    @Field("chemAds")
    private List<OnSiteEquipment> chemAds = new ArrayList<>();

    @Field("ironManifolds")
    private List<OnSiteEquipment> ironManifolds = new ArrayList<>();

    @Field("dataVans")
    private List<OnSiteEquipment> dataVans = new ArrayList<>();

    @Field("silos")
    private List<OnSiteEquipment> silos = new ArrayList<>();

    @Field("emails")
    private Map<String, List<EmailGroup>> emails = new HashMap<>();

    @Field("chemicalOrders")
    private List<ChemicalOrder> chemicalOrders = new ArrayList<>();

    @Field("chemicalBOLEmails")
    private List<ChemicalBOLEmail> chemicalBOLEmails = new ArrayList<>();

    @Field("startStageEmails")
    private List<StartStageEmail> startStageEmails = new ArrayList<>();

    @Field("endStageEmails")
    private List<EndStageEmailNoIndex> endStageEmails = new ArrayList<>();

    @Field("updateEmails")
    private List<UpdateEmail> updateEmails = new ArrayList<>();

    @Field("postJobEmails")
    private List<PostJobEmail> postJobEmails = new ArrayList<>();

    @Field("operatorEndStageEmails")
    private List<OperatorEndStageEmail> operatorEndStageEmails = new ArrayList<>();

    @Field("users")
    private List<User> users = new ArrayList<>();

    @Field("curWellId")
    private String curWellId;

    @Field("curStage")
    private String curStage;

    @Field("startDate")
    private Long startDate;

    @Field("endDate")
    private Long endDate;

    @Field("expectedStartDate")
    private Long expectedStartDate;

    @Field("expectedEndDate")
    private Long expectedEndDate;

    @Field("startDateStr")
    private String startDateStr;

    @Field("timezone")
    private String timezone;

    @Field("discounts")
    private Map<String, Float> discounts = new HashMap<>();

    @Field("organizationId")
    @Indexed
    private String organizationId;

    @Field("status")
    private String status;

    @Field("beltDirection")
    private String beltDirection = "left";

    @Field("mileageChargeDistance")
    private Integer mileageChargeDistance = 0;

    @Field("activityLogStartTime")
    private String activityLogStartTime = "00:00";

    @Field("wellheadCo")
    private String wellheadCo;

    @Field("wirelineCo")
    private String wirelineCo;

    @Field("waterTransferCo")
    private String waterTransferCo;

    @Field("goToMeetingId")
    private String goToMeetingId;

    @Field("includeToeStage")
    private Boolean includeToeStage;

    @Field("predefinedChannels")
    private List<String> predefinedChannels;

    @Field("channelConfigs")
    private ChannelConfig channelConfigs;

    @Field("sharedWithOrganizationId")
    private String sharedWithOrganizationId;

    @JsonIgnore
    private Integer padStageTotal;

    @Field("ts")
    private Long ts;

    @Field("rts")
    private Long rts;

    @Field("serviceCompany")
    private String serviceCompany;

    @Field("disableOffline")
    private Boolean disableOffline;

    @Field("created")
    private Long created = new Date().getTime();

    @Field("modified")
    private Long modified = new Date().getTime();

    @Field("startDateModified")
    private Long startDateModified;

    @Field("backupDate")
    @Indexed
    private Date backupDate;

    @Field("lastModifiedBy")
    private String lastModifiedBy;

    @Field("connectJobTime")
    private boolean connectJobTime;

    @Field("automatize")
    private boolean automatize;

    @Field("additionalJobsFieldTicket")
    private List<String> additionalJobsFieldTicket = new ArrayList<>();

    @Field("hpp")
    private String hpp;

    @Field("mpn")
    private String mpn;

    @Field("connector")
    private String connector;

    @Field("singleOrDouble")
    private String singleOrDouble;

    @Field("length")
    private String length;

    @Field("manufacturer")
    private String manufacturer;


    @Field("swapOverTime")
    private int swapOverTime;

    @Field("targetWirelineTimePerStage")
    private float targetWirelineTimePerStage;

    @Field("targetMaintenanceTimePerDay")
    private float targetMaintenanceTimePerDay;

    @Field("latestBtu")
    private float latestBtu;

    @Field("btu")
    private float btu;

    @Field("hideDiscounts ")
    private boolean hideDiscounts = false;

    @Field("districtId")
    private String districtId;

    @Field("proposalId")
    private String proposalId;

    @Field("priceBookId")
    private String priceBookId;

    @Field("districtWhenCompleted")
    private String districtWhenCompleted;

    @Field("isNewWorkflow")
    private boolean isNewWorkflow;

    @Field("receiveEBol")
    private Boolean receiveEBol;

    @Field("fitToPage")
    private Boolean fitToPage = false;
    @Field("fleetType")
    private String fleetType;
    @Field("dualFuelPumpCount")
    private int dualFuelPumpCount;
    @Field("taxFlag")
    private boolean taxFlag = false;
    @Field("taxPercentage")
    private float taxPercentage;

    @Field("splitStream")
    private boolean splitStream;

    @Field("producedWater")
    private boolean producedWater=false;

    @Field("dirtyMixed")
    private boolean dirtyMixed = false;

    @Field("cng")
    private boolean cng;

    @Field("lng")
    private boolean lng;

    @Field("fieldGas")
    private boolean fieldGas;

    @NotBlank(message = "operationsType can't be blank")
    @Field("operationsType")
    private String operationsType;

    @NotBlank(message = "padEnergyType can't be blank")
    @Field("padEnergyType")
    private String padEnergyType;

    @Field("popOffs")
    private List<OnSiteEquipment> popOffs = new ArrayList<>();

    @Field("centipedes")
    private List<OnSiteEquipment> centipedes = new ArrayList<>();

    @Field("transformers")
    private List<OnSiteEquipment> transformers = new ArrayList<>();

    @Field("turbines")
    private List<OnSiteEquipment> turbines = new ArrayList<>();

    @Field("powerGenerations")
    private List<OnSiteEquipment> powerGenerations = new ArrayList<>();

    @Field("reciprocatingGasFuels")
    private List<OnSiteEquipment> reciprocatingGasFuels = new ArrayList<>();

    @Field("fracLocks")
    private List<OnSiteEquipment> fracLocks = new ArrayList<>();

    @Field("naturalGasDistributionTrailers")
    private List<OnSiteEquipment> naturalGasDistributionTrailers = new ArrayList<>();

    @Field("naturalGasEmergencyStopTrailers")
    private List<OnSiteEquipment> naturalGasEmergencyStopTrailers = new ArrayList<>();

    @Field("naturalGasHeaters")
    private List<OnSiteEquipment> naturalGasHeaters = new ArrayList<>();

    @Field("naturalGasJTSkids")
    private List<OnSiteEquipment> naturalGasJTSkids = new ArrayList<>();

    @Field("diverterSkids")
    private List<OnSiteEquipment> diverterSkids = new ArrayList<>();

    @Field("transports")
    private List<OnSiteEquipment> transports = new ArrayList<>();

    @Field("rockCatchers")
    private List<OnSiteEquipment> rockCatchers = new ArrayList<>();

    @Field("waterMonitoringSystems")
    private List<OnSiteEquipment> waterMonitoringSystems = new ArrayList<>();

    @Field("bodyLoadPumps")
    private List<OnSiteEquipment> bodyLoadPumps = new ArrayList<>();

    @Field("currentActivityLogBank")
    private String currentActivityLogBank;

    @Field("meteringSkids")
    private List<OnSiteEquipment> meteringSkids = new ArrayList<>();

    @Field("bankCount")
    private BankCountEnum bankCount;



    public Integer getPadStageTotal() {
        int ret = 0;
        for (Well well : wells) {
            ret += well.getTotalStages();
        }
        return ret;
    }

    public void updateModified() {
        this.modified = new Date().getTime();
    }
    public boolean hasPostJobReportEmail(String wellName, String stage) {
        return getPostJobEmails().stream().filter(email -> email.getWell().equals(wellName) && email.getStage().equals(stage)).collect(Collectors.counting()) > 0;
    }

    public boolean notifyChangeToExtSystem(long startDateTime, Long endDateTime, FieldTicket fieldTicket) {
        boolean containedQualifiedPostJobEmail = getPostJobEmails()
                .stream()
                .filter(email -> email.sentBetween(startDateTime, endDateTime))
                .collect(Collectors.counting()) > 0;
        boolean containedQualifiedNewApprovedVersion = fieldTicket.getLastVersion().isPresent() &&
                fieldTicket.getLastVersion().get().isApprovedBetween(startDateTime, endDateTime);
        return containedQualifiedPostJobEmail ||
                hasPostJobReportEmail(fieldTicket.getWell(), fieldTicket.getName()) && containedQualifiedNewApprovedVersion;
    }

}
