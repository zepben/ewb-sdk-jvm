/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.auxiliaryequipment

/**
 * Instrument transformer used to measure electrical qualities of the circuit that is being protected and/or monitored.
 * Typically used as current transducer for the purpose of metering or protection.
 * A typical secondary current rating would be 5A.
 */
class CurrentTransformer @JvmOverloads constructor(mRID: String = "") : Sensor(mRID) {

    /**
     * Power burden of the CT core in watts.
     */
    var coreBurden: Int? = null

}
