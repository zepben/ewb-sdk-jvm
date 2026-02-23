/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.variant

import com.zepben.ewb.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProject
import com.zepben.ewb.cim.iec61970.base.core.Identifiable
import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.AnnotatedProjectDependency
import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProjectStage
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.*
import com.zepben.ewb.services.common.verifyWhenServiceFunctionSupportsAllServiceTypes
import com.zepben.testutils.junit.SystemLogExtension
import org.junit.jupiter.api.extension.RegisterExtension

internal class VariantServiceUtilsTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    // TODO: ObjCreation etc
//    @Test
    internal fun `supports all variant service types`() {
        verifyWhenServiceFunctionSupportsAllServiceTypes(VariantService().supportedKClasses, ::whenVariantIdentifiedObjectProxy)
    }

    internal fun whenVariantIdentifiedObjectProxy(
        identifiedObject: Identifiable,
        isNetworkModelProject: (NetworkModelProject) -> String,
        isNetworkModelProjectStage: (NetworkModelProjectStage) -> String,
        isAnnotatedProjectDependency: (AnnotatedProjectDependency) -> String,
        isChangeSet: (ChangeSet) -> String,
        isObjectCreation: (ObjectCreation) -> String,
        isObjectDeletion: (ObjectDeletion) -> String,
        isObjectModification: (ObjectModification) -> String,
        isObjectReverseModification: (ObjectReverseModification) -> String,
        isOther: (Any) -> String,
    ): String = whenVariantIdentifiedObject(
        identifiedObject,
        isNetworkModelProject = isNetworkModelProject,
        isNetworkModelProjectStage = isNetworkModelProjectStage,
        isAnnotatedProjectDependency = isAnnotatedProjectDependency,
        isChangeSet = isChangeSet,
        isObjectCreation = isObjectCreation,
        isObjectDeletion = isObjectDeletion,
        isObjectModification = isObjectModification,
        isObjectReverseModification = isObjectReverseModification,
        isOther = isOther
    )

}
