package com.carbo.job.utils;

import com.carbo.job.model.DiscountAuditDetails;
import com.carbo.job.model.Job;
import com.carbo.job.model.Pad;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;
import java.security.Principal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import com.carbo.job.model.widget.AuditDetails;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class ControllerUtil {
    public static String getOrganizationId(HttpServletRequest request) {
        return extractFieldValueFromRequest(request, "organizationId").get();
    }

    public static String getOrganization(HttpServletRequest request) {
        return extractFieldValueFromRequest(request, "organization").get();
    }

    public static Optional<String> getDistrictId(HttpServletRequest request) {
        return extractFieldValueFromRequest(request, "districtId");
    }

    public static List<String> getDistrictIds(HttpServletRequest request) {
        return extractDistrictIds(request, "districtIds");
    }

    public static String getUserFullName(HttpServletRequest request) {
        return extractFieldValueFromRequest(request, "fullName").get();
    }

    public static String getUserId(HttpServletRequest request) {
        return extractFieldValueFromRequest(request, "id").get();
    }

    private static Optional<String> extractFieldValueFromRequest(HttpServletRequest request, String fieldName) {
        Principal principal = request.getUserPrincipal();

        if (principal instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken oauth2AuthToken = (JwtAuthenticationToken) principal;
            Map<String, Object> details = (Map<String, Object>) oauth2AuthToken.getDetails();
            Object value = details.get(fieldName);
            return value == null ? Optional.empty() : Optional.of(value.toString());
        }

        return Optional.empty(); // Return empty if not an OAuth2AuthenticationToken
    }

    private static List<String> extractDistrictIds(HttpServletRequest request, String fieldName) {
        Principal principal = request.getUserPrincipal();

        if (principal instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken oauth2AuthToken = (JwtAuthenticationToken) principal;
            Map<String, Object> details = (Map<String, Object>) oauth2AuthToken.getDetails();
            Object value = details.get(fieldName);

            if (value instanceof List) {
                return (List<String>) value;
            }
        }

        return List.of(); // Return an empty list if the principal is not OAuth2AuthenticationToken or value is not a List
    }

    public static String getOrganizationType(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();

        if (principal instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken oauth2AuthToken = (JwtAuthenticationToken) principal;
            Map<String, Object> details = (Map<String, Object>) oauth2AuthToken.getDetails();
            Object value = details.get("organizationType");

            return value != null ? value.toString() : ""; // Return empty string if the value is null
        }

        return ""; // Return empty string if the principal is not OAuth2AuthenticationToken
    }

    public static String getUserName(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();

        if (principal instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken oauth2AuthToken = (JwtAuthenticationToken) principal;
            Map<String, Object> details = (Map<String, Object>) oauth2AuthToken.getDetails();
            Object value = details.get("userName");

            return value != null ? value.toString() : ""; // Return empty string if the value is null
        }

        return ""; // Return empty string if the principal is not OAuth2AuthenticationToken
    }

    public static String getRoles (HttpServletRequest request) {
        return extractFieldValueFromRequest(request, "authorities").get();
    }

    /**
     * @param requestObject
     * @param requestBodyClass
     * @return String
     * @throws JAXBException
     * @author Lakshya Soni
     * Purpose of this method is to convert the Java Object to XML String
     */
    @SuppressWarnings("rawtypes")
    public static String convertObjectToXml(Object requestObject, Class requestBodyClass) throws JAXBException {

        if (requestObject == null) {
            return null;
        } else {
            JAXBContext jContext = JAXBContext.newInstance(requestBodyClass);
            Marshaller marshallObj = jContext.createMarshaller();

            marshallObj.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            StringWriter sw = new StringWriter();
            marshallObj.marshal(requestObject, sw);

            return sw.toString();
        }

    }// convertObjectToXml

    /**
     * @param value
     * @return true if value is not empty else false
     */
    public static boolean isNullOrEmpty(Object value) {
        return value != null && !value.toString().trim().isEmpty();
    }

    public static AuditDetails setAuditDetails (HttpServletRequest request, AuditDetails auditDetails){
        // Check if the provided auditDetails object is null.
        if (auditDetails == null) {
            // If it is null, create a new AuditDetails object and set the created time and created by.
            auditDetails = new AuditDetails();
            auditDetails.setCreatedTime(System.currentTimeMillis() / 1000);
            auditDetails.setCreatedBy(getUserName(request));
        }
        // Set the modified time and modified by with the current timestamp and the authenticated user's name.
        auditDetails.setModifiedTime(System.currentTimeMillis() / 1000);
        auditDetails.setModifiedBy(getUserName(request));
        // Return the updated AuditDetails object with the audit details set.
        return auditDetails;
    }

    public static DiscountAuditDetails setDiscountAuditDetails(HttpServletRequest request, DiscountAuditDetails auditDetails, String stageDiscountAppliedFor) {

        auditDetails.setCreatedTime(System.currentTimeMillis() / 1000);
        auditDetails.setCreatedBy(getUserName(request));
        auditDetails.setStageDiscountFor(stageDiscountAppliedFor);

        return auditDetails;
    }

    public static double roundValue(double value, int places) {
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static Integer getCurDay(Long jobStartDate, ZoneId zone) {
        ZonedDateTime previousDate = Instant.now().atZone(zone).minusDays(1);
        LocalDate startDate = Instant.ofEpochMilli(jobStartDate).atZone(zone).toLocalDate();
        //calculating number of days in between
        long noOfDaysBetween = ChronoUnit.DAYS.between(startDate, previousDate);
        return (int) noOfDaysBetween + 1;
    }
    public static Map<String, String> getHeadersInfo(HttpServletRequest request) {

        Map<String, String> map = new HashMap<String, String>();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        return map;
    }
}
