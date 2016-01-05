# Protocol Adapter for Device Language Message Specification protocol

### Build Status

[![Build Status](http://54.77.62.182/job/OSGP_Protocol-Adapter-DLMS_development/badge/icon?style=plastic)](http://54.77.62.182/job/OSGP_Protocol-Adapter-DLMS_development)

### Component Description

These components offer an implementation of DLMS. At the moment, it can send and receive jms message from and to the OSGP.

- osgp-device-simulator-dlms, DLMS device simulator
- osgp-dlms, Implementation of DLMS
- osgp-adapter-protocol-dlms, Protocol Adapter

The components have dependencies.

- shared, Common classes used by the Protocol Adapter and Device Simulator
- osgp-dto, Data Transfer Objects

### GPLv3 OpenMUC jDLMS Library

To communicate with Smart Meters that understand DLMS/COSEM you will need to include the jDLMS library.

The jDLMS library is developed by the department "Intelligent Energy Systems" at the Fraunhofer Institute for Solar Energy Systems in Freiburg, Germany.

jDLMS is licensed under the GPLv3, more information can be found on the OpenMUC web site: https://www.openmuc.org/index.php?id=42
