package com.carbo.job.services;

import com.carbo.job.constants.ErrorCodes;
import com.carbo.job.model.*;
import com.carbo.job.model.widget.PriceBookComponents;
import com.carbo.job.repository.ProppantDeliveryMongoDbRepository;
import com.carbo.job.repository.ProppantMongoDbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/*
"This code is not in use and comments have been added by Jyotish Kumar, Walkingtree on 2022-11-28 as part of JobService Stabilization activity."

*/
@Service
public class ProppantDeliveryService {
    private final ProppantDeliveryMongoDbRepository proppantDeliveryRepository;

    private final ProppantService proppantService;

    private final JobService jobService;

    @Autowired
    public ProppantDeliveryService(ProppantDeliveryMongoDbRepository proppantDeliveryRepository, ProppantMongoDbRepository proppantMongoDbRepository, ProppantService proppantService, JobService jobService) {
        this.proppantDeliveryRepository = proppantDeliveryRepository;
        this.proppantService = proppantService;
        this.jobService = jobService;
    }

    public List<ProppantDeliveryEntry> findByOrganizationIdAndJobId(String organizationId, String jobId) {
        List<ProppantDeliveryEntry> ret = proppantDeliveryRepository.findByOrganizationIdAndJobId(organizationId, jobId);
        if (ret == null || ret.isEmpty()) {
            Job job = jobService.getSimplifiedJob(jobId);
            for (ProppantDeliveryEntry each : job.getProppantDeliveries()) {
                each.setJobId(job.getId());
                each.setTs(job.getTs());
                each.setOrganizationId(organizationId);
                ret.add(saveProppantDelivery(each));
            }
        }
        return ret;
    }

    public ProppantDeliveryEntry saveProppantDelivery(ProppantDeliveryEntry proppantDelivery) {

        return proppantDeliveryRepository.save(proppantDelivery);
    }

    public void updateProppantDelivery(ProppantDeliveryEntry proppantDelivery) {
        proppantDeliveryRepository.save(proppantDelivery);
    }

    public void deleteProppantDelivery(String proppantDeliveryId) {
        proppantDeliveryRepository.deleteById(proppantDeliveryId);
    }

    public Optional<ProppantDeliveryEntry> findByJobId(String jobId){
        List<ProppantDeliveryEntry> deliveries = proppantDeliveryRepository.findByJobId(jobId);
        Optional<ProppantDeliveryEntry> optionalDelivery = deliveries.isEmpty() ? Optional.empty() : Optional.of(deliveries.get(0));
        return optionalDelivery;
    }

    public Optional<ProppantDeliveryEntry> findByJobIdAndProppantAndBolAndPoAndSource(String jobId, String proppant, String bol, String po, String source){
        List<ProppantDeliveryEntry> deliveries = proppantDeliveryRepository.findByJobIdAndProppantAndBolAndPoAndSource(jobId, proppant, bol, po, source);
        Optional<ProppantDeliveryEntry> optionalDelivery = deliveries.isEmpty() ? Optional.empty() : Optional.of(deliveries.get(0));
        return optionalDelivery;
    }

