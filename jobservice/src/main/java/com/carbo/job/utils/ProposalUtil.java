package com.carbo.job.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.carbo.job.controllers.JobServiceController;
import com.carbo.job.model.Chemical;
import com.carbo.job.model.Job;
import com.carbo.job.model.Proppant;
import com.carbo.job.model.PumpSchedule;
import com.carbo.job.model.PumpScheduleJobCfg;
import com.carbo.job.model.proposal.ChemicalDiscount;
import com.carbo.job.model.proposal.Proposal;
import com.carbo.job.model.proposal.ProposalData;
import com.carbo.job.model.proposal.PumpScheduleStage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProposalUtil {
    private static final Logger logger = LoggerFactory.getLogger(ProposalUtil.class);
    private static final  Float conversion = 0.042f;
    public static ProposalData calculateProposal(List<Proposal> p, String organizationId, Job job, List<PumpSchedule> pumpSchedules, PumpScheduleJobCfg pumpScheduleJobCfg){
        int totalStages = job.getWells().get(0).getTotalStages();
        Map<String, Proppant> dbproppantsMap = new HashMap<>();
        job.getWells().forEach(well -> {
            well.getProppants().forEach(proppant -> {
                dbproppantsMap.put(proppant.getName(), proppant);
            });
        });
        double totalCleanVolume = 0;
        Map<String, Proppant> proppantsMap = new HashMap<>();  // key: proppant name, value: proppant
        Map<String, Float> fluidTypeMap = new HashMap<>();  // key: fluid type
        Map<String, Float> fluidTypeMapGals = new HashMap<>();  // key: fluid type
        Map<String, List<Chemical>> fluidTypeChemicalMap = new HashMap<>();  // key: fluid type
        Map<String, List<PumpSchedule>> selectedPumpSchedules = new HashMap<>();  // key: fluid type
        Map<String, PumpScheduleStage> sumPumpSchedules = new HashMap<>();  // key: fluid type
        // get the proposal if already created
        Float tempStage = 0.0f;
        String tempWellId = "";
        int tempLastSteps = 0;
        Float lastStageSum = 0.0f;
        ProposalData proposalData = new ProposalData(p, organizationId, totalStages, totalCleanVolume, null, null);
        for (PumpSchedule pump : pumpSchedules) {
            // start calculate total clean vol
            Float cleanVolBbls = 0.0f;
            Float cleanVolGals = 0.0f;
            Float cleanVolIbs = 0.0f;
            Float concentration = 0.0f;
            Float overFlush = 0.0f;
            if (pump.getCleanVol() != null) {
                if (pump.getStepName().indexOf("Flush") > 0) {
                    if (pumpScheduleJobCfg != null && pumpScheduleJobCfg.getOverflush() != null) {
                        try {
                            overFlush = Float.parseFloat(pumpScheduleJobCfg.getOverflush());
                        } catch (Exception e) {
                        }
                        ;
                    }
                    cleanVolBbls = pump.getCleanVol() + overFlush;
                } else {
                    cleanVolBbls = pump.getCleanVol();
                }
                proposalData.setTotalCleanVolume(proposalData.getTotalCleanVolume() + cleanVolBbls);
            } else {
                pump.setCleanVol(cleanVolBbls);
            }
            // update last calculated for stage
            lastStageSum = lastStageSum + cleanVolBbls;
            // update temp stage
            if (pump.getStageNumber() != null) {
                if (Float.compare(tempStage, pump.getStageNumber()) != 0) {
                    //new stage 
                    tempStage = pump.getStageNumber();
                    tempLastSteps = 1;
                    lastStageSum = 0.0f;
                    List<PumpSchedule> pumpSchedulesList = new ArrayList<>();
                    logger.error("pump details 1. stage - {}, 2. wellId - {}, 3. cleanVol - {}", pump.getStageNumber(), pump.getWellId(), pump.getCleanVol());
                    pumpSchedulesList.add(pump);
                    selectedPumpSchedules.put(pump.getStageNumber()+pump.getWellId(), pumpSchedulesList);
                    sumPumpSchedules.put(pump.getStageNumber()+pump.getWellId(), new PumpScheduleStage(cleanVolBbls,1,Math.round(pump.getStageNumber()),pump.getWellId(),pumpSchedulesList));
                }else{
                    tempLastSteps++;
                    List<PumpSchedule> pumpSchedulesList = new ArrayList<>();
                    logger.error("pump details 1. stage - {}, 2. wellId - {}, 3. cleanVol - {}", pump.getStageNumber(), pump.getWellId(), pump.getCleanVol());
                    pumpSchedulesList.add(pump);
                    selectedPumpSchedules.put(pump.getStageNumber()+pump.getWellId(), pumpSchedulesList);
                    sumPumpSchedules.put(pump.getStageNumber()+pump.getWellId(), new PumpScheduleStage(sumPumpSchedules.get(pump.getStageNumber()+pump.getWellId()).getTotalCleanVolume()+cleanVolBbls,tempLastSteps,Math.round(pump.getStageNumber()),pump.getWellId(),pumpSchedulesList));
                }
            }
            // get clean vol gals
            cleanVolGals = cleanVolBbls * 42;
            // get concentration
            if (pump.getProppantConcentrationTo() != null && pump.getProppantConcentration() != null)
                concentration = (pump.getProppantConcentrationTo() + pump.getProppantConcentration()) / 2;
            if (pump.getProppantConcentration() != null)
                concentration = pump.getProppantConcentration();
            // get clean vol Ibs    
            cleanVolIbs = cleanVolGals * concentration;
            // end calculate total clean vol
            // update propantsMap
            if (pump.getProppantType() != null && dbproppantsMap.containsKey(pump.getProppantType())) {
                Float proppantVal = pump.getCleanVol() * 42 * concentration;
                if (proppantsMap.containsKey(pump.getProppantType()))
                    proppantsMap.get(pump.getProppantType()).setTotalCleanVolume(proppantsMap.get(pump.getProppantType()).getTotalCleanVolume() + proppantVal);
                else {
                    Proppant proppant = dbproppantsMap.get(pump.getProppantType());
                    proppant.setTotalCleanVolume(proppantVal);
                    proppantsMap.put(pump.getProppantType(), proppant);
                }
            }
            // end update propantsMap
            if (pump.getFluidType() != null) {
                if (fluidTypeMap.containsKey(pump.getFluidType()))
                    fluidTypeMap.put(pump.getFluidType(), fluidTypeMap.get(pump.getFluidType()) + getRelativeVolume(pump.getFluidType(), cleanVolBbls, cleanVolGals, cleanVolIbs));
                else {
                    fluidTypeMap.put(pump.getFluidType(), getRelativeVolume(pump.getFluidType(), cleanVolBbls, cleanVolGals, cleanVolIbs));
                }
                // pdf fluids
                if (fluidTypeMapGals.containsKey(pump.getFluidType()))
                    fluidTypeMapGals.put(pump.getFluidType(), fluidTypeMapGals.get(pump.getFluidType()) + cleanVolGals);
                else {
                    fluidTypeMapGals.put(pump.getFluidType(), cleanVolGals);
                }
            }
        }
        Map<String, PumpScheduleStage> sumPumpSchedulesSorted = sumPumpSchedules.entrySet().stream()
                .sorted(Comparator.comparingInt(pump -> Math.round(pump.getValue().getPump().get(0).getStageNumber())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        Collection<PumpScheduleStage> pumpSchedule = sumPumpSchedulesSorted.values().stream()
                .collect(Collectors.toMap(PumpScheduleStage::generateUniqueKey, Function.identity(), (a, b) -> a))
                .values();
        Map<String, Chemical> additivesMap = new HashMap<>(); // key 
        job.getWells().forEach(chem -> {
            chem.getAcidAdditives().forEach(obj -> {
                if (fluidTypeMap.containsKey("Acid")) {
                    Chemical chemical = getChemical("Acid", obj, fluidTypeMap);
                    if (additivesMap.containsKey(chemical.getName()))
                        chemical.setTotalCleanVolume(additivesMap.get(chemical.getName()).getTotalCleanVolume() + chemical.getTotalCleanVolume());

                    additivesMap.put(chemical.getName(), chemical);
                    fluidTypeChemicalMap.put("Acid", getChemicalList("Acid", obj, fluidTypeMap, fluidTypeChemicalMap));
                }
            });
            chem.getSlickwaters().forEach(obj -> {
                // calculate
                if (fluidTypeMap.containsKey("Slickwater")) {
                    Chemical chemical = getChemical("Slickwater", obj, fluidTypeMap);
                    if (additivesMap.containsKey(chemical.getName()))
                        chemical.setTotalCleanVolume(additivesMap.get(chemical.getName()).getTotalCleanVolume() + chemical.getTotalCleanVolume());

                    additivesMap.put(chemical.getName(), chemical);
                    fluidTypeChemicalMap.put("Slickwater", getChemicalList("Slickwater", obj, fluidTypeMap, fluidTypeChemicalMap));
                }
            });
            chem.getLinearGelCrosslinks().forEach(obj -> {
                // calculate
                if (fluidTypeMap.containsKey("Linear Gel")) {
                    Chemical chemical = getChemical("Linear Gel", obj, fluidTypeMap);
                    if (additivesMap.containsKey(chemical.getName()))
                        chemical.setTotalCleanVolume(additivesMap.get(chemical.getName()).getTotalCleanVolume() + chemical.getTotalCleanVolume());

                    additivesMap.put(chemical.getName(), chemical);
                    fluidTypeChemicalMap.put("Linear Gel", getChemicalList("Linear Gel", obj, fluidTypeMap, fluidTypeChemicalMap));
                }
            });
            chem.getDiverters().forEach(obj -> {
                if (fluidTypeMap.containsKey("Diverter")) {
                    Chemical chemical = getChemical("Diverter", obj, fluidTypeMap);
                    if (additivesMap.containsKey(chemical.getName()))
                        chemical.setTotalCleanVolume(additivesMap.get(chemical.getName()).getTotalCleanVolume() + chemical.getTotalCleanVolume());

                    additivesMap.put(chemical.getName(), chemical);
                    fluidTypeChemicalMap.put("Diverter", getChemicalList("Diverter", obj, fluidTypeMap, fluidTypeChemicalMap));
                }
            });
            chem.getAdditionalChemicalTypes().forEach((k, v) -> {
                v.forEach(obj -> {
                    if (fluidTypeMap.containsKey(k)) {
                        Chemical chemical = getChemical(k, obj, fluidTypeMap);
                        if (additivesMap.containsKey(chemical.getName()))
                            chemical.setTotalCleanVolume(additivesMap.get(chemical.getName()).getTotalCleanVolume() + chemical.getTotalCleanVolume());

                        additivesMap.put(chemical.getName(), chemical);
                        fluidTypeChemicalMap.put(k, getChemicalList(k, obj, fluidTypeMap, fluidTypeChemicalMap));
                    }
                });
            });
        });
        Map<String, Integer> discountsMap = proposalData.getDiscounts() != null ? proposalData.getDiscounts().stream().collect(Collectors.toMap(ChemicalDiscount::getName, ChemicalDiscount::getDiscount)) : new HashMap<>();
        List<Chemical> chemicals = additivesMap.values().stream().map(chem -> {
            chem.setTotalCleanVolumeRound(floatToBigInteger(chem.getTotalCleanVolume()));
            chem.setDiscount(discountsMap.containsKey(chem.getName()) ? (float) discountsMap.get(chem.getName()) : 0.0f);
            return chem;
        }).collect(Collectors.toList());
        List<Proppant> proppants = proppantsMap.values().stream().map(chem -> {
            chem.setTotalCleanVolumeRound(floatToBigInteger(chem.getTotalCleanVolume()));
            chem.setDiscount(discountsMap.containsKey(chem.getName()) ? (float) discountsMap.get(chem.getName()) : 0.0f);
            return chem;
        }).collect(Collectors.toList());
        proposalData.setAdditives(chemicals);
        proposalData.setProppants(proppants);
        proposalData.setPumpSchedules(pumpSchedule.stream().collect(Collectors.toList()));
        proposalData.setFluids(fluidTypeMapGals.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> floatToBigInteger(e.getValue()))));
        proposalData.setFluidTypeChemicalMap(fluidTypeChemicalMap);
        return proposalData;
    }

    public static ProposalData calculateProposal(ProposalData proposalData, Proposal p, String organizationId, Job job, List<PumpSchedule> pumpSchedules, PumpScheduleJobCfg pumpScheduleJobCfg, String wellId) {

        int totalStages = job.getWells().get(0).getTotalStages();
        Map<String, Proppant> dbproppantsMap = new HashMap<>();
        job.getWells().forEach(well -> {
            well.getProppants().forEach(proppant -> {
                dbproppantsMap.put(proppant.getName(), proppant);
            });
        });
        double totalCleanVolume = 0;
        Map<String, Proppant> proppantsMap = new HashMap<>();  // key: proppant name, value: proppant
        Map<String, Float> fluidTypeMap = new HashMap<>();  // key: fluid type
        Map<String, Float> fluidTypeMapGals = new HashMap<>();  // key: fluid type
        Map<String, List<Chemical>> fluidTypeChemicalMap = new HashMap<>();  // key: fluid type
        Map<String, List<PumpSchedule>> selectedPumpSchedules = new HashMap<>();  // key: fluid type
        Map<String, PumpScheduleStage> sumPumpSchedules = new HashMap<>();  // key: fluid type
        // get the proposal if already created
        Float tempStage = 0.0f;
        String tempWellId = "";
        int tempLastSteps = 0;
        Float lastStageSum = 0.0f;
        proposalData = new ProposalData(p, organizationId, totalStages, totalCleanVolume, null, null);
        for (PumpSchedule pump : pumpSchedules) {
            // start calculate total clean vol
            Float cleanVolBbls = 0.0f;
            Float cleanVolGals = 0.0f;
            Float cleanVolIbs = 0.0f;
            Float concentration = 0.0f;
            Float overFlush = 0.0f;
            if (pump.getCleanVol() != null) {
                if (pump.getStepName().indexOf("Flush") > 0) {
                    if (pumpScheduleJobCfg != null && pumpScheduleJobCfg.getOverflush() != null) {
                        try {
                            overFlush = Float.parseFloat(pumpScheduleJobCfg.getOverflush());
                        } catch (Exception e) {
                        }
                        ;
                    }
                    cleanVolBbls = pump.getCleanVol() + overFlush;
                } else {
                    cleanVolBbls = pump.getCleanVol();
                }
                proposalData.setTotalCleanVolume(proposalData.getTotalCleanVolume() + cleanVolBbls);
            } else {
                pump.setCleanVol(cleanVolBbls);
            }
            // update last calculated for stage
            lastStageSum = lastStageSum + cleanVolBbls;
            // update temp stage
            if (pump.getStageNumber() != null) {
                if (Float.compare(tempStage, pump.getStageNumber()) != 0) {
                    //new stage
                    tempStage = pump.getStageNumber();
                    tempLastSteps = 1;
                    lastStageSum = 0.0f;
                    List<PumpSchedule> pumpSchedulesList = new ArrayList<>();
                    pumpSchedulesList.add(pump);
                    selectedPumpSchedules.put(pump.getStageNumber() + pump.getWellId(), pumpSchedulesList);
                    sumPumpSchedules.put(pump.getStageNumber() + pump.getWellId(), new PumpScheduleStage(cleanVolBbls, 1, Math.round(pump.getStageNumber()), pump.getWellId(), pumpSchedulesList));
                } else {
                    tempLastSteps++;
                    List<PumpSchedule> pumpSchedulesList = selectedPumpSchedules.get(pump.getStageNumber() + pump.getWellId());
                    pumpSchedulesList.add(pump);
                    selectedPumpSchedules.put(pump.getStageNumber() + pump.getWellId(), pumpSchedulesList);
                    sumPumpSchedules.put(pump.getStageNumber() + pump.getWellId(), new PumpScheduleStage(sumPumpSchedules.get(pump.getStageNumber() + pump.getWellId()).getTotalCleanVolume() + cleanVolBbls, tempLastSteps, Math.round(pump.getStageNumber()), pump.getWellId(), pumpSchedulesList));
                }
            }
            // get clean vol gals
            cleanVolGals = cleanVolBbls * 42;
            // get concentration
            if (pump.getProppantConcentrationTo() != null && pump.getProppantConcentration() != null)
                concentration = (pump.getProppantConcentrationTo() + pump.getProppantConcentration()) / 2;
            if (pump.getProppantConcentration() != null)
                concentration = pump.getProppantConcentration();
            // get clean vol Ibs
            cleanVolIbs = cleanVolGals * concentration;
            // end calculate total clean vol
            // update propantsMap
            if (pump.getProppantType() != null && dbproppantsMap.containsKey(pump.getProppantType())) {
                Float proppantVal = pump.getCleanVol() * 42 * concentration;
                if (proppantsMap.containsKey(pump.getProppantType())) {
                    Proppant proppant = proppantsMap.get(pump.getProppantType());
                    Float total = (Float) proppant.getTotalCleanVolume() + proppantVal;
                    proppant.setTotalCleanVolume(total);
                    //proppantsMap.get(pump.getProppantType()).setTotalCleanVolume(total);
                } else {
                    Proppant proppant = dbproppantsMap.get(pump.getProppantType());
                    proppant.setTotalCleanVolume(proppantVal);
                    proppantsMap.put(pump.getProppantType(), proppant);
                }
            }

            // end update propantsMap
            if (pump.getFluidType() != null) {
                if (fluidTypeMap.containsKey(pump.getFluidType()))
                    fluidTypeMap.put(pump.getFluidType(), fluidTypeMap.get(pump.getFluidType()) + getRelativeVolume(pump.getFluidType(), cleanVolBbls, cleanVolGals, cleanVolIbs));
                else {
                    fluidTypeMap.put(pump.getFluidType(), getRelativeVolume(pump.getFluidType(), cleanVolBbls, cleanVolGals, cleanVolIbs));
                }
                // pdf fluids
                if (fluidTypeMapGals.containsKey(pump.getFluidType()))
                    fluidTypeMapGals.put(pump.getFluidType(), fluidTypeMapGals.get(pump.getFluidType()) + cleanVolGals);
                else {
                    fluidTypeMapGals.put(pump.getFluidType(), cleanVolGals);
                }
            }
        }
        Map<String, PumpScheduleStage> sumPumpSchedulesSorted = sumPumpSchedules.entrySet().stream()
                .sorted(Comparator.comparingInt(pump -> Math.round(pump.getValue().getPump().get(0).getStageNumber())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        Collection<PumpScheduleStage> pumpSchedule = new ArrayList<>();
        Iterator itr = sumPumpSchedulesSorted.values().iterator();
        while (itr.hasNext()) {
            PumpScheduleStage item = (PumpScheduleStage) itr.next();
            pumpSchedule.add(item);
        }
        Map<String, Chemical> additivesMap = new HashMap<>(); // key
        job.getWells().forEach(chem -> {
            if (chem.getId().equals(wellId)) {
                chem.getAcidAdditives().forEach(obj -> {
                    if (fluidTypeMap.containsKey("Acid")) {
                        Chemical chemical = getChemical("Acid", obj, fluidTypeMap);
                        if (additivesMap.containsKey(chemical.getName()))
                            chemical.setTotalCleanVolume(additivesMap.get(chemical.getName()).getTotalCleanVolume() + chemical.getTotalCleanVolume());

                        additivesMap.put(chemical.getName(), chemical);
                        fluidTypeChemicalMap.put("Acid", getChemicalList("Acid", obj, fluidTypeMap, fluidTypeChemicalMap));
                    }
                });
                chem.getSlickwaters().forEach(obj -> {
                    // calculate
                    if (fluidTypeMap.containsKey("Slickwater")) {
                        Chemical chemical = getChemical("Slickwater", obj, fluidTypeMap);
                        if (additivesMap.containsKey(chemical.getName()))
                            chemical.setTotalCleanVolume(additivesMap.get(chemical.getName()).getTotalCleanVolume() + chemical.getTotalCleanVolume());

                        additivesMap.put(chemical.getName(), chemical);
                        fluidTypeChemicalMap.put("Slickwater", getChemicalList("Slickwater", obj, fluidTypeMap, fluidTypeChemicalMap));
                    }
                });
                chem.getLinearGelCrosslinks().forEach(obj -> {
                    // calculate
                    if (fluidTypeMap.containsKey("Linear Gel")) {
                        Chemical chemical = getChemical("Linear Gel", obj, fluidTypeMap);
                        if (additivesMap.containsKey(chemical.getName()))
                            chemical.setTotalCleanVolume(additivesMap.get(chemical.getName()).getTotalCleanVolume() + chemical.getTotalCleanVolume());

                        additivesMap.put(chemical.getName(), chemical);
                        fluidTypeChemicalMap.put("Linear Gel", getChemicalList("Linear Gel", obj, fluidTypeMap, fluidTypeChemicalMap));
                    }
                });
                chem.getDiverters().forEach(obj -> {
                    if (fluidTypeMap.containsKey("Diverter")) {
                        Chemical chemical = getChemical("Diverter", obj, fluidTypeMap);
                        if (additivesMap.containsKey(chemical.getName()))
                            chemical.setTotalCleanVolume(additivesMap.get(chemical.getName()).getTotalCleanVolume() + chemical.getTotalCleanVolume());

                        additivesMap.put(chemical.getName(), chemical);
                        fluidTypeChemicalMap.put("Diverter", getChemicalList("Diverter", obj, fluidTypeMap, fluidTypeChemicalMap));
                    }
                });
                chem.getAdditionalChemicalTypes().forEach((k, v) -> {
                    v.forEach(obj -> {
                        if (fluidTypeMap.containsKey(k)) {
                            Chemical chemical = getChemical(k, obj, fluidTypeMap);
                            if (additivesMap.containsKey(chemical.getName()))
                                chemical.setTotalCleanVolume(additivesMap.get(chemical.getName()).getTotalCleanVolume() + chemical.getTotalCleanVolume());

                            additivesMap.put(chemical.getName(), chemical);
                            fluidTypeChemicalMap.put(k, getChemicalList(k, obj, fluidTypeMap, fluidTypeChemicalMap));
                        }
                    });
                });
            }
        });
        Map<String, Integer> discountsMap = proposalData.getDiscounts() != null ? proposalData.getDiscounts().stream().collect(Collectors.toMap(ChemicalDiscount::getName, ChemicalDiscount::getDiscount)) : new HashMap<>();
        List<Chemical> chemicals = additivesMap.values().stream().map(chem -> {
            chem.setTotalCleanVolumeRound(floatToBigInteger(chem.getTotalCleanVolume()));
            chem.setDiscount(discountsMap.containsKey(chem.getName()) ? (float) discountsMap.get(chem.getName()) : 0.0f);
            return chem;
        }).collect(Collectors.toList());
        Iterator i = proppantsMap.values().iterator();
        for (Map.Entry<String, Proppant> entry : proppantsMap.entrySet()) {
            Proppant p2 = (Proppant) entry.getValue();
        }
        List<Proppant> proppants = proppantsMap.values().stream().map(chem -> {
            chem.setTotalCleanVolumeRound(floatToBigInteger(chem.getTotalCleanVolume()));
            chem.setDiscount(discountsMap.containsKey(chem.getName()) ? (float) discountsMap.get(chem.getName()) : 0.0f);
            return chem;
        }).collect(Collectors.toList());
        proposalData.setAdditives(chemicals);
        proposalData.setProppants(proppants);
        proposalData.setPumpSchedules(pumpSchedule.stream().collect(Collectors.toList()));
        proposalData.setFluids(fluidTypeMapGals.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> floatToBigInteger(e.getValue()))));
        proposalData.setFluidTypeChemicalMap(fluidTypeChemicalMap);
        return proposalData;
    }

    private static Float getRelativeVolume(String fluidType, Float cleanVolBbls, Float cleanVolGals, Float cleanVolIbs) {
        fluidType = fluidType.toLowerCase();
        if (fluidType.contains("slickwater") || fluidType.contains("linear") || fluidType.contains("gel") || fluidType.contains("crosslink"))
            return cleanVolBbls;
        if (fluidType.contains("acid"))
            return cleanVolGals;
        if (fluidType.contains("diverter"))
            return cleanVolIbs;
        return cleanVolBbls;
    }

    private static List<Chemical> getChemicalList(String fluidType, Chemical chemical, Map<String, Float> fluidTypeMap, Map<String, List<Chemical>> fluidTypeChemicalMap) {
        Chemical newChemical = getChemical(fluidType, chemical, fluidTypeMap);
        List<Chemical> chemicals = null;
        if (fluidTypeChemicalMap.containsKey(fluidType)) {
            chemicals = fluidTypeChemicalMap.get(fluidType);
        } else
            chemicals = new ArrayList<>();
        chemicals.add(newChemical);
        return chemicals;
    }

    private static Chemical getChemical(String fluidType, Chemical chemical, Map<String, Float> fluidTypeMap) {
        Float totalVol = 0.0f;
        if (fluidType.contains("Slickwater") || fluidType.contains("Linear") || fluidType.contains("Gel") || fluidType.contains("Crosslink")) {
            totalVol = fluidTypeMap.get(fluidType) * Float.parseFloat(chemical.getConcentration()) * conversion;
        } else {
            totalVol = fluidTypeMap.get(fluidType);
        }
        Chemical newChemical = new Chemical(chemical);
        newChemical.setTotalCleanVolume(totalVol);
        newChemical.setTotalCleanVolumeRound(floatToBigInteger(totalVol));
        return newChemical;
    }

    private static BigInteger floatToBigInteger(Float value) {
        return BigDecimal.valueOf(value).setScale(0, RoundingMode.HALF_UP).toBigInteger();
    }
}