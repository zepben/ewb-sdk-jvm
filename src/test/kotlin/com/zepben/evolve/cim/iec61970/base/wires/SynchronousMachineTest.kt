/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.wires

import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.testdata.fillFields
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class SynchronousMachineTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(SynchronousMachine().mRID, not(equalTo("")))
        assertThat(SynchronousMachine("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val synchronousMachine = SynchronousMachine()

        assertThat(synchronousMachine.baseQ, equalTo(null))
        assertThat(synchronousMachine.condenserP, equalTo(null))
        assertThat(synchronousMachine.earthing, equalTo(null))
        assertThat(synchronousMachine.earthingStarPointR, equalTo(null))
        assertThat(synchronousMachine.earthingStarPointX, equalTo(null))
        assertThat(synchronousMachine.ikk, equalTo(null))
        assertThat(synchronousMachine.maxQ, equalTo(null))
        assertThat(synchronousMachine.maxU, equalTo(null))
        assertThat(synchronousMachine.minQ, equalTo(null))
        assertThat(synchronousMachine.minU, equalTo(null))
        assertThat(synchronousMachine.mu, equalTo(null))
        assertThat(synchronousMachine.r, equalTo(null))
        assertThat(synchronousMachine.r0, equalTo(null))
        assertThat(synchronousMachine.r2, equalTo(null))
        assertThat(synchronousMachine.satDirectSubtransX, equalTo(null))
        assertThat(synchronousMachine.satDirectSyncX, equalTo(null))
        assertThat(synchronousMachine.satDirectTransX, equalTo(null))
        assertThat(synchronousMachine.x0, equalTo(null))
        assertThat(synchronousMachine.x2, equalTo(null))
        assertThat(synchronousMachine.type, equalTo(SynchronousMachineKind.UNKNOWN))
        assertThat(synchronousMachine.operatingMode, equalTo(SynchronousMachineKind.UNKNOWN))

        synchronousMachine.fillFields(NetworkService())

        assertThat(synchronousMachine.baseQ, equalTo(1.1))
        assertThat(synchronousMachine.condenserP, equalTo(2))
        assertThat(synchronousMachine.earthing, equalTo(false))
        assertThat(synchronousMachine.earthingStarPointR, equalTo(3.3))
        assertThat(synchronousMachine.earthingStarPointX, equalTo(4.4))
        assertThat(synchronousMachine.ikk, equalTo(5.5))
        assertThat(synchronousMachine.maxQ, equalTo(6.6))
        assertThat(synchronousMachine.maxU, equalTo(7))
        assertThat(synchronousMachine.minQ, equalTo(8.8))
        assertThat(synchronousMachine.minU, equalTo(9))
        assertThat(synchronousMachine.mu, equalTo(10.10))
        assertThat(synchronousMachine.r, equalTo(11.11))
        assertThat(synchronousMachine.r0, equalTo(12.12))
        assertThat(synchronousMachine.r2, equalTo(13.13))
        assertThat(synchronousMachine.satDirectSubtransX, equalTo(14.14))
        assertThat(synchronousMachine.satDirectSyncX, equalTo(15.15))
        assertThat(synchronousMachine.satDirectTransX, equalTo(16.16))
        assertThat(synchronousMachine.x0, equalTo(17.17))
        assertThat(synchronousMachine.x2, equalTo(18.18))
        assertThat(synchronousMachine.type, equalTo(SynchronousMachineKind.generatorOrMotor))
        assertThat(synchronousMachine.operatingMode, equalTo(SynchronousMachineKind.generator))
    }

    @Test
    internal fun `add reactiveCapabilityCurve`() {
        val synchronousMachine = SynchronousMachine()
        val rcc = ReactiveCapabilityCurve("default-curve")

        synchronousMachine.addCurve(rcc)

        assertThat(synchronousMachine.curves.contains(rcc), equalTo(true))
    }

    @Test
    internal fun `numbCurves() shows the number of reactiveCapabilityCurve associated with this synchronous machine`() {
        val synchronousMachine = SynchronousMachine()
        val rcc = ReactiveCapabilityCurve("default-curve")
        assertThat(synchronousMachine.numCurves(), equalTo(0))

        synchronousMachine.addCurve(rcc)

        assertThat(synchronousMachine.numCurves(), equalTo(1))
    }

    @Test
    internal fun `get reactiveCapabilityCurve`() {
        val synchronousMachine = SynchronousMachine()
        val rcc = ReactiveCapabilityCurve("default-curve")

        synchronousMachine.addCurve(rcc)

        assertThat(synchronousMachine.getCurve("default-curve"), equalTo(rcc))
    }

    @Test
    internal fun `remove synchronousMachineData by removing the same synchronousMachineData`() {
        val synchronousMachine = SynchronousMachine()
        val rcc = ReactiveCapabilityCurve("default-curve")
        assertThat(synchronousMachine.removeCurve(rcc), equalTo(false))

        synchronousMachine.addCurve(rcc)

        assertThat(synchronousMachine.removeCurve(rcc), equalTo(true))
    }

    @Test
    internal fun `remove synchronousMachineData with same x value`() {
        val synchronousMachine = SynchronousMachine()
        val rcc = ReactiveCapabilityCurve("default-curve")
        synchronousMachine.addCurve(rcc)
        assertThat(synchronousMachine.numCurves(), equalTo(1))

        synchronousMachine.clearCurve()

        assertThat(synchronousMachine.numCurves(), equalTo(0))
    }

}
