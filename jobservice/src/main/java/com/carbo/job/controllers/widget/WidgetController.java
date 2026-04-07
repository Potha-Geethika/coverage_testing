package com.carbo.job.controllers.widget;

import com.carbo.job.model.widget.ChemicalEnum;
import com.carbo.job.model.widget.MaterialNeeded;
import com.carbo.job.services.widget.WidgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/v1/widget")
public class WidgetController {
    private final WidgetService widgetService;

    @Autowired
    public WidgetController(WidgetService widgetService) {
        this.widgetService = widgetService;
    }

    @GetMapping("/chemical")
    public ResponseEntity getChemicalData(HttpServletRequest request, @RequestParam String jobId, @RequestParam(required = false) String wellId, @RequestParam(required = false) Float stageNumber, @RequestParam(defaultValue = "JOB") ChemicalEnum chemicalEnum) {
        return widgetService.getChemicalPlotData(request, jobId, wellId, stageNumber, chemicalEnum);
    }

    @GetMapping("/cost-tracking")
    public ResponseEntity getChemicalCostData(HttpServletRequest request, @RequestParam String jobId, @RequestParam(required = false) String wellId, @RequestParam(required = false) Float stageNumber, @RequestParam(defaultValue = "JOB") ChemicalEnum chemicalEnum) {
        return widgetService.getCostsTrackingData(request, jobId, wellId, stageNumber, chemicalEnum);
    }

    @GetMapping("/performanceParameter")
    public ResponseEntity getPerformanceParameter(HttpServletRequest request, @RequestParam String jobId, @RequestParam(required = false, defaultValue = "1") String days, @RequestParam(required = false) boolean forDay) {
        return widgetService.getPerformanceParameter(request,jobId, Integer.parseInt(days),forDay);
    }

    @GetMapping("/proppantPlaced")
    public ResponseEntity getProppantPlaced(HttpServletRequest request,
                                            @RequestParam String jobId,
                                            @RequestParam String wellId,
                                            @RequestParam(defaultValue = "WELL") ChemicalEnum chemicalEnum) {
        return widgetService.getProppantPlaced(request, jobId, wellId, chemicalEnum);
    }

    @GetMapping("/cleanVolume")
    public ResponseEntity getCleanVolumeData(HttpServletRequest request,
                                             @RequestParam String jobId,
                                             @RequestParam(required = false) String wellId,
                                             @RequestParam(defaultValue = "JOB") ChemicalEnum chemicalEnum) {
        return widgetService.getCleanVolumeData(request, jobId, wellId, chemicalEnum);
    }

    @GetMapping("/material-usages")
    public ResponseEntity getMaterialData(HttpServletRequest request,
                                          @RequestParam String jobId,
                                          @RequestParam(required = false) String wellId,
                                          @RequestParam(required = false) Float stageNumber,
                                          @RequestParam(defaultValue = "WELL") ChemicalEnum chemicalEnum) {
        return widgetService.getMaterialUsagesData(request, jobId, wellId, stageNumber, chemicalEnum);
    }

    @GetMapping("/timeTracking")
    public ResponseEntity getTimeTracking(HttpServletRequest request, @RequestParam String jobId, @RequestParam(required = false) Integer day) {
        return widgetService.getTimeTracking(request,jobId,day);
    }

    @GetMapping("/materialInventory")
    public ResponseEntity getMaterialInventory(HttpServletRequest request, @RequestParam String jobId) {
        return widgetService.getMaterialInventory(request,jobId);
    }

    @PostMapping ("/materialNeeded")
    public ResponseEntity createMaterialNeeded(HttpServletRequest request, @RequestBody MaterialNeeded materialNeeded) {
        return widgetService.createMaterialNeeded(request,materialNeeded);
    }

    @GetMapping("/get")
    public ResponseEntity getMaterialNeeded(HttpServletRequest request, @RequestParam String jobId,@RequestParam(required = false) boolean isChemical) {
        return widgetService.getMaterialNeeded(request,jobId,isChemical);
    }
}
