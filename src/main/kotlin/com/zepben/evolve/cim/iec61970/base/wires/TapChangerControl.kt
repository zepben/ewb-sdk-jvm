/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.wires

/**
 * Describes behaviour specific to tap changers, e.g. how the voltage at the end of a line varies with the load level and compensation of the voltage drop by
 * tap adjustment.
 *
 * @property limitVoltage Maximum allowed regulated voltage on the PT secondary, regardless of line drop compensation. Sometimes referred to as first-house protection.
 * @property lineDropCompensation If true, then line drop compensation is to be applied.
 * @property lineDropR Line drop compensator resistance setting for normal (forward) power flow in Ohms.
 * @property lineDropX Line drop compensator reactance setting for normal (forward) power flow in Ohms.
 * @property reverseLineDropR Line drop compensator resistance setting for reverse power flow in Ohms.
 * @property reverseLineDropX Line drop compensator reactance setting for reverse power flow in Ohms.
 * @property forwardLDCBlocking True implies this tap changer turns off/ignores reverse current flows for line drop compensation when power flow is reversed and
 * no reverse line drop is set.
 * @property timeDelay The time delay for the tap changer in seconds.
 * @property coGenerationEnabled True implies cogeneration mode is enabled and that the control will regulate to the new source bushing (downline bushing),
 * keeping locations downline from experiencing overvoltage situations.
 */
class TapChangerControl(mRID: String = "") : RegulatingControl(mRID) {

    var limitVoltage: Int? = null

    var lineDropCompensation: Boolean? = null
    var lineDropR: Double? = null
    var lineDropX: Double? = null
    var reverseLineDropR: Double? = null
    var reverseLineDropX: Double? = null

    var forwardLDCBlocking: Boolean? = null

    var timeDelay: Double? = null

    var coGenerationEnabled: Boolean? = null

}
