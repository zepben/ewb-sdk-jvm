/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.variant.translator

import com.zepben.ewb.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProject
import com.zepben.ewb.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProjectComponent
import com.zepben.ewb.cim.iec61970.base.core.Identifiable
import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.AnnotatedProjectDependency
import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProjectStage
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.*
import com.zepben.ewb.database.sql.cim.tables.iec61970.infiec61970.infpart303.networkmodelprojects.TableNetworkModelProjectStageEquipmentContainers
import com.zepben.ewb.database.sql.cim.variant.VariantDatabaseTables
import com.zepben.ewb.services.common.translator.TranslatorTestBase
import com.zepben.ewb.services.variant.VariantService
import com.zepben.ewb.services.variant.VariantServiceComparator
import com.zepben.ewb.services.variant.testdata.fillFields

internal class VariantTranslatorTest : TranslatorTestBase<VariantService>(
    ::VariantService,
    VariantServiceComparator(),
    VariantDatabaseTables(),
    VariantService::addFromPb,
    ::variantObject

){
    private val vsToPb = VariantServiceCimToProto()

    override val validationInfo = listOf(

        // #######################################################
        // # Extensions IEC61970 InfPart303 NetworkModelProjects #
        // #######################################################

        ValidationInfo(::NetworkModelProject, { fillFields(it) }, { addFromPb(vsToPb.toPb(it)) }),

        // ############################################
        // # IEC61970 InfPart303 NetworkModelProjects #
        // ############################################

        ValidationInfo(::AnnotatedProjectDependency, { fillFields(it) }, { addFromPb(vsToPb.toPb(it)) }),
        ValidationInfo(::NetworkModelProjectStage, { fillFields(it) }, { addFromPb(vsToPb.toPb(it)) }),

        ValidationInfo(::ChangeSet, { fillFields(it) }, { addFromPb(vsToPb.toPb(it)) }),

        // Note the below have more complex translation logic due to having a calculated mRID.
        ValidationInfo({ ObjectCreation() }, { fillFields(it) }, {
            val convertedCim = vsToPb.toPb(it)
            add(ChangeSet(it.changeSet.mRID))   // Resolve unresolved reference before adding from PB as changeSet must be present to compute an mRID.
            addFromPb(convertedCim)
        }),
        ValidationInfo({ ObjectDeletion() }, { fillFields(it) }, {
            val convertedCim = vsToPb.toPb(it)
            add(ChangeSet(it.changeSet.mRID))   // Resolve unresolved reference before adding from PB as changeSet must be present to compute an mRID.
            addFromPb(convertedCim)
        }),
        ValidationInfo({ ObjectModification() }, { fillFields(it) }, {
            it.populateReverseModification(this)
            val convertedCim = vsToPb.toPb(it)

            add(ChangeSet(it.changeSet.mRID))   // Resolve unresolved reference before adding from PB as changeSet must be present to compute an mRID.
            add(ObjectReverseModification().also { orm ->    // Resolve the reverse modification as it must also have changeSet present before the conversion.
                orm.changeSet = it.changeSet; orm.targetObjectMRID = it.targetObjectMRID.asObjectReverseModificationId
            })
            addFromPb(convertedCim)
        }),

    )

    override val abstractCreators = mapOf<Class<*>, (String) -> Identifiable>(
        NetworkModelProjectComponent::class.java to { NetworkModelProjectStage(it) },
    )
    override val abstractCreatorsIdentifiable = mapOf<Class<*>, (Identifiable) -> Identifiable>(
        ChangeSetMember::class.java to {
            ObjectCreation().apply { it as ChangeSet; changeSet = it; targetObjectMRID = "creation" }
        }
    )


    override val excludedTables =
        super.excludedTables + setOf(
            TableNetworkModelProjectStageEquipmentContainers::class,
        )
}