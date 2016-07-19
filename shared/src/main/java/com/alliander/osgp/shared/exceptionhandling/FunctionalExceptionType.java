/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.shared.exceptionhandling;

public enum FunctionalExceptionType {
    // Organisation exceptions
    UNKNOWN_ORGANISATION(101, "UNKNOWN_ORGANISATION"),
    EXISTING_ORGANISATION(102, "EXISTING_ORGANISATION"),
    EXISTING_ORGANISATION_WITH_SAME_IDENTIFICATION(103, "EXISTING_ORGANISATION_WITH_SAME_IDENTIFICATION"),

    // Device exceptions
    UNKNOWN_DEVICE(201, "UNKNOWN_DEVICE"),
    UNREGISTERED_DEVICE(202, "UNREGISTERED_DEVICE"),
    UNSCHEDULED_DEVICE(203, "UNSCHEDULED_DEVICE"),
    EXISTING_DEVICE(204, "EXISTING_DEVICE"),
    PROTOCOL_UNKNOWN_FOR_DEVICE(205, "PROTOCOL_UNKNOWN_FOR_DEVICE"),
    UNKNOWN_PROTOCOL_NAME_OR_VERSION(206, "UNKNOWN_PROTOCOL_NAME_OR_VERSION"),

    // Authorization exceptions
    UNAUTHORIZED(301, "UNAUTHORIZED"),
    EXISTING_DEVICE_AUTHORIZATIONS(302, "EXISTING_DEVICE_AUTHORIZATIONS"),
    METHOD_NOT_ALLOWED_FOR_OWNER(303, "METHOD_NOT_ALLOWED_FOR_OWNER"),
    DEVICE_IN_MAINTENANCE(304, "DEVICE_IN_MAINTENANCE"),

    // Other exceptions
    VALIDATION_ERROR(401, "VALIDATION_ERROR"),
    TARIFF_SCHEDULE_NOT_ALLOWED_FOR_PSLD(402, "TARIFF_SCHEDULE_NOT_ALLOWED_FOR_PSLD"),
    ARGUMENT_NULL(403, "ARGUMENT_NULL"),
    JMS_TEMPLATE_NULL(404, "JMS_TEMPLATE_NULL"),
    UNKNOWN_CORRELATION_UID(405, "UNKNOWN_CORRELATION_UID"),
    LIGHT_SWITCHING_NOT_ALLOWED_FOR_RELAY(406, "LIGHT_SWITCHING_NOT_ALLOWED_FOR_RELAY"),
    UNSUPPORTED_DEVICE_ACTION(407, "UNSUPPORTED_DEVICE_ACTION"),
    ACTION_NOT_ALLOWED_FOR_LIGHT_RELAY(406, "ACTION_NOT_ALLOWED_FOR_LIGHT_RELAY"),
    ACTION_NOT_ALLOWED_FOR_TARIFF_RELAY(407, "ACTION_NOT_ALLOWED_FOR_TARIFF_RELAY"),

    // Manufacturer exceptions
    UNKNOWN_MANUFACTURER(501, "UNKNOWN_MANUFACTURER"),
    EXISTING_MANUFACTURER(502, "EXISTING_MANUFACTURER"),
    EXISTING_DEVICEMODEL_MANUFACTURER(503, "EXISTING_DEVICEMODEL_MANUFACTURER"),

    // DeviceModel exceptions
    UNKNOWN_DEVICEMODEL(601, "UNKNOWN_DEVICEMODEL"),
    EXISTING_DEVICEMODEL(602, "EXISTING_DEVICEMODEL"),
    EXISTING_DEVICE_DEVICEMODEL(603, "EXISTING_DEVICE_DEVICEMODEL"),
    EXISTING_DEVICEMODEL_FIRMWARE(604, "EXISTING_DEVICEMODEL_FIRMWARE"),

    // Firmware exceptions
    UNKNOWN_FIRMWARE(701, "UNKNOWN_FIRMWARE"),
    EXISTING_FIRMWARE(702, "EXISTING_FIRMWARE"),
    EXISTING_FIRMWARE_DEVICEFIRMWARE(703, "EXISTING_FIRMWARE_DEVICEFIRMWARE");

    private int code;
    private String message;

    private FunctionalExceptionType(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
