/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset

import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject

/**
 * A CRUD-style data object.
 *
 * @param changeSet [ChangeSet] this [ChangeSetMember] belongs to.
 * @param targetObject The registered CIM object.
 */
abstract class ChangeSetMember {
    /**
     * [ChangeSet] this [ChangeSetMember] belongs to.
     */
    private var _changeSet: ChangeSet? = null

    val getChangeSet: ChangeSet? get() = _changeSet

    /**
     * The registered CIM object.
     */
    var targetObject: IdentifiedObject? = null

    fun setChangeSet(changeSet: ChangeSet) {
        require(this._changeSet == null) { "changeSet already exists" }
        changeSet.addChangeSetMember(this)
        this._changeSet = changeSet
    }

}
