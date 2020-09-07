/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.cim.iec61968.metering

import com.zepben.cimbend.cim.iec61968.common.Location
import com.zepben.cimbend.cim.iec61970.base.core.Equipment
import com.zepben.cimbend.cim.iec61970.base.core.IdentifiedObject
import com.zepben.cimbend.common.extensions.asUnmodifiable
import com.zepben.cimbend.common.extensions.getByMRID
import com.zepben.cimbend.common.extensions.validateReference

/**
 * Logical or physical point in the network to which readings or events may be attributed. Used at the place where a physical
 * or virtual meter may be located; however, it is not required that a meter be present.
 *
 * @property usagePointLocation Service location where the service delivered by this usage point is consumed.
 */
class UsagePoint @JvmOverloads constructor(mRID: String = "") : IdentifiedObject(mRID) {

    var usagePointLocation: Location? = null
    private var _equipment: MutableList<Equipment>? = null
    private var _endDevices: MutableList<EndDevice>? = null

    /**
     *  All equipment connecting this usage point to the electrical grid. The returned collection is read only
     */
    val equipment: Collection<Equipment> get() = _equipment.asUnmodifiable()

    /**
     * Get the number of entries in the [Equipment] collection.
     */
    fun numEquipment() = _equipment?.size ?: 0

    /**
     * All equipment connecting this usage point to the electrical grid.
     *
     * @param mRID the mRID of the required [Equipment]
     * @return The [Equipment] with the specified [mRID] if it exists, otherwise null
     */
    fun getEquipment(mRID: String) = _equipment?.getByMRID(mRID)

    fun addEquipment(equipment: Equipment): UsagePoint {
        if (validateReference(equipment, ::getEquipment, "An Equipment"))
            return this

        _equipment = _equipment ?: mutableListOf()
        _equipment!!.add(equipment)

        return this
    }

    fun removeEquipment(equipment: Equipment?): Boolean {
        val ret = _equipment?.remove(equipment) == true
        if (_equipment.isNullOrEmpty()) _equipment = null
        return ret
    }

    fun clearEquipment(): UsagePoint {
        _equipment = null
        return this
    }

    /**
     * All end devices at this usage point. The returned collection is read only.
     */
    val endDevices: Collection<EndDevice> get() = _endDevices.asUnmodifiable()

    /**
     * Get the number of entries in the [EndDevice] collection.
     */
    fun numEndDevices() = _endDevices?.size ?: 0

    /**
     * All end devices at this usage point.
     *
     * @param mRID the mRID of the required [EndDevice]
     * @return The [EndDevice] with the specified [mRID] if it exists, otherwise null
     */
    fun getEndDevice(mRID: String) = _endDevices?.getByMRID(mRID)

    fun addEndDevice(endDevice: EndDevice): UsagePoint {
        if (validateReference(endDevice, ::getEndDevice, "An EndDevice"))
            return this

        _endDevices = _endDevices ?: mutableListOf()
        _endDevices!!.add(endDevice)

        return this
    }

    fun removeEndDevice(endDevice: EndDevice?): Boolean {
        val ret = _endDevices?.remove(endDevice) == true
        if (_endDevices.isNullOrEmpty()) _endDevices = null
        return ret
    }

    fun clearEndDevices(): UsagePoint {
        _endDevices = null
        return this
    }
}
