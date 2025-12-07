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
import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.AnnotatedProjectDependency
import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProjectStage
import com.zepben.ewb.database.sql.cim.variant.VariantDatabaseTables
import com.zepben.ewb.database.sql.cim.tables.extensions.iec61970.infpart303.networkmodelprojects.TableNetworkModelProjectNetworkModelProjectComponents
import com.zepben.ewb.database.sql.cim.tables.iec61970.infiec61970.infpart303.networkmodelprojects.TableNetworkModelProjectStageEquipmentContainers
import com.zepben.ewb.services.common.translator.TranslatorTestBase
import com.zepben.ewb.services.variant.VariantService
import com.zepben.ewb.services.variant.VariantServiceComparator
import com.zepben.ewb.services.variant.testdata.fillFields

internal class VariantTranslatorTest : TranslatorTestBase<VariantService>(
    ::VariantService,
    VariantServiceComparator(),
    VariantDatabaseTables(),
    VariantService::addFromPb,
    ::variantIdentifiedObject

){
    private val vsToPb = NetworkModelProjectCimToProto()

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

        //ValidationInfo(::ChangeSet, { fillFields(it) }, { addFromPb(vsToPb.toPb(it)) }),  // TODO: datasets not being IO's bites me in the ass again.

    )

    override val abstractCreators = mapOf<Class<*>, (String) -> IdentifiedObject>(
        NetworkModelProjectComponent::class.java to { NetworkModelProjectStage(it) }
    )

    override val excludedTables =
        super.excludedTables + setOf(
            TableNetworkModelProjectNetworkModelProjectComponents::class,
            TableNetworkModelProjectStageEquipmentContainers::class,
        )
}