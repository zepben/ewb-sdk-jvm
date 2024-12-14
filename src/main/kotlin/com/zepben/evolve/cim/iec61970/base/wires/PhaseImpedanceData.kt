/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.wires

/**
 * Impedance and conductance matrix element values. The diagonal elements are described by the elements having the same toPhase and fromPhase value and the off
 * diagonal elements have different toPhase and fromPhase values.
 *
 * @property fromPhase Refer to the class description.
 * @property toPhase Refer to the class description.
 * @property b Susceptance matrix element value, per length of unit.
 * @property g Conductance matrix element value, per length of unit.
 * @property r Resistance matrix element value, per length of unit.
 * @property x Reactance matrix element value, per length of unit.
 */
data class PhaseImpedanceData(
    val fromPhase: SinglePhaseKind,
    val toPhase: SinglePhaseKind,
    val b: Double? = null,
    val g: Double? = null,
    val r: Double? = null,
    val x: Double? = null
)
