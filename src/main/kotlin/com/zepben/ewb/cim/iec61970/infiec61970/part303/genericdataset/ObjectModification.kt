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
class ObjectModification(changeSet: ChangeSet, targetObjectMRID: String) : ChangeSetMember(changeSet, targetObjectMRID) {

    val objectReverseModification: ObjectReverseModification = ObjectReverseModification(changeSet, targetObjectMRID.asObjectReverseModificationId, this)

}

// TODO: move me somewhere if it gets used elsewhere.
internal val String.asObjectReverseModificationId: String get() = "-$this"

internal val String.asTargetObjectMRID: String get() = this.removePrefix("-")
