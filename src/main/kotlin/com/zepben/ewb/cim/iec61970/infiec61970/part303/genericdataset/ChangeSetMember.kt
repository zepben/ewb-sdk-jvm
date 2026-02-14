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
 * @property changeSet The [ChangeSet] this [ChangeSetMember] belongs to.
 * @property targetObjectMRID The CIM object [changeSet] applies to.
 */
abstract class ChangeSetMember(val changeSet: ChangeSet, val targetObjectMRID: String)
