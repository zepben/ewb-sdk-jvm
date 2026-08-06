/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.extensions.iec61970.base.protection

import com.zepben.ewb.boilerplate.collections.LazyIndexList
import com.zepben.ewb.boilerplate.collections.LazyMridList
import com.zepben.ewb.boilerplate.collections.MridCollection
import com.zepben.ewb.cim.extensions.ZBEX
import com.zepben.ewb.cim.extensions.iec61968.assetinfo.RelayInfo
import com.zepben.ewb.cim.iec61970.base.auxiliaryequipment.Sensor
import com.zepben.ewb.cim.iec61970.base.core.PowerSystemResource
import com.zepben.ewb.cim.iec61970.base.wires.ProtectedSwitch
import java.util.function.BiConsumer

/**
 * [ZBEX] This extension is in-line with the CIM working group for replacing the `protection` package, can be replaced when the working
 * group outcome is merged into the CIM model.
 *
 * A function that a relay implements to protect equipment.
 *
 * @property model [ZBEX] The protection equipment type name (manufacturer information)
 * @property reclosing [ZBEX] True if the protection equipment is reclosing or False otherwise.
 * @property relayDelayTime [ZBEX] The time delay from detection of abnormal conditions to relay operation in seconds.
 * @property protectionKind [ZBEX] The kind of protection being provided by this [ProtectionRelayFunction].
 * @property directable [ZBEX] Whether this [ProtectionRelayFunction] responds to power flow in a given direction.
 * @property powerDirection [ZBEX] The flow of power direction used by this [ProtectionRelayFunction].
 * @property timeLimits [ZBEX] The time limits (in seconds) for this relay function. Order of entries corresponds to the order of entries in thresholds.
 * @property thresholds [ZBEX] The thresholds for this relay function. The order of thresholds corresponds to the order of time limits.
 * @property protectedSwitches [ZBEX] The [ProtectedSwitch]es operated by this [ProtectionRelayFunction].
 * @property sensors [ZBEX] The [Sensor]s for this relay function.
 * @property schemes [ZBEX] The schemes this function operates under.
 */
@ZBEX
abstract class ProtectionRelayFunction(mRID: String) : PowerSystemResource(mRID) {

    override var assetInfo: RelayInfo? = null

    @ZBEX
    var model: String? = null

    @ZBEX
    var reclosing: Boolean? = null

    @ZBEX
    var relayDelayTime: Double? = null

    @ZBEX
    var protectionKind: ProtectionKind = ProtectionKind.UNKNOWN

    @ZBEX
    var directable: Boolean? = null

    @ZBEX
    var powerDirection: PowerDirectionKind = PowerDirectionKind.UNKNOWN

    private var _timeLimits: MutableList<Double>? = null
    private var _protectedSwitches: MutableList<ProtectedSwitch>? = null
    private var _sensors: MutableList<Sensor>? = null
    private var _thresholds: MutableList<RelaySetting>? = null
    private var _schemes: MutableList<ProtectionRelayScheme>? = null

    @ZBEX
    val timeLimits: LazyIndexList<Double> get() = LazyIndexList(
        { _timeLimits },
        { _timeLimits = it },
        this,
        "Double"
    )

    @ZBEX
    val thresholds: LazyIndexList<RelaySetting> get() = LazyIndexList(
        { _thresholds },
        { _thresholds = it },
        this,
        "RelaySetting"
    )

    @ZBEX
    val protectedSwitches: MridCollection<ProtectedSwitch> get() = LazyMridList(
        getter = { _protectedSwitches },
        setter = { _protectedSwitches = it },
        owner = this,
        elementDescription = "A ProtectedSwitch"
    )

    @ZBEX
    val sensors: MridCollection<Sensor> get() = LazyMridList(
        getter = { _sensors },
        setter = { _sensors = it },
        owner = this,
        elementDescription = "A Sensor"
    )

    @ZBEX
    val schemes: MridCollection<ProtectionRelayScheme> get() = LazyMridList(
        getter = { _schemes },
        setter = { _schemes = it },
        owner = this,
        elementDescription = "A ProtectionRelayScheme"
    )

    // region deprecated list boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding list.

    // region timeLimits boilerplate

    @Deprecated(
        message = "Use timeLimits.size instead.",
        replaceWith = ReplaceWith("timeLimits.size")
    )
    fun numTimeLimits(): Int = timeLimits.size

