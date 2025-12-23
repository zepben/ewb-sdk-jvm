/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects

import com.zepben.ewb.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProjectComponent
import com.zepben.ewb.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProjectStageConflict
import com.zepben.ewb.cim.iec61970.base.core.EquipmentContainer
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ChangeSet
import com.zepben.ewb.services.common.extensions.asUnmodifiable
import com.zepben.ewb.services.common.extensions.getByMRID
import com.zepben.ewb.services.common.extensions.typeNameAndMRID
import java.time.Instant
import com.zepben.ewb.cim.extensions.ZBEX
import com.zepben.ewb.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProject
import java.util.UUID

/**
 * A specific phase in a network model project.
 */
class NetworkModelProjectStage(mRID: String) : NetworkModelProjectComponent(mRID) {

    private var _dependentOnStage: MutableList<AnnotatedProjectDependency>? = null
    private var _dependingStage: MutableList<AnnotatedProjectDependency>? = null
    private var _changeSet: ChangeSet? = null
    // FIXME: This is a cross service reference. Do we need to treat it differently?
    //  can this cause the whole network to be left in memory?
    //  i guess thats what we want, but still... @charlta.... HALP!
    private var _equipmentContainers: MutableList<EquipmentContainer>? = null

    /**
     * The date expected for this stage to be commissioned.
     */
    var plannedCommissionedDate: Instant? = null

    /**
     * The date this stage was commissioned.
     */
    var commissionedDate: Instant? = null

    /**
     * [ZBEX] The percentage confidence that this project will be committed to.
     */
    var confidenceLevel: Int? = null

    /**
     * [ZBEX] The version of the base model this stage was imported against.
     */
    var baseModelVersion: String? = null

    /**
     * [ZBEX] The time the last conflict check occured.
     */
    var lastConflictCheckedAt: Instant? = null

    /**
     * [ZBEX] User comments.
     */
    var userComments: String? = null

    /**
     * [ZBEX] The set of changes that this stage of the project will do.
     */
    val changeSet: ChangeSet? get() = _changeSet  // TODO: should this be a lazy call to the service to load the changeSet from blob?

    /**
     * Associate a [ChangeSet] with this [NetworkModelProjectStage].
     *
     * @param changeSet the [ChangeSet] to associate.
     * @return this [NetworkModelProjectStage] for fluent use.
     */
    fun setChangeSet (changeSet: ChangeSet) : NetworkModelProjectStage {
        require(_changeSet == null) {
            "`changeSet` property for ${typeNameAndMRID()} is already set."
        }

        if (changeSet.networkModelProjectStage == null)
            changeSet.networkModelProjectStage = this

        require(changeSet.networkModelProjectStage === this) {
            "${changeSet.typeNameAndMRID()} `networkModelProjectStage` property references ${changeSet.networkModelProjectStage!!.typeNameAndMRID()}, expected ${typeNameAndMRID()}"
        }

        _changeSet = changeSet
        return this
    }

    /**
     * The stages that depend on this stage.
     */
    val dependentOnStage: Collection<AnnotatedProjectDependency> get() = _dependentOnStage.asUnmodifiable()

    /**
     * Create an [AnnotatedProjectDependency] where the other stage in [annotatedProjectDependency] depends on this stage.
     *
     * eg:
     *   to apply the [ChangeSet]s in the other stage we MUST resolve the dependency.
     *   to apply the [ChangeSet]s in this stage we do not resolve the dependency.
     *
     * @param annotatedProjectDependency the [AnnotatedProjectDependency] specifying the dependency link on this stage.
     */
    fun addDependentOnStage (annotatedProjectDependency: AnnotatedProjectDependency) {

        if (_dependentOnStage == null)
            _dependentOnStage = mutableListOf()

        _dependentOnStage!!.add(annotatedProjectDependency)

    }

    /**
     * The stages that this stage depends on.
     */
    val dependingStage: Collection<AnnotatedProjectDependency> get() = _dependingStage.asUnmodifiable()

    /**
     * Create an [AnnotatedProjectDependency] where this stage depends on the other stage in [annotatedProjectDependency].
     *
     * eg:
     *   to apply the [ChangeSet]s in the other stage we do not resolve the dependency.
     *   to apply the [ChangeSet]s in this stage we MUST resolve the dependency.
     *
     * @param annotatedProjectDependency the [AnnotatedProjectDependency] specifying the dependency link on this stage.
     */
    fun addDependingStage (annotatedProjectDependency: AnnotatedProjectDependency) {

        if (_dependingStage == null)
            _dependingStage = mutableListOf()

        _dependingStage!!.add(annotatedProjectDependency)
    }

    /**
     * [ZBEX] The equipment containers this stage is related to.
     */
    val equipmentContainers: Collection<EquipmentContainer> get() = _equipmentContainers.asUnmodifiable()

    /**
     * Associate an [EquipmentContainer] with this [NetworkModelProjectStage].
     *
     * @param equipmentContainer The [EquipmentContainer] to associate.
     * @return this [NetworkModelProjectStage] for fluent use.
     */
    fun addEquipmentContainer (equipmentContainer: EquipmentContainer): NetworkModelProjectStage {
        if (_equipmentContainers == null)
            _equipmentContainers = mutableListOf()

        if (_equipmentContainers.getByMRID(equipmentContainer.mRID) == null)
            _equipmentContainers!!.add(equipmentContainer)
        return this
    }

}
