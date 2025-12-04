/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.metering

/**
 * Physical asset that performs the metering role of the usage point. Used for measuring consumption and detection of events.
 *
 * @property companyMeterId Helper function for getting the company specific meter ID, which is stored as the name of the meter.
 */
class Meter(mRID: String) : EndDevice(mRID) {

    var companyMeterId: String?
        get() = name
        set(value) {
            name = value
        }
}
