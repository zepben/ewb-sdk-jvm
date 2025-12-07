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
 * Used to specify precondition properties for a preconditioned update.
 *
 * @param changeSet [ChangeSet] this [ObjectReverseModification] belongs to.
 * @param targetObject original, unmodified [IdentifiedObject] to be modified by the [objectModification] property of this [ObjectReverseModification].
 */
class ObjectReverseModification(changeSet: ChangeSet, targetObject: IdentifiedObject) : ChangeSetMember(changeSet, targetObject) {

    /**
     * ObjectModification specifying modifications to an object already existing.
     */
    var objectModification: ObjectModification? = null
}