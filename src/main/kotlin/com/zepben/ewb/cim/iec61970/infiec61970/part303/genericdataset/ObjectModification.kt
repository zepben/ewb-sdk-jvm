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
 */
class ObjectModification : ChangeSetMember() {

    /**
     * ObjectReverseModification specifying precondition properties for a preconditioned update.
     */
    var objectReverseModification: ObjectReverseModification? = null

    fun setObjectReverseModification(targetObjectMRID: String): ObjectModification {
        require(objectReverseModification == null) { "objectReverseModification already set" }
        require(changeSet != null) { "set changeset before calling this helper." }
        objectReverseModification = ObjectReverseModification().also {
            it.setChangeSet(changeSet!!)
            it.targetObjectMRID = targetObjectMRID
            require(it.objectModification == null) { "objectModification already set" }
            it.objectModification = this
        }
        return this
    }

    companion object {
        fun createObjectModification(changeSet: ChangeSet, modifiedObjectMRID: String, originalObjectMRID: String? = null): ObjectModification {
            return ObjectModification().also {
                it.setChangeSet(changeSet)
                it.targetObjectMRID = modifiedObjectMRID
                originalObjectMRID?.let { _ ->
                    it.objectReverseModification = ObjectReverseModification().also { orm ->
                        orm.setChangeSet(changeSet)
                        orm.targetObjectMRID = originalObjectMRID
                    }
                }
            }
        }
    }
}
