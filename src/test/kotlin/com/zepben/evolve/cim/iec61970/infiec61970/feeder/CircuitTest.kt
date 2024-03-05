/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.infiec61970.feeder

import com.zepben.evolve.cim.iec61970.base.core.Substation
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.utils.PrivateCollectionValidator
import com.zepben.testutils.junit.SystemLogExtension
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
