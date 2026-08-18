/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.core

import com.zepben.ewb.boilerplate.collections.LazyMridList
import com.zepben.ewb.boilerplate.collections.MridCollection
import com.zepben.ewb.cim.extensions.iec61970.base.core.Site
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvFeeder
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvSubstation
import com.zepben.ewb.cim.iec61968.metering.UsagePoint
import com.zepben.ewb.cim.iec61968.operations.OperationalRestriction
import com.zepben.ewb.services.common.extensions.getByMRID
import com.zepben.ewb.services.common.extensions.validateReference
import java.time.Instant

/**
 * @property inService If true, the equipment is in service.
 * @property normallyInService If true, the equipment is _normally_ in service.
 * @property commissionedDate The date this equipment was commissioned into service.
 * @property sites [Site]'s this equipment belongs to.
 * @property normalFeeders [Feeder]'s that represent the normal feeders of the equipment.
 * @property currentFeeders [Feeder]'s that represent the current feeders of the equipment.
 * @property normalLvFeeders [LvFeeder]'s that represent the normal LV feeders of the equipment.
 * @property currentLvFeeders [LvFeeder]'s that represent the current LV feeders of the equipment.
 * @property substations [Substation]'s that represent the substation of the equipment.
 */
abstract class Equipment(mRID: String) : PowerSystemResource(mRID) {

    var inService: Boolean = true
    var normallyInService: Boolean = true
    var commissionedDate: Instant? = null
    private var _equipmentContainers: MutableList<EquipmentContainer>? = null
    private var _usagePoints: MutableList<UsagePoint>? = null
    private var _operationalRestrictions: MutableList<OperationalRestriction>? = null
    private var _currentContainers: MutableList<EquipmentContainer>? = null

    val sites: List<Site> get() = _equipmentContainers.ofType()
    val normalFeeders: List<Feeder> get() = _equipmentContainers.ofType()
    val normalLvFeeders: List<LvFeeder> get() = _equipmentContainers.ofType()
    val normalLvSubstations: List<LvSubstation> get() = _equipmentContainers.ofType()
    val substations: List<Substation> get() = _equipmentContainers.ofType()

    val currentFeeders: List<Feeder> get() = _currentContainers.ofType()
    val currentLvFeeders: List<LvFeeder> get() = _currentContainers.ofType()

    /**
     * The equipment containers this equipment belongs to. The returned collection is read only.
     */
    val containers: MridCollection<EquipmentContainer> get() = LazyMridList(
        getter = { _equipmentContainers },
        setter = { _equipmentContainers = it },
        owner = this,
        elementDescription = "An EquipmentContainer"
    )

    /**
     * The equipment containers this equipment belongs to in the current network state. The returned collection is read only.
     */
    val currentContainers: MridCollection<EquipmentContainer> get() = LazyMridList(
        getter = { _currentContainers },
        setter = { _currentContainers = it },
        owner = this,
        elementDescription = "An EquipmentContainer"
    )

    /**
     * The usage points for this equipment. The returned collection is read only.
     */
    val usagePoints: MridCollection<UsagePoint> get() = LazyMridList(
        getter = { _usagePoints },
        setter = { _usagePoints = it },
        owner = this,
        elementDescription = "A UsagePoint"
    )

    /**
     *  [OperationalRestriction]'s that this equipment is associated with. The returned collection is read only.
     */
    val operationalRestrictions: MridCollection<OperationalRestriction> get() = LazyMridList(
        getter = { _operationalRestrictions },
        setter = { _operationalRestrictions = it },
        owner = this,
        elementDescription = "An OperationalRestriction"
    )

    private inline fun <reified T : EquipmentContainer> List<*>?.ofType(): List<T> = this?.filterIsInstance(T::class.java) ?: emptyList()

    // region deprecated list boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding list.

    // region containers boilerplate

    /**
     * Get the number of entries in the [EquipmentContainer] collection.
     */
    @Deprecated(
        message = "Use containers.size instead.",
        replaceWith = ReplaceWith("containers.size")
    )
    fun numContainers(): Int = _equipmentContainers?.size ?: 0

    /**
     * [EquipmentContainer]'s that this equipment is associated with.
     *
     * @param mRID the mRID of the required [EquipmentContainer]
     * @return The [EquipmentContainer] with the specified [mRID] if it exists, otherwise null
     */
    @Deprecated(
        message = "Use containers.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("containers.getByMRID(mRID)")
    )
    fun getContainer(mRID: String): EquipmentContainer? = _equipmentContainers.getByMRID(mRID)

