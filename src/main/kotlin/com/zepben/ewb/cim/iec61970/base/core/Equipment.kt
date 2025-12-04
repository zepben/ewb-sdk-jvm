/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.core

import com.zepben.ewb.cim.extensions.iec61970.base.core.Site
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvFeeder
import com.zepben.ewb.cim.iec61968.metering.UsagePoint
import com.zepben.ewb.cim.iec61968.operations.OperationalRestriction
import com.zepben.ewb.services.common.extensions.asUnmodifiable
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
    val substations: List<Substation> get() = _equipmentContainers.ofType()

    val currentFeeders: List<Feeder> get() = _currentContainers.ofType()
    val currentLvFeeders: List<LvFeeder> get() = _currentContainers.ofType()

    /**
     * The equipment containers this equipment belongs to. The returned collection is read only.
     */
    val containers: Collection<EquipmentContainer> get() = _equipmentContainers.asUnmodifiable()

    /**
     * Get the number of entries in the [EquipmentContainer] collection.
     */
    fun numContainers(): Int = _equipmentContainers?.size ?: 0

    /**
     * [EquipmentContainer]'s that this equipment is associated with.
     *
     * @param mRID the mRID of the required [EquipmentContainer]
     * @return The [EquipmentContainer] with the specified [mRID] if it exists, otherwise null
     */
    fun getContainer(mRID: String): EquipmentContainer? = _equipmentContainers.getByMRID(mRID)

    /**
     * @param equipmentContainer the equipment container to associate with this equipment.
     * @return A reference to this [Equipment] to allow fluent use.
     */
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
    fun removeContainer(equipmentContainer: EquipmentContainer): Boolean {
        val ret = _equipmentContainers?.remove(equipmentContainer) == true
        if (_equipmentContainers.isNullOrEmpty()) _equipmentContainers = null
        return ret
    }

    /**
     * Clear this [Equipment]'s associated [EquipmentContainer]'s
     * @return this [Equipment]
     */
    fun clearContainers(): Equipment {
        _equipmentContainers = null
        return this
    }

    /**
     * The equipment containers this equipment belongs to in the current network state. The returned collection is read only.
     */
    val currentContainers: Collection<EquipmentContainer> get() = _currentContainers.asUnmodifiable()

    /**
     * Get the number of entries in the current [EquipmentContainer] collection.
     */
    fun numCurrentContainers(): Int = _currentContainers?.size ?: 0

    /**
     * [EquipmentContainer]'s that represent the current containers of the equipment.
     *
     * @param mRID the mRID of the required current [EquipmentContainer]
     * @return The current [EquipmentContainer] with the specified [mRID] if it exists, otherwise null
     */
    fun getCurrentContainer(mRID: String): EquipmentContainer? = _currentContainers.getByMRID(mRID)

    /**
     * @param equipmentContainer the equipment container to associate with this equipment.
     * @return A reference to this [Equipment] to allow fluent use.
     */
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
    fun removeCurrentContainer(equipmentContainer: EquipmentContainer): Boolean {
        val ret = _currentContainers?.remove(equipmentContainer) == true
        if (_currentContainers.isNullOrEmpty()) _currentContainers = null
        return ret
    }

    /**
     * Clear this [Equipment]'s associated current [EquipmentContainer]'s
     * @return this [Equipment]
     */
    fun clearCurrentContainers(): Equipment {
        _currentContainers = null
        return this
    }

    /**
     * The usage points for this equipment. The returned collection is read only.
     */
    val usagePoints: List<UsagePoint> get() = _usagePoints.asUnmodifiable()

    /**
     * Get the number of entries in the [UsagePoint] collection.
     */
    fun numUsagePoints(): Int = _usagePoints?.size ?: 0

    /**
     * [UsagePoint]'s connected to the electrical grid through this equipment.
     *
     * @param mRID the mRID of the required [UsagePoint]
     * @return The [UsagePoint] with the specified [mRID] if it exists, otherwise null
     */
    fun getUsagePoint(mRID: String): UsagePoint? = _usagePoints.getByMRID(mRID)

    /**
     * @param usagePoint the usage point that connects to the electrical grid through this equipment.
     * @return true if the usage point is associated.
     * @return A reference to this [Equipment] to allow fluent use.
     */
    fun addUsagePoint(usagePoint: UsagePoint): Equipment {
        if (validateReference(usagePoint, ::getUsagePoint, "A UsagePoint"))
            return this

        _usagePoints = _usagePoints ?: mutableListOf()
        _usagePoints!!.add(usagePoint)

        return this
    }

    /**
     * @param usagePoint the usage point to disconnect from this equipment.
     * @return this [Equipment]
     */
    fun removeUsagePoint(usagePoint: UsagePoint): Boolean {
        val ret = _usagePoints?.remove(usagePoint) == true
        if (_usagePoints.isNullOrEmpty()) _usagePoints = null
        return ret
    }

    /**
     * Clear this [Equipment]'s associated [UsagePoint]'s
     * @return this [Equipment]
     */
    fun clearUsagePoints(): Equipment {
        _usagePoints = null
        return this
    }

    /**
     *  [OperationalRestriction]'s that this equipment is associated with. The returned collection is read only.
     */
    val operationalRestrictions: Collection<OperationalRestriction> get() = _operationalRestrictions.asUnmodifiable()

    /**
     * Get the number of entries in the [OperationalRestriction] collection.
     */
    fun numOperationalRestrictions(): Int = _operationalRestrictions?.size ?: 0

    /**
     * [OperationalRestriction]'s that this equipment is associated with.
     *
     * @param mRID the mRID of the required [OperationalRestriction]
     * @return The [OperationalRestriction] with the specified [mRID] if it exists, otherwise null
     */
    fun getOperationalRestriction(mRID: String): OperationalRestriction? = _operationalRestrictions.getByMRID(mRID)

    /**
     * Add an operational restriction that applies to this equipment.
     *
     * @param restriction The operational restriction that applies to this equipment.
     * @return true if the operation restriction was added.
     * @return A reference to this [Equipment] to allow fluent use.
     */
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
    fun removeOperationalRestriction(restriction: OperationalRestriction): Boolean {
        val ret = _operationalRestrictions?.remove(restriction) == true
        if (_operationalRestrictions.isNullOrEmpty()) _operationalRestrictions = null
        return ret
    }

    /**
     * Clear this [Equipment]'s associated [OperationalRestriction]'s
     * @return this [Equipment]
     */
    fun clearOperationalRestrictions(): Equipment {
        _operationalRestrictions = null
        return this
    }

    private inline fun <reified T : EquipmentContainer> List<*>?.ofType(): List<T> = this?.filterIsInstance(T::class.java) ?: emptyList()
}