    @Deprecated(
        message = "Use timeLimits.getOrNull(sequenceNumber) instead.",
        replaceWith = ReplaceWith("timeLimits.getOrNull(sequenceNumber)")
    )
    fun getTimeLimit(sequenceNumber: Int): Double? = timeLimits.getOrNull(sequenceNumber)

    @Deprecated(
        message = "Use timeLimits.forEachIndexed(action::accept) instead.",
        replaceWith = ReplaceWith("timeLimits.forEachIndexed(action::accept)")
    )
    fun forEachTimeLimit(action: BiConsumer<Int, Double>) = timeLimits.forEachIndexed(action::accept)

    @Deprecated(
        message = "Use timeLimits.add(index, timeLimit) instead.",
        replaceWith = ReplaceWith("also { it.timeLimits.add(index, timeLimit) }")
    )
    @JvmOverloads
    fun addTimeLimit(
        timeLimit: Double,
        index: Int = numTimeLimits(),
    ): ProtectionRelayFunction {
        timeLimits.add(index, timeLimit)

        return this
    }

    @Deprecated(
        message = "Use timeLimits.addAll(timeLimits.asList()) instead.",
        replaceWith = ReplaceWith("also { it.timeLimits.addAll(timeLimits.asList()) }")
    )
    fun addTimeLimits(vararg timeLimits: Double): ProtectionRelayFunction = apply {
        this.timeLimits.addAll(timeLimits.asList())
    }

    @Deprecated(
        message = "Use timeLimits.remove(timeLimit) instead.",
        replaceWith = ReplaceWith("timeLimits.remove(timeLimit)")
    )
    fun removeTimeLimit(timeLimit: Double): Boolean = timeLimits.remove(timeLimit)

    @Deprecated(
        message = "Use timeLimits.removeAt(index) instead.",
        replaceWith = ReplaceWith("timeLimits.removeAt(index)")
    )
    fun removeTimeLimitAt(index: Int): Double? = timeLimits.removeAt(index)

    @Deprecated(
        message = "Use timeLimits.clear() instead.",
        replaceWith = ReplaceWith("also { it.timeLimits.clear() }")
    )
    fun clearTimeLimits(): ProtectionRelayFunction = apply {
        timeLimits.clear()
    }

    // endregion

    // region thresholds boilerplate

    @Deprecated(
        message = "Use thresholds.size instead.",
        replaceWith = ReplaceWith("thresholds.size")
    )
    fun numThresholds(): Int = thresholds.size

    @Deprecated(
        message = "Use thresholds.getOrNull(sequenceNumber) instead.",
        replaceWith = ReplaceWith("thresholds.getOrNull(sequenceNumber)")
    )
    fun getThreshold(sequenceNumber: Int): RelaySetting? = thresholds.getOrNull(sequenceNumber)

    @Deprecated(
        message = "Use thresholds.forEachIndexed(action::accept) instead.",
        replaceWith = ReplaceWith("thresholds.forEachIndexed(action::accept)")
    )
    fun forEachThreshold(action: BiConsumer<Int, RelaySetting>) = thresholds.forEachIndexed(action::accept)

    @Deprecated(
        message = "Use thresholds.add(sequenceNumber, threshold) instead.",
        replaceWith = ReplaceWith("also { it.thresholds.add(sequenceNumber, threshold) }")
    )
    @JvmOverloads
    fun addThreshold(threshold: RelaySetting, sequenceNumber: Int = numThresholds()): ProtectionRelayFunction = apply {
        thresholds.add(sequenceNumber, threshold)
    }

    @Deprecated(
        message = "Use thresholds.remove(threshold) instead.",
        replaceWith = ReplaceWith("thresholds.remove(threshold)")
    )
    fun removeThreshold(threshold: RelaySetting): Boolean = thresholds.remove(threshold)

    @Deprecated(
        message = "Use thresholds.removeAt(sequenceNumber) instead.",
        replaceWith = ReplaceWith("thresholds.removeAt(sequenceNumber)")
    )
    fun removeThreshold(sequenceNumber: Int): RelaySetting? = thresholds.removeAt(sequenceNumber)

    @Deprecated(
        message = "Use thresholds.clear() instead.",
        replaceWith = ReplaceWith("also { it.thresholds.clear() }")
    )
    fun clearThresholds(): ProtectionRelayFunction = apply {
        thresholds.clear()
    }

    // endregion

    // region protectedSwitches boilerplate

