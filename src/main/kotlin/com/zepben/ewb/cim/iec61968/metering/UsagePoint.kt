/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.metering

import com.zepben.ewb.boilerplate.LazyMridList
import com.zepben.ewb.cim.extensions.ZBEX
import com.zepben.ewb.cim.extensions.iec61968.common.ContactDetails
import com.zepben.ewb.cim.iec61968.common.Location
import com.zepben.ewb.cim.iec61970.base.core.Equipment
import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.cim.iec61970.base.core.PhaseCode
import com.zepben.ewb.services.common.extensions.asUnmodifiable
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
 * @property approvedInverterCapacity [ZBEX] The approved inverter capacity at this UsagePoint in volt-amperes.
 * @property phaseCode Phase code. Number of wires and specific nominal phases can be deduced from enumeration literal values. For example, ABCN is three-phase,
 *                     four-wire, s12n (splitSecondary12N) is single-phase, three-wire, and s1n and s2n are single-phase, two-wire.
 * @property contacts [ZBEX] All contact details for this UsagePoint.
 */
class UsagePoint(mRID: String) : IdentifiedObject(mRID) {

    var usagePointLocation: Location? = null
    var isVirtual: Boolean? = null
    var connectionCategory: String? = null
    var ratedPower: Int? = null

    @ZBEX
    var approvedInverterCapacity: Int? = null
    var phaseCode: PhaseCode = PhaseCode.NONE

    private var _equipment: MutableList<Equipment>? = null
    private var _endDevices: MutableList<EndDevice>? = null
    private var _contacts: MutableList<ContactDetails>? = null

    /**
     *  All equipment connecting this usage point to the electrical grid. The returned collection is read only
     */
    val equipment: LazyMridList<Equipment> get() = LazyMridList(
        getter = { _equipment },
        setter = { _equipment = it },
        owner = this,
        elementDescription = "An Equipment"
    )

    /**
     * All end devices at this usage point. The returned collection is read only.
     */
    val endDevices: LazyMridList<EndDevice> get() = LazyMridList(
        getter = { _endDevices },
        setter = { _endDevices = it },
        owner = this,
        elementDescription = "An EndDevice"
    )

    /**
     * Add an [Equipment] to this [UsagePoint].
     *
     * @param equipment The [Equipment] to add.
     * @return This [UsagePoint] for fluent use.
     */
    fun addEquipment(equipment: Equipment): UsagePoint {
        if (validateReference(equipment, ::getEquipment, "An Equipment"))
            return this

        _equipment = _equipment ?: mutableListOf()
        _equipment!!.add(equipment)

        return this
    }

    /**
     * Add an [EndDevice] to this [UsagePoint].
     *
     * @param endDevice The [EndDevice] to add.
     * @return This [UsagePoint] for fluent use.
     */
    fun addEndDevice(endDevice: EndDevice): UsagePoint {
        if (validateReference(endDevice, ::getEndDevice, "An EndDevice"))
            return this

        _endDevices = _endDevices ?: mutableListOf()
        _endDevices!!.add(endDevice)

        return this
    }

    /**
     * Get the number of entries in the [ContactDetails] collection.
     */
    fun numContacts(): Int = _contacts?.size ?: 0

    /**
     * All end devices at this usage point.
     *
     * @param id the ID of the required [ContactDetails]
     * @return The [ContactDetails] with the specified [id] if it exists, otherwise null
     */
    fun getContact(id: String): ContactDetails? = _contacts?.firstOrNull { it.id == id }

    /**
     * Add a [ContactDetails] to this [UsagePoint].
     *
     * @param contact The [ContactDetails] to add.
     * @return This [UsagePoint] for fluent use.
     */
    fun addContact(contact: ContactDetails): UsagePoint {
        if (validateReference(contact, ::getContact, "A ContactDetails"))
            return this

        _contacts = _contacts ?: mutableListOf()
        _contacts!!.add(contact)

        return this
    }

    /**
     * Remove a [ContactDetails] from this [UsagePoint].
     *
     * @param contact The [ContactDetails] to remove.
     * @return true if the [ContactDetails] were removed.
     */
    fun removeContact(contact: ContactDetails): Boolean {
        val ret = _contacts?.remove(contact) == true
        if (_contacts.isNullOrEmpty()) _contacts = null
        return ret
    }

    /**
     * Clear all [ContactDetails] from this [UsagePoint].
     *
     * @return This [UsagePoint] for fluent use.
     */
    fun clearContacts(): UsagePoint {
        _contacts = null
        return this
    }

    // region deprecated list boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding list.

    @Deprecated(
        message = "Use equipment.size instead.",
        replaceWith = ReplaceWith("equipment.size")
    )
    fun numEquipment(): Int = equipment.size

    @Deprecated(
        message = "Use equipment.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("equipment.getByMRID(mRID)")
    )
    fun getEquipment(mRID: String): Equipment? = equipment.getByMrid(mRID)

    @Deprecated(
        message = "Use this.equipment.remove(equipment) instead.",
        replaceWith = ReplaceWith("this.equipment.remove(equipment)")
    )
    fun removeEquipment(equipment: Equipment): Boolean = this.equipment.remove(equipment)

    @Deprecated(
        message = "Use equipment.clear() instead.",
        replaceWith = ReplaceWith("equipment.clear()")
    )
    fun clearEquipment(): UsagePoint {
        equipment.clear()
        return this
    }

    @Deprecated(
        message = "Use endDevices.size instead.",
        replaceWith = ReplaceWith("endDevices.size")
    )
    fun numEndDevices(): Int = endDevices.size

    @Deprecated(
        message = "Use endDevices.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("endDevices.getByMRID(mRID)")
    )
    fun getEndDevice(mRID: String): EndDevice? = endDevices.getByMrid(mRID)

    @Deprecated(
        message = "Use endDevices.remove(endDevice) instead.",
        replaceWith = ReplaceWith("endDevices.remove(endDevice)")
    )
    fun removeEndDevice(endDevice: EndDevice): Boolean = endDevices.remove(endDevice)

    @Deprecated(
        message = "Use endDevices.clear() instead.",
        replaceWith = ReplaceWith("endDevices.clear()")
    )
    fun clearEndDevices(): UsagePoint {
        endDevices.clear()
        return this
    }

    @Deprecated(
        message = "Use this.equipment.add(equipment) instead.",
        replaceWith = ReplaceWith("also { it.equipment.add(equipment) }")
    )
    fun addEquipment(equipment: Equipment): UsagePoint {
        this.equipment.add(equipment)
        return this
    }

    @Deprecated(
        message = "Use endDevices.add(endDevice) instead.",
        replaceWith = ReplaceWith("also { it.endDevices.add(endDevice) }")
    )
    fun addEndDevice(endDevice: EndDevice): UsagePoint {
        endDevices.add(endDevice)
        return this
    }

    // endregion
}
