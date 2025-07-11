/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.assets

import com.zepben.ewb.cim.iec61968.common.Location
import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.cim.iec61970.base.core.PowerSystemResource
import com.zepben.ewb.services.common.extensions.asUnmodifiable
import com.zepben.ewb.services.common.extensions.getByMRID
import com.zepben.ewb.services.common.extensions.safeRemove
import com.zepben.ewb.services.common.extensions.validateReference

/**
 * Tangible resource of the utility, including power system equipment, various end devices, cabinets, buildings, etc. For electrical
 * network equipment, the role of the asset is defined through PowerSystemResource and its subclasses, defined mainly in the Wires
 * model (refer to IEC61970-301 and model package IEC61970::Wires). Asset description places emphasis on the physical characteristics
 * of the equipment fulfilling that role.
 */
abstract class Asset(mRID: String = "") : IdentifiedObject(mRID) {

    private var _organisationRoles: MutableList<AssetOrganisationRole>? = null
    private var _powerSystemResources: MutableList<PowerSystemResource>? = null

    /**
     * Location of this asset.
     */
    var location: Location? = null

    /**
     * All roles an organisation plays for this asset. The returned collection is read only.
     */
    val organisationRoles: Collection<AssetOrganisationRole> get() = _organisationRoles.asUnmodifiable()

    /**
     * All power system resources used to electrically model this asset. For example, transformer asset is electrically modelled with a transformer and its
     * windings and tap changer.
     */
    val powerSystemResources: Collection<PowerSystemResource> get() = _powerSystemResources.asUnmodifiable()

    /**
     * Get the number of entries in the [AssetOrganisationRole] collection.
     */
    fun numOrganisationRoles(): Int = _organisationRoles?.size ?: 0

    /**
     * All roles an organisation plays for this asset.
     *
     * @param mRID the mRID of the required [AssetOrganisationRole]
     * @return The [AssetOrganisationRole] with the specified [mRID] if it exists, otherwise null
     */
    fun getOrganisationRole(mRID: String): AssetOrganisationRole? = _organisationRoles.getByMRID(mRID)

    /**
     * @param organisationRole the [AssetOrganisationRole] to associate with this [Asset].
     * @return A reference to this [Asset] to allow fluent use.
     */
    fun addOrganisationRole(organisationRole: AssetOrganisationRole): Asset {
        if (validateReference(organisationRole, ::getOrganisationRole, "An AssetOrganisationRole"))
            return this

        _organisationRoles = _organisationRoles ?: mutableListOf()
        _organisationRoles!!.add(organisationRole)

        return this
    }

    /**
     * @param organisationRole the [AssetOrganisationRole] to disassociate with this [Asset].
     * @return true if the organisation role is disassociated.
     */
    fun removeOrganisationRole(organisationRole: AssetOrganisationRole): Boolean {
        val ret = _organisationRoles.safeRemove(organisationRole)
        if (_organisationRoles.isNullOrEmpty()) _organisationRoles = null
        return ret
    }

    fun clearOrganisationRoles(): Asset {
        _organisationRoles = null
        return this
    }

    /**
     * Get the number of entries in the [PowerSystemResource] collection.
     */
    fun numPowerSystemResources(): Int = _powerSystemResources?.size ?: 0

    /**
     * Get a [PowerSystemResource]s associated with this [Asset]
     *
     * @param mRID the mRID of the required [PowerSystemResource]
     * @return The [PowerSystemResource] with the specified [mRID] if it exists, otherwise null
     */
    fun getPowerSystemResource(mRID: String): PowerSystemResource? = _powerSystemResources.getByMRID(mRID)

    /**
     * Add a [PowerSystemResource] to this [Asset]
     *
     * @param powerSystemResource the [PowerSystemResource] to associate with this [Asset].
     * @return A reference to this [Asset] to allow fluent use.
     */
    fun addPowerSystemResource(powerSystemResource: PowerSystemResource): Asset {
        if (validateReference(powerSystemResource, ::getPowerSystemResource, "A PowerSystemResource"))
            return this

        _powerSystemResources = _powerSystemResources ?: mutableListOf()
        _powerSystemResources!!.add(powerSystemResource)

        return this
    }

    /**
     * @param powerSystemResource the [PowerSystemResource] to disassociate from this [Asset].
     * @return true if the [PowerSystemResource] is disassociated.
     */
    fun removePowerSystemResource(powerSystemResource: PowerSystemResource): Boolean {
        val ret = _powerSystemResources.safeRemove(powerSystemResource)
        if (_powerSystemResources.isNullOrEmpty()) _powerSystemResources = null
        return ret
    }

    /**
     * Remove all [PowerSystemResource]s from this [Asset]
     * @return A reference to this [Asset] to allow fluent use.
     */
    fun clearPowerSystemResources(): Asset {
        _powerSystemResources = null
        return this
    }

}
