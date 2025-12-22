/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.networkmodelproject

import com.zepben.ewb.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProject
import com.zepben.ewb.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProjectStageConflict
import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.AnnotatedProjectDependency
import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProjectStage
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ChangeSet
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.DataSet
import com.zepben.ewb.services.common.BaseService
import com.zepben.ewb.services.common.meta.MetadataCollection
import com.zepben.ewb.services.network.NetworkService
import kotlin.reflect.KClass

/**
 * Maintains an in-memory model of NetworkModelProjects.
 */
class NetworkModelProjectService (name: String = "networkmodelproject", metadata: MetadataCollection = MetadataCollection()) : NetworkService(name, metadata) {

    val changeSetsByMRID: MutableMap<String, DataSet> = mutableMapOf()

    // #######################################################
    // # Extensions IEC61970 InfPart303 NetworkModelProjects #
    // #######################################################

    /**
     * Add the [NetworkModelProject] to this service.
     *
     * @param networkModelProject The [NetworkModelProject] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(networkModelProject: NetworkModelProject): Boolean = super.add(networkModelProject)

    /**
     * Remove the [NetworkModelProject] from this service.
     *
     * @param networkModelProject The [NetworkModelProject] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(networkModelProject: NetworkModelProject): Boolean = super.remove(networkModelProject)

    /**
     * Add the [NetworkModelProjectStageConflict] to this service.
     *
     * @param networkModelProjectStageConflict The [NetworkModelProjectStageConflict] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(networkModelProjectStageConflict: NetworkModelProjectStageConflict): Boolean = super.add(networkModelProjectStageConflict)

    /**
     * Remove the [NetworkModelProjectStageConflict] from this service.
     *
     * @param networkModelProjectStageConflict The [NetworkModelProjectStageConflict] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(networkModelProjectStageConflict: NetworkModelProjectStageConflict): Boolean = super.remove(networkModelProjectStageConflict)

    // ############################################
    // # IEC61970 InfPart303 NetworkModelProjects #
    // ############################################

    /**
     * Add the [AnnotatedProjectDependency] to this service.
     *
     * @param annotatedProjectDependency The [AnnotatedProjectDependency] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(annotatedProjectDependency: AnnotatedProjectDependency): Boolean = super.add(annotatedProjectDependency)

    /**
     * Remove the [AnnotatedProjectDependency] from this service.
     *
     * @param annotatedProjectDependency The [AnnotatedProjectDependency] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(annotatedProjectDependency: AnnotatedProjectDependency): Boolean = super.remove(annotatedProjectDependency)

    /**
     * Add the [NetworkModelProjectStage] to this service.
     *
     * @param networkModelProjectStage The [NetworkModelProjectStage] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(networkModelProjectStage: NetworkModelProjectStage): Boolean = super.add(networkModelProjectStage)

    /**
     * Remove the [NetworkModelProjectStage] from this service.
     *
     * @param networkModelProjectStage The [NetworkModelProjectStage] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(networkModelProjectStage: NetworkModelProjectStage): Boolean = super.remove(networkModelProjectStage)

    // ###################################
    // # IEC61970 Part303 GenericDataSet #
    // ###################################

    // TODO: None of these are IO's, and it would be a lot of work to make the addable the the service (not really, but still.
    //   Leaving the skeleton defs here incase, but going to try and have their "added" state be the fact they're attached to the NetworkModelProjectStage
    //    fun add(changeSet: ChangeSet): Boolean = super.add(changeSet)
    //    fun remove(changeSet: ChangeSet): Boolean = super.remove(changeSet)

    // fun add(objectCreation: ObjectCreation): Boolean = super.add(objectCreation)
    // fun remove(objectCreation: ObjectCreation): Boolean = super.remove(objectCreation)

    // fun add(objectDeletion: ObjectDeletion): Boolean = super.add(objectDeletion)
    // fun remove(objectDeletion: ObjectDeletion): Boolean = super.remove(objectDeletion)

    // fun add(objectModification: ObjectModification): Boolean = super.add(objectModification)
    // fun remove(objectModification: ObjectModification): Boolean = super.remove(objectModification)

    // fun add(objectReverseModification: ObjectReverseModification): Boolean = super.add(objectReverseModification)
    // fun remove(objectReverseModification: ObjectReverseModification): Boolean = super.remove(objectReverseModification)

}