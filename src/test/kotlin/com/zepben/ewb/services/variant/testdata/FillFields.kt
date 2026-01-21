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
import com.zepben.ewb.cim.iec61970.base.core.Feeder
import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.AnnotatedProjectDependency
import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.DependencyKind
import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProjectStage
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ChangeSet
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ChangeSetMember
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.DataSet
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ObjectCreation
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ObjectDeletion
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ObjectModification
import com.zepben.ewb.services.common.testdata.fillFieldsCommon
import com.zepben.ewb.services.variant.VariantService
import java.time.Instant
import java.util.UUID

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

    return this
}

// ############################################
// # IEC61970 InfPart303 NetworkModelProjects #
// ############################################

fun AnnotatedProjectDependency.fillFields(service: VariantService, includeRuntime: Boolean = true): AnnotatedProjectDependency {
    (this as IdentifiedObject).fillFieldsCommon(service, includeRuntime)
    dependencyType = DependencyKind.mutuallyExclusive
    addDependencyDependentOnStage(NetworkModelProjectStage("${mRID}-dependent-on-stage").also { service.add(it) })
    addDependencyDependingStage(NetworkModelProjectStage("${mRID}-depending-on-stage").also { service.add(it) })

    return this
}

fun NetworkModelProjectStage.fillFields(service: VariantService, includeRuntime: Boolean = true): NetworkModelProjectStage {
    (this as NetworkModelProjectComponent).fillFields(service, includeRuntime)
    plannedCommissionedDate = Instant.now().plusSeconds(3200)
    confidenceLevel = 10
    baseModelVersion = "2025-10-12"
    lastConflictCheckedAt = Instant.now().minusSeconds(20000)
    userComments = "Dodgy network, probably dont use this in production..."

    if (includeRuntime) {
        setChangeSet(ChangeSet("${mRID}-changeset-1").fillFields(service, includeRuntime))
        addDependingStage(AnnotatedProjectDependency("$mRID-apd").fillFields(service, includeRuntime))
        addDependentOnStage(AnnotatedProjectDependency("$mRID-apd").fillFields(service, includeRuntime))

        addEquipmentContainer(
            Feeder("${mRID}-equipment-container")
        )
    }
    service.add(this)

    return this
}

// ###################################
// # IEC61970 Part303 GenericDataSet #
// ###################################

fun ChangeSet.fillFields(service: VariantService, includeRuntime: Boolean = true): ChangeSet {
    (this as DataSet).fillFieldsCommon(service, includeRuntime)

    addChangeSetMember(ObjectCreation().fillFields(service, includeRuntime))
    addChangeSetMember(ObjectDeletion().fillFields(service, includeRuntime))
    addChangeSetMember(
        ObjectCreation().also {
            it.targetObjectMRID = "${mRID}-creation-target"
        })
    addChangeSetMember(
        ObjectDeletion().also {
            it.targetObjectMRID = "$mRID-deletion-target"
        }
    )
    addChangeSetMember(ObjectModification.createObjectModification(
        changeSet = this,
        modifiedObjectMRID = "${mRID}-modified",
        originalObjectMRID = "${mRID}-original"
    ))

    return this
}

fun DataSet.fillFieldsCommon(service: VariantService, @Suppress("UNUSED_PARAMETER") includeRuntime: Boolean = true): DataSet {
    name = "1"
    description = "the description"

    return this
}
fun ChangeSetMember.generateMRID(suffix: String): String = "${UUID.randomUUID()}-${suffix}"

fun ChangeSetMember.fillFields(csm: ChangeSetMember, changeSet: ChangeSet? = null): Boolean {
    val changeSet = changeSet ?: ChangeSet(generateMRID("change-set"))
    csm.apply {
        targetObjectMRID = "${changeSet.mRID}-target"
    }
    return true
}


fun ObjectCreation.fillFields(service: VariantService, includeRuntime: Boolean = true): ObjectCreation =
    ObjectCreation().also {
        fillFields(it as ChangeSetMember)
    }

fun ObjectDeletion.fillFields(service: VariantService, includeRuntime: Boolean = true): ObjectDeletion =
    ObjectDeletion().also {
        fillFields(it as ChangeSetMember)
    }

fun ObjectModification.fillFields(service: VariantService, includeRuntime: Boolean = true): ObjectModification {
    val baseUUID = UUID.randomUUID().toString()

    return ObjectModification.createObjectModification(
        changeSet = ChangeSet("${baseUUID}-change-set").also{ service.add(it) },
        modifiedObjectMRID = "${baseUUID}-modified",
        originalObjectMRID = "${baseUUID}-original"
    )
}
