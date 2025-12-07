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
 */
@ZBEX
abstract class NetworkModelProjectComponent (
    mRID: String,
    created: Instant = Instant.now(),
    updated: Instant? = null,
    closed: Instant? = null,
    parent: NetworkModelProject? = null
) : IdentifiedObject(mRID) {

    private var _created: Instant = created
    private var _updated: Instant? = updated
    private var _closed: Instant? = closed
    private var _parent: NetworkModelProject? = parent

    /**
     * [ZBEX] When the component was created.
     */
    @ZBEX
    val created: Instant get() = _created

    /**
     * [ZBEX] When the component was last updated.
     */
    @ZBEX
    val updated: Instant? get() = _updated

    /**
     * [ZBEX] When the component was deleted.
     */
    @ZBEX
    val closed: Instant? get() = _closed

    /**
     * [ZBEX] The contained Network Model Project Component (Restricted to just NetworkModelProject)
     */
    @ZBEX
    var parent: NetworkModelProject? = _parent

    /**
     * Delete this [NetworkModelProject]
     *
     * @return true if the [NetworkModelProjectComponent] was deleted, false otherwise.
     */
    fun delete(): Boolean {
        require(_closed == null) {
            "${javaClass.simpleName} already marked as deleted."
            return false
        }

        _closed = Instant.now()
        return true
    }

    // TODO: function to update `_updated` whenever an applicable attribute is modified, should this propogate from attributes of attributes?
}