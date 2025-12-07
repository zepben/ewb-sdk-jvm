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
 * An object is to be deleted in the context.
 *
 * @param changeSet [ChangeSet] this [ObjectDeletion] belongs to.
 * @param targetObject [IdentifiedObject] to be deleted by this [ObjectDeletion].
 */
class ObjectDeletion(changeSet: ChangeSet, targetObject: IdentifiedObject) : ChangeSetMember(changeSet, targetObject)
