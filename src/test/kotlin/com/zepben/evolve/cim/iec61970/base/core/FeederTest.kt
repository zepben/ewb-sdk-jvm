/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.core

import com.zepben.evolve.cim.iec61970.base.wires.PowerTransformer
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.LvFeeder
import com.zepben.evolve.utils.PrivateCollectionValidator
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

    private val feeder = Feeder()

    @Test
    internal fun constructorCoverage() {
        assertThat(Feeder().mRID, not(equalTo("")))
        assertThat(Feeder("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
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
    internal fun normalEnergizedLvFeeders() {
        PrivateCollectionValidator.validate(
            { Feeder() },
            { id, _ -> LvFeeder(id) },
            Feeder::numNormalEnergizedLvFeeders,
            Feeder::getNormalEnergizedLvFeeder,
            Feeder::normalEnergizedLvFeeders,
            Feeder::addNormalEnergizedLvFeeder,
            Feeder::removeNormalEnergizedLvFeeder,
            Feeder::clearNormalEnergizedLvFeeders
        )
    }

    @Test
    internal fun `can set feeder head terminal on feeder without equipment`() {
        val terminal = Terminal()
        val terminal2 = Terminal()

        feeder.normalHeadTerminal = terminal
        assertThat(feeder.normalHeadTerminal, equalTo(terminal))

        feeder.normalHeadTerminal = terminal2
        assertThat(feeder.normalHeadTerminal, equalTo(terminal2))
    }

    @Test
    internal fun `can set feeder head terminal on feeder with equipment but no head terminal`() {
        val terminal = Terminal()

        feeder.addEquipment(PowerTransformer())
        feeder.normalHeadTerminal = terminal

        assertThat(feeder.normalHeadTerminal, equalTo(terminal))
    }

    @Test
    internal fun `can set feeder head terminal on feeder with current equipment but no head terminal`() {
        val terminal = Terminal()

        feeder.addCurrentEquipment(PowerTransformer())
        feeder.normalHeadTerminal = terminal

        assertThat(feeder.normalHeadTerminal, equalTo(terminal))
    }

    @Test
    internal fun `cannot set feeder head terminal on feeder with equipment and head terminal assigned`() {
        feeder.apply {
            normalHeadTerminal = Terminal()
            addEquipment(PowerTransformer())
        }

        expect { feeder.normalHeadTerminal = Terminal() }.toThrow()
            .withMessage("Feeder ${feeder.mRID} has equipment assigned to it. Cannot update normalHeadTerminal on a feeder with equipment assigned.")
    }

    @Test
    internal fun `cannot set feeder head terminal on feeder with current equipment and head terminal assigned`() {
        feeder.apply {
            normalHeadTerminal = Terminal()
            addCurrentEquipment(PowerTransformer())
        }

        expect { feeder.normalHeadTerminal = Terminal() }.toThrow()
            .withMessage("Feeder ${feeder.mRID} has equipment assigned to it. Cannot update normalHeadTerminal on a feeder with equipment assigned.")
    }

}
