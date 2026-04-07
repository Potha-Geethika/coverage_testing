package com.carbo.job.controllers;

import com.carbo.job.model.*;
import com.carbo.job.model.proposal.ProposalData;
import com.carbo.job.model.well.Wells;
import com.carbo.job.services.*;
import com.carbo.job.storage.StorageService;
import com.carbo.job.model.proposal.Proposal;
import com.carbo.ws.model.ProppantStage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.carbo.job.utils.ControllerUtil.getOrganizationId;
import static com.carbo.job.utils.ProposalUtil.calculateProposal;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "v1/jobs/proposal")
public class ProposalController {

    private static final Logger logger = LoggerFactory.getLogger(ProposalController.class);

    private final JobService jobService;

    private final PumpScheduleService pumpScheduleService;
    private final PumpScheduleJobCfgService pumpScheduleJobCfgService;

    private final ProposalService proposalService;

    private final StorageService storageService;

    @Autowired
    public ProposalController(JobService jobService, PumpScheduleService pumpScheduleService, ProposalService proposalService, StorageService storageService, PumpScheduleJobCfgService pumpScheduleJobCfgService) {
        this.jobService = jobService;
        this.pumpScheduleService = pumpScheduleService;
        this.proposalService = proposalService;
        this.storageService = storageService;
        this.pumpScheduleJobCfgService = pumpScheduleJobCfgService;
    }

    @RequestMapping(value = "/job/{jobId}", method = RequestMethod.GET)
    public Object getJobProposal(HttpServletRequest request, @PathVariable("jobId") String jobId) {
        logger.debug("Looking up data for job {}", jobId);
        String organizationId = getOrganizationId(request);
        List<Proposal> proposal = null;
        Optional<Job> OptionalJob = jobService.getJob(jobId);
        if (OptionalJob.isPresent()) {
            Job job = OptionalJob.get();
            List<PumpSchedule> pumpSchedules = pumpScheduleService.getByJobId(job.getId());
            PumpScheduleJobCfg pumpScheduleJobCfg = pumpScheduleJobCfgService.getByJobId(request, jobId);
            List<Proposal> dbproposal = proposalService.getByJobId(request, jobId);
            if (!dbproposal.isEmpty())
                proposal = dbproposal;
            return calculateProposal(proposal, organizationId, job, pumpSchedules, pumpScheduleJobCfg);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Job found with the provided Job Id : " + jobId);
        }
    }

    @RequestMapping(value = "/job/{jobId}/{wellId}", method = RequestMethod.GET)
    public Object getJobProposal(HttpServletRequest request, @PathVariable("jobId") String jobId, @PathVariable("wellId") String wellId) {
        logger.debug("Looking up data for job {}", jobId);
        String organizationId = getOrganizationId(request);
        Proposal proposal = null;
        Optional<Job> OptionalJob = jobService.getJob(jobId);
        if (OptionalJob.isPresent()) {
            Job job = OptionalJob.get();
            List<PumpSchedule> pumpSchedules = pumpScheduleService.getByJobIdAndWellId(request, job.getId(), wellId);
            PumpScheduleJobCfg pumpScheduleJobCfg = pumpScheduleJobCfgService.getByJobId(request, jobId);
            List<Proposal> dbproposal = proposalService.getByJobIdAndWellId(request, jobId, wellId);
            if (!dbproposal.isEmpty())
                proposal = dbproposal.get(0);
            ProposalData proposalData = null;
            return calculateProposal(proposalData, proposal, organizationId, job, pumpSchedules, pumpScheduleJobCfg, wellId);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Job found with the provided Job Id : " + jobId);
        }
    }

    @RequestMapping(value = "/jobPdf/{jobId}", method = RequestMethod.POST)
    public Object getJobProposalForMergedData(HttpServletRequest request, @PathVariable("jobId") String jobId, @RequestBody List<WellIdContainer> wellIds) {
        logger.debug("<-------------Inside jobPdf------------------>");
        logger.debug("Looking up data for job {}", jobId);
        List<String> ids = new ArrayList();
        for (WellIdContainer id : wellIds) {
            ids.add(id.getId());
        }
        String organizationId = getOrganizationId(request);
        Proposal proposal = null;
        Optional<Job> optionalJob = jobService.getJob(jobId);
        List<ProposalData> responseList = new ArrayList<>();
        if (optionalJob.isPresent()) {
            for (String wellId : ids) {
                Job job = optionalJob.get();
                List<PumpSchedule> pumpSchedules = pumpScheduleService.getByJobIdAndWellId(request, job.getId(), wellId);
                PumpScheduleJobCfg pumpScheduleJobCfg = pumpScheduleJobCfgService.getByJobId(request, jobId);
                List<Proposal> dbproposal = proposalService.getByJobIdAndWellId(request, jobId, wellId);
                if (!dbproposal.isEmpty())
                    proposal = dbproposal.get(0);
                else
                    proposal = null;
                ProposalData proposalData = null;
                ProposalData pd = calculateProposal(proposalData, proposal, organizationId, job, pumpSchedules, pumpScheduleJobCfg, wellId);
                ProposalData objProposalData = new ProposalData();
                objProposalData.setProposalId(pd.getProposalId());
                if (pd.getAdditives() != null)
                    objProposalData.setAdditives(pd.getAdditives().stream().collect(Collectors.toList()));
                objProposalData.setFluids(pd.getFluids());
                if (pd.getDiscounts() != null)
                    objProposalData.setDiscounts(pd.getDiscounts().stream().collect(Collectors.toList()));
                objProposalData.setInternal(pd.getInternal());
                if (pd.getProppants() != null)
                    objProposalData.setProppants(copyPropants(pd.getProppants()));
                if (pd.getEquipmentCharges() != null)
                    objProposalData.setEquipmentCharges(pd.getEquipmentCharges().stream().collect(Collectors.toList()));
                objProposalData.setFluidTypeChemicalMap(pd.getFluidTypeChemicalMap());
                objProposalData.setOrganizationId(pd.getOrganizationId());
                if (pd.getPumpSchedules() != null)
                    objProposalData.setPumpSchedules(pd.getPumpSchedules().stream().collect(Collectors.toList()));
                objProposalData.setTotalCleanVolume(pd.getTotalCleanVolume());
                objProposalData.setTotalStages(pd.getTotalStages());
                if (pd.getOtherCharges() != null)
                    objProposalData.setOtherCharges(pd.getOtherCharges().stream().collect(Collectors.toList()));
                responseList.add(objProposalData);
            }
            return responseList;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Job found with the provided Job Id : " + jobId);
        }
    }

