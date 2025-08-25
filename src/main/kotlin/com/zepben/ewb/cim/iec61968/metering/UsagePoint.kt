/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.metering

import com.zepben.ewb.cim.extensions.ZBEX
import com.zepben.ewb.cim.iec61968.common.Location
import com.zepben.ewb.cim.iec61970.base.core.Equipment
import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.cim.iec61970.base.core.PhaseCode
import com.zepben.ewb.services.common.extensions.asUnmodifiable
import com.zepben.ewb.services.common.extensions.getByMRID
import com.zepben.ewb.services.common.extensions.validateReference


/**
 * Logical or physical point in the network to which readings or events may be attributed. Used at the place where a physical
 * or virtual meter may be located; however, it is not required that a meter be present.
 *
 * @property usagePointLocation Service location where the service delivered by this usage point is consumed.
 * @property isVirtual If true, this usage point is virtual, i.e., no physical location exists in the network where a meter could be located to
 *                     collect the meter readings. For example, one may define a virtual usage point to serve as an aggregation of usage for all
 *                     of a company's premises distributed widely across the distribution territory. Otherwise, the usage point is physical,
 *                     i.e., there is a logical point in the network where a meter could be located to collect meter readings.
 * @property connectionCategory A code used to specify the connection category, e.g., low voltage or low pressure, where the usage point is defined.
 * @property ratedPower Active power that this usage point is configured to deliver in watts.
 * @property approvedInverterCapacity [ZBEX] ]The approved inverter capacity at this UsagePoint in volt-amperes.
 * @property phaseCode Phase code. Number of wires and specific nominal phases can be deduced from enumeration literal values. For example, ABCN is three-phase,
 *                     four-wire, s12n (splitSecondary12N) is single-phase, three-wire, and s1n and s2n are single-phase, two-wire.
 */
class UsagePoint @JvmOverloads constructor(mRID: String = "") : IdentifiedObject(mRID) {

    var usagePointLocation: Location? = null
    var isVirtual: Boolean? = null
    var connectionCategory: String? = null
    var ratedPower: Int? = null

    @ZBEX
    var approvedInverterCapacity: Int? = null
    var phaseCode: PhaseCode = PhaseCode.NONE

    private var _equipment: MutableList<Equipment>? = null
    private var _endDevices: MutableList<EndDevice>? = null

    /**
     *  All equipment connecting this usage point to the electrical grid. The returned collection is read only
     */
    val equipment: Collection<Equipment> get() = _equipment.asUnmodifiable()

    /**
     * Get the number of entries in the [Equipment] collection.
     */
    fun numEquipment(): Int = _equipment?.size ?: 0

    /**
     * All equipment connecting this usage point to the electrical grid.
     *
     * @param mRID the mRID of the required [Equipment]
     * @return The [Equipment] with the specified [mRID] if it exists, otherwise null
     */
    fun getEquipment(mRID: String): Equipment? = _equipment?.getByMRID(mRID)

    fun addEquipment(equipment: Equipment): UsagePoint {
        if (validateReference(equipment, ::getEquipment, "An Equipment"))
            return this

        _equipment = _equipment ?: mutableListOf()
        _equipment!!.add(equipment)

        return this
    }

    fun removeEquipment(equipment: Equipment): Boolean {
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
    fun numEndDevices(): Int = _endDevices?.size ?: 0

    /**
     * All end devices at this usage point.
     *
     * @param mRID the mRID of the required [EndDevice]
     * @return The [EndDevice] with the specified [mRID] if it exists, otherwise null
     */
    fun getEndDevice(mRID: String): EndDevice? = _endDevices?.getByMRID(mRID)

    fun addEndDevice(endDevice: EndDevice): UsagePoint {
        if (validateReference(endDevice, ::getEndDevice, "An EndDevice"))
            return this

        _endDevices = _endDevices ?: mutableListOf()
        _endDevices!!.add(endDevice)

        return this
    }

    fun removeEndDevice(endDevice: EndDevice): Boolean {
        val ret = _endDevices?.remove(endDevice) == true
        if (_endDevices.isNullOrEmpty()) _endDevices = null
        return ret
    }

    fun clearEndDevices(): UsagePoint {
        _endDevices = null
        return this
    }
}
