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
import com.zepben.ewb.testing.MRIDListWrapper
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
abstract class Equipment(mRID: String = "") : PowerSystemResource(mRID) {

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
    val containers: MRIDListWrapper<EquipmentContainer>
        get() = MRIDListWrapper(
            getter = { _equipmentContainers },
            setter = { _equipmentContainers = it })

    @Deprecated("BOILERPLATE: Use containers.size instead")
    fun numContainers(): Int = containers.size

    @Deprecated("BOILERPLATE: Use equipmentContainers.getByMRID(mRID) instead")
    fun getContainer(mRID: String): EquipmentContainer? = containers.getByMRID(mRID)

    @Deprecated("BOILERPLATE: Use equipmentContainers.add(equipmentContainer) instead")
    fun addContainer(equipmentContainer: EquipmentContainer): Equipment {
        containers.add(equipmentContainer)
        return this
    }

    @Deprecated("BOILERPLATE: Use containers.remove(equipmentContainer) instead")
    fun removeContainer(equipmentContainer: EquipmentContainer): Boolean = containers.remove(equipmentContainer)

    @Deprecated("BOILERPLATE: Use containers.clear() instead")
    fun clearContainers(): Equipment {
        containers.clear()
        return this
    }

    /**
     * The equipment containers this equipment belongs to in the current network state. The returned collection is read only.
     */
    val currentContainers: MRIDListWrapper<EquipmentContainer>
        get() = MRIDListWrapper(
            getter = { _currentContainers },
            setter = { _currentContainers = it })

    @Deprecated("BOILERPLATE: Use currentContainers.size instead")
    fun numCurrentContainers(): Int = currentContainers.size

    @Deprecated("BOILERPLATE: Use currentContainers.getByMRID(mRID) instead")
    fun getCurrentContainer(mRID: String): EquipmentContainer? = currentContainers.getByMRID(mRID)

    @Deprecated("BOILERPLATE: Use currentContainers.add(equipmentContainer) instead")
    fun addCurrentContainer(equipmentContainer: EquipmentContainer): Equipment {
        currentContainers.add(equipmentContainer)
        return this
    }

    @Deprecated("BOILERPLATE: Use currentContainers.remove(equipmentContainer) instead")
    fun removeCurrentContainer(equipmentContainer: EquipmentContainer): Boolean = currentContainers.remove(equipmentContainer)

    @Deprecated("BOILERPLATE: Use currentContainers.clear() instead")
    fun clearCurrentContainers(): Equipment {
        currentContainers.clear()
        return this
    }

    /**
     * The usage points for this equipment. The returned collection is read only.
     */
    val usagePoints: MRIDListWrapper<UsagePoint>
        get() = MRIDListWrapper(
            getter = { _usagePoints },
            setter = { _usagePoints = it })

    @Deprecated("BOILERPLATE: Use usagePoints.size instead")
    fun numUsagePoints(): Int = usagePoints.size

    @Deprecated("BOILERPLATE: Use usagePoints.getByMRID(mRID) instead")
    fun getUsagePoint(mRID: String): UsagePoint? = usagePoints.getByMRID(mRID)

    @Deprecated("BOILERPLATE: Use usagePoints.add(usagePoint) instead")
    fun addUsagePoint(usagePoint: UsagePoint): Equipment {
        usagePoints.add(usagePoint)
        return this
    }

    @Deprecated("BOILERPLATE: Use usagePoints.remove(usagePoint) instead")
    fun removeUsagePoint(usagePoint: UsagePoint): Boolean = usagePoints.remove(usagePoint)

    @Deprecated("BOILERPLATE: Use usagePoints.clear() instead")
    fun clearUsagePoints(): Equipment {
        usagePoints.clear()
        return this
    }

    /**
     *  [OperationalRestriction]'s that this equipment is associated with. The returned collection is read only.
     */
    val operationalRestrictions: MRIDListWrapper<OperationalRestriction>
        get() = MRIDListWrapper(
            getter = { _operationalRestrictions },
            setter = { _operationalRestrictions = it })

    @Deprecated("BOILERPLATE: Use operationalRestrictions.size instead")
    fun numOperationalRestrictions(): Int = operationalRestrictions.size

    @Deprecated("BOILERPLATE: Use operationalRestrictions.getByMRID(mRID) instead")
    fun getOperationalRestriction(mRID: String): OperationalRestriction? = operationalRestrictions.getByMRID(mRID)


    @Deprecated("BOILERPLATE: Use operationalRestrictions.add(restriction) instead")
    fun addOperationalRestriction(restriction: OperationalRestriction): Equipment {
        operationalRestrictions.add(restriction)
        return this
    }

    @Deprecated("BOILERPLATE: Use operationalRestrictions.remove(restriction) instead")
    fun removeOperationalRestriction(restriction: OperationalRestriction): Boolean = operationalRestrictions.remove(restriction)

    @Deprecated("BOILERPLATE: Use operationalRestrictions.clear() instead")
    fun clearOperationalRestrictions(): Equipment {
        operationalRestrictions.clear()
        return this
    }

    private inline fun <reified T : EquipmentContainer> List<*>?.ofType(): List<T> = this?.filterIsInstance(T::class.java) ?: emptyList()
}
