/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset

/**
 * A CRUD-style data object.
 *
 * @property changeSet [ChangeSet] this [ChangeSetMember] belongs to.
 * @property targetObjectMRID The registered CIM object.
 */
abstract class ChangeSetMember {

    private var _changeSet: ChangeSet? = null

    val changeSet: ChangeSet? get() = _changeSet
    var targetObjectMRID: String? = null

    internal fun setChangeSet(changeSet: ChangeSet): Boolean{
        require(this._changeSet == null) { "changeSet already exists" }
        this._changeSet = changeSet
        return true
    }

}