    /**
     * @param equipmentContainer the equipment container to associate with this equipment.
     * @return A reference to this [Equipment] to allow fluent use.
     */
    @Deprecated(
        message = "Use containers.add(equipmentContainer) instead.",
        replaceWith = ReplaceWith("also { it.containers.add(equipmentContainer) }")
    )
    fun addContainer(equipmentContainer: EquipmentContainer): Equipment {
        if (validateReference(equipmentContainer, ::getContainer, "An EquipmentContainer"))
            return this

        _equipmentContainers = _equipmentContainers ?: mutableListOf()
        _equipmentContainers!!.add(equipmentContainer)

        return this
    }

    /**
     * @param equipmentContainer the equipment container to disassociate with this equipment.
     * @return `true` if [equipmentContainer] has been successfully removed; `false` if it was not present in the set.
     */
    @Deprecated(
        message = "Use containers.remove(equipmentContainer) instead.",
        replaceWith = ReplaceWith("containers.remove(equipmentContainer)")
    )
    fun removeContainer(equipmentContainer: EquipmentContainer): Boolean {
        val ret = _equipmentContainers?.remove(equipmentContainer) == true
        if (_equipmentContainers.isNullOrEmpty()) _equipmentContainers = null
        return ret
    }

    /**
     * Clear this [Equipment]'s associated [EquipmentContainer]'s
     * @return this [Equipment]
     */
    @Deprecated(
        message = "Use containers.clear() instead.",
        replaceWith = ReplaceWith("containers.clear()")
    )
    fun clearContainers(): Equipment {
        _equipmentContainers = null
        return this
    }

    // endregion

    // region currentContainers boilerplate

    /**
     * Get the number of entries in the current [EquipmentContainer] collection.
     */
    @Deprecated(
        message = "Use currentContainers.size instead.",
        replaceWith = ReplaceWith("currentContainers.size")
    )
    fun numCurrentContainers(): Int = _currentContainers?.size ?: 0

    /**
     * [EquipmentContainer]'s that represent the current containers of the equipment.
     *
     * @param mRID the mRID of the required current [EquipmentContainer]
     * @return The current [EquipmentContainer] with the specified [mRID] if it exists, otherwise null
     */
    @Deprecated(
        message = "Use currentContainers.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("currentContainers.getByMRID(mRID)")
    )
    fun getCurrentContainer(mRID: String): EquipmentContainer? = _currentContainers.getByMRID(mRID)

    /**
     * @param equipmentContainer the equipment container to associate with this equipment.
     * @return A reference to this [Equipment] to allow fluent use.
     */
    @Deprecated(
        message = "Use currentContainers.add(equipmentContainer) instead.",
        replaceWith = ReplaceWith("also { it.currentContainers.add(equipmentContainer) }")
    )
    fun addCurrentContainer(equipmentContainer: EquipmentContainer): Equipment {
        if (validateReference(equipmentContainer, ::getCurrentContainer, "A current EquipmentContainer"))
            return this

        _currentContainers = _currentContainers ?: mutableListOf()
        _currentContainers!!.add(equipmentContainer)

        return this
    }

    /**
     * @param equipmentContainer the equipment container to disassociate with this equipment.
     * @return `true` if [equipmentContainer] has been successfully removed; `false` if it was not present in the set.
     */
    @Deprecated(
        message = "Use currentContainers.remove(equipmentContainer) instead.",
        replaceWith = ReplaceWith("currentContainers.remove(equipmentContainer)")
    )
    fun removeCurrentContainer(equipmentContainer: EquipmentContainer): Boolean {
        val ret = _currentContainers?.remove(equipmentContainer) == true
        if (_currentContainers.isNullOrEmpty()) _currentContainers = null
        return ret
    }

    /**
     * Clear this [Equipment]'s associated current [EquipmentContainer]'s
     * @return this [Equipment]
     */
    @Deprecated(
        message = "Use currentContainers.clear() instead.",
        replaceWith = ReplaceWith("currentContainers.clear()")
    )
    fun clearCurrentContainers(): Equipment {
        _currentContainers = null
        return this
    }

    // endregion

    // region usagePoints boilerplate

    /**
     * Get the number of entries in the [UsagePoint] collection.
     */
    @Deprecated(
        message = "Use usagePoints.size instead.",
        replaceWith = ReplaceWith("usagePoints.size")
    )
    fun numUsagePoints(): Int = _usagePoints?.size ?: 0

