/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.wires

/**
 * A variable impedance device normally used to offset line charging during single line faults in an ungrounded section of network.
 *
 * @property xGroundNominal The nominal reactance. This is the operating point (normally over compensation) that is defined based on the resonance point in the
 * healthy network condition. The impedance is calculated based on nominal voltage divided by position current.
 */
class PetersenCoil @JvmOverloads constructor(mRID: String = "") : EarthFaultCompensator(mRID) {

    var xGroundNominal: Double? = null

}