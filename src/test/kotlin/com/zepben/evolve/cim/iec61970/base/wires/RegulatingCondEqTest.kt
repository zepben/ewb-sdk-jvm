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
import com.zepben.testutils.exception.ExpectException.Companion.expect
import com.zepben.testutils.junit.SystemLogExtension
import io.mockk.mockk
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class RegulatingCondEqTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(object : RegulatingCondEq() {}.mRID, not(equalTo("")))
        assertThat(object : RegulatingCondEq("id") {}.mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val regulatingCondEq = object : RegulatingCondEq() {}

        assertThat(regulatingCondEq.controlEnabled, equalTo(true))
        assertThat(regulatingCondEq.regulatingControl, nullValue())

        regulatingCondEq.fillFields(NetworkService())

        assertThat(regulatingCondEq.controlEnabled, equalTo(false))
        assertThat(regulatingCondEq.regulatingControl, notNullValue())
    }

    @Test
    internal fun cannotChangeRegulatingControl() {
        val regulatingCondEq = object : RegulatingCondEq() {}
        val regControl = mockk<RegulatingControl>()
        regulatingCondEq.regulatingControl = regControl

        expect {
            regulatingCondEq.regulatingControl = mockk()
        }.toThrow<IllegalStateException>().withMessage("regulatingControl has already been set to $regControl. Cannot set this field again")
        assertThat(regulatingCondEq.regulatingControl, equalTo(regControl))
    }
}