    @Deprecated(
        message = "Use protectedSwitches.size instead.",
        replaceWith = ReplaceWith("protectedSwitches.size")
    )
    fun numProtectedSwitches(): Int = protectedSwitches.size

    @Deprecated(
        message = "Use protectedSwitches.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("protectedSwitches.getByMRID(mRID)")
    )
    fun getProtectedSwitch(mRID: String): ProtectedSwitch? = protectedSwitches.getByMrid(mRID)

    @Deprecated(
        message = "Use protectedSwitches.add(protectedSwitch) instead.",
        replaceWith = ReplaceWith("also { it.protectedSwitches.add(protectedSwitch) }")
    )
    fun addProtectedSwitch(protectedSwitch: ProtectedSwitch): ProtectionRelayFunction = apply {
        protectedSwitches.add(protectedSwitch)
    }

    @Deprecated(
        message = "Use protectedSwitches.remove(protectedSwitch) instead.",
        replaceWith = ReplaceWith("protectedSwitches.remove(protectedSwitch)")
    )
    fun removeProtectedSwitch(protectedSwitch: ProtectedSwitch): Boolean = protectedSwitches.remove(protectedSwitch)

    @Deprecated(
        message = "Use protectedSwitches.clear() instead.",
        replaceWith = ReplaceWith("protectedSwitches.clear()")
    )
    fun clearProtectedSwitches(): ProtectionRelayFunction = apply {
        protectedSwitches.clear()
    }

    // endregion

    // region sensors boilerplate

    @Deprecated(
        message = "Use sensors.size instead.",
        replaceWith = ReplaceWith("sensors.size")
    )
    fun numSensors(): Int = sensors.size

    @Deprecated(
        message = "Use sensors.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("sensors.getByMRID(mRID)")
    )
    fun getSensor(mRID: String): Sensor? = sensors.getByMrid(mRID)

    @Deprecated(
        message = "Use sensors.add(sensor) instead.",
        replaceWith = ReplaceWith("also { it.sensors.add(sensor) }")
    )
    fun addSensor(sensor: Sensor): ProtectionRelayFunction = apply {
        sensors.add(sensor)
    }

    @Deprecated(
        message = "Use sensors.remove(sensor) instead.",
        replaceWith = ReplaceWith("sensors.remove(sensor)")
    )
    fun removeSensor(sensor: Sensor): Boolean = sensors.remove(sensor)

    @Deprecated(
        message = "Use sensors.clear() instead.",
        replaceWith = ReplaceWith("sensors.clear()")
    )
    fun clearSensors(): ProtectionRelayFunction = apply {
        sensors.clear()
    }

    // endregion

    // region schemes boilerplate

    @Deprecated(
        message = "Use schemes.size instead.",
        replaceWith = ReplaceWith("schemes.size")
    )
    fun numSchemes(): Int = schemes.size

    @Deprecated(
        message = "Use schemes.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("schemes.getByMRID(mRID)")
    )
    fun getScheme(mRID: String): ProtectionRelayScheme? = schemes.getByMrid(mRID)

    @Deprecated(
        message = "Use schemes.add(scheme) instead.",
        replaceWith = ReplaceWith("also { it.schemes.add(scheme) }")
    )
    fun addScheme(scheme: ProtectionRelayScheme): ProtectionRelayFunction = apply {
        schemes.add(scheme)
    }

    @Deprecated(
        message = "Use schemes.remove(scheme) instead.",
        replaceWith = ReplaceWith("schemes.remove(scheme)")
    )
    fun removeScheme(scheme: ProtectionRelayScheme): Boolean = schemes.remove(scheme)

    @Deprecated(
        message = "Use schemes.clear() instead.",
        replaceWith = ReplaceWith("schemes.clear()")
    )
    fun clearSchemes(): ProtectionRelayFunction = apply {
        schemes.clear()
    }

    // endregion

    // endregion
}

/**
 * Perform the specified action against each time limit.
 *
 * @param action The action to perform on each time limit
 */
fun ProtectionRelayFunction.forEachTimeLimits(action: (sequenceNumber: Int, timeLimit: Double) -> Unit): Unit = forEachTimeLimit(BiConsumer(action))

/**
 * Perform the specified action against each threshold.
 *
 * @param action The action to perform on each threshold
 */
fun ProtectionRelayFunction.forEachThreshold(action: (sequenceNumber: Int, threshold: RelaySetting) -> Unit): Unit = forEachThreshold(BiConsumer(action))
