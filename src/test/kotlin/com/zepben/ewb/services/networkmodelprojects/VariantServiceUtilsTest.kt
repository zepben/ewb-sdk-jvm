/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.networkmodelprojects

import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ChangeSetMember
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ObjectCreation
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ObjectDeletion
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ObjectModification

import com.zepben.ewb.services.common.verifyWhenServiceFunctionSupportsAllServiceTypes
import com.zepben.ewb.services.networkmodelproject.NetworkModelProjectService
import com.zepben.ewb.services.networkmodelproject.whenVariantChangeSetMember
import com.zepben.testutils.junit.SystemLogExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class VariantServiceUtilsTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun `supports all customer service types`() {
        verifyWhenServiceFunctionSupportsAllServiceTypes(NetworkModelProjectService().supportedKClasses, ::whenVariantChangeSetMemberProxy)
    }

    // Function references to functions with generics are not yet supported, so we take a copy of the function that has a concrete type and pass through.
    // If you get failed tests about missing IdentifiedObject types, first update the proxied function, then update this one to match.
    internal fun whenVariantChangeSetMemberProxy(
        obj: ChangeSetMember,
        isObjectCreation: (ObjectCreation) -> String,
        isObjectDeletion: (ObjectDeletion) -> String,
        isObjectModification: (ObjectModification) -> String,
        isOther: (Any) -> String
    ): String = whenVariantChangeSetMember(
        obj,
        isObjectCreation = isObjectCreation,
        isObjectDeletion = isObjectDeletion,
        isObjectModification = isObjectModification,
        isOther = isOther
    )

}
