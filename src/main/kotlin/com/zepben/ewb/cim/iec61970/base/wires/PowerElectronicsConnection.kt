/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

import com.zepben.ewb.boilerplate.LazyMridList
import com.zepben.ewb.cim.extensions.ZBEX
import com.zepben.ewb.cim.iec61970.base.generation.production.PowerElectronicsUnit

/**
 * A connection to the AC network for energy production or consumption that uses power electronics rather than
 * rotating machines.
 *
 * @property maxIFault Maximum fault current this device will contribute, in per-unit of rated current, before the converter protection
 *                     will trip or bypass.
 * @property maxQ Maximum reactive power limit. This is the maximum (nameplate) limit for the unit.
 * @property minQ Minimum reactive power limit for the unit. This is the minimum (nameplate) limit for the unit.
 * @property p Active power injection. Load sign convention is used, i.e. positive sign means flow out from a node.
 *             Starting value for a steady state solution.
 * @property q Reactive power injection. Load sign convention is used, i.e. positive sign means flow out from a node.
 *             Starting value for a steady state solution.
 * @property ratedS Nameplate apparent power rating for the unit. The attribute shall have a positive value.
 * @property ratedU Rated voltage (nameplate data, Ur in IEC 60909-0). It is primarily used for short circuit data exchange according
 *                  to IEC 60909. The attribute shall be a positive value.
 * @property units An AC network connection may have several power electronics units connecting through it.
 * @property phases The individual units models for the power electronics connection.
 * @property inverterStandard [ZBEX] The standard this inverter follows, such as AS4777.2:2020
 * @property sustainOpOvervoltLimit [ZBEX] Indicates the sustained operation overvoltage limit in volts, when the average voltage for a 10-minute period exceeds the V¬nom-max.
 * @property stopAtOverFreq [ZBEX] Over frequency (stop) in Hz. Permitted range is between 51 and 52 (inclusive)
 * @property stopAtUnderFreq [ZBEX] Under frequency (stop) in Hz Permitted range is between 47 and 49 (inclusive)
 * @property invVoltWattRespMode [ZBEX] Volt-Watt response mode allows an inverter to reduce is real power output depending on the measured voltage. This mode is further described in AS4777.2:2015, section 6.3.2.2. True implies the mode is enabled.
 * @property invWattRespV1 [ZBEX] Set point 1 in volts for inverter Volt-Watt response mode. Permitted range is between 200 and 300 (inclusive).
 * @property invWattRespV2 [ZBEX] Set point 2 in volts for inverter Volt-Watt response mode. Permitted range is between 216 and 230 (inclusive).
 * @property invWattRespV3 [ZBEX] Set point 3 in volts for inverter Volt-Watt response mode. Permitted range is between 235 and 255 (inclusive).
 * @property invWattRespV4 [ZBEX] Set point 4 in volts for inverter Volt-Watt response mode. Permitted range is between 244 and 265 (inclusive).
 * @property invWattRespPAtV1 [ZBEX] Power output set point 1 as a percentage of rated output for inverter Volt-Watt response mode. Permitted range is between 0 and 1 (inclusive).
 * @property invWattRespPAtV2 [ZBEX] Power output set point 2 as a percentage of rated output for inverter Volt-Watt response mode. Permitted range is between 0 and 1 (inclusive).
 * @property invWattRespPAtV3 [ZBEX] Power output set point 3 as a percentage of rated output for inverter Volt-Watt response mode. Permitted range is between 0 and 1 (inclusive).
 * @property invWattRespPAtV4 [ZBEX] Power output set point 4 as a percentage of rated output for inverter Volt-Watt response mode. Permitted range is between 0 and 0.2 (inclusive).
 * @property invVoltVarRespMode [ZBEX] Volt-VAr response mode allows an inverter to consume (sink) or produce (source) reactive power depending on the measured voltage. This mode is further described in AS4777.2:2015, section 6.3.2.3. True implies the mode is enabled.
 * @property invVarRespV1 [ZBEX] Set point 1 in volts for inverter Volt-VAr response mode. Permitted range is between 200 and 300 (inclusive).
 * @property invVarRespV2 [ZBEX] Set point 2 in volts for inverter Volt-VAr response mode. Permitted range is between 200 and 300 (inclusive).
 * @property invVarRespV3 [ZBEX] Set point 3 in volts for inverter Volt-VAr response mode. Permitted range is between 200 and 300 (inclusive).
 * @property invVarRespV4 [ZBEX] Set point 4 in volts for inverter Volt-VAr response mode. Permitted range is between 200 and 300 (inclusive).
 * @property invVarRespQAtV1 [ZBEX] Power output set point 1 as a percentage of rated output for inverter Volt-VAr response mode. Permitted range is between 0 and 0.6 (inclusive).
 * @property invVarRespQAtV2 [ZBEX] Power output set point 2 as a percentage of rated output for inverter Volt-VAr response mode. Permitted range is between -1 and 1 (inclusive) with a negative number referring to a sink.
 * @property invVarRespQAtV3 [ZBEX] Power output set point 3 as a percentage of rated output for inverter Volt-VAr response mode. Permitted range is between -1 and 1 (inclusive) with a negative number referring to a sink.
 * @property invVarRespQAtV4 [ZBEX] Power output set point 4 as a percentage of rated output for inverter Volt-VAr response mode. Permitted range is between -0.6 and 0 (inclusive) with a negative number referring to a sink.
 * @property invReactivePowerMode [ZBEX] If true, enables Static Reactive Power mode on the inverter. Note: It must be false if invVoltVarRespMode or InvVoltWattRespMode is true.
 * @property invFixReactivePower [ZBEX] Static Reactive Power, specified in a percentage output of the system. Permitted range is between -1.0 and 1.0 (inclusive), with a negative sign referring to “sink”.
 */
