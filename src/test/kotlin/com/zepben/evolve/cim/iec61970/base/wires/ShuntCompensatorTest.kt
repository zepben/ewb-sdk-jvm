/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.wires

import com.zepben.evolve.cim.iec61968.assetinfo.ShuntCompensatorInfo
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.testdata.fillFields
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

        assertThat(shuntCompensator.assetInfo, nullValue())
        assertThat(shuntCompensator.grounded, equalTo(false))
        assertThat(shuntCompensator.nomU, nullValue())
        assertThat(shuntCompensator.phaseConnection, equalTo(PhaseShuntConnectionKind.UNKNOWN))
        assertThat(shuntCompensator.sections, nullValue())

        shuntCompensator.fillFields(NetworkService())

        assertThat(shuntCompensator.assetInfo, notNullValue())
        assertThat(shuntCompensator.assetInfo, instanceOf(ShuntCompensatorInfo::class.java))
        assertThat(shuntCompensator.grounded, equalTo(true))
        assertThat(shuntCompensator.nomU, equalTo(1))
        assertThat(shuntCompensator.phaseConnection, equalTo(PhaseShuntConnectionKind.I))
        assertThat(shuntCompensator.sections, equalTo(2.2))
    }

}
