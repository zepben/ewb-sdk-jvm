/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset

/**
 * An object already exists and is to be modified.
 *
 * @property objectReverseModification ObjectReverseModification specifying precondition properties for a preconditioned update.
 */
class ObjectModification(changeSet: ChangeSet, modifiedObjectMRID: String, originalObjectMRID: String) : ChangeSetMember(changeSet, modifiedObjectMRID) {

    init {
        changeSet.addMember(this)
    }

    val objectReverseModification: ObjectReverseModification = ObjectReverseModification(changeSet, originalObjectMRID, this)

}
