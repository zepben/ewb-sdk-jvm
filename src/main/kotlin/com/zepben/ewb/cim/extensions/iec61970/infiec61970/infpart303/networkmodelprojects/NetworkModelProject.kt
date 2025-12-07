/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects

import com.zepben.ewb.cim.extensions.ZBEX
import com.zepben.ewb.services.common.extensions.asUnmodifiable
import com.zepben.ewb.services.common.extensions.getByMRID
import com.zepben.ewb.services.common.extensions.typeNameAndMRID
import java.time.Instant

/**
 * [ZBEX] A grouping of network model stages. Primarily used to organize the stages of an overall project.
 */
class NetworkModelProject(mRID: String) : NetworkModelProjectComponent(mRID) {

    var _children: MutableList<NetworkModelProjectComponent>? = null

    /**
     * [ZBEX] The status of the project in the external system.
     */
    @ZBEX
    var externalStatus: String? = null

    /**
     * [ZBEX] When the project is expected to be comissioned.
     */
    @ZBEX
    var forecastCommissionDate: Instant? = null

    /**
     * [ZBEX] The driver of the project.
     */
    @ZBEX
    var externalDriver: String? = null

    /**
     * [ZBEX] Contained NetworkModelProjectComponent classes of this Project.
     */
    @ZBEX
    val children: Collection<NetworkModelProjectComponent> get() = _children.asUnmodifiable()

    fun addChild(child: NetworkModelProjectComponent): NetworkModelProject {
        if (_children == null)
            _children = mutableListOf()

        if (_children.getByMRID(child.mRID) == null) {
            if (child.parent == null || child.parent === this) {
                _children!!.add(child)
                child.parent = this
            }
        }

        require(child.parent === this && _children.getByMRID(child.mRID) === child) {
            "Failed to add child [${child.typeNameAndMRID()} to ${javaClass.simpleName}."
        }

        return this
    }

}
