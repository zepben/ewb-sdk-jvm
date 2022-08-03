/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.connectivity

import com.zepben.evolve.cim.iec61970.base.wires.Junction
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.kotlin.*

internal class ConnectedEquipmentTraversalTest {

    @JvmField
    @RegisterExtension
    var systemOut: SystemLogExtension = SystemLogExtension.SYSTEM_OUT.captureLog().muteOnSuccess()

    private val traversal = spy(ConnectedEquipmentTraversal(mock(), mock(), mock())).also {
        doAnswer {}.`when`(it).run(any<ConductingEquipmentStep>(), any())
    }

    @Test
    internal fun wrapsConductingEquipmentInStepZero() {
        val j = Junction()

        traversal.run(j)

        val captor = argumentCaptor<ConductingEquipmentStep>()
        verify(traversal).run(captor.capture(), any())

        assertThat(captor.firstValue.conductingEquipment, equalTo(j))
        assertThat(captor.firstValue.step, equalTo(0))
    }

    @Test
    internal fun runDefaultsToStopOnStart() {
        traversal.run(Junction())

        verify(traversal).run(any<ConductingEquipmentStep>(), eq(true))
    }

    @Test
    internal fun runCanChangeStopOnStart() {
        traversal.run(Junction(), canStopOnStartItem = false)

        verify(traversal).run(any<ConductingEquipmentStep>(), eq(false))
    }

}
