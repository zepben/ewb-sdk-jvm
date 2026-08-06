/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.assets

import com.zepben.ewb.boilerplate.collections.LazyMridList
import com.zepben.ewb.boilerplate.collections.MridCollection
import com.zepben.ewb.cim.iec61968.common.Location
import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.cim.iec61970.base.core.PowerSystemResource

/**
 * Tangible resource of the utility, including power system equipment, various end devices, cabinets, buildings, etc. For electrical
 * network equipment, the role of the asset is defined through PowerSystemResource and its subclasses, defined mainly in the Wires
 * model (refer to IEC61970-301 and model package IEC61970::Wires). Asset description places emphasis on the physical characteristics
 * of the equipment fulfilling that role.
 */
abstract class Asset(mRID: String) : IdentifiedObject(mRID) {

    private var _organisationRoles: MutableList<AssetOrganisationRole>? = null
    private var _powerSystemResources: MutableList<PowerSystemResource>? = null

    /**
     * Location of this asset.
     */
    var location: Location? = null

    /**
     * All roles an organisation plays for this asset. The returned collection is read only.
     */
    val organisationRoles: MridCollection<AssetOrganisationRole> get() = LazyMridList(
        getter = { _organisationRoles },
        setter = { _organisationRoles = it },
        owner = this,
        elementDescription = "An AssetOrganisationRole"
    )

    /**
     * All power system resources used to electrically model this asset. For example, transformer asset is electrically modelled with a transformer and its
     * windings and tap changer.
     */
    val powerSystemResources: MridCollection<PowerSystemResource> get() = LazyMridList(
        getter = { _powerSystemResources },
        setter = { _powerSystemResources = it },
        owner = this,
        elementDescription = "A PowerSystemResource"
    )

    // region deprecated list boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding list.

    // region organisationRoles boilerplate

    @Deprecated(
        message = "Use organisationRoles.size instead.",
        replaceWith = ReplaceWith("organisationRoles.size")
    )
    fun numOrganisationRoles(): Int = organisationRoles.size

    @Deprecated(
        message = "Use organisationRoles.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("organisationRoles.getByMRID(mRID)")
    )
    fun getOrganisationRole(mRID: String): AssetOrganisationRole? = organisationRoles.getByMrid(mRID)

    @Deprecated(
        message = "Use organisationRoles.add(organisationRole) instead.",
        replaceWith = ReplaceWith("also { it.organisationRoles.add(organisationRole) }")
    )
    fun addOrganisationRole(organisationRole: AssetOrganisationRole): Asset = apply {
        organisationRoles.add(organisationRole)
    }

    @Deprecated(
        message = "Use organisationRoles.remove(organisationRole) instead.",
        replaceWith = ReplaceWith("organisationRoles.remove(organisationRole)")
    )
    fun removeOrganisationRole(organisationRole: AssetOrganisationRole): Boolean = organisationRoles.remove(organisationRole)

    @Deprecated(
        message = "Use organisationRoles.clear() instead.",
        replaceWith = ReplaceWith("organisationRoles.clear()")
    )
    fun clearOrganisationRoles(): Asset = apply {
        organisationRoles.clear()
    }

    // endregion

    // region powerSystemResources boilerplate

    @Deprecated(
        message = "Use powerSystemResources.size instead.",
        replaceWith = ReplaceWith("powerSystemResources.size")
    )
    fun numPowerSystemResources(): Int = powerSystemResources.size

    @Deprecated(
        message = "Use powerSystemResources.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("powerSystemResources.getByMRID(mRID)")
    )
    fun getPowerSystemResource(mRID: String): PowerSystemResource? = powerSystemResources.getByMrid(mRID)

    @Deprecated(
        message = "Use powerSystemResources.add(powerSystemResource) instead.",
        replaceWith = ReplaceWith("also { it.powerSystemResources.add(powerSystemResource) }")
    )
    fun addPowerSystemResource(powerSystemResource: PowerSystemResource): Asset = apply {
        powerSystemResources.add(powerSystemResource)
    }

    @Deprecated(
        message = "Use powerSystemResources.remove(powerSystemResource) instead.",
        replaceWith = ReplaceWith("powerSystemResources.remove(powerSystemResource)")
    )
    fun removePowerSystemResource(powerSystemResource: PowerSystemResource): Boolean = powerSystemResources.remove(powerSystemResource)

    @Deprecated(
        message = "Use powerSystemResources.clear() instead.",
        replaceWith = ReplaceWith("powerSystemResources.clear()")
    )
    fun clearPowerSystemResources(): Asset = apply {
        powerSystemResources.clear()
    }

    // endregion

    // endregion
}
