/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.variant

import com.zepben.ewb.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProject
import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.AnnotatedProjectDependency
import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProjectStage
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ChangeSet
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.DataSet
import com.zepben.ewb.database.sql.common.DuplicateMRIDException
import com.zepben.ewb.database.sql.common.MRIDLookupException
import com.zepben.ewb.services.common.BaseService
import com.zepben.ewb.services.common.BoundReferenceResolver
import com.zepben.ewb.services.common.UnresolvedReference
import com.zepben.ewb.services.common.exceptions.UnsupportedIdentifiedObjectException
import com.zepben.ewb.services.common.meta.MetadataCollection
import com.zepben.ewb.services.network.NetworkService
import kotlin.reflect.KClass
import kotlin.reflect.full.isSuperclassOf

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


}
