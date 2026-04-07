package com.carbo.job.model;

public enum ConsumableTypesEnum {
    SEATS("Seats"),
    VALVES("Valves"),
    PACKING("Packing"),
    PLUNGERS("Plungers"),
    POWEREND("Power End"),
    FLUIDEND("Fluid End"),
    OILCHANGE("Oil Change");

    private final String value;

    ConsumableTypesEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ConsumableTypesEnum consumableTypesEnumValue(String consumableType) {
        for (ConsumableTypesEnum consumableTypesEnum : ConsumableTypesEnum.values()) {
            if (consumableTypesEnum.value.equalsIgnoreCase(consumableType)) {
                return consumableTypesEnum;
            }
        }
        return null;
    }
}
