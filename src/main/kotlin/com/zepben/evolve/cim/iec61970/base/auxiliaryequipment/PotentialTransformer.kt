/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.auxiliaryequipment

/**
 * Instrument transformer (also known as Voltage Transformer) used to measure electrical qualities of the circuit that
 * is being protected and/or monitored. Typically used as voltage transducer for the purpose of metering, protection, or
 * sometimes auxiliary substation supply. A typical secondary voltage rating would be 120V.
 *
 * @property type Potential transformer construction type.
 */
class PotentialTransformer @JvmOverloads constructor(mRID: String = "") : Sensor(mRID) {

    var type: PotentialTransformerKind = PotentialTransformerKind.UNKNOWN

}
