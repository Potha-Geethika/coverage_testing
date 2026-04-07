package com.carbo.job.constants;

public enum ErrorCodes {
    WELL_NOT_NOT_FOUND("1000", "There is no Well in the system based on the provided wellAPI"),
    WELL_INFO_NOT_FOUND("1001", "There is no Well-Infos entry in the system based on the provided wellAPI "),
    WELL_INFO_STAGE_OBJECT_NOT_FOUND("1002", "There is no Stage Object in the Well-Infos entry in the system"),
    WELL_INFO_STAGE_ENTRY_NOT_FOUND("1003", "There is no Stage entry in the Well-Infos document in the system based on the provided wellAPI"),
    DIESEL_MUST_BE_POSITIVE_VALUE("1004", "Diesel value must be positive"),
    FIELD_GAS_MUST_BE_POSITIVE_VALUE("1005", "Field Gas value must be positive"),

    WELL_STAGE_NOT_NOT_FOUND("1006", "There is no Stage Object in the Well entry in the system"),
    CNG_MUST_BE_POSITIVE_VALUE("1007", "CNG value must be positive"),
    JOB_NOT_FOUND("1008","Job not found for job number/nav id"),
    ORDER_ID_MISMATCHED("1009","Order ID should be 3,4 or 6"),

    PROPPANT_NOT_FOUND("1010", "Proppant not found for given PO"),
    AUTOMATIZE_TOGGLE_OFF("1011", "Automatize toggle off for given job"),
    AUTOMATIZE_TOGGLE_OFF_IN_GENERAL_SETTING("1012", "Automatize toggle off in general setting"),
    GENERAL_SETTING_DATA_NOT_FOUND("1013", "General setting data not found"),
    DELIVERY_ALREADY_USED("1014", "delivery already used,please create new delivery"),
    DEMO_DATA_DELETED("1015", "The demo data is deleted successfully."),
    ALREADY_EXISTS_AUTO_ORDER_ID("1016", "Record already exists with this auto order id"),


    ;

    private final String errorCode;
    private String errorMessage;


    ErrorCodes(final String code, final String errorMessage) {
        this.errorCode = code;
        this.errorMessage = errorMessage;
    }

    public String getCode() {
        return errorCode;
    }

    public String getMessage() {
        return errorMessage;
    }

}