/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects

import com.zepben.ewb.cim.extensions.ZBEX
import com.zepben.ewb.cim.iec61968.operations.OperationalRestriction
import com.zepben.ewb.cim.iec61970.base.core.Equipment
import com.zepben.ewb.services.common.extensions.asUnmodifiable
import com.zepben.ewb.services.common.extensions.getByMRID
import com.zepben.ewb.services.common.extensions.validateReference
import java.time.Instant

/**
 * [ZBEX] A grouping of network model stages. Primarily used to organize the stages of an overall project.
 *
 * @property externalStatus [ZBEX] The status of the project in the external system.
 * @property forecastCommissionDate [ZBEX] When the project is expected to be commissioned.
 * @property externalDriver [ZBEX] The driver of the project.
 * @property children [ZBEX] Contained NetworkModelProjectComponent classes of this Project.
 */
@ZBEX
class NetworkModelProject(mRID: String) : NetworkModelProjectComponent(mRID) {
    private var _children: MutableList<NetworkModelProjectComponent>? = null

    @ZBEX var externalStatus: String? = null
        set(it) {
            updated = Instant.now()
            field = it
        }
    @ZBEX var forecastCommissionDate: Instant? = null
        set(it) {
            updated = Instant.now()
            field = it
        }
    @ZBEX var externalDriver: String? = null
        set(it) {
            updated = Instant.now()
            field = it
        }
    @ZBEX val children: Collection<NetworkModelProjectComponent> get() = _children.asUnmodifiable()


    /**
     * Get the number of entries in the [children] collection.
     */
    fun numChildren(): Int = _children?.size ?: 0

    /**
     * Get the child [NetworkModelProjectComponent] identified by [mRID]
     *
     * @param mRID the mRID of the required [NetworkModelProjectComponent]
     * @return The [NetworkModelProjectComponent] with the specified [mRID] if it exists, otherwise null
     */
    fun getChild(mRID: String): NetworkModelProjectComponent? = _children?.firstOrNull { it.mRID == mRID }

    /**
     * Add equipment to which this restriction applies.
     *
     * @param equipment the equipment to add.
     * @return A reference to this [OperationalRestriction] to allow fluent use.
     */
    fun addChild(equipment: NetworkModelProjectComponent): NetworkModelProject {
        if (validateReference(equipment, ::getChild, "A NetworkModelProjectComponent"))
            return this

        _children = _children ?: mutableListOf()
        _children!!.add(equipment)

        updated = Instant.now()
        return this
    }

    fun removeChild(child: NetworkModelProjectComponent): Boolean {
        val ret = _children?.remove(child) == true
        if (_children.isNullOrEmpty()) _children = null
        updated = Instant.now()
        return ret
    }

    /**
     * Clear the collection of [NetworkModelProjectComponent]s.
     *
     * @return A reference to this [NetworkModelProject] to allow fluent use.
     */
    fun clearChildren(): NetworkModelProject {
        _children = null
        updated = Instant.now()
        return this
    }

}
