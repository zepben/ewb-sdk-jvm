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

    fun setChangeSet(changeSet: ChangeSet) {
        require(this._changeSet == null) { "changeSet already exists" }
        changeSet.addChangeSetMember(this)
        this._changeSet = changeSet
    }

}
