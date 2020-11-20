/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.cim.iec61970.base.core

import com.zepben.cimbend.utils.PrivateCollectionValidator
import com.zepben.cimbend.cim.iec61970.base.wires.PowerTransformer
import com.zepben.testutils.exception.ExpectException.expect
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class FeederTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(Feeder().mRID, not(equalTo("")))
        assertThat(Feeder("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val feeder = Feeder()
        val terminal = Terminal()
        val substation = Substation()

        assertThat(feeder.normalHeadTerminal, nullValue())
        assertThat(feeder.normalEnergizingSubstation, nullValue())

        feeder.apply {
            normalHeadTerminal = terminal
            normalEnergizingSubstation = substation
        }

        assertThat(feeder.normalHeadTerminal, equalTo(terminal))
        assertThat(feeder.normalEnergizingSubstation, equalTo(substation))
    }

    @Test
    internal fun currentEquipment() {
        PrivateCollectionValidator.validate(
            { Feeder() },
            { id, _ -> object : Equipment(id) {} },
            Feeder::numCurrentEquipment,
            Feeder::getCurrentEquipment,
            Feeder::currentEquipment,
            Feeder::addCurrentEquipment,
            Feeder::removeCurrentEquipment,
            Feeder::clearCurrentEquipment
        )
    }

    @Test
    internal fun `can set feeder head terminal on feeder without equipment`() {
        val feeder = Feeder()
        val terminal = Terminal()
        feeder.normalHeadTerminal = terminal

        assertThat(feeder.normalHeadTerminal, equalTo(terminal))

        val terminal2 = Terminal()
        feeder.normalHeadTerminal = terminal2

        assertThat(feeder.normalHeadTerminal, equalTo(terminal2))
    }

    @Test
    internal fun `can set feeder head terminal on feeder with equipment but no head terminal`() {
        val feeder = Feeder()
        feeder.addEquipment(PowerTransformer())
        val terminal = Terminal()
        feeder.normalHeadTerminal = terminal

        assertThat(feeder.normalHeadTerminal, equalTo(terminal))
    }

    @Test
    internal fun `cannot set feeder head terminal on feeder with equipment and head terminal assigned`() {
        val feeder = Feeder()
        feeder.addEquipment(PowerTransformer())
        val terminal = Terminal()
        feeder.normalHeadTerminal = terminal

        expect { feeder.normalHeadTerminal = Terminal() }.toThrow()
            .withMessage("Feeder ${feeder.mRID} has equipment assigned to it. Cannot update normalHeadTerminal on a feeder with equipment assigned.")
    }

}