    /**
     * [UsagePoint]'s connected to the electrical grid through this equipment.
     *
     * @param mRID the mRID of the required [UsagePoint]
     * @return The [UsagePoint] with the specified [mRID] if it exists, otherwise null
     */
    @Deprecated(
        message = "Use usagePoints.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("usagePoints.getByMRID(mRID)")
    )
    fun getUsagePoint(mRID: String): UsagePoint? = _usagePoints.getByMRID(mRID)

    /**
     * @param usagePoint the usage point that connects to the electrical grid through this equipment.
     * @return A reference to this [Equipment] to allow fluent use.
     */
    @Deprecated(
        message = "Use usagePoints.add(usagePoint) instead.",
        replaceWith = ReplaceWith("also { it.usagePoints.add(usagePoint) }")
    )
    fun addUsagePoint(usagePoint: UsagePoint): Equipment {
        if (validateReference(usagePoint, ::getUsagePoint, "A UsagePoint"))
            return this

        _usagePoints = _usagePoints ?: mutableListOf()
        _usagePoints!!.add(usagePoint)

        return this
    }

    /**
     * @param usagePoint the usage point to disconnect from this equipment.
     * @return true if [usagePoint] was removed from this equipment.
     */
    @Deprecated(
        message = "Use usagePoints.remove(usagePoint) instead.",
        replaceWith = ReplaceWith("usagePoints.remove(usagePoint)")
    )
    fun removeUsagePoint(usagePoint: UsagePoint): Boolean {
        val ret = _usagePoints?.remove(usagePoint) == true
        if (_usagePoints.isNullOrEmpty()) _usagePoints = null
        return ret
    }

    /**
     * Clear this [Equipment]'s associated [UsagePoint]'s
     * @return this [Equipment]
     */
    @Deprecated(
        message = "Use usagePoints.clear() instead.",
        replaceWith = ReplaceWith("usagePoints.clear()")
    )
    fun clearUsagePoints(): Equipment {
        _usagePoints = null
        return this
    }

    // endregion

    // region operationalRestrictions boilerplate

    /**
     * Get the number of entries in the [OperationalRestriction] collection.
     */
    @Deprecated(
        message = "Use operationalRestrictions.size instead.",
        replaceWith = ReplaceWith("operationalRestrictions.size")
    )
    fun numOperationalRestrictions(): Int = _operationalRestrictions?.size ?: 0

    /**
     * [OperationalRestriction]'s that this equipment is associated with.
     *
     * @param mRID the mRID of the required [OperationalRestriction]
     * @return The [OperationalRestriction] with the specified [mRID] if it exists, otherwise null
     */
    @Deprecated(
        message = "Use operationalRestrictions.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("operationalRestrictions.getByMRID(mRID)")
    )
    fun getOperationalRestriction(mRID: String): OperationalRestriction? = _operationalRestrictions.getByMRID(mRID)

    /**
     * Add an operational restriction that applies to this equipment.
     *
     * @param restriction The operational restriction that applies to this equipment.
     * @return true if the operation restriction was added.
     * @return A reference to this [Equipment] to allow fluent use.
     */
    @Deprecated(
        message = "Use operationalRestrictions.add(restriction) instead.",
        replaceWith = ReplaceWith("also { it.operationalRestrictions.add(restriction) }")
    )
    fun addOperationalRestriction(restriction: OperationalRestriction): Equipment {
        if (validateReference(restriction, ::getOperationalRestriction, "An OperationalRestriction"))
            return this

        _operationalRestrictions = _operationalRestrictions ?: mutableListOf()
        _operationalRestrictions!!.add(restriction)

        return this
    }

    /**
     * Removes an operational restriction that has been associated with this equipment.
     * @param restriction The operational restriction to be removed.
     * @return this [Equipment]
     */
    @Deprecated(
        message = "Use operationalRestrictions.remove(restriction) instead.",
        replaceWith = ReplaceWith("operationalRestrictions.remove(restriction)")
    )
    fun removeOperationalRestriction(restriction: OperationalRestriction): Boolean {
        val ret = _operationalRestrictions?.remove(restriction) == true
        if (_operationalRestrictions.isNullOrEmpty()) _operationalRestrictions = null
        return ret
    }

    /**
     * Clear this [Equipment]'s associated [OperationalRestriction]'s
     * @return this [Equipment]
     */
    @Deprecated(
        message = "Use operationalRestrictions.clear() instead.",
        replaceWith = ReplaceWith("operationalRestrictions.clear()")
    )
    fun clearOperationalRestrictions(): Equipment {
        _operationalRestrictions = null
        return this
    }

    // endregion

    // endregion
}
