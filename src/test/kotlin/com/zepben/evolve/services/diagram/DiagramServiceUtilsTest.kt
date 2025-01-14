/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.diagram

import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject
import com.zepben.evolve.cim.iec61970.base.diagramlayout.Diagram
import com.zepben.evolve.cim.iec61970.base.diagramlayout.DiagramObject
import com.zepben.evolve.services.common.verifyWhenServiceFunctionSupportsAllServiceTypes
import com.zepben.testutils.junit.SystemLogExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class DiagramServiceUtilsTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun `supports all diagram service types`() {
        verifyWhenServiceFunctionSupportsAllServiceTypes(DiagramService().supportedKClasses, ::whenDiagramServiceObjectProxy)
    }

    // Function references to functions with generics are not yet supported, so we take a copy of the function that has a concrete type and pass through.
    // If you get failed tests about missing IdentifiedObject types, first update the proxied function, then update this one to match.
    internal fun whenDiagramServiceObjectProxy(
        identifiedObject: IdentifiedObject,
        isDiagram: (Diagram) -> String,
        isDiagramObject: (DiagramObject) -> String,
        isOther: (IdentifiedObject) -> String
    ): String = whenDiagramServiceObject(
        identifiedObject,
        isDiagram = isDiagram,
        isDiagramObject = isDiagramObject,
        isOther = isOther
    )

}
