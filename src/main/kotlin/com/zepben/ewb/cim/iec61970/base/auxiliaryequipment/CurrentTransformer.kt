/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.auxiliaryequipment

import com.zepben.ewb.cim.iec61968.infiec61968.infassetinfo.CurrentTransformerInfo

/**
 * Instrument transformer used to measure electrical qualities of the circuit that is being protected and/or monitored.
 * Typically used as current transducer for the purpose of metering or protection.
 * A typical secondary current rating would be 5A.
 *
 * @property assetInfo Datasheet information for this current transformer.
 * @property coreBurden Power burden of the CT core in watts.
 */
class CurrentTransformer @JvmOverloads constructor(mRID: String = "") : Sensor(mRID) {

    override var assetInfo: CurrentTransformerInfo? = null

    var coreBurden: Int? = null

}
