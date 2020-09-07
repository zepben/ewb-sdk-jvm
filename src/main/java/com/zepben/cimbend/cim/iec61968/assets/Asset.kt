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
package com.zepben.cimbend.cim.iec61968.assets

import com.zepben.cimbend.cim.iec61968.common.Location
import com.zepben.cimbend.cim.iec61970.base.core.IdentifiedObject
import com.zepben.cimbend.common.extensions.asUnmodifiable
import com.zepben.cimbend.common.extensions.getByMRID
import com.zepben.cimbend.common.extensions.safeRemove
import com.zepben.cimbend.common.extensions.validateReference

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
    fun numOrganisationRoles() = _organisationRoles?.size ?: 0

    /**
     * All roles an organisation plays for this asset.
     *
     * @param mRID the mRID of the required [AssetOrganisationRole]
     * @return The [AssetOrganisationRole] with the specified [mRID] if it exists, otherwise null
     */
    fun getOrganisationRole(mRID: String) = _organisationRoles.getByMRID(mRID)

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
