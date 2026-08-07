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
 * @property dependentOnStages The stages that depend on this stage.
 * @property dependencies The stages that this stage depends on.
 * @property equipmentContainerMRIDs [ZBEX] The equipment containers this stage is related to.
 */
@ZBEX
class NetworkModelProjectStage(mRID: String) : NetworkModelProjectComponent(mRID) {

    private var _dependencies: MutableList<AnnotatedProjectDependency>? = null
    private var _equipmentContainerMRIDs: MutableList<String>? = null

    var plannedCommissionedDate: Instant? = null
    var commissionedDate: Instant? = null
    @ZBEX var confidenceLevel: Int? = null
    @ZBEX var baseModelVersion: String? = null
    @ZBEX var lastConflictCheckedAt: Instant? = null
    @ZBEX var userComments: String? = null

    val dependencies: List<AnnotatedProjectDependency> get() = _dependencies.asUnmodifiable()
    var changeSet: ChangeSet? = null

    @ZBEX
    val equipmentContainerMRIDs: List<String> get() = _equipmentContainerMRIDs.asUnmodifiable()


    /**
     * Get the number of stages that are dependent on this stage.
     */
    fun numDependentOnStages(): Int = _dependencies?.filter { it.dependencyDependentOnStage != this }?.size ?: 0

    /**
     * Get the number of stages that are depending on this stage.
     */
    fun numDependingStages(): Int = _dependencies?.filter { it.dependencyDependingStage != this }?.size ?: 0

    /**
     * Get a dependent [AnnotatedProjectDependency] for this stage.
     *
     * @param mRID the mRID of the required [AnnotatedProjectDependency]
     * @return The [AnnotatedProjectDependency] with the specified [mRID] if it exists, otherwise null
     */
    fun getDependency(mRID: String): AnnotatedProjectDependency? = _dependencies.getByMRID(mRID)

    /**
     * Create an [AnnotatedProjectDependency] where this stage depends on the other stage in [annotatedProjectDependency].
     *
     * eg:
     *   to apply the [ChangeSet]s in the other stage we do not resolve the dependency.
     *   to apply the [ChangeSet]s in this stage we MUST resolve the dependency.
     *
     * @param dependency the [AnnotatedProjectDependency] that depends on this stage.
     * @return A reference to this [NetworkModelProjectStage] to allow fluent use.
     */
    fun addDependency(dependency: AnnotatedProjectDependency): NetworkModelProjectStage {
        if (validateReference(dependency, ::getDependency, "An AnnotatedProjectDependency"))
            return this

        _dependencies = _dependencies ?: mutableListOf()
        _dependencies!!.add(dependency)

        return this
    }

    /**
     * @param dependency the [AnnotatedProjectDependency] to remove its dependency on this stage.
     * @return true if [dependency] was removed as a dependency from this stage.
     */
    fun removeDependency(dependency: AnnotatedProjectDependency): Boolean {
        val ret = _dependencies?.remove(dependency) == true
        if (_dependencies.isNullOrEmpty()) _dependencies = null
        return ret
    }

    /**
     * Clear this [NetworkModelProjectStage]'s [dependencies] collection.
     * @return this [NetworkModelProjectStage]
     */
    fun clearDependencies(): NetworkModelProjectStage {
        _dependencies = null
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
        _equipmentContainerMRIDs = _equipmentContainerMRIDs ?: mutableListOf()
        _equipmentContainerMRIDs!!.add(equipmentContainerMRID)

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
        return ret
    }

    /**
     * Clear this [NetworkModelProjectStage]'s associated [EquipmentContainer]'s
     * @return this [NetworkModelProjectStage]
     */
    fun clearContainers(): NetworkModelProjectStage {
        _equipmentContainerMRIDs = null
        return this
    }

}
