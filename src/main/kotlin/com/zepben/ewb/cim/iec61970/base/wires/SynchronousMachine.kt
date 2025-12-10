/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

import com.zepben.ewb.services.common.extensions.asUnmodifiable
import com.zepben.ewb.services.common.extensions.getByMRID
import com.zepben.ewb.services.common.extensions.validateReference
import com.zepben.ewb.testing.MRIDListWrapper

/**
 * An electromechanical device that operates with shaft rotating synchronously with the network. It is a single machine operating either as a generator or
 * synchronous condenser or pump.
 *
 * @property baseQ Default base reactive power value in VAr. This value represents the initial reactive power that can be used by any application function.
 * @property condenserP Active power consumed (watts) when in condenser mode operation.
 * @property earthing Indicates whether the generator is earthed. Used for short circuit data exchange according to IEC 60909.
 * @property earthingStarPointR Generator star point earthing resistance in Ohms (Re). Used for short circuit data exchange according to IEC 60909.
 * @property earthingStarPointX Generator star point earthing reactance in Ohms (Xe). Used for short circuit data exchange according to IEC 60909.
 * @property ikk Steady-state short-circuit current (in A for the profile) of generator with compound excitation during 3-phase short circuit.
 *               - Ikk=0: Generator with no compound excitation. - Ikk<>0: Generator with compound excitation.
 *               Ikk is used to calculate the minimum steady-state short-circuit current for generators with compound excitation.
 *               (4.6.1.2 in IEC 60909-0:2001). Used only for single fed short circuit on a generator. (4.3.4.2. in IEC 60909-0:2001).
 * @property maxQ Maximum reactive power limit in VAr. This is the maximum (nameplate) limit for the unit.
 * @property maxU Maximum voltage limit for the unit in volts.
 * @property minQ Minimum reactive power limit for the unit in VAr.
 * @property minU Minimum voltage limit for the unit in volts.
 * @property mu Factor to calculate the breaking current (Section 4.5.2.1 in IEC 60909-0).
 *              Used only for single fed short circuit on a generator (Section 4.3.4.2. in IEC 60909-0).
 * @property r Equivalent resistance (RG) of generator as a percentage. RG is considered for the calculation of all currents,
 *             except for the calculation of the peak current ip. Used for short circuit data exchange according to IEC 60909.
 * @property r0 Zero sequence resistance of the synchronous machine as a percentage.
 * @property r2 Negative sequence resistance as a percentage.
 * @property satDirectSubtransX Direct-axis subtransient reactance saturated as a percentage, also known as Xd"sat.
 * @property satDirectSyncX Direct-axes saturated synchronous reactance (xdsat); reciprocal of short-circuit ration, as a percentage.
 *                          Used for short circuit data exchange, only for single fed short circuit on a generator. (4.3.4.2. in IEC 60909-0:2001).
 * @property satDirectTransX Saturated Direct-axis transient reactance as a percentage.
 *                           The attribute is primarily used for short circuit calculations according to ANSI.
 * @property x0 Zero sequence reactance of the synchronous machine as a percentage.
 * @property x2 Negative sequence reactance as a percentage.
 * @property type Modes that this synchronous machine can operate in.
 * @property operatingMode Current mode of operation.
 */
class SynchronousMachine @JvmOverloads constructor(mRID: String = "") : RotatingMachine(mRID) {

    private var _reactiveCapabilityCurves: MutableList<ReactiveCapabilityCurve>? = null

    var baseQ: Double? = null
    var condenserP: Int? = null
    var earthing: Boolean? = false
    var earthingStarPointR: Double? = null
    var earthingStarPointX: Double? = null
    var ikk: Double? = null

    var maxQ: Double? = null
    var maxU: Int? = null
    var minQ: Double? = null
    var minU: Int? = null

    var mu: Double? = null
    var r: Double? = null
    var r0: Double? = null
    var r2: Double? = null

    var satDirectSubtransX: Double? = null
    var satDirectSyncX: Double? = null
    var satDirectTransX: Double? = null

    var x0: Double? = null
    var x2: Double? = null

    var type: SynchronousMachineKind = SynchronousMachineKind.UNKNOWN
    var operatingMode: SynchronousMachineKind = SynchronousMachineKind.UNKNOWN

    /**
     * All available [ReactiveCapabilityCurve] for this synchronous machine.
     * First entry is the default [ReactiveCapabilityCurve]
     */
    val curves: MRIDListWrapper<ReactiveCapabilityCurve>
        get() = MRIDListWrapper(
            getter = { _reactiveCapabilityCurves },
            setter = { _reactiveCapabilityCurves = it })

    @Deprecated("BOILERPLATE: Use curves.size instead")
    fun numCurves(): Int = curves.size

    @Deprecated("BOILERPLATE: Use curves.getByMRID(mRID) instead")
    fun getCurve(mRID: String): ReactiveCapabilityCurve? = curves.getByMRID(mRID)

    /**
     * Add a [ReactiveCapabilityCurve] for this [SynchronousMachine]
     *
     * @param rcc the [ReactiveCapabilityCurve] to be added from this [SynchronousMachine]
     */
    fun addCurve(rcc: ReactiveCapabilityCurve): SynchronousMachine {
        if (validateReference(rcc, ::getCurve, "A ReactiveCapabilityCurve"))
            return this

        _reactiveCapabilityCurves = _reactiveCapabilityCurves.or(::mutableListOf) { add(rcc) }

        return this
    }

    @Deprecated("BOILERPLATE: Use curves.remove(curve) instead")
    fun removeCurve(curve: ReactiveCapabilityCurve?): Boolean = curves.remove(curve)

    /**
     * Clear all [ReactiveCapabilityCurve] for this [SynchronousMachine].
     */
    fun clearCurve(): SynchronousMachine {
        _reactiveCapabilityCurves = null
        return this
    }

}
