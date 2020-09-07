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
package com.zepben.cimbend.cim.iec61970.infiec61970.feeder

import com.zepben.cimbend.cim.iec61970.base.core.Substation
import com.zepben.cimbend.cim.iec61970.base.core.Terminal
import com.zepben.cimbend.utils.PrivateCollectionValidator
import com.zepben.test.util.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class CircuitTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(Circuit().mRID, not(equalTo("")))
        assertThat(Circuit("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val circuit = Circuit()
        val loop = Loop()

        assertThat(circuit.loop, nullValue())

        circuit.loop = loop
        assertThat(circuit.loop, equalTo(loop))
    }

    @Test
    internal fun endTerminalAssociations() {
        PrivateCollectionValidator.validate(
            ::Circuit,
            { id, _ -> Terminal(id) },
            Circuit::numEndTerminals,
            Circuit::getEndTerminal,
            Circuit::endTerminals,
            Circuit::addEndTerminal,
            Circuit::removeEndTerminal,
            Circuit::clearEndTerminals
        )
    }

    @Test
    internal fun endSubstationAssociations() {
        PrivateCollectionValidator.validate(
            ::Circuit,
            { id, _ -> Substation(id) },
            Circuit::numEndSubstations,
            Circuit::getEndSubstation,
            Circuit::endSubstations,
            Circuit::addEndSubstation,
            Circuit::removeEndSubstation,
            Circuit::clearEndSubstations
        )
    }
}
