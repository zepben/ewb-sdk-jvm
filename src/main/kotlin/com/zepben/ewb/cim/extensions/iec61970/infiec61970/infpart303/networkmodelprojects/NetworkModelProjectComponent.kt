/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects

import com.zepben.ewb.cim.extensions.ZBEX
import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import java.time.Instant

/**
 * [ZBEX] Abstract class for both a network model project and network model change.
 *
 * @property created [ZBEX] When the component was created.
 * @property updated [ZBEX] When the component was last updated.
 * @property closed [ZBEX] When the component was deleted.
 * @property parent [ZBEX] The contained Network Model Project Component (Restricted to just NetworkModelProject)
 */
@ZBEX
abstract class NetworkModelProjectComponent(mRID: String) : IdentifiedObject(mRID) {
    private var _parent: NetworkModelProject? = null

    @ZBEX var created: Instant? = null
    @ZBEX var updated: Instant? = null
    @ZBEX var closed: Instant? = null
    @ZBEX val parent: NetworkModelProject? get() = _parent

    fun setParent(networkModelProject: NetworkModelProject): NetworkModelProjectComponent {
        // TODO: Validation
        _parent = networkModelProject
        return this
    }

    /**
     * Delete this [NetworkModelProject]
     *
     * @return true if the [NetworkModelProjectComponent] was deleted, false otherwise.
     */
    fun delete(): Boolean {
        require(closed == null) {
            "${javaClass.simpleName} already marked as deleted."
            return false
        }

        closed = Instant.now()
        return true
    }

    // TODO: function to update `_updated` whenever an applicable attribute is modified, should this propogate from attributes of attributes?
}