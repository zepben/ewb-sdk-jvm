/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects

import com.zepben.ewb.cim.extensions.ZBEX
import com.zepben.ewb.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProjectComponent
import com.zepben.ewb.cim.iec61970.base.core.EquipmentContainer
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ChangeSet
import com.zepben.ewb.services.common.extensions.asUnmodifiable
import com.zepben.ewb.services.common.extensions.getByMRID
import com.zepben.ewb.services.common.extensions.validateReference
import java.time.Instant

/**
 * A specific phase in a network model project.
 *
 * @property plannedCommissionedDate The date expected for this stage to be commissioned.
 * @property commissionedDate The date this stage was commissioned.
 * @property confidenceLevel [ZBEX] The percentage confidence that this project will be committed to.
 * @property baseModelVersion [ZBEX] The version of the base model this stage was imported against.
 * @property lastConflictCheckedAt [ZBEX] The time the last conflict check occurred.
 * @property userComments [ZBEX] User comments.
 * @property changeSet [ZBEX] The set of changes that this stage of the project will do.
 * @property dependentOnStage The stages that depend on this stage.
 * @property dependingStage The stages that this stage depends on.
 * @property equipmentContainerMRIDs [ZBEX] The equipment containers this stage is related to.
 */
@ZBEX
class NetworkModelProjectStage(mRID: String) : NetworkModelProjectComponent(mRID) {

    private var _dependentOnStage: MutableList<AnnotatedProjectDependency>? = null
    private var _dependingStage: MutableList<AnnotatedProjectDependency>? = null
    private var _equipmentContainerMRIDs: MutableSet<String>? = null

    var plannedCommissionedDate: Instant? = null
        set(it) {
            updated = Instant.now()
            field = it
        }
    var commissionedDate: Instant? = null
        set(it) {
            updated = Instant.now()
            field = it
        }
    @ZBEX var confidenceLevel: Int? = null
        set(it) {
            updated = Instant.now()
            field = it
        }
    @ZBEX var baseModelVersion: String? = null
        set(it) {
            updated = Instant.now()
            field = it
        }
    @ZBEX var lastConflictCheckedAt: Instant? = null
        set(it) {
            updated = Instant.now()
            field = it
        }
    @ZBEX var userComments: String? = null
        set(it) {
            updated = Instant.now()
            field = it
        }

    val dependentOnStage: Collection<AnnotatedProjectDependency> get() = _dependentOnStage.asUnmodifiable()
    val dependingStage: Collection<AnnotatedProjectDependency> get() = _dependingStage.asUnmodifiable()
    var changeSet: ChangeSet? = null
        set(it) {
            updated = Instant.now()
            field = it
        }

    @ZBEX
    val equipmentContainerMRIDs: Collection<String> get() = _equipmentContainerMRIDs.asUnmodifiable()


    /**
     * Get the number of entries in the [dependentOnStage] collection.
     */
    fun numDependentOnStages(): Int = _dependentOnStage?.size ?: 0

    /**
     * Get a dependent [AnnotatedProjectDependency] for this stage.
     *
     * @param mRID the mRID of the required [AnnotatedProjectDependency]
     * @return The [AnnotatedProjectDependency] with the specified [mRID] if it exists, otherwise null
     */
    fun getDependentOnStage(mRID: String): AnnotatedProjectDependency? = _dependentOnStage.getByMRID(mRID)

    /**
     * Create an [AnnotatedProjectDependency] where the other stage in [AnnotatedProjectDependency] depends on this stage.
     *
     * e.g:
     *   to apply the [ChangeSet]s in the other stage we MUST resolve the dependency.
     *   to apply the [ChangeSet]s in this stage we do not resolve the dependency.
     *
     * @param dependentOnStage the [AnnotatedProjectDependency] that depends on this stage.
     * @return A reference to this [NetworkModelProjectStage] to allow fluent use.
     */
    fun addDependentOnStage(dependentOnStage: AnnotatedProjectDependency): NetworkModelProjectStage {
        if (validateReference(dependentOnStage, ::getDependentOnStage, "An AnnotatedProjectDependency"))
            return this

        _dependentOnStage = _dependentOnStage ?: mutableListOf()
        _dependentOnStage!!.add(dependentOnStage)

        updated = Instant.now()
        return this
    }

    /**
     * @param dependentOnStage the [AnnotatedProjectDependency] to remove its dependency on this stage.
     * @return true if [dependentOnStage] was removed as a dependency from this stage.
     */
    fun removeDependentOnStage(dependentOnStage: AnnotatedProjectDependency): Boolean {
        val ret = _dependentOnStage?.remove(dependentOnStage) == true
        if (_dependentOnStage.isNullOrEmpty()) _dependentOnStage = null
        updated = Instant.now()
        return ret
    }

