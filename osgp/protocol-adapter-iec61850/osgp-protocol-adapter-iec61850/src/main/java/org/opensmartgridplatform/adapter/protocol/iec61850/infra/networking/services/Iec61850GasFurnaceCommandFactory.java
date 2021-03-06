/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import org.opensmartgridplatform.adapter.protocol.iec61850.device.rtu.RtuReadCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import org.opensmartgridplatform.dto.valueobjects.microgrids.MeasurementDto;

@Component
public class Iec61850GasFurnaceCommandFactory extends AbstractIec61850RtuReadCommandFactory {

    private static final int TEMPERATURE_ID_START = 1;
    private static final int TEMPERATURE_ID_END = 2;
    private static final int MATERIAL_ID_START = 1;
    private static final int MATERIAL_ID_END = 2;

    public Iec61850GasFurnaceCommandFactory() {
        super(rtuCommandMap(), dataAttributesUsingFilterId());
    }

    private static final Set<DataAttribute> dataAttributesUsingFilterId() {
        return EnumSet.of(DataAttribute.TEMPERATURE, DataAttribute.MATERIAL_STATUS, DataAttribute.MATERIAL_TYPE,
                DataAttribute.MATERIAL_FLOW);
    }

    private static Map<String, RtuReadCommand<MeasurementDto>> rtuCommandMap() {

        final CommandsByAttributeBuilder builder = new CommandsByAttributeBuilder();

        final Set<DataAttribute> simpleCommandAttributes = EnumSet.of(DataAttribute.BEHAVIOR, DataAttribute.HEALTH,
                DataAttribute.MODE, DataAttribute.ALARM_ONE, DataAttribute.ALARM_TWO, DataAttribute.ALARM_THREE,
                DataAttribute.ALARM_FOUR, DataAttribute.ALARM_OTHER, DataAttribute.WARNING_ONE,
                DataAttribute.WARNING_TWO, DataAttribute.WARNING_THREE, DataAttribute.WARNING_FOUR,
                DataAttribute.WARNING_OTHER);
        builder.withSimpleCommandsFor(simpleCommandAttributes);

        final Set<DataAttribute> temperatureCommandAttributes = EnumSet.of(DataAttribute.TEMPERATURE);
        builder.withIndexedCommandsFor(temperatureCommandAttributes, TEMPERATURE_ID_START, TEMPERATURE_ID_END);

        final Set<DataAttribute> materialCommandAttributes = EnumSet.of(DataAttribute.MATERIAL_STATUS,
                DataAttribute.MATERIAL_TYPE, DataAttribute.MATERIAL_FLOW);
        builder.withIndexedCommandsFor(materialCommandAttributes, MATERIAL_ID_START, MATERIAL_ID_END);

        return builder.build();
    }

}
