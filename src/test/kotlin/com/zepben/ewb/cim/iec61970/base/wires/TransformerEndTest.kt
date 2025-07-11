/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

import com.zepben.ewb.cim.iec61970.base.core.Terminal
import com.zepben.ewb.services.common.extensions.typeNameAndMRID
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.testdata.fillFields
import com.zepben.testutils.exception.ExpectException.Companion.expect
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class TransformerEndTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(object : TransformerEnd() {}.mRID, not(equalTo("")))
        assertThat(object : TransformerEnd("id") {}.mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val transformerEnd = object : TransformerEnd() {}

        assertThat(transformerEnd.grounded, equalTo(false))
        assertThat(transformerEnd.rGround, nullValue())
        assertThat(transformerEnd.xGround, nullValue())
        assertThat(transformerEnd.baseVoltage, nullValue())
        assertThat(transformerEnd.ratioTapChanger, nullValue())
        assertThat(transformerEnd.terminal, nullValue())
        assertThat(transformerEnd.starImpedance, nullValue())

        transformerEnd.fillFields(NetworkService())

        assertThat(transformerEnd.grounded, equalTo(true))
        assertThat(transformerEnd.rGround, equalTo(1.0))
        assertThat(transformerEnd.xGround, equalTo(2.0))
        assertThat(transformerEnd.baseVoltage, notNullValue())
        assertThat(transformerEnd.ratioTapChanger, notNullValue())
        assertThat(transformerEnd.terminal, notNullValue())
        assertThat(transformerEnd.starImpedance, notNullValue())
    }

    @Test
    internal fun throwsOnUnknownEndType() {
        val end = object : TransformerEnd() {}
        expect { end.resistanceReactance() }
            .toThrow<NotImplementedError>()
            .withMessage("Unknown transformer end leaf type: ${end.typeNameAndMRID()}. Add support which should at least include `starImpedance?.resistanceReactance() ?: ResistanceReactance()`.")
    }

    @Test
    internal fun throwsOnAssignmentToNonTransformerTerminal() {
        val end = object : TransformerEnd() {}
        val junction = Junction("j0")
        val terminal = Terminal("j0-t1").also { junction.addTerminal(it) }
        expect { end.terminal = terminal }
            .toThrow<IllegalArgumentException>()
            .withMessage(
                "Cannot assign ${end.typeNameAndMRID()} to ${terminal.typeNameAndMRID()}, which is connected to " +
                    "${junction.typeNameAndMRID()} rather than a PowerTransformer."
            )
    }

    @Test
    internal fun doesNotThrowOnAssignmentToDisconnectedTerminal() {
        val end = object : TransformerEnd() {}
        val terminal = Terminal()
        end.terminal = terminal
        assertThat(end.terminal, equalTo(terminal))
    }

}
