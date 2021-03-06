/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.integrationtests;

import static org.mockito.Mockito.reset;

import org.openmuc.j60870.Connection;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.DeviceResponseMessageSender;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.LogItemRequestMessageSender;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.OsgpRequestMessageSender;
import org.opensmartgridplatform.adapter.protocol.iec60870.integrationtests.config.InboundRequestsTestConfiguration;
import org.opensmartgridplatform.adapter.protocol.iec60870.integrationtests.config.LogItemTestConfiguration;
import org.opensmartgridplatform.adapter.protocol.iec60870.integrationtests.config.OutboundRequestsTestConfiguration;
import org.opensmartgridplatform.adapter.protocol.iec60870.integrationtests.config.OutboundResponsesTestConfiguration;
import org.opensmartgridplatform.adapter.protocol.iec60870.integrationtests.config.TestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;

import io.cucumber.java.Before;

@ContextConfiguration(classes = { TestConfiguration.class, InboundRequestsTestConfiguration.class,
        OutboundResponsesTestConfiguration.class, OutboundRequestsTestConfiguration.class,
        LogItemTestConfiguration.class })
public class TestContextConfiguration {

    @Autowired
    private LogItemRequestMessageSender logItemRequestMessageSenderMock;

    @Autowired
    @Qualifier("protocolIec60870OutboundOsgpCoreResponsesMessageSender")
    private DeviceResponseMessageSender responseMessageSenderMock;

    @Autowired
    @Qualifier("protocolIec60870OutboundOsgpCoreRequestsMessageSender")
    public OsgpRequestMessageSender osgpRequestMessageSenderMock;

    @Autowired
    private Connection connection;

    @Before
    public void resetContext() {
        reset(this.logItemRequestMessageSenderMock, this.responseMessageSenderMock, this.osgpRequestMessageSenderMock,
                this.connection);
    }
}
