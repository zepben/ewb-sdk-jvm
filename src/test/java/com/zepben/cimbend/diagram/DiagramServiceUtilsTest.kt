/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.diagram

import com.zepben.cimbend.cim.iec61970.base.core.IdentifiedObject
import com.zepben.cimbend.cim.iec61970.base.diagramlayout.Diagram
import com.zepben.cimbend.cim.iec61970.base.diagramlayout.DiagramObject
import com.zepben.cimbend.common.InvokeChecker
import com.zepben.cimbend.common.InvokedChecker
import com.zepben.cimbend.common.NeverInvokedChecker
import com.zepben.cimbend.common.verifyWhenServiceObjectFunctionSupportsAllServiceObjectTypes
import com.zepben.test.util.junit.SystemLogExtension
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertThat
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
    fun `supports all diagram service types`() {
        verifyWhenServiceObjectFunctionSupportsAllServiceObjectTypes(DiagramService().supportedKClasses, ::whenDiagramServiceObjectProxy)
    }

    @Test
    internal fun `invokes correct function`() {
        Diagram().also { whenDiagramServiceObjectTester(it, isDiagram = InvokedChecker(it)) }
        DiagramObject().also { whenDiagramServiceObjectTester(it, isDiagramObject = InvokedChecker(it)) }
        object : IdentifiedObject() {}.also { whenDiagramServiceObjectTester(it, isOther = InvokedChecker(it)) }
    }
}

