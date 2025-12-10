/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.metering

import com.zepben.ewb.cim.extensions.ZBEX
import com.zepben.ewb.cim.extensions.iec61968.common.ContactDetails
import com.zepben.ewb.cim.iec61968.common.Location
import com.zepben.ewb.cim.iec61970.base.core.Equipment
import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.cim.iec61970.base.core.PhaseCode
import com.zepben.ewb.services.common.extensions.asUnmodifiable
import com.zepben.ewb.services.common.extensions.getByMRID
import com.zepben.ewb.services.common.extensions.validateReference
import com.zepben.ewb.testing.ListWrapper
import com.zepben.ewb.testing.MRIDListWrapper


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
 * @property contacts [ZBEX] All contact details for this UsagePoint.
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
    private var _contacts: MutableList<ContactDetails>? = null

    /**
     *  All equipment connecting this usage point to the electrical grid. The returned collection is read only
     */
    val equipment: MRIDListWrapper<Equipment>
        get() = MRIDListWrapper(
            getter = { _equipment },
            setter = { _equipment = it })

    @Deprecated("BOILERPLATE: Use equipment.size instead")
    fun numEquipment(): Int = equipment.size

    @Deprecated("BOILERPLATE: Use equipment.getByMRID(mRID) instead")
    fun getEquipment(mRID: String): Equipment? = equipment.getByMRID(mRID)

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

    @Deprecated("BOILERPLATE: Use equipment.remove(equipment) instead")
    fun removeEquipment(equipment: Equipment): Boolean = this.equipment.remove(equipment)

    /**
     * Clear all [Equipment] from this [UsagePoint].
     *
     * @return This [UsagePoint] for fluent use.
     */
    fun clearEquipment(): UsagePoint {
        _equipment = null
        return this
    }

    /**
     * All end devices at this usage point. The returned collection is read only.
     */
    val endDevices: MRIDListWrapper<EndDevice>
        get() = MRIDListWrapper(
            getter = { _endDevices },
            setter = { _endDevices = it })

    @Deprecated("BOILERPLATE: Use endDevices.size instead")
    fun numEndDevices(): Int = endDevices.size

    @Deprecated("BOILERPLATE: Use endDevices.getByMRID(mRID) instead")
    fun getEndDevice(mRID: String): EndDevice? = endDevices.getByMRID(mRID)

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

    @Deprecated("BOILERPLATE: Use endDevices.remove(endDevice) instead")
    fun removeEndDevice(endDevice: EndDevice): Boolean = endDevices.remove(endDevice)

    /**
     * Clear all [EndDevice]'s from this [UsagePoint].
     *
     * @return This [UsagePoint] for fluent use.
     */
    fun clearEndDevices(): UsagePoint {
        _endDevices = null
        return this
    }

    @ZBEX
    val contacts: ListWrapper<ContactDetails>
        get() = ListWrapper(
            getter = { _contacts },
            setter = { _contacts = it })

    @Deprecated("BOILERPLATE: Use contacts.size instead")
    fun numContacts(): Int = contacts.size

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
        if (validateReference(contact, ContactDetails::id, ::getContact) { "A ContactDetails with ID ${contact.id}" })
            return this

        _contacts = _contacts ?: mutableListOf()
        _contacts!!.add(contact)

        return this
    }

    @Deprecated("BOILERPLATE: Use contacts.remove(contact) instead")
    fun removeContact(contact: ContactDetails): Boolean = contacts.remove(contact)

    /**
     * Clear all [ContactDetails] from this [UsagePoint].
     *
     * @return This [UsagePoint] for fluent use.
     */
    fun clearContacts(): UsagePoint {
        _contacts = null
        return this
    }

}
