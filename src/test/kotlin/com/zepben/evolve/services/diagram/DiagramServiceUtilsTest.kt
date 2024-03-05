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
import com.zepben.evolve.services.common.InvokeChecker
import com.zepben.evolve.services.common.InvokedChecker
import com.zepben.evolve.services.common.NeverInvokedChecker
import com.zepben.evolve.services.common.verifyWhenServiceObjectFunctionSupportsAllServiceObjectTypes
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class DiagramServiceUtilsTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    // Function references to functions with generics are not yet supported.
    // So, we take a copy of the function that has a concrete type and pass through.
    // If you get failed tests about missing IdentifiedObject types, first update the proxied function,
    // then update this one to match and then update the tests.
    private fun whenDiagramServiceObjectProxy(
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

    private fun whenDiagramServiceObjectTester(
        identifiedObject: IdentifiedObject,
        isDiagram: InvokeChecker<Diagram> = NeverInvokedChecker(),
        isDiagramObject: InvokeChecker<DiagramObject> = NeverInvokedChecker(),
        isOther: InvokeChecker<IdentifiedObject> = NeverInvokedChecker()
    ) {
        val returnValue = whenDiagramServiceObjectProxy(
            identifiedObject,
            isDiagram = isDiagram,
            isDiagramObject = isDiagramObject,
            isOther = isOther
        )

        assertThat(returnValue, equalTo(identifiedObject.toString()))
        isDiagram.verifyInvoke()
        isDiagramObject.verifyInvoke()
        isOther.verifyInvoke()
    }

    @Test
    internal fun `supports all diagram service types`() {
        verifyWhenServiceObjectFunctionSupportsAllServiceObjectTypes(DiagramService().supportedKClasses, ::whenDiagramServiceObjectProxy)
    }

    @Test
    internal fun `invokes correct function`() {
        Diagram().also { whenDiagramServiceObjectTester(it, isDiagram = InvokedChecker(it)) }
        DiagramObject().also { whenDiagramServiceObjectTester(it, isDiagramObject = InvokedChecker(it)) }
        object : IdentifiedObject() {}.also { whenDiagramServiceObjectTester(it, isOther = InvokedChecker(it)) }
    }
}
