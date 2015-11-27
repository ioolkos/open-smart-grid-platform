package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.ClientConnection;
import org.openmuc.jdlms.DataObject;
import org.openmuc.jdlms.GetRequestParameter;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodType;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReads;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsContainer;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestData;

@Component()
public class GetPeriodicMeterReadsCommandExecutor implements
        CommandExecutor<PeriodicMeterReadsRequestData, PeriodicMeterReadsContainer> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetPeriodicMeterReadsCommandExecutor.class);

    private static final int CLASS_ID_PROFILE_GENERIC = 7;
    private static final ObisCode OBIS_CODE_INTERVAL_BILLING = new ObisCode("1.0.99.1.0.255");
    private static final ObisCode OBIS_CODE_DAILY_BILLING = new ObisCode("1.0.99.2.0.255");
    private static final ObisCode OBIS_CODE_MONTHLY_BILLING = new ObisCode("0.0.98.1.0.255");
    private static final int ATTRIBUTE_ID_BUFFER = 2;

    private static final int BUFFER_INDEX_CLOCK = 0;
    private static final int BUFFER_INDEX_AMR_STATUS = 1;
    private static final int BUFFER_INDEX_A_POS_RATE_1 = 2;
    private static final int BUFFER_INDEX_A_POS_RATE_2 = 3;
    private static final int BUFFER_INDEX_A_NEG_RATE_1 = 4;
    private static final int BUFFER_INDEX_A_NEG_RATE_2 = 5;
    private static final int BUFFER_INDEX_A_POS = 2;
    private static final int BUFFER_INDEX_A_NEG = 3;

    @Autowired
    private DlmsHelperService dlmsHelperService;

    @Override
    public PeriodicMeterReadsContainer execute(final ClientConnection conn,
            final PeriodicMeterReadsRequestData periodicMeterReadsRequest) throws IOException, ProtocolAdapterException {

        final PeriodType periodType;
        final DateTime beginDateTime;
        final DateTime endDateTime;
        if (periodicMeterReadsRequest != null) {
            periodType = periodicMeterReadsRequest.getPeriodType();
            beginDateTime = new DateTime(periodicMeterReadsRequest.getBeginDate());
            endDateTime = new DateTime(periodicMeterReadsRequest.getEndDate());
        } else {
            periodType = PeriodType.MONTHLY;
            beginDateTime = DateTime.now();
            endDateTime = DateTime.now();
        }

        final GetRequestParameter profileBuffer = this.getProfileBuffer(periodType);

        LOGGER.debug(
                "Retrieving current billing period and profiles for class id: {}, obis code: {}, attribute id: {}",
                profileBuffer.classId(), profileBuffer.obisCode(), profileBuffer.attributeId());

        // we retrieve it all and filter results based on request data later on
        final List<GetResult> getResultList = conn.get(profileBuffer);

        checkResultList(getResultList);

        final List<PeriodicMeterReads> periodicMeterReads = new ArrayList<>();

        final GetResult getResult = getResultList.get(0);
        final AccessResultCode resultCode = getResult.resultCode();
        LOGGER.info("AccessResultCode: {}({})", resultCode.name(), resultCode.value());
        final DataObject resultData = getResult.resultData();
        LOGGER.debug(this.dlmsHelperService.getDebugInfo(resultData));
        final List<DataObject> bufferedObjectsList = resultData.value();

        final int beginYear = beginDateTime.getYear();
        final int beginMonthOfYear = beginDateTime.getMonthOfYear();
        final int beginDayOfMonth = beginDateTime.getDayOfMonth();
        final int endYear = endDateTime.getYear();
        final int endMonthOfYear = endDateTime.getMonthOfYear();
        final int endDayOfMonth = endDateTime.getDayOfMonth();

        for (final DataObject bufferedObject : bufferedObjectsList) {

            final List<DataObject> bufferedObjects = bufferedObject.value();

            final DataObject clock = bufferedObjects.get(BUFFER_INDEX_CLOCK);
            final DateTime bufferedDateTime = this.dlmsHelperService.fromDateTimeValue((byte[]) clock.value());

            final boolean useBufferedObject = useBufferedObject(beginYear, beginMonthOfYear, beginDayOfMonth, endYear,
                    endMonthOfYear, endDayOfMonth, bufferedDateTime);

            if (useBufferedObject) {
                LOGGER.debug("Using object from capture buffer, because the date matches the given period.");
            } else {
                LOGGER.debug("Not using an object from capture buffer, because the date does not match the given period.");
                continue;
            }

            LOGGER.debug("clock: {}", this.dlmsHelperService.getDebugInfo(clock));

            final DataObject amrStatus = bufferedObjects.get(BUFFER_INDEX_AMR_STATUS);
            LOGGER.debug("Skipping amrStatus ({}) and M-Bus values.", this.dlmsHelperService.getDebugInfo(amrStatus));
            /*
             * for DAILY and MONTHLY we have 4 entries (2 pos, 2 neg), for
             * INTERVAL only 2 (1 pos, 1 neg)
             */
            final boolean interval = periodType == PeriodType.INTERVAL;

            final DataObject positiveActiveEnergyTariff1 = bufferedObjects.get(interval ? BUFFER_INDEX_A_POS
                    : BUFFER_INDEX_A_POS_RATE_1);
            LOGGER.debug("positiveActiveEnergyTariff1: {}",
                    this.dlmsHelperService.getDebugInfo(positiveActiveEnergyTariff1));
            final DataObject positiveActiveEnergyTariff2 = interval ? null : bufferedObjects
                    .get(BUFFER_INDEX_A_POS_RATE_2);
            this.logPositiveActiveEnergyTariff2(positiveActiveEnergyTariff2);
            final DataObject negativeActiveEnergyTariff1 = bufferedObjects.get(interval ? BUFFER_INDEX_A_NEG
                    : BUFFER_INDEX_A_NEG_RATE_1);
            LOGGER.debug("negativeActiveEnergyTariff1: {}",
                    this.dlmsHelperService.getDebugInfo(negativeActiveEnergyTariff1));
            final DataObject negativeActiveEnergyTariff2 = interval ? null : bufferedObjects
                    .get(BUFFER_INDEX_A_NEG_RATE_2);
            this.logNegativeActiveEnergyTariff2(negativeActiveEnergyTariff2);

            final PeriodicMeterReads nextPeriodicMeterReads = new PeriodicMeterReads(bufferedDateTime.toDate(),
                    (Long) positiveActiveEnergyTariff1.value(), getActiveEnergyTariff2(interval,
                            positiveActiveEnergyTariff2), (Long) negativeActiveEnergyTariff1.value(),
                    getActiveEnergyTariff2(interval, negativeActiveEnergyTariff2), periodType);
            periodicMeterReads.add(nextPeriodicMeterReads);
        }

        return new PeriodicMeterReadsContainer(periodicMeterReadsRequest.getDeviceIdentification(), periodicMeterReads);
    }

    private void logNegativeActiveEnergyTariff2(final DataObject negativeActiveEnergyTariff2) {
        if (negativeActiveEnergyTariff2 != null) {
            LOGGER.debug("negativeActiveEnergyTariff2: {}",
                    this.dlmsHelperService.getDebugInfo(negativeActiveEnergyTariff2));
        }
    }

    private void logPositiveActiveEnergyTariff2(final DataObject positiveActiveEnergyTariff2) {
        if (positiveActiveEnergyTariff2 != null) {
            LOGGER.debug("positiveActiveEnergyTariff2: {}",
                    this.dlmsHelperService.getDebugInfo(positiveActiveEnergyTariff2));
        }
    }

    private static Long getActiveEnergyTariff2(final boolean interval, final DataObject positiveActiveEnergyTariff2) {
        return interval ? null : (Long) positiveActiveEnergyTariff2.value();
    }

    private static boolean useBufferedObject(final int beginYear, final int beginMonthOfYear,
            final int beginDayOfMonth, final int endYear, final int endMonthOfYear, final int endDayOfMonth,
            final DateTime bufferedDateTime) {
        final boolean checkBegin = beginYear <= bufferedDateTime.getYear()
                && beginMonthOfYear <= bufferedDateTime.getMonthOfYear()
                && beginDayOfMonth <= bufferedDateTime.getDayOfMonth();
        final boolean checkEnd = endYear >= bufferedDateTime.getYear()
                && endMonthOfYear >= bufferedDateTime.getMonthOfYear()
                && endDayOfMonth >= bufferedDateTime.getDayOfMonth();

        return checkBegin && checkEnd;
    }

    private static void checkResultList(final List<GetResult> getResultList) throws ProtocolAdapterException {
        if (getResultList.isEmpty()) {
            throw new ProtocolAdapterException(
                    "No GetResult received while retrieving current billing period and profiles.");
        }

        if (getResultList.size() > 1) {
            LOGGER.info("Expected 1 GetResult while retrieving current billing period and profiles, got "
                    + getResultList.size());
        }
    }

    private GetRequestParameter getProfileBuffer(final PeriodType periodType) throws ProtocolAdapterException {
        GetRequestParameter profileBuffer;

        switch (periodType) {
        case INTERVAL:
            profileBuffer = new GetRequestParameter(CLASS_ID_PROFILE_GENERIC, OBIS_CODE_INTERVAL_BILLING,
                    ATTRIBUTE_ID_BUFFER);
            break;
        case DAILY:
            profileBuffer = new GetRequestParameter(CLASS_ID_PROFILE_GENERIC, OBIS_CODE_DAILY_BILLING,
                    ATTRIBUTE_ID_BUFFER);
            break;
        case MONTHLY:
            profileBuffer = new GetRequestParameter(CLASS_ID_PROFILE_GENERIC, OBIS_CODE_MONTHLY_BILLING,
                    ATTRIBUTE_ID_BUFFER);
            break;
        default:
            throw new ProtocolAdapterException(String.format("periodtype %s not supported", periodType));
        }
        return profileBuffer;
    }

}
