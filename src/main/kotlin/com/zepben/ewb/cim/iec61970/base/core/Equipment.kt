/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.core

import com.zepben.ewb.boilerplate.LazyMridList
import com.zepben.ewb.boilerplate.MridCollection
import com.zepben.ewb.cim.extensions.iec61970.base.core.Site
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvFeeder
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvSubstation
import com.zepben.ewb.cim.iec61968.metering.UsagePoint
import com.zepben.ewb.cim.iec61968.operations.OperationalRestriction
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

    @Deprecated(
        message = "Use containers.size instead.",
        replaceWith = ReplaceWith("containers.size")
    )
    fun numContainers(): Int = containers.size

    @Deprecated(
        message = "Use containers.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("containers.getByMRID(mRID)")
    )
    fun getContainer(mRID: String): EquipmentContainer? = containers.getByMrid(mRID)

    @Deprecated(
        message = "Use containers.add(equipmentContainer) instead.",
        replaceWith = ReplaceWith("also { it.containers.add(equipmentContainer) }")
    )
    fun addContainer(equipmentContainer: EquipmentContainer): Equipment {
        containers.add(equipmentContainer)
        return this
    }

    @Deprecated(
        message = "Use containers.remove(equipmentContainer) instead.",
        replaceWith = ReplaceWith("containers.remove(equipmentContainer)")
    )
    fun removeContainer(equipmentContainer: EquipmentContainer): Boolean = containers.remove(equipmentContainer)

    @Deprecated(
        message = "Use containers.clear() instead.",
        replaceWith = ReplaceWith("containers.clear()")
    )
    fun clearContainers(): Equipment {
        containers.clear()
        return this
    }

    // endregion

    // region currentContainers boilerplate

    @Deprecated(
        message = "Use currentContainers.size instead.",
        replaceWith = ReplaceWith("currentContainers.size")
    )
    fun numCurrentContainers(): Int = currentContainers.size

    @Deprecated(
        message = "Use currentContainers.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("currentContainers.getByMRID(mRID)")
    )
    fun getCurrentContainer(mRID: String): EquipmentContainer? = currentContainers.getByMrid(mRID)

    @Deprecated(
        message = "Use currentContainers.add(equipmentContainer) instead.",
        replaceWith = ReplaceWith("also { it.currentContainers.add(equipmentContainer) }")
    )
    fun addCurrentContainer(equipmentContainer: EquipmentContainer): Equipment {
        currentContainers.add(equipmentContainer)
        return this
    }

    @Deprecated(
        message = "Use currentContainers.remove(equipmentContainer) instead.",
        replaceWith = ReplaceWith("currentContainers.remove(equipmentContainer)")
    )
    fun removeCurrentContainer(equipmentContainer: EquipmentContainer): Boolean = currentContainers.remove(equipmentContainer)

    @Deprecated(
        message = "Use currentContainers.clear() instead.",
        replaceWith = ReplaceWith("currentContainers.clear()")
    )
    fun clearCurrentContainers(): Equipment {
        currentContainers.clear()
        return this
    }

    // endregion

    // region usagePoints boilerplate

    @Deprecated(
        message = "Use usagePoints.size instead.",
        replaceWith = ReplaceWith("usagePoints.size")
    )
    fun numUsagePoints(): Int = usagePoints.size

    @Deprecated(
        message = "Use usagePoints.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("usagePoints.getByMRID(mRID)")
    )
    fun getUsagePoint(mRID: String): UsagePoint? = usagePoints.getByMrid(mRID)

    @Deprecated(
        message = "Use usagePoints.add(usagePoint) instead.",
        replaceWith = ReplaceWith("also { it.usagePoints.add(usagePoint) }")
    )
    fun addUsagePoint(usagePoint: UsagePoint): Equipment {
        usagePoints.add(usagePoint)
        return this
    }

    @Deprecated(
        message = "Use usagePoints.remove(usagePoint) instead.",
        replaceWith = ReplaceWith("usagePoints.remove(usagePoint)")
    )
    fun removeUsagePoint(usagePoint: UsagePoint): Boolean = usagePoints.remove(usagePoint)

    @Deprecated(
        message = "Use usagePoints.clear() instead.",
        replaceWith = ReplaceWith("usagePoints.clear()")
    )
    fun clearUsagePoints(): Equipment {
        usagePoints.clear()
        return this
    }

    // endregion

    // region operationalRestrictions boilerplate

    @Deprecated(
        message = "Use operationalRestrictions.size instead.",
        replaceWith = ReplaceWith("operationalRestrictions.size")
    )
    fun numOperationalRestrictions(): Int = operationalRestrictions.size

    @Deprecated(
        message = "Use operationalRestrictions.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("operationalRestrictions.getByMRID(mRID)")
    )
    fun getOperationalRestriction(mRID: String): OperationalRestriction? = operationalRestrictions.getByMrid(mRID)

    @Deprecated(
        message = "Use operationalRestrictions.add(restriction) instead.",
        replaceWith = ReplaceWith("also { it.operationalRestrictions.add(restriction) }")
    )
    fun addOperationalRestriction(restriction: OperationalRestriction): Equipment {
        operationalRestrictions.add(restriction)
        return this
    }

    @Deprecated(
        message = "Use operationalRestrictions.remove(restriction) instead.",
        replaceWith = ReplaceWith("operationalRestrictions.remove(restriction)")
    )
    fun removeOperationalRestriction(restriction: OperationalRestriction): Boolean = operationalRestrictions.remove(restriction)

    @Deprecated(
        message = "Use operationalRestrictions.clear() instead.",
        replaceWith = ReplaceWith("operationalRestrictions.clear()")
    )
    fun clearOperationalRestrictions(): Equipment {
        operationalRestrictions.clear()
        return this
    }

    // endregion

    // endregion
}