class PowerElectronicsConnection(mRID: String) : RegulatingCondEq(mRID) {

    private var _powerElectronicsUnits: MutableList<PowerElectronicsUnit>? = null
    private var _powerElectronicsConnectionPhases: MutableList<PowerElectronicsConnectionPhase>? = null
    var maxIFault: Int? = null
    var maxQ: Double? = null
    var minQ: Double? = null
    var p: Double? = null
    var q: Double? = null
    var ratedS: Int? = null
    var ratedU: Int? = null

    @ZBEX
    var inverterStandard: String? = null

    @ZBEX
    var sustainOpOvervoltLimit: Int? = null

    @ZBEX
    var stopAtOverFreq: Float? = null

    @ZBEX
    var stopAtUnderFreq: Float? = null

    @ZBEX
    var invVoltWattRespMode: Boolean? = null

    @ZBEX
    var invWattRespV1: Int? = null
        set(value) {
            check(value == null || (value >= 200) && (value <= 300)) { "invWattRespV1 [$value] must be between 200 and 300." }
            field = value
        }

    @ZBEX
    var invWattRespV2: Int? = null
        set(value) {
            check(value == null || (value >= 216) && (value <= 230)) { "invWattRespV2 [$value] must be between 216 and 230." }
            field = value
        }

    @ZBEX
    var invWattRespV3: Int? = null
        set(value) {
            check(value == null || (value >= 235) && (value <= 255)) { "invWattRespV3 [$value] must be between 235 and 255." }
            field = value
        }

    @ZBEX
    var invWattRespV4: Int? = null
        set(value) {
            check(value == null || (value >= 244) && (value <= 265)) { "invWattRespV4 [$value] must be between 244 and 265." }
            field = value
        }

    @ZBEX
    var invWattRespPAtV1: Float? = null
        set(value) {
            check(value == null || (value >= 0.0f) && (value <= 1.0f)) { "invWattRespPAtV1 [$value] must be between 0.0 and 1.0." }
            field = value
        }

    @ZBEX
    var invWattRespPAtV2: Float? = null
        set(value) {
            check(value == null || (value >= 0.0f) && (value <= 1.0f)) { "invWattRespPAtV2 [$value] must be between 0.0 and 1.0." }
            field = value
        }

    @ZBEX
    var invWattRespPAtV3: Float? = null
        set(value) {
            check(value == null || (value >= 0.0f) && (value <= 1.0f)) { "invWattRespPAtV3 [$value] must be between 0.0 and 1.0." }
            field = value
        }

    @ZBEX
    var invWattRespPAtV4: Float? = null
        set(value) {
            check(value == null || (value >= 0.0f) && (value <= 0.2f)) { "invWattRespPAtV4 [$value] must be between 0.0 and 0.2." }
            field = value
        }

    @ZBEX
    var invVoltVarRespMode: Boolean? = null

    @ZBEX
    var invVarRespV1: Int? = null
        set(value) {
            check(value == null || (value >= 200) && (value <= 300)) { "invVarRespV1 [$value] must be between 200 and 300." }
            field = value
        }

    @ZBEX
    var invVarRespV2: Int? = null
        set(value) {
            check(value == null || (value >= 200) && (value <= 300)) { "invVarRespV2 [$value] must be between 200 and 300." }
            field = value
        }

    @ZBEX
    var invVarRespV3: Int? = null
        set(value) {
            check(value == null || (value >= 200) && (value <= 300)) { "invVarRespV3 [$value] must be between 200 and 300." }
            field = value
        }

    @ZBEX
    var invVarRespV4: Int? = null
        set(value) {
            check(value == null || (value >= 200) && (value <= 300)) { "invVarRespV4 [$value] must be between 200 and 300." }
            field = value
        }

