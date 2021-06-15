/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.wires

import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class ShuntCompensatorTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(object : ShuntCompensator() {}.mRID, not(equalTo("")))
        assertThat(object : ShuntCompensator("id") {}.mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val shuntCompensator = object : ShuntCompensator() {}

        assertThat(shuntCompensator.grounded, equalTo(false))
        assertThat(shuntCompensator.nomU, nullValue())
        assertThat(shuntCompensator.phaseConnection, equalTo(PhaseShuntConnectionKind.UNKNOWN))
        assertThat(shuntCompensator.sections, nullValue())

        shuntCompensator.grounded = true
        shuntCompensator.nomU = 2
        shuntCompensator.phaseConnection = PhaseShuntConnectionKind.G
        shuntCompensator.sections = 5.6

        assertThat(shuntCompensator.grounded, equalTo(true))
        assertThat(shuntCompensator.nomU, equalTo(2))
        assertThat(shuntCompensator.phaseConnection, equalTo(PhaseShuntConnectionKind.G))
        assertThat(shuntCompensator.sections, equalTo(5.6))
    }
}
