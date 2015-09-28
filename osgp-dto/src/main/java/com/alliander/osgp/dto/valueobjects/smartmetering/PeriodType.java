/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

public enum PeriodType {

    DAILY,
    MONTHLY;

    public String value() {
        return this.name();
    }

    public static PeriodType fromValue(final String v) {
        return valueOf(v);
    }

}
