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
import com.zepben.ewb.testing.MRIDListWrapper

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
    val organisationRoles: MRIDListWrapper<AssetOrganisationRole>
        get() = MRIDListWrapper(
            getter = { _organisationRoles },
            setter = { _organisationRoles = it })

    /**
     * All power system resources used to electrically model this asset. For example, transformer asset is electrically modelled with a transformer and its
     * windings and tap changer.
     */
    val powerSystemResources: MRIDListWrapper<PowerSystemResource>
        get() = MRIDListWrapper(
            getter = { _powerSystemResources },
            setter = { _powerSystemResources = it })

    @Deprecated("BOILERPLATE: Use organisationRoles.size instead")
    fun numOrganisationRoles(): Int = organisationRoles.size

    @Deprecated("BOILERPLATE: Use organisationRoles.getByMRID(mRID) instead")
    fun getOrganisationRole(mRID: String): AssetOrganisationRole? = organisationRoles.getByMRID(mRID)

    @Deprecated("BOILERPLATE: Use organisationRoles.add(organisationRole) instead")
    fun addOrganisationRole(organisationRole: AssetOrganisationRole): Asset {
        organisationRoles.add(organisationRole)
        return this
    }

    @Deprecated("BOILERPLATE: Use organisationRoles.remove(organisationRole) instead")
    fun removeOrganisationRole(organisationRole: AssetOrganisationRole): Boolean = organisationRoles.remove(organisationRole)

    fun clearOrganisationRoles(): Asset {
        _organisationRoles = null
        return this
    }

    @Deprecated("BOILERPLATE: Use powerSystemResources.size instead")
    fun numPowerSystemResources(): Int = powerSystemResources.size

    @Deprecated("BOILERPLATE: Use powerSystemResources.getByMRID(mRID) instead")
    fun getPowerSystemResource(mRID: String): PowerSystemResource? = powerSystemResources.getByMRID(mRID)

    @Deprecated("BOILERPLATE: Use powerSystemResources.add(powerSystemResource) instead")
    fun addPowerSystemResource(powerSystemResource: PowerSystemResource): Asset {
        powerSystemResources.add(powerSystemResource)
        return this
    }

    @Deprecated("BOILERPLATE: Use powerSystemResources.remove(powerSystemResource) instead")
    fun removePowerSystemResource(powerSystemResource: PowerSystemResource): Boolean = powerSystemResources.remove(powerSystemResource)

    @Deprecated("BOILERPLATE: Use powerSystemResources.clear() instead")
    fun clearPowerSystemResources(): Asset {
        powerSystemResources.clear()
        return this
    }

}