    /**
     * Clear this [NetworkModelProjectStage]'s [dependentOnStage] collection.
     * @return this [NetworkModelProjectStage]
     */
    fun clearDependentOnStages(): NetworkModelProjectStage {
        _dependentOnStage = null
        updated = Instant.now()
        return this
    }

    /**
     * Get the number of entries in the [dependingStage] collection.
     */
    fun numDependingStages(): Int = _dependingStage?.size ?: 0

    /**
     * Get a dependent [AnnotatedProjectDependency] for this stage.
     *
     * @param mRID the mRID of the required [AnnotatedProjectDependency]
     * @return The [AnnotatedProjectDependency] with the specified [mRID] if it exists, otherwise null
     */
    fun getDependingStage(mRID: String): AnnotatedProjectDependency? = _dependingStage.getByMRID(mRID)

    /**
     * Create an [AnnotatedProjectDependency] where this stage depends on the other stage in [annotatedProjectDependency].
     *
     * eg:
     *   to apply the [ChangeSet]s in the other stage we do not resolve the dependency.
     *   to apply the [ChangeSet]s in this stage we MUST resolve the dependency.
     *
     * @param dependingStage the [AnnotatedProjectDependency] that depends on this stage.
     * @return A reference to this [NetworkModelProjectStage] to allow fluent use.
     */
    fun addDependingStage(dependingStage: AnnotatedProjectDependency): NetworkModelProjectStage {
        if (validateReference(dependingStage, ::getDependingStage, "An AnnotatedProjectDependency"))
            return this

        _dependingStage = _dependingStage ?: mutableListOf()
        _dependingStage!!.add(dependingStage)

        updated = Instant.now()
        return this
    }

    /**
     * @param dependingStage the [AnnotatedProjectDependency] to remove its dependency on this stage.
     * @return true if [dependingStage] was removed as a dependency from this stage.
     */
    fun removeDependingStage(dependingStage: AnnotatedProjectDependency): Boolean {
        val ret = _dependingStage?.remove(dependingStage) == true
        if (_dependingStage.isNullOrEmpty()) _dependingStage = null
        updated = Instant.now()
        return ret
    }

    /**
     * Clear this [NetworkModelProjectStage]'s [dependingStage] collection.
     * @return this [NetworkModelProjectStage]
     */
    fun clearDependingStages(): NetworkModelProjectStage {
        _dependingStage = null
        updated = Instant.now()
        return this
    }

    /**
     * Get the number of entries in the [equipmentContainerMRIDs] collection.
     */
    fun numContainers(): Int = _equipmentContainerMRIDs?.size ?: 0

    fun contains(mRID: String): Boolean = _equipmentContainerMRIDs?.contains(mRID) ?: false

    /**
     * Associate an [EquipmentContainer] with this [NetworkModelProjectStage] by its mRID.
     *
     * @param equipmentContainerMRID The [EquipmentContainer] mRID to associate.
     * @return this [NetworkModelProjectStage] for fluent use.
     */
    fun addContainer(equipmentContainerMRID: String): NetworkModelProjectStage {
        _equipmentContainerMRIDs = _equipmentContainerMRIDs ?: mutableSetOf()
        _equipmentContainerMRIDs!!.add(equipmentContainerMRID)

        updated = Instant.now()
        return this
    }

    /**
     * Helper method to make use of existing tests.
     */
    internal fun getContainer(mRID: String): String? = if (_equipmentContainerMRIDs?.contains(mRID) == true) mRID else null

    /**
     * @param equipmentContainerMRID the equipment container to disassociate with this equipment.
     * @return `true` if [equipmentContainerMRID] has been successfully removed; `false` if it was not present in the set.
     */
    fun removeContainer(equipmentContainerMRID: String): Boolean {
        val ret = _equipmentContainerMRIDs?.remove(equipmentContainerMRID) == true
        if (_equipmentContainerMRIDs.isNullOrEmpty()) _equipmentContainerMRIDs = null
        updated = Instant.now()
        return ret
    }

    /**
     * Clear this [NetworkModelProjectStage]'s associated [EquipmentContainer]'s
     * @return this [NetworkModelProjectStage]
     */
    fun clearContainers(): NetworkModelProjectStage {
        _equipmentContainerMRIDs = null
        updated = Instant.now()
        return this
    }

}
