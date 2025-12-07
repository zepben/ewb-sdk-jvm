/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset

import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProjectStage
import com.zepben.ewb.services.common.extensions.asUnmodifiable

/**
 * Describes a set of changes that can be applied in different situations. A given registered
 * target object MRID may only be referenced once by the contained change set members.
 *
 * @property changeSetMembers Data objects contained in the dataset.
 * @property networkModelProjectStage NetworkModelProjectStage this ChangeSet belongs to.
 */
class ChangeSet(mRID: String) : DataSet(mRID) {

    private var _changeSetMembers: MutableList<ChangeSetMember> = mutableListOf()

    val changeSetMembers: List<ChangeSetMember> get() = _changeSetMembers.asUnmodifiable()
    var networkModelProjectStage: NetworkModelProjectStage? = null


    internal fun addChangeSetMember(member: ChangeSetMember): Boolean {
        require(member.changeSet === this) {  // TODO: This code should never be hit, it exists incase the init of ChangeSetMember changes.
            "${member.javaClass.simpleName} `changeSet` property references ${member.changeSet?.typeNameAndMRID()}, expected ${typeNameAndMRID()}."
            return false
        }
        _changeSetMembers.add(member)
        return true
    }

}