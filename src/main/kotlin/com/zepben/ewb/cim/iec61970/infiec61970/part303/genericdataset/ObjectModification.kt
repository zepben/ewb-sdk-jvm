/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset

import com.zepben.ewb.services.variant.VariantService

/**
 * An object already exists and is to be modified.
 *
 * @property objectReverseModification ObjectReverseModification specifying precondition properties for a preconditioned update.
 */
class ObjectModification : ChangeSetMember() {

    lateinit var objectReverseModification: ObjectReverseModification

    private var _initialised: Boolean = false
    /**
     * Helper function to populate the reverse modification. Should only be called after [changeSet] and [targetObjectMRID] have been set.
     * @param service The [VariantService] to add the reverse modification to. Should be the same service that contains this [ObjectModification].
     */
    fun populateReverseModification(service: VariantService) {
        if (!_initialised) {
            objectReverseModification = ObjectReverseModification().also {
                it.changeSet = changeSet
                it.targetObjectMRID = targetObjectMRID.asObjectReverseModificationId
                it.objectModification = this
                changeSet.addMember(it)
            }
            service.add(objectReverseModification)
        }
        _initialised = true
    }

}

val String.asObjectReverseModificationId: String get() = "-$this"

val String.asTargetObjectMRID: String get() = this.removePrefix("-")
