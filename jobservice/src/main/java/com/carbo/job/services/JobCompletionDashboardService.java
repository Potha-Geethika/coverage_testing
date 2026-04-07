package com.carbo.job.services;

import com.carbo.job.model.*;
import com.carbo.job.model.proposal.BasicInformation;
import com.carbo.job.model.proposal.Proposal;
import com.carbo.job.model.proposal.Well;
import com.carbo.job.repository.*;
import com.carbo.job.utils.ControllerUtil;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.carbo.job.utils.CommonUtils.round;
import static com.carbo.job.utils.ControllerUtil.getOrganizationId;

@Service
public class JobCompletionDashboardService {

    @Autowired
    JobMongoDbRepository jobMongoDbRepository;

    @Autowired
    WellMongoDbRepository wellMongoDbRepository;

    @Autowired
    ActivityLogMongoDbRepository activityLogMongoDbRepository;

    @Autowired
    private ProposalMongoDbRepository proposalMongoDbRepository;

    @Autowired
    private BasicInformationMongoDbRepository basicInformationMongoDbRepository;

    @Autowired
    private ProposalWellMongoDbRepository proposalWellMongoDbRepository;

    @Autowired
    private JobDashboardWellInfoRepository jobDashboardWellInfoRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    // ───────────────────────────────────────────────────────────────────────────────
    //                              SECURITY & UTIL
    // ───────────────────────────────────────────────────────────────────────────────

    private Optional<Job> getJobById(HttpServletRequest request, String jobId) {
        String userOrgId = getOrganizationId(request);
        String orgType = "OPERATOR";

        try {
            orgType = ControllerUtil.getOrganizationType(request);
        } catch (Exception e) {
            // Token might be missing organizationType, ignore safely
        }

        if ("OPERATOR".equalsIgnoreCase(orgType)) {
            return jobMongoDbRepository.findByIdAndSharedWithOrganizationId(jobId, userOrgId);
        } else {
            return jobMongoDbRepository.findByIdAndOrganizationId(jobId, userOrgId);
        }
    }

    private String safeString(String v) {
        return v == null ? "" : v;
    }

    // ───────────────────────────────────────────────────────────────────────────────
    //                              MAIN METHODS
    // ───────────────────────────────────────────────────────────────────────────────

    public JobDashboardWellInfo getWellCompletionReport(
            HttpServletRequest request,
            String jobId,
            String wellId
    ) {
        // 1. DATABASE CALL: Mandatory for Security & Context
        Optional<Job> jobOptional = getJobById(request, jobId);

        if (jobOptional.isEmpty()) {
            // Access Denied or Job Not Found -> Return empty object (Blank PDF, no crash)
            return new JobDashboardWellInfo();
        }
        Job job = jobOptional.get();

        // 2. CONTEXT SWITCH: Use the Job Owner's ID (ProFrac), not the User's ID (SM Energy)
        String organizationId = job.getOrganizationId();

        Optional<JobDashboardWellInfo> jobDashboardWellInfo = jobDashboardWellInfoRepository.findByOrganizationIdAndJobIdAndWellId(organizationId, jobId, wellId);
        JobDashboardWellInfo res = new JobDashboardWellInfo();

        com.carbo.job.model.Well well = wellMongoDbRepository.findByOrganizationIdAndId(organizationId, wellId);
        if (ObjectUtils.isEmpty(well)){
            return res;
        }

        List<ActivityLogEntry> activityLogEntries =
                activityLogMongoDbRepository
                        .findByOrganizationIdAndJobIdAndWell(
                                organizationId,
                                jobId,
                                safeString(well.getName())
                        );

        // ---- Fetch Proposal ----
        Query proposalQuery = new Query();
        proposalQuery.addCriteria(Criteria.where("jobId").is(jobId));
        Proposal proposal = mongoTemplate.findOne(proposalQuery, Proposal.class, "proposal-v2");

        // ---- Well Basic Info ----
        res.setWellName(safeString(well.getName()));
        res.setWellId(well.getId());
        res.setApi(safeString(well.getApi()));
        res.setAfe(safeString(well.getAfeNumber()));
        res.setWellType(safeString(well.getWellType()));

        // ---- Proposal Extras ----
        if (proposal != null) {

            BasicInformation basicInfo =
                    basicInformationMongoDbRepository.findByProposalIdAndOrganizationId(
                            safeString(proposal.getId()), organizationId);

            Well proposalWell =
                    proposalWellMongoDbRepository.findByProposalIdAndOrganizationId(
                            safeString(proposal.getId()), organizationId
                    );

            if (basicInfo != null && basicInfo.getRpfInformation() != null) {
                res.setFormation(safeString(basicInfo.getRpfInformation().getFormation()));
            } else {
                res.setFormation("");
            }

            if (proposalWell != null && proposalWell.getHhpCalculations() != null) {
                res.setBhst(proposalWell.getHhpCalculations().getBottomHoleTemp());
                res.setMaxPressure(proposalWell.getHhpCalculations().getMaxPressure());
                res.setMaxTvd(proposalWell.getHhpCalculations().getTVD());
            } else {
                res.setBhst(0.0);
                res.setMaxPressure(0.0);
                res.setMaxTvd(0.0);
            }

        } else {
            // no proposal available
            res.setFormation("");
            res.setBhst(0.0);
            res.setMaxPressure(0.0);
            res.setMaxTvd(0.0);
        }

        res.setMainFluidType("Main Fluid");

        // ---- Proppants ----
        List<String> proppantTypes = new ArrayList<>();
        double designProppantLbs = 0.0;

        Optional<com.carbo.job.model.Well> wellFromJob = job.getWells() != null
                ? job.getWells().stream()
                .filter(w -> safeString(w.getId()).equalsIgnoreCase(well.getId()))
                .findFirst()
                : Optional.empty();

        if (wellFromJob.isPresent()) {
            com.carbo.job.model.Well w = wellFromJob.get();
            if (w.getProppants() != null) {
                proppantTypes = w.getProppants().stream()
                        .map(p -> safeString(p.getName()))
                        .filter(s -> !s.isEmpty())
                        .distinct()
                        .toList();

                designProppantLbs = w.getProppants().stream()
                        .mapToDouble(p -> p.getVolumePerStage() != null ? p.getVolumePerStage() : 0f)
                        .sum();
            }
        }
        res.setProppantTypes(proppantTypes);
        res.setDesignProppantLbs(designProppantLbs);
        res.setCreated(System.currentTimeMillis());
        res.setJobId(jobId);
        res.setOrganizationId(organizationId);

        // ---- Chemicals ----
        res.setDesignFluidBbls(wellFromJob.map(this::calculateTotalChemicalVolume).orElse(0.0));

        // ---- Pump Hours ----
        res.setTotalPumpTimeHrs(round(calculateTotalPumpHours(activityLogEntries),2));

        if (jobDashboardWellInfo.isPresent()) {
            JobDashboardWellInfo dashboardWellInfo = jobDashboardWellInfo.get();
            res.setId(dashboardWellInfo.getId());
            res.setFormation(dashboardWellInfo.getFormation());
            res.setBhst(dashboardWellInfo.getBhst());
            res.setWellType(dashboardWellInfo.getWellType());
            res.setCompletionType(dashboardWellInfo.getCompletionType());
            res.setMaxPressure(dashboardWellInfo.getMaxPressure());
            res.setMaxTvd(dashboardWellInfo.getMaxTvd());
            res.setMainFluidType(dashboardWellInfo.getMainFluidType());
        }

        jobDashboardWellInfoRepository.save(res);
        return res;
    }