    @ZBEX
    var invVarRespQAtV1: Float? = null
        set(value) {
            check(value == null || (value >= 0.0f) && (value <= 0.6f)) { "invVarRespQAtV1 [$value] must be between 0.0 and 0.6." }
            field = value
        }

    @ZBEX
    var invVarRespQAtV2: Float? = null
        set(value) {
            check(value == null || (value >= -1.0f) && (value <= 1.0f)) { "invVarRespQAtV2 [$value] must be between -1.0 and 1.0." }
            field = value
        }

    @ZBEX
    var invVarRespQAtV3: Float? = null
        set(value) {
            check(value == null || (value >= -1.0f) && (value <= 1.0f)) { "invVarRespQAtV3 [$value] must be between -1.0 and 1.0." }
            field = value
        }

    @ZBEX
    var invVarRespQAtV4: Float? = null
        set(value) {
            check(value == null || (value >= -0.6f) && (value <= 0.0f)) { "invVarRespQAtV4 [$value] must be between -0.6 and 0.0." }
            field = value
        }

    @ZBEX
    var invReactivePowerMode: Boolean? = null

    @ZBEX
    var invFixReactivePower: Float? = null

    /**
     * The units for this power electronics connection. The returned collection is read only.
     */
    val units: LazyMridList<PowerElectronicsUnit> get() = LazyMridList(
        getter = { _powerElectronicsUnits },
        setter = { _powerElectronicsUnits = it },
        owner = this,
        elementDescription = "A PowerElectronicsUnit",
    )

    /**
     * The phases for this power electronics connection. The returned collection is read only.
     */
    val phases: LazyMridList<PowerElectronicsConnectionPhase> get() = LazyMridList(
        getter = { _powerElectronicsConnectionPhases },
        setter = { _powerElectronicsConnectionPhases = it },
        owner = this,
        elementDescription = "A PowerElectronicsConnectionPhase",
        validate = { validatePhase(it) }
    )

    private fun validatePhase(phase: PowerElectronicsConnectionPhase) {
        if (phase.powerElectronicsConnection == null)
            phase.powerElectronicsConnection = this

        require(phase.powerElectronicsConnection === this) {
            "${phase.typeNameAndMRID()} `powerElectronicsConnection` property references ${phase.powerElectronicsConnection!!.typeNameAndMRID()}, expected ${typeNameAndMRID()}."
        }
    }

    // region deprecated list boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding list.

    // region units boilerplate

    @Deprecated(
        message = "Use units.size instead.",
        replaceWith = ReplaceWith("units.size")
    )
    fun numUnits(): Int = units.size

    @Deprecated(
        message = "Use units.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("units.getByMRID(mRID)")
    )
    fun getUnit(mRID: String): PowerElectronicsUnit? = units.getByMrid(mRID)

    @Deprecated(
        message = "Use units.add(unit) instead.",
        replaceWith = ReplaceWith("also { it.units.add(unit) }")
    )
    fun addUnit(unit: PowerElectronicsUnit): PowerElectronicsConnection {
        units.add(unit)
        return this
    }

    @Deprecated(
        message = "Use units.remove(unit) instead.",
        replaceWith = ReplaceWith("units.remove(unit)")
    )
    fun removeUnit(unit: PowerElectronicsUnit): Boolean = units.remove(unit)

    @Deprecated(
        message = "Use units.clear() instead.",
        replaceWith = ReplaceWith("units.clear()")
    )
    fun clearUnits(): PowerElectronicsConnection {
        units.clear()
        return this
    }

    // endregion

    // region phases boilerplate

    @Deprecated(
        message = "Use phases.size instead.",
        replaceWith = ReplaceWith("phases.size")
    )
    fun numPhases(): Int = phases.size

    @Deprecated(
        message = "Use phases.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("phases.getByMRID(mRID)")
    )
    fun getPhase(mRID: String): PowerElectronicsConnectionPhase? = phases.getByMrid(mRID)

    @Deprecated(
        message = "Use phases.add(phase) instead.",
        replaceWith = ReplaceWith("also { it.phases.add(phase) }")
    )
    fun addPhase(phase: PowerElectronicsConnectionPhase): PowerElectronicsConnection {
        phases.add(phase)
        return this
    }

    @Deprecated(
        message = "Use phases.remove(phase) instead.",
        replaceWith = ReplaceWith("phases.remove(phase)")
    )
    fun removePhase(phase: PowerElectronicsConnectionPhase): Boolean = phases.remove(phase)

    @Deprecated(
        message = "Use phases.clear() instead.",
        replaceWith = ReplaceWith("phases.clear()")
    )
    fun clearPhases(): PowerElectronicsConnection {
        phases.clear()
        return this
    }

    // endregion

    // endregion
}
