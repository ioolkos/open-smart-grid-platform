/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.protocol.iec60870.testutils.factories;

import java.util.EnumMap;
import java.util.Map;

import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceType;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DomainInfo;

public class DomainInfoFactory {

    private static final DomainInfo DOMAIN_INFO_DISTRIBUTION_AUTOMATION = new DomainInfo("DISTRIBUTION_AUTOMATION",
            "1.0");
    private static final DomainInfo DOMAIN_INFO_PUBLIC_LIGHTING = new DomainInfo("PUBLIC_LIGHTING", "1.0");
    private static Map<DeviceType, DomainInfo> DEVICE_TYPE_DOMAIN_INFO_MAP = new EnumMap<>(DeviceType.class);
    static {
        DEVICE_TYPE_DOMAIN_INFO_MAP.put(DeviceType.DISTRIBUTION_AUTOMATION_DEVICE, DOMAIN_INFO_DISTRIBUTION_AUTOMATION);
        DEVICE_TYPE_DOMAIN_INFO_MAP.put(DeviceType.LIGHT_MEASUREMENT_DEVICE, DOMAIN_INFO_PUBLIC_LIGHTING);
        DEVICE_TYPE_DOMAIN_INFO_MAP.put(DeviceType.LIGHT_MEASUREMENT_GATEWAY, DOMAIN_INFO_PUBLIC_LIGHTING);
    }

    public static DomainInfo forDeviceType(final DeviceType deviceType) {
        return DEVICE_TYPE_DOMAIN_INFO_MAP.getOrDefault(deviceType, DOMAIN_INFO_DISTRIBUTION_AUTOMATION);
    }
}