    public double calculateTotalChemicalVolume(com.carbo.job.model.Well well) {
        if (well == null) return 0.0;
        double total = 0.0;
        total += sumWellLevelChemicals(well.getAcidAdditives());
        total += sumWellLevelChemicals(well.getSlickwaters());
        total += sumWellLevelChemicals(well.getLinearGelCrosslinks());
        total += sumWellLevelChemicals(well.getDiverters());
        if (well.getAdditionalChemicalTypes() != null) {
            for (List<Chemical> list : well.getAdditionalChemicalTypes().values()) {
                total += sumWellLevelChemicals(list);
            }
        }
        return total;
    }

    private double sumWellLevelChemicals(List<Chemical> list) {
        if (list == null) return 0.0;
        return list.stream()
                .filter(c -> c != null && c.getVolumePerStage() != null)
                .mapToDouble(Chemical::getVolumePerStage)
                .sum();
    }

    private double calculateTotalPumpHours(List<ActivityLogEntry> activityLogEntries) {
        if (activityLogEntries == null || activityLogEntries.isEmpty()) {
            return 0.0;
        }
        long totalPumpMs = activityLogEntries.stream()
                .filter(l -> l.getEventOrNptCode() != null && l.getEventOrNptCode().equalsIgnoreCase("Pump Time"))
                .mapToLong(ActivityLogEntry::getMillisecondsSpan)
                .sum();
        return totalPumpMs / (1000.0 * 60 * 60);
    }

    public JobDashboardWellInfo saveWellInfo(HttpServletRequest request, JobDashboardWellInfo jobDashboardWellInfo) {
        // 1. Secure Check
        Optional<Job> jobOptional = getJobById(request, jobDashboardWellInfo.getJobId());
        if (jobOptional.isEmpty()) {
            return null; // Access Denied
        }
        Job job = jobOptional.get();

        // 2. ID Swap (Save to Owner's Record)
        String organizationId = job.getOrganizationId();

        Optional<JobDashboardWellInfo> existing = jobDashboardWellInfoRepository.findByOrganizationIdAndJobIdAndWellId(
                organizationId,
                jobDashboardWellInfo.getJobId(), jobDashboardWellInfo.getWellId()
        );

        if (existing.isPresent()) {
            jobDashboardWellInfo.setId(existing.get().getId());
            jobDashboardWellInfo.setModified(new Date().getTime());
        } else {
            jobDashboardWellInfo.setCreated(new Date().getTime());
            jobDashboardWellInfo.setModified(new Date().getTime());
        }

        jobDashboardWellInfo.setOrganizationId(organizationId);
        return jobDashboardWellInfoRepository.save(jobDashboardWellInfo);
    }
}