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
 * An object already exists and is to be modified.
 *
 * @param changeSet [ChangeSet] this [ObjectModification] belongs to.
 * @param targetObject [IdentifiedObject] to be modified by this [ObjectModification].
 */
class ObjectModification(changeSet: ChangeSet, targetObject: IdentifiedObject) : ChangeSetMember(changeSet, targetObject) {

    /**
     * ObjectReverseModification specifying precondition properties for a preconditioned update.
     */
    var objectReverseModification: ObjectReverseModification? = null

    companion object {
        fun createObjectModification(changeSet: ChangeSet, modifiedObject: IdentifiedObject, originalObject: IdentifiedObject? = null): ObjectModification {
            return ObjectModification(changeSet, modifiedObject).also {
                originalObject?.let{ _ ->
                    it.objectReverseModification = ObjectReverseModification(changeSet, originalObject)
                }
            }
        }
    }
}
