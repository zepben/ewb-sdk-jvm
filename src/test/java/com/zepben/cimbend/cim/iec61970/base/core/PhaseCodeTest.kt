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
package com.zepben.cimbend.cim.iec61970.base.core

import com.zepben.cimbend.cim.iec61968.assetinfo.WireInfo
import com.zepben.cimbend.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.test.util.junit.SystemLogExtension
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
