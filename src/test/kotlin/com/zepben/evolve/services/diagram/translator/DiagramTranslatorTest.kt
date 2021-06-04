/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.diagram.translator

import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject
import com.zepben.evolve.cim.iec61970.base.diagramlayout.Diagram
import com.zepben.evolve.cim.iec61970.base.diagramlayout.DiagramObject
import com.zepben.evolve.services.diagram.DiagramService
import com.zepben.evolve.services.diagram.DiagramServiceComparator
import com.zepben.evolve.services.diagram.testdata.fillFields
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.anEmptyMap
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.api.fail

internal class DiagramTranslatorTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val comparator = DiagramServiceComparator()

    @Test
    internal fun convertsCorrectly() {
        /************ IEC61970 BASE DIAGRAM LAYOUT ************/
        validate({ Diagram() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(it.toPb()) })
        validate({ DiagramObject() }, { ns, it -> it.fillFields(ns) }, { ns, it -> ns.addFromPb(it.toPb()) })
    }

    private inline fun <reified T : IdentifiedObject> validate(creator: () -> T, filler: (DiagramService, T) -> Unit, adder: (DiagramService, T) -> T?) {
        val cim = creator()
        val blankDifferences = comparator.compare(cim, adder(DiagramService(), cim)!!).differences
        assertThat("Failed to convert blank ${T::class.simpleName}:${blankDifferences}", blankDifferences, anEmptyMap())

        filler(DiagramService(), cim)

        val populatedDifferences = comparator.compare(cim, addWithUnresolvedReferences(cim, adder)).differences
        assertThat("Failed to convert populated ${T::class.simpleName}:${populatedDifferences}", populatedDifferences, anEmptyMap())
    }

    private inline fun <reified T : IdentifiedObject> addWithUnresolvedReferences(cim: T, adder: (DiagramService, T) -> T?): T {
        // We need to convert the populated item before we check the differences so we can complete the unresolved references.
        val service = DiagramService()
        val convertedCim = adder(service, cim)!!
        service.unresolvedReferences().toList().forEach { ref ->
            try {
                // There are no abstract classes in the chain currently for the diagram service. If they ever show up copy the code
                // form network to support them.
                ref.resolver.toClass.getDeclaredConstructor(String::class.java).newInstance(ref.toMrid).also { service.tryAdd(it) }
            } catch (e: Exception) {
                // If this fails you need to add a concrete type mapping to the abstractCreators map at the top of this class.
                fail("Failed to create unresolved reference for ${ref.resolver.toClass}.", e)
            }
        }
        return convertedCim
    }

}
