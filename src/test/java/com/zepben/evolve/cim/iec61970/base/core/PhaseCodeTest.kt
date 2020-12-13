/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.core

import com.zepben.evolve.cim.iec61968.assetinfo.WireInfo
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class PhaseCodeTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        val wireInfo = object : WireInfo() {}
        assertThat(wireInfo.mRID, not(equalTo("")))

        val wireInfoId = object : WireInfo("id") {}
        assertThat(wireInfoId.mRID, equalTo("id"))
    }

    @Test
    internal fun valueCoverage() {
        PhaseCode.values().forEach {
            assertThat(PhaseCode.valueOf(it.name), equalTo(it))
        }
    }

    @Test
    internal fun singlePhases() {
        PhaseCode.values()
            .asSequence()
            .filter { it !== PhaseCode.NONE }
            .forEach {
                assertThat(it.singlePhases().size, equalTo(it.name.length))

                val singlePhases = it.singlePhases()
                    .asSequence()
                    .map(SinglePhaseKind::name)
                    .toSet()

                val namePhases = it.name.toCharArray().map(Any::toString).toSet()

                assertThat(singlePhases.containsAll(namePhases), equalTo(true))
            }
    }
}