    private List copyPropants(List propants) {
        List newList = new ArrayList();
        for (int i = 0; i < propants.size(); i++) {
            Proppant p = (Proppant) propants.get(i);
            newList.add(new Proppant(p));
        }
        return newList;

    }

    @RequestMapping(value = "/{proposalId}", method = RequestMethod.GET)
    public Proposal getProposalById(HttpServletRequest request, @PathVariable("proposalId") String proposalId) {
        logger.debug("Looking up data for proposal {}", proposalId);

        Proposal proposal = proposalService.getById(request, proposalId).get();
        return proposal;
    }

    @RequestMapping(value = "/{proposalId}", method = RequestMethod.PUT)
    public void updateProposal(HttpServletRequest request, @PathVariable("proposalId") String proposalId, @RequestBody Proposal proposal) {
        proposalService.updateProposal(request, proposal);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public void saveProposal(HttpServletRequest request, @RequestBody Proposal proposal) {
        List<Proposal> proposals = proposalService.getByJobIdAndWellId(request, proposal.getJobId(), proposal.getWellId());
        if (!proposals.isEmpty()) {
            List<Proposal> p = proposals;
            proposal.setId(p.get(0).getId());
            proposalService.updateProposal(request, proposal);
        } else
            proposalService.saveProposal(request, proposal);
    }

    @RequestMapping(value = "/{proposalId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProposal(HttpServletRequest request, @PathVariable("proposalId") String proposalId) {
        proposalService.deleteProposal(request, proposalId);
    }

    @RequestMapping(value = "/pdf/blob/{jobId}/{wellId}", method = RequestMethod.POST)
    public JobProposalPbfBlobResponse getJobProposalPbfBlob(HttpServletRequest request, @PathVariable("jobId") String jobId, @PathVariable("wellId") String wellId, @RequestParam("file") MultipartFile multipartFile) throws IOException {
        logger.debug("Looking up data for job {}", jobId);

        // by byte code
        byte[] bytes = multipartFile.getBytes();
        // save this string
        storageService.store(jobId, multipartFile);
        //saving fileName
        List<Proposal> proposal = proposalService.getByJobIdAndWellId(request, jobId, wellId);
        Proposal p = proposal.get(0);
        p.setFileName(multipartFile.getOriginalFilename());
        proposalService.updateProposal(request, p);

        JobProposalPbfBlobResponse pdfResponse = new JobProposalPbfBlobResponse();
        pdfResponse.setString(Base64.getEncoder().encodeToString(bytes));
        pdfResponse.setFileName(multipartFile.getOriginalFilename());
        return pdfResponse;
    }

    @RequestMapping(value = "/pdf/blob/{jobId}/{wellId}", method = RequestMethod.GET)
    public ProposalPdfResponse getJobProposalPbf(HttpServletRequest request, @PathVariable("jobId") String jobId, @PathVariable("wellId") String wellId) throws IOException {
        logger.debug("Looking up data for job {}", jobId);
        ProposalPdfResponse proposalPdfResponse = new ProposalPdfResponse();
        List<Proposal> proposal = proposalService.getByJobIdAndWellId(request, jobId, wellId);
        try {
            proposalPdfResponse.setUploadedPdf(storageService.loadFileAsBase64String(jobId));
            proposalPdfResponse.setFileName(proposal.get(0).getFileName());
            proposalPdfResponse.setWellId(wellId);
            proposalPdfResponse.setJobId(jobId);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File is not Found for Terms & Conditions");
        }
        return proposalPdfResponse;
    }

    @RequestMapping(value = "/copyFieldTicket", method = RequestMethod.PUT)
    public void copyFieldTicket(HttpServletRequest request,
                                @RequestParam(name = "jobId") String jobId,
                                @RequestParam(name = "well") String well,
                                @RequestParam(name = "name") String name, @RequestBody FieldTicket fieldTicket) {
        proposalService.copyFieldTicket(request, jobId, well, name, fieldTicket);
    }




}