    public void mapper(DeliveryRecordRequest deliveryRecordRequest, String organizationId, String jobNumber, SimplifiedJob job, Optional<ProppantDeliveryEntry> proppantDeliveryEntry, Optional<ProppantDeliveryEntry> proppantDeliveryEntryByJobId, PriceBookComponents proppant, String vendorName, boolean isSplit, List<ProppantDeliveryEntry> proppantDeliveryEntriesSplit){
        ProppantDeliveryEntry proppantDeliveryEntry1 = new ProppantDeliveryEntry();
        EventTimes eventTimes = new EventTimes();
        if (proppantDeliveryEntry.isPresent() && proppantDeliveryEntry.get().getStatus() != null && !proppantDeliveryEntry.get().getStatus().equalsIgnoreCase("accepted") && !proppantDeliveryEntry.get().getStatus().equalsIgnoreCase("transferred")
        || (proppantDeliveryEntry.isPresent() && proppantDeliveryEntry.get().getStatus() != null && proppantDeliveryEntry.get().getStatus().equalsIgnoreCase("accepted") && (proppantDeliveryEntry.get().getOrderStatusID() == 6 || proppantDeliveryEntry.get().getOrderStatusID() == 3 || proppantDeliveryEntry.get().getOrderStatusID() == 4))
        || proppantDeliveryEntry.isPresent() && proppantDeliveryEntry.get().getStatus() == null || proppantDeliveryEntry.isPresent() && proppantDeliveryEntry.get().getStatus().equalsIgnoreCase("accepted")) {
            // Rest of your code
            //if (proppantDeliveryEntry.isPresent() && !proppantDeliveryEntry.get().getStatus().equalsIgnoreCase("accepted") && !proppantDeliveryEntry.get().getStatus().equalsIgnoreCase("transferred")) {

            if (deliveryRecordRequest.getOrderStatusID()!=4 && deliveryRecordRequest.getOrderStatusID()!=6 && !proppantDeliveryEntry.get().getUsedIn().isEmpty()){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorCodes.DELIVERY_ALREADY_USED.getCode() + " - " + ErrorCodes.DELIVERY_ALREADY_USED.getMessage());
            }
            if (isSplit) {
                for (ProppantDeliveryEntry entry : proppantDeliveryEntriesSplit) {
                    updateDeliveryEntry(
                            entry,
                            deliveryRecordRequest,
                            proppant,
                            vendorName,
                            isSplit
                    );
                    saveProppantDelivery(entry);
                }
            } else {
                ProppantDeliveryEntry entry = proppantDeliveryEntry.get();
                updateDeliveryEntry(
                        entry,
                        deliveryRecordRequest,
                        proppant,
                        vendorName,
                        isSplit
                );
                saveProppantDelivery(entry);
            }
        } else if (proppantDeliveryEntryByJobId.isPresent()) {
//            if(ObjectUtils.isEmpty(proppantDeliveryEntryByJobId.get().getEventTimes())){
                eventTimes = new EventTimes();
//            }
//            else {
//                eventTimes = proppantDeliveryEntryByJobId.get().getEventTimes();
//            }
            if (proppantDeliveryEntryByJobId.get().isDelivered() && proppantDeliveryEntryByJobId.get().getOrderStatusID()==6 && deliveryRecordRequest.getOrderStatusID()==4){
                proppantDeliveryEntry1.setAutoOrderId(0);
            } else if (proppantDeliveryEntryByJobId.get().isDelivered() && proppantDeliveryEntryByJobId.get().getOrderStatusID()==3 && deliveryRecordRequest.getOrderStatusID()==4){
                proppantDeliveryEntry.get().setAutoOrderId(0);
            }
            else {
                proppantDeliveryEntry1.setAutoOrderId(deliveryRecordRequest.getAutoOrderId());
            }
            if (deliveryRecordRequest.getOrderStatusID() == 3) {
                proppantDeliveryEntry1.setOrderStatusID(3);
                proppantDeliveryEntry1.setStatus("pending");
                eventTimes.setEnRouteTime(System.currentTimeMillis() / 1000);
            } else if (deliveryRecordRequest.getOrderStatusID() == 6) {
                proppantDeliveryEntry1.setOrderStatusID(6);
                proppantDeliveryEntry1.setStatus("pending");
                eventTimes.setOnSiteTime(System.currentTimeMillis() / 1000);
            } else if (deliveryRecordRequest.getOrderStatusID() == 4) {
                proppantDeliveryEntry1.setOrderStatusID(4);
                proppantDeliveryEntry1.setStatus("delivered");
                eventTimes.setDeliveredTime(System.currentTimeMillis() / 1000);
            }
            proppantDeliveryEntry1.setEventTimes(eventTimes);
            proppantDeliveryEntry1.setSource("External");
            proppantDeliveryEntry1.setJobId(proppantDeliveryEntryByJobId.get().getJobId());
            proppantDeliveryEntry1.setBol(deliveryRecordRequest.getBol());
            proppantDeliveryEntry1.setBolQuantity(proppantDeliveryEntryByJobId.get().getBolQuantity());
            proppantDeliveryEntry1.setWtAmount(deliveryRecordRequest.getWtAmount());
            proppantDeliveryEntry1.setCreated(proppantDeliveryEntryByJobId.get().getCreated());
            proppantDeliveryEntry1.setDate(deliveryRecordRequest.getDateTime());
            proppantDeliveryEntry1.setLastModifiedBy(proppantDeliveryEntryByJobId.get().getLastModifiedBy());
            proppantDeliveryEntry1.setModified(new Date().getTime());
            proppantDeliveryEntry1.setOrganizationId(proppantDeliveryEntryByJobId.get().getOrganizationId());
            proppantDeliveryEntry1.setProppant(proppantDeliveryEntryByJobId.get().getProppant());
            proppantDeliveryEntry1.setPo(deliveryRecordRequest.getPo());
            proppantDeliveryEntry1.setProppant(proppant.getName());
            proppantDeliveryEntry1.setVendor(vendorName);
            proppantDeliveryEntry1.setUom(proppantDeliveryEntryByJobId.get().getUom());
            proppantDeliveryEntry1.setTs(proppantDeliveryEntryByJobId.get().getTs());
            proppantDeliveryEntry1.setTruckNumber(deliveryRecordRequest.getTruckNumber());
            proppantDeliveryEntry1.setCustomerID(deliveryRecordRequest.getCustomerID());
            proppantDeliveryEntry1.setDelivered(false);
            proppantDeliveryEntry1.setCopyGeoFenceData(false);

            saveProppantDelivery(proppantDeliveryEntry1);
        } else {
            eventTimes = new EventTimes();
            if (deliveryRecordRequest.getOrderStatusID() == 3) {
                proppantDeliveryEntry1.setOrderStatusID(3);
                proppantDeliveryEntry1.setStatus("pending");
                eventTimes.setEnRouteTime(System.currentTimeMillis() / 1000);
            } else if (deliveryRecordRequest.getOrderStatusID() == 6) {
                proppantDeliveryEntry1.setOrderStatusID(6);
                proppantDeliveryEntry1.setStatus("pending");
                eventTimes.setOnSiteTime(System.currentTimeMillis() / 1000);
            } else if (deliveryRecordRequest.getOrderStatusID() == 4) {
                proppantDeliveryEntry1.setOrderStatusID(4);
                proppantDeliveryEntry1.setStatus("delivered");
                eventTimes.setDeliveredTime(System.currentTimeMillis() / 1000);
            }
            proppantDeliveryEntry1.setAutoOrderId(deliveryRecordRequest.getAutoOrderId());
            proppantDeliveryEntry1.setEventTimes(eventTimes);
            proppantDeliveryEntry1.setSource("External");
            proppantDeliveryEntry1.setJobId(job.getId());
            proppantDeliveryEntry1.setBol(deliveryRecordRequest.getBol());
            proppantDeliveryEntry1.setWtAmount(deliveryRecordRequest.getWtAmount());
            proppantDeliveryEntry1.setCreated(new Date().getTime());
            proppantDeliveryEntry1.setDate(deliveryRecordRequest.getDateTime());
            proppantDeliveryEntry1.setModified(new Date().getTime());
            proppantDeliveryEntry1.setOrganizationId(job.getOrganizationId());
            proppantDeliveryEntry1.setProppant(deliveryRecordRequest.getItemNo());
            proppantDeliveryEntry1.setPo(deliveryRecordRequest.getPo());
            proppantDeliveryEntry1.setProppant(proppant.getName());
            proppantDeliveryEntry1.setVendor(vendorName);
            proppantDeliveryEntry1.setTruckNumber(deliveryRecordRequest.getTruckNumber());
            proppantDeliveryEntry1.setUom("lb");
            proppantDeliveryEntry1.setCustomerID(deliveryRecordRequest.getCustomerID());
            proppantDeliveryEntry1.setDelivered(false);
            proppantDeliveryEntry1.setCopyGeoFenceData(false);

            saveProppantDelivery(proppantDeliveryEntry1);
        }
    }

    private int getStatusRank(int statusId) {
        switch (statusId) {
            case 3:
                return 1; // en route
            case 6:
                return 2; // on site
            case 4:
                return 3; // delivered
            default:
                return 0; // unknown or lowest priority
        }
    }

    public Optional<ProppantDeliveryEntry> findByOrganizationIdAndAutoOrderId(String organizationId, int autoOrderId) {
        return proppantDeliveryRepository.findByOrganizationIdAndAutoOrderId(organizationId, autoOrderId);
    }

    public Optional<ProppantDeliveryEntry> findByJobIdAndBolAndPoAndAutoOrderId(String id, String bol, String po, int autoOrderId) {
        return proppantDeliveryRepository.findByJobIdAndBolAndPoAndAutoOrderId(id, bol, po, autoOrderId);
    }

    public List<ProppantDeliveryEntry> findByJobIdAndProppantAndBolAndPo(String id, String name, String bol, String po) {
        return proppantDeliveryRepository.findByJobIdAndProppantAndBolAndPo(id, name, bol, po);
    }

    private void updateDeliveryEntry(
            ProppantDeliveryEntry deliveryEntry,
            DeliveryRecordRequest deliveryRecordRequest,
            PriceBookComponents proppant,
            String vendorName,
            boolean isSplit
    ) {
        EventTimes eventTimes = ObjectUtils.isEmpty(deliveryEntry.getEventTimes())
                ? new EventTimes()
                : deliveryEntry.getEventTimes();
        if (deliveryEntry.isDelivered() && deliveryEntry.getOrderStatusID()==6 && deliveryRecordRequest.getOrderStatusID()==4){
            deliveryEntry.setAutoOrderId(0);
        } else if (deliveryEntry.isDelivered() && deliveryEntry.getOrderStatusID()==3 && deliveryRecordRequest.getOrderStatusID()==4){
            deliveryEntry.setAutoOrderId(0);
        }
        else {
            Integer box = deliveryEntry.getBox();
            if (box == null || box != 2) {
                deliveryEntry.setAutoOrderId(deliveryRecordRequest.getAutoOrderId());
            }
        }
        int newStatus = deliveryRecordRequest.getOrderStatusID();
        int currentStatus = deliveryEntry.getOrderStatusID();

        if (getStatusRank(newStatus) > getStatusRank(currentStatus)) {
            deliveryEntry.setOrderStatusID(newStatus);
            if (newStatus == 3) {
                deliveryEntry.setStatus("pending");
                eventTimes.setEnRouteTime(System.currentTimeMillis() / 1000);
            } else if (newStatus == 6) {
                if(ObjectUtils.isEmpty(deliveryEntry.getStatus())){
                    deliveryEntry.setStatus("accepted");
                    deliveryEntry.setDelivered(true);
                    deliveryEntry.setAcceptedDate(deliveryEntry.getCreated());
                    eventTimes.setOnSiteTime(System.currentTimeMillis() / 1000);
                } else if (!deliveryEntry.getStatus().equals("accepted")) {
                    deliveryEntry.setStatus("pending");
                }
                eventTimes.setOnSiteTime(System.currentTimeMillis() / 1000);
            } else if (newStatus == 4) {
                if(ObjectUtils.isEmpty(deliveryEntry.getStatus())) {
                    deliveryEntry.setStatus("accepted");
                    deliveryEntry.setDelivered(true);
                    deliveryEntry.setAcceptedDate(deliveryEntry.getCreated());
                } else if(deliveryEntry.getStatus().equals("accepted")){
                    deliveryEntry.setDelivered(true);
                    deliveryEntry.setStatus("delivered");
                    deliveryEntry.setAcceptedDate(deliveryEntry.getCreated());
                } else{
                    deliveryEntry.setStatus("delivered");
                }
                eventTimes.setDeliveredTime(System.currentTimeMillis() / 1000);
            }
        }

        if(currentStatus == 0){
            deliveryEntry.setStatus("accepted");
            deliveryEntry.setSource("External");
            deliveryEntry.setAcceptedDate(deliveryEntry.getCreated());
        }

        deliveryEntry.setEventTimes(eventTimes);
        deliveryEntry.setDate(deliveryRecordRequest.getDateTime());
        deliveryEntry.setModified(new Date().getTime());
        deliveryEntry.setPo(deliveryRecordRequest.getPo());
        deliveryEntry.setProppant(proppant.getName());
        deliveryEntry.setVendor(vendorName);
        deliveryEntry.setTruckNumber(deliveryRecordRequest.getTruckNumber());
        deliveryEntry.setBol(deliveryRecordRequest.getBol());
        deliveryEntry.setCustomerID(deliveryRecordRequest.getCustomerID());
        deliveryEntry.setDelivered(deliveryEntry.isDelivered());
        deliveryEntry.setCopyGeoFenceData(deliveryEntry.isCopyGeoFenceData());
    }

    public Optional<ProppantDeliveryEntry> findByJobIdAndProppantAndBolAndPoWithoutSource(
            String id, String name, String bol, String po) {

        return Optional.ofNullable(findByJobIdAndProppantAndBolAndPo(id, name, bol, po))
                .orElseGet(Collections::emptyList)
                .stream()
                .findFirst();
    }

}
