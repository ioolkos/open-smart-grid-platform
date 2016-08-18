/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ObisCodeValuesDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SpecificConfigurationObjectRequestDto;

@Component
public class GetSpecificConfigurationObjectCommandExecutor extends
        AbstractCommandExecutor<SpecificConfigurationObjectRequestDto, String> {

    @Autowired
    private DlmsHelperService dlmsHelper;

    private static final Logger LOGGER = LoggerFactory.getLogger(GetSpecificConfigurationObjectCommandExecutor.class);

    public GetSpecificConfigurationObjectCommandExecutor() {
        super(SpecificConfigurationObjectRequestDto.class);
    }

    @Override
    public ActionResponseDto asBundleResponse(final String executionResult) throws ProtocolAdapterException {
        return new ActionResponseDto(executionResult);
    }

    @Override
    public String execute(final DlmsConnection conn, final DlmsDevice device,
            final SpecificConfigurationObjectRequestDto requestData) throws ProtocolAdapterException {

        final ObisCodeValuesDto obisCodeValues = requestData.getObisCode();
        final byte[] obisCodeBytes = { obisCodeValues.getA(), obisCodeValues.getB(), obisCodeValues.getC(),
                obisCodeValues.getD(), obisCodeValues.getE(), obisCodeValues.getF() };
        final ObisCode obisCode = new ObisCode(obisCodeBytes);

        LOGGER.debug("Get specific configuration object for class id: {}, obis code: {}, attribute id: {}",
                requestData.getClassId(), obisCode, requestData.getAttribute());

        final AttributeAddress attributeAddress = new AttributeAddress(requestData.getClassId(), obisCode,
                requestData.getAttribute());

        final DataObject attributeValue = this.dlmsHelper.getAttributeValue(conn, attributeAddress);
        return this.dlmsHelper.getDebugInfo(attributeValue);
    }

}
