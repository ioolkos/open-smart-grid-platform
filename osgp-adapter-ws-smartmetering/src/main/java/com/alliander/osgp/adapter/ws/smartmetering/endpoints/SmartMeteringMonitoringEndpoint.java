/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.endpoints;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.alliander.osgp.adapter.ws.endpointinterceptors.OrganisationIdentification;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.AsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.AsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsGasAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsGasAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsGasRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsGasResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsGasAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsGasAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsGasRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsGasResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicReadsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ReadAlarmRegisterAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ReadAlarmRegisterAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ReadAlarmRegisterRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ReadAlarmRegisterResponse;
import com.alliander.osgp.adapter.ws.smartmetering.application.mapping.MonitoringMapper;
import com.alliander.osgp.adapter.ws.smartmetering.application.services.MonitoringService;
import com.alliander.osgp.adapter.ws.smartmetering.domain.entities.MeterResponseData;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;

@Endpoint
public class SmartMeteringMonitoringEndpoint extends SmartMeteringEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmartMeteringMonitoringEndpoint.class);
    private static final String SMARTMETER_MONITORING_NAMESPACE = "http://www.alliander.com/schemas/osgp/smartmetering/sm-monitoring/2014/10";

    @Autowired
    private MonitoringService monitoringService;

    @Autowired
    private MonitoringMapper monitoringMapper;

    public SmartMeteringMonitoringEndpoint() {
        // Empty constructor
    }

    @PayloadRoot(localPart = "PeriodicMeterReadsRequest", namespace = SMARTMETER_MONITORING_NAMESPACE)
    @ResponsePayload
    public PeriodicMeterReadsAsyncResponse getPeriodicMeterReads(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final PeriodicMeterReadsRequest request) throws OsgpException {

        LOGGER.debug("Incoming PeriodicMeterReadsRequest for meter: {}.", request.getDeviceIdentification());

        return (PeriodicMeterReadsAsyncResponse) this.getPeriodicAsyncResponseForEandG(organisationIdentification,
                request);
    }

    @PayloadRoot(localPart = "PeriodicMeterReadsGasRequest", namespace = SMARTMETER_MONITORING_NAMESPACE)
    @ResponsePayload
    public PeriodicMeterReadsGasAsyncResponse getPeriodicMeterReadsGas(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final PeriodicMeterReadsGasRequest request) throws OsgpException {

        LOGGER.debug("Incoming PeriodicMeterReadsGasRequest for meter: {}.", request.getDeviceIdentification());

        return (PeriodicMeterReadsGasAsyncResponse) this.getPeriodicAsyncResponseForEandG(organisationIdentification,
                request);
    }

    private AsyncResponse getPeriodicAsyncResponseForEandG(final String organisationIdentification,
            final PeriodicReadsRequest request) throws OsgpException {
        AsyncResponse response = null;
        try {
            final com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsQuery dataRequest = this.monitoringMapper
                    .map(request,
                            com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsQuery.class);

            final String correlationUid = this.monitoringService.enqueuePeriodicMeterReadsRequestData(
                    organisationIdentification, request.getDeviceIdentification(), dataRequest);

            response = request instanceof PeriodicMeterReadsRequest ? new PeriodicMeterReadsAsyncResponse()
            : new PeriodicMeterReadsGasAsyncResponse();
            response.setCorrelationUid(correlationUid);
            response.setDeviceIdentification(request.getDeviceIdentification());
        } catch (final Exception e) {
            LOGGER.error("Exception: {} while requesting meter reads for device: {} for organisation {}.",
                    new Object[] { e.getMessage(), request.getDeviceIdentification(), organisationIdentification }, e);

            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "PeriodicMeterReadsAsyncRequest", namespace = SMARTMETER_MONITORING_NAMESPACE)
    @ResponsePayload
    public PeriodicMeterReadsResponse getPeriodicMeterReadsResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final PeriodicMeterReadsAsyncRequest request) throws OsgpException {

        LOGGER.debug("Incoming PeriodicMeterReadsAsyncRequest for meter: {}.", request.getDeviceIdentification());

        PeriodicMeterReadsResponse response = null;
        try {
            final MeterResponseData meterResponseData = this.monitoringService
                    .dequeuePeriodicMeterReadsResponse(request.getCorrelationUid());

            if (OsgpResultType.NOT_OK == OsgpResultType.fromValue(meterResponseData.getResultType().getValue())) {
                if (meterResponseData.getMessageData() instanceof String) {
                    throw new TechnicalException(ComponentType.WS_SMART_METERING,
                            (String) meterResponseData.getMessageData(), null);
                } else {
                    throw new TechnicalException(ComponentType.WS_SMART_METERING,
                            "An exception occurred retrieving the periodic meter reads.", null);
                }
            }

            response = this.monitoringMapper.map(meterResponseData.getMessageData(),
                    com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsResponse.class);
        } catch (final Exception e) {
            this.handleRetrieveException(e, request, organisationIdentification);
        }
        return response;
    }

    @PayloadRoot(localPart = "PeriodicMeterReadsGasAsyncRequest", namespace = SMARTMETER_MONITORING_NAMESPACE)
    @ResponsePayload
    public PeriodicMeterReadsGasResponse getPeriodicMeterReadsGasResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final PeriodicMeterReadsGasAsyncRequest request) throws OsgpException {

        LOGGER.debug("Incoming PeriodicMeterReadsGasAsyncRequest for meter: {}.", request.getDeviceIdentification());

        PeriodicMeterReadsGasResponse response = null;
        try {
            final MeterResponseData meterResponseData = this.monitoringService
                    .dequeuePeriodicMeterReadsGasResponse(request.getCorrelationUid());

            if (OsgpResultType.NOT_OK == OsgpResultType.fromValue(meterResponseData.getResultType().getValue())) {
                if (meterResponseData.getMessageData() instanceof String) {
                    throw new TechnicalException(ComponentType.WS_SMART_METERING,
                            (String) meterResponseData.getMessageData(), null);
                } else {
                    throw new TechnicalException(ComponentType.WS_SMART_METERING,
                            "An exception occurred retrieving the periodic meter reads for gas.", null);
                }
            }

            response = this.monitoringMapper.map(meterResponseData.getMessageData(),
                    com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsGasResponse.class);
        } catch (final Exception e) {
            this.handleRetrieveException(e, request, organisationIdentification);
        }
        return response;
    }

    void handleRetrieveException(final Exception e, final AsyncRequest request, final String organisationIdentification)
            throws OsgpException {
        if (!(e instanceof FunctionalException)) {
            LOGGER.error("Exception: {} while sending PeriodicMeterReads of device: {} for organisation {}.",
                    new Object[] { e.getMessage(), request.getDeviceIdentification(), organisationIdentification });
        }

        this.handleException(e);
    }

    @PayloadRoot(localPart = "ActualMeterReadsRequest", namespace = SMARTMETER_MONITORING_NAMESPACE)
    @ResponsePayload
    public ActualMeterReadsAsyncResponse getActualMeterReads(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final ActualMeterReadsRequest request) throws OsgpException {

        final String deviceIdentification = request.getDeviceIdentification();

        LOGGER.debug("Incoming ActualMeterReadsRequest for meter: {}", deviceIdentification);

        return (ActualMeterReadsAsyncResponse) this.getActualAsyncResponseForEandG(organisationIdentification,
                deviceIdentification, false);
    }

    @PayloadRoot(localPart = "ActualMeterReadsGasRequest", namespace = SMARTMETER_MONITORING_NAMESPACE)
    @ResponsePayload
    public ActualMeterReadsGasAsyncResponse getActualMeterReadsGas(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final ActualMeterReadsGasRequest request) throws OsgpException {

        final String deviceIdentification = request.getDeviceIdentification();

        LOGGER.debug("Incoming ActualMeterReadsGasRequest for meter: {}", deviceIdentification);

        return (ActualMeterReadsGasAsyncResponse) this.getActualAsyncResponseForEandG(organisationIdentification,
                deviceIdentification, true);
    }

    private AsyncResponse getActualAsyncResponseForEandG(final String organisationIdentification,
            final String deviceIdentification, final boolean gas) throws OsgpException {
        AsyncResponse asyncResponse = null;
        try {
            final com.alliander.osgp.domain.core.valueobjects.smartmetering.ActualMeterReadsQuery requestValueObject = new com.alliander.osgp.domain.core.valueobjects.smartmetering.ActualMeterReadsQuery(
                    gas);

            final String correlationUid = this.monitoringService.enqueueActualMeterReadsRequestData(
                    organisationIdentification, deviceIdentification, requestValueObject);

            asyncResponse = gas ? new com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ObjectFactory()
            .createActualMeterReadsGasAsyncResponse()
            : new com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ObjectFactory()
            .createActualMeterReadsAsyncResponse();
            asyncResponse.setCorrelationUid(correlationUid);
            asyncResponse.setDeviceIdentification(deviceIdentification);

        } catch (final Exception e) {
            LOGGER.error("Exception: {} while requesting actual meter reads for device: {} for organisation {}.",
                    new Object[] { e.getMessage(), deviceIdentification, organisationIdentification }, e);

            this.handleException(e);
        }
        return asyncResponse;
    }

    @PayloadRoot(localPart = "ActualMeterReadsAsyncRequest", namespace = SMARTMETER_MONITORING_NAMESPACE)
    @ResponsePayload
    public ActualMeterReadsResponse getActualMeterReadsResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final ActualMeterReadsAsyncRequest request) throws OsgpException {

        LOGGER.debug("Incoming ActualMeterReadsAsyncRequest for meter: {}", request.getDeviceIdentification());

        ActualMeterReadsResponse response = null;
        try {
            final MeterResponseData meterResponseData = this.monitoringService.dequeueActualMeterReadsResponse(request
                    .getCorrelationUid());

            if (OsgpResultType.NOT_OK == OsgpResultType.fromValue(meterResponseData.getResultType().getValue())) {
                if (meterResponseData.getMessageData() instanceof String) {
                    throw new TechnicalException(ComponentType.WS_SMART_METERING,
                            (String) meterResponseData.getMessageData(), null);
                } else {
                    throw new TechnicalException(ComponentType.WS_SMART_METERING,
                            "An exception occurred retrieving the actual meter reads.", null);
                }
            }

            response = this.monitoringMapper.map(meterResponseData.getMessageData(), ActualMeterReadsResponse.class);
        } catch (final Exception e) {
            this.handleRetrieveException(e, request, organisationIdentification);
        }
        return response;
    }

    @PayloadRoot(localPart = "ActualMeterReadsGasAsyncRequest", namespace = SMARTMETER_MONITORING_NAMESPACE)
    @ResponsePayload
    public ActualMeterReadsGasResponse getActualMeterReadsGasResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final ActualMeterReadsGasAsyncRequest request) throws OsgpException {

        LOGGER.debug("Incoming ActualMeterReadsGasAsyncRequest for meter: {}", request.getDeviceIdentification());

        ActualMeterReadsGasResponse response = null;
        try {
            final MeterResponseData meterResponseData = this.monitoringService
                    .dequeueActualMeterReadsGasResponse(request.getCorrelationUid());

            if (OsgpResultType.NOT_OK == OsgpResultType.fromValue(meterResponseData.getResultType().getValue())) {
                if (meterResponseData.getMessageData() instanceof String) {
                    throw new TechnicalException(ComponentType.WS_SMART_METERING,
                            (String) meterResponseData.getMessageData(), null);
                } else {
                    throw new TechnicalException(ComponentType.WS_SMART_METERING,
                            "An exception occurred retrieving the actual meter reads for gas.", null);
                }
            }

            response = this.monitoringMapper.map(meterResponseData.getMessageData(), ActualMeterReadsGasResponse.class);
        } catch (final Exception e) {
            this.handleRetrieveException(e, request, organisationIdentification);
        }
        return response;
    }

    @PayloadRoot(localPart = "ReadAlarmRegisterRequest", namespace = SMARTMETER_MONITORING_NAMESPACE)
    @ResponsePayload
    public ReadAlarmRegisterAsyncResponse readAlarmRegister(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final ReadAlarmRegisterRequest request) throws OsgpException {

        LOGGER.info("Incoming ReadAlarmRegisterRequest for meter: {}", request.getDeviceIdentification());

        ReadAlarmRegisterAsyncResponse response = null;
        try {
            response = new ReadAlarmRegisterAsyncResponse();
            final com.alliander.osgp.domain.core.valueobjects.smartmetering.ReadAlarmRegisterRequest requestValueObject = this.monitoringMapper
                    .map(request,
                            com.alliander.osgp.domain.core.valueobjects.smartmetering.ReadAlarmRegisterRequest.class);

            final String correlationUid = this.monitoringService.enqueueReadAlarmRegisterRequestData(
                    organisationIdentification, request.getDeviceIdentification(), requestValueObject);

            final AsyncResponse asyncResponse = new AsyncResponse();
            asyncResponse.setCorrelationUid(correlationUid);
            asyncResponse.setDeviceIdentification(request.getDeviceIdentification());
            response.setAsyncResponse(asyncResponse);

        } catch (final Exception e) {
            LOGGER.error("Exception: {} while requesting read alarm register for device: {} for organisation {}.",
                    new Object[] { e.getMessage(), request.getDeviceIdentification(), organisationIdentification }, e);

            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "ReadAlarmRegisterAsyncRequest", namespace = SMARTMETER_MONITORING_NAMESPACE)
    @ResponsePayload
    public ReadAlarmRegisterResponse getReadAlarmRegisterResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final ReadAlarmRegisterAsyncRequest request) throws OsgpException {

        LOGGER.info("Incoming RetrieveReadAlarmRegisterRequest for meter: {}", request.getDeviceIdentification());

        ReadAlarmRegisterResponse response = null;
        try {
            response = new ReadAlarmRegisterResponse();
            final MeterResponseData meterResponseData = this.monitoringService.dequeueReadAlarmRegisterResponse(request
                    .getCorrelationUid());

            if (OsgpResultType.NOT_OK == OsgpResultType.fromValue(meterResponseData.getResultType().getValue())) {
                if (meterResponseData.getMessageData() instanceof String) {
                    throw new TechnicalException(ComponentType.WS_SMART_METERING,
                            (String) meterResponseData.getMessageData(), null);
                } else {
                    throw new TechnicalException(ComponentType.WS_SMART_METERING,
                            "An exception occurred retrieving the alarm register.", null);
                }
            }

            response.setAlarmRegister(this.monitoringMapper.map(meterResponseData.getMessageData(),
                    com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.AlarmRegister.class));

        } catch (final FunctionalException e) {
            throw e;
        } catch (final Exception e) {
            LOGGER.error(
                    "Exception: {} while sending RetrieveReadAlarmRegisterRequest of device: {} for organisation {}.",
                    new Object[] { e.getMessage(), request.getDeviceIdentification(), organisationIdentification });

            this.handleException(e);
        }
        return response;
    }
}
