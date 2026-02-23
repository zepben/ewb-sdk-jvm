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
 * @property members Data objects contained in the dataset.
 * @property networkModelProjectStage NetworkModelProjectStage this ChangeSet belongs to.
 */
class ChangeSet(mRID: String) : DataSet(mRID) {

    private var _members: MutableList<ChangeSetMember>? = null

    val members: List<ChangeSetMember> get() = _members.asUnmodifiable()
    var networkModelProjectStage: NetworkModelProjectStage? = null

    val creations: List<ObjectCreation> get() = _members.ofType()
    val deletions: List<ObjectDeletion> get() = _members.ofType()
    val modifications: List<ObjectModification> get() = _members.ofType()
    val reverseModifications: List<ObjectReverseModification> get() = _members.ofType()

    /**
     * Get the number of entries in the [ChangeSetMember] collection.
     */
    fun numMembers(): Int = _members?.size ?: 0

    /**
     * Retrieve the [ChangeSetMember] for the specified [mRID]
     *
     * @param mRID the [ChangeSetMember.targetObjectMRID] to retrieve.
     * @return The [ChangeSetMember] with the specified [ChangeSetMember.targetObjectMRID] if it exists, otherwise null
     */
    fun getMember(mRID: String): ChangeSetMember? = _members?.firstOrNull { it.targetObjectMRID == mRID }


    /**
     * Add a [ChangeSetMember] to this [ChangeSet].
     * A [ChangeSetMember.targetObjectMRID] may only be referenced by one member in this [ChangeSet].
     *
     * @param member The [ChangeSetMember] to add.
     * @return this [ChangeSet] for fluent use.
     */
    fun addMember(member: ChangeSetMember): ChangeSet {
        require(member.changeSet == this) { "${member.javaClass.simpleName} `changeSet` property references ${member.changeSet.typeNameAndMRID()}, expected ${typeNameAndMRID()}." }
        require(members.none { it.targetObjectMRID == member.targetObjectMRID }) { "A ChangeSetMember already exists in ${typeNameAndMRID()} with targetObjectMRID ${member.targetObjectMRID}." }

        _members = _members ?: mutableListOf()
        _members!!.add(member)

        return this
    }

    /**
     * Remove a [ChangeSetMember] from this [ChangeSet].
     *
     * @param member the [ChangeSetMember] to disconnect from this [ChangeSet].
     * @return true if [member] was removed from this [ChangeSet].
     */
    fun removeMember(member: ChangeSetMember): Boolean {
        val ret = _members?.remove(member) == true
        if (_members.isNullOrEmpty()) _members = null
        return ret
    }

    private inline fun <reified T : ChangeSetMember> List<*>?.ofType(): List<T> = this?.filterIsInstance(T::class.java) ?: emptyList()
}
