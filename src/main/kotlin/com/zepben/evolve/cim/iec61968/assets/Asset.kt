/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61968.assets

import com.zepben.evolve.cim.iec61968.common.Location
import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject
import com.zepben.evolve.services.common.extensions.asUnmodifiable
import com.zepben.evolve.services.common.extensions.getByMRID
import com.zepben.evolve.services.common.extensions.safeRemove
import com.zepben.evolve.services.common.extensions.validateReference

/**
 * Tangible resource of the utility, including power system equipment, various end devices, cabinets, buildings, etc. For electrical
 * network equipment, the role of the asset is defined through PowerSystemResource and its subclasses, defined mainly in the Wires
 * model (refer to IEC61970-301 and model package IEC61970::Wires). Asset description places emphasis on the physical characteristics
 * of the equipment fulfilling that role.
 */
abstract class Asset(mRID: String = "") : IdentifiedObject(mRID) {

    private var _organisationRoles: MutableList<AssetOrganisationRole>? = null

    /**
     * Location of this asset.
     */
    var location: Location? = null

    /**
     * All roles an organisation plays for this asset. The returned collection is read only.
     */
    val organisationRoles: Collection<AssetOrganisationRole> get() = _organisationRoles.asUnmodifiable()

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
    fun removeOrganisationRole(organisationRole: AssetOrganisationRole?): Boolean {
        val ret = _organisationRoles.safeRemove(organisationRole)
        if (_organisationRoles.isNullOrEmpty()) _organisationRoles = null
        return ret
    }

    fun clearOrganisationRoles(): Asset {
        _organisationRoles = null
        return this
    }
}
