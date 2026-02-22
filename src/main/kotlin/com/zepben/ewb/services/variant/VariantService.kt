/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.variant

import com.zepben.ewb.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProject
import com.zepben.ewb.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProjectComponent
import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.AnnotatedProjectDependency
import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProjectStage
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.*
import com.zepben.ewb.services.common.BaseService
import com.zepben.ewb.services.common.meta.MetadataCollection
import java.time.Instant

/**
 * Maintains an in-memory model of variants for the network.
 */
class VariantService(name: String = "variants", metadata: MetadataCollection = MetadataCollection()) : BaseService(name, metadata) {

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


    /**
     * Add the [changeSet] to this service.
     *
     * @param changeSet The [ChangeSet] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(changeSet: ChangeSet): Boolean  = super.add(changeSet)

    /**
     * Remove the [changeSet] from this service.
     *
     * @param changeSet The [ChangeSet] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(changeSet: ChangeSet): Boolean = super.remove(changeSet)

    /**
     * Add the [objectCreation] to this service.
     *
     * @param objectCreation The [ChangeSet] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(objectCreation: ObjectCreation): Boolean = super.add(objectCreation)

    /**
     * Remove the [objectCreation] from this service.
     *
     * @param objectCreation The [ChangeSet] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(objectCreation: ObjectCreation): Boolean = super.remove(objectCreation)

    /**
     * Add the [objectDeletion] to this service.
     *
     * @param objectDeletion The [ChangeSet] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(objectDeletion: ObjectDeletion): Boolean = super.add(objectDeletion)

    /**
     * Remove the [objectDeletion] from this service.
     *
     * @param objectDeletion The [ChangeSet] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(objectDeletion: ObjectDeletion): Boolean = super.remove(objectDeletion)

    /**
     * Add the [objectModification] to this service.
     *
     * @param objectModification The [ChangeSet] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(objectModification: ObjectModification): Boolean = super.add(objectModification)

    /**
     * Remove the [objectModification] from this service.
     *
     * @param objectModification The [ChangeSet] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(objectModification: ObjectModification): Boolean = super.remove(objectModification)

    /**
     * Add the [objectReverseModification] to this service.
     *
     * @param objectReverseModification The [ChangeSet] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(objectReverseModification: ObjectReverseModification): Boolean = super.add(objectReverseModification)

    /**
     * Remove the [objectReverseModification] from this service.
     *
     * @param objectReverseModification The [ChangeSet] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(objectReverseModification: ObjectReverseModification): Boolean = super.remove(objectReverseModification)

    inline fun <reified TComp : NetworkModelProjectComponent> getComponentsBetween(start: Instant, end: Instant): Sequence<TComp> =
        sequenceOf<TComp>().filter { it.created?.let { created -> created in start..end } == true }

    fun getProjectsByDriver(driver: String?): Sequence<NetworkModelProject> =
        sequenceOf<NetworkModelProject>().filter { it.externalDriver == driver }

}
