/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.wires

/**
 * A wire or combination of wires, with consistent electrical characteristics, building a single electrical system, used to carry alternating current
 * between points in the power system.
 *
 * For symmetrical, transposed 3ph lines, it is sufficient to use  attributes of the line segment, which describe impedances and admittances for the
 * entire length of the segment. Additionally, impedances can be computed by using length and associated per length impedances.
 *
 * The BaseVoltage at the two ends of ACLineSegments in a Line shall have the same BaseVoltage.nominalVoltage. However, boundary lines  may have
 * slightly different BaseVoltage.nominalVoltages and  variation is allowed. Larger voltage difference in general requires use of an equivalent branch.
 *
 * @property perLengthImpedance Per-length impedance of this line segment.
 * @property perLengthPhaseImpedance Per-length phase impedance of this line segment.
 * @property perLengthSequenceImpedance Per-length sequence impedance of this line segment.
 */
class AcLineSegment @JvmOverloads constructor(mRID: String = "") : Conductor(mRID) {

    var perLengthImpedance: PerLengthImpedance? = null

    var perLengthSequenceImpedance: PerLengthSequenceImpedance?
        get() = perLengthImpedance.takeIf { it is PerLengthSequenceImpedance } as? PerLengthSequenceImpedance
        set(it) {
            perLengthImpedance = it
        }

    var perLengthPhaseImpedance: PerLengthPhaseImpedance?
        get() = perLengthImpedance.takeIf { it is PerLengthPhaseImpedance } as? PerLengthPhaseImpedance
        set(it) {
            perLengthImpedance = it
        }
}
