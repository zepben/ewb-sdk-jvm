/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.variant.testdata

import com.zepben.ewb.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProject
import com.zepben.ewb.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProjectComponent
import com.zepben.ewb.cim.iec61970.base.core.Identifiable
import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.AnnotatedProjectDependency
import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.DependencyKind
import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProjectStage
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.*
import com.zepben.ewb.services.common.testdata.fillFieldsCommon
import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.ewb.services.variant.VariantService
import java.time.Instant

// #######################################################
// # Extensions IEC61970 InfPart303 NetworkModelProjects #
// #######################################################

fun NetworkModelProject.fillFields(service: VariantService, includeRuntime: Boolean = true): NetworkModelProject {
    (this as NetworkModelProjectComponent).fillFields(service, includeRuntime)
    externalStatus = "Probably Fine"
    forecastCommissionDate = Instant.now().plusSeconds(600)
    externalDriver = "Capacity"

    @Suppress("unused")
    if (includeRuntime) {
        for (i in 0..1) {
            addChild(NetworkModelProjectStage("$mRID-child-$i").also {
                service.add(it)
            })
        }
    }

    return this
}

fun NetworkModelProjectComponent.fillFields(service: VariantService, includeRuntime: Boolean = true): NetworkModelProjectComponent {
    (this as IdentifiedObject).fillFieldsCommon(service, includeRuntime)

    created = Instant.now().minusSeconds(10)
    closed = Instant.now()

    parent = NetworkModelProject("parent-project").also { it.addChild(this); service.add(it) }

    // TODO
    return this
}

// ############################################
// # IEC61970 InfPart303 NetworkModelProjects #
// ############################################

fun AnnotatedProjectDependency.fillFields(service: VariantService, includeRuntime: Boolean = true): AnnotatedProjectDependency {
    (this as IdentifiedObject).fillFieldsCommon(service, includeRuntime)
    dependencyType = DependencyKind.mutuallyExclusive
    dependencyDependentOnStage = NetworkModelProjectStage(generateId()).also { service.add(it) }
    dependencyDependingStage = NetworkModelProjectStage(generateId()).also { service.add(it) }

    return this
}

fun NetworkModelProjectStage.fillFields(service: VariantService, includeRuntime: Boolean = true): NetworkModelProjectStage {
    (this as NetworkModelProjectComponent).fillFields(service, includeRuntime)
    plannedCommissionedDate = Instant.now().plusSeconds(3200)
    confidenceLevel = 10
    baseModelVersion = "2025-10-12"
    lastConflictCheckedAt = Instant.now().minusSeconds(20000)
    userComments = "Dodgy network, probably dont use this in production..."
    changeSet = ChangeSet(generateId()).also {
        service.add(it)
        it.networkModelProjectStage = this
    }

    addDependingStage(AnnotatedProjectDependency(generateId()).also { it.dependencyDependingStage = this })
    addDependentOnStage(AnnotatedProjectDependency(generateId()).also { it.dependencyDependentOnStage = this })

    addContainer(generateId())

    return this
}

// ###################################
// # IEC61970 Part303 GenericDataSet #
// ###################################

fun ChangeSet.fillFields(service: VariantService, includeRuntime: Boolean = true): ChangeSet {
    (this as DataSet).fillFields(service, includeRuntime)

    networkModelProjectStage = NetworkModelProjectStage(generateId()).also { it.changeSet = this; service.add(it) }

    addMember(ObjectCreation().also { it.changeSet = this; it.targetObjectMRID = "creation"; service.add(it) })

    return this
}

@Suppress("UNUSED_PARAMETER")
fun DataSet.fillFields(service: VariantService, includeRuntime: Boolean = true): DataSet {
    name = "1"
    description = "the description"

    return this
}

fun ChangeSetMember.fillFields(service: VariantService, includeRuntime: Boolean): ChangeSetMember {
    (this as Identifiable).fillFieldsCommon(service, includeRuntime)
    val cs = ChangeSet(generateId()).also { service.add(it) }
    changeSet = cs
    cs.addMember(this)
    targetObjectMRID = generateId()

    return this
}


fun ObjectCreation.fillFields(service: VariantService, includeRuntime: Boolean = true): ObjectCreation {
    (this as ChangeSetMember).fillFields(service, includeRuntime)
    return this
}


fun ObjectDeletion.fillFields(service: VariantService, includeRuntime: Boolean = true): ObjectDeletion {
    (this as ChangeSetMember).fillFields(service, includeRuntime)
    return this
}

fun ObjectModification.fillFields(service: VariantService, includeRuntime: Boolean = true): ObjectModification {
    (this as ChangeSetMember).fillFields(service, includeRuntime)

    populateReverseModification(service)
    return this
}

fun ObjectReverseModification.fillFields(service: VariantService, includeRuntime: Boolean = true): ObjectReverseModification {
    (this as ChangeSetMember).fillFields(service, includeRuntime)

    objectModification = ObjectModification().also { it.targetObjectMRID = generateId(); it.changeSet = this.changeSet; service.add(it) }
    return this
}