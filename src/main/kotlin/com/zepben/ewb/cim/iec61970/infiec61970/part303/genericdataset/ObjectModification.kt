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
class ObjectModification: ChangeSetMember() {

    /**
     * ObjectReverseModification specifying precondition properties for a preconditioned update.
     */
    var objectReverseModification: ObjectReverseModification? = null

    fun setObjectReverseModification(targetObject: IdentifiedObject): ObjectModification {
        require(objectReverseModification == null) {"objectReverseModification already set"}
        require(changeSet != null) {"set changeset before calling this helper."}
        objectReverseModification = ObjectReverseModification().also {
            it.changeSet = changeSet!!
            it.targetObject = targetObject
            require(it.objectModification == null) {"objectModification already set"}
            it.objectModification = this
        }
        return this
    }

    companion object {
        fun createObjectModification(changeSet: ChangeSet, modifiedObject: IdentifiedObject, originalObject: IdentifiedObject? = null): ObjectModification {
            return ObjectModification().also {
                it.setChangeSet(changeSet)
                it.targetObject = modifiedObject
                originalObject?.let{ _ ->
                    it.objectReverseModification = ObjectReverseModification().also { orm ->
                        orm.changeSet = changeSet
                        orm.targetObject = originalObject
                    }
                }
            }
        }
    }
}
