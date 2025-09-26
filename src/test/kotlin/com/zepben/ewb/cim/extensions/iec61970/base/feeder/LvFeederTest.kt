/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.extensions.iec61970.base.feeder

import com.zepben.ewb.cim.iec61970.base.core.Equipment
import com.zepben.ewb.cim.iec61970.base.core.Feeder
import com.zepben.ewb.cim.iec61970.base.core.Terminal
import com.zepben.ewb.cim.iec61970.base.wires.PowerTransformer
import com.zepben.ewb.utils.PrivateCollectionValidator
import com.zepben.testutils.exception.ExpectException
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class LvFeederTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val lvFeeder = LvFeeder()

    @Test
    internal fun constructorCoverage() {
        assertThat(LvFeeder().mRID, not(equalTo("")))
        assertThat(LvFeeder("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val terminal = Terminal()

        assertThat(lvFeeder.normalHeadTerminal, nullValue())

        lvFeeder.apply {
            normalHeadTerminal = terminal
        }

        assertThat(lvFeeder.normalHeadTerminal, equalTo(terminal))
    }

    @Test
    internal fun normalEnergizingFeeders() {
        PrivateCollectionValidator.validateUnordered(
            ::LvFeeder,
            ::Feeder,
            LvFeeder::normalEnergizingFeeders,
            LvFeeder::numNormalEnergizingFeeders,
            LvFeeder::getNormalEnergizingFeeder,
            LvFeeder::addNormalEnergizingFeeder,
            LvFeeder::removeNormalEnergizingFeeder,
            LvFeeder::clearNormalEnergizingFeeders
        )
    }

    @Test
    internal fun currentEnergizingFeeders() {
        PrivateCollectionValidator.validateUnordered(
            ::LvFeeder,
            ::Feeder,
            LvFeeder::currentEnergizingFeeders,
            LvFeeder::numCurrentEnergizingFeeders,
            LvFeeder::getCurrentEnergizingFeeder,
            LvFeeder::addCurrentEnergizingFeeder,
            LvFeeder::removeCurrentEnergizingFeeder,
            LvFeeder::clearCurrentEnergizingFeeders
        )
    }

    @Test
    internal fun currentEquipment() {
        PrivateCollectionValidator.validateUnordered(
            ::LvFeeder,
            { id -> object : Equipment(id) {} },
            LvFeeder::currentEquipment,
            LvFeeder::numCurrentEquipment,
            LvFeeder::getCurrentEquipment,
            LvFeeder::addCurrentEquipment,
            LvFeeder::removeCurrentEquipment,
            LvFeeder::clearCurrentEquipment
        )
    }

    @Test
    internal fun `can set feeder head terminal on feeder without equipment`() {
        val terminal = Terminal()
        val terminal2 = Terminal()

        lvFeeder.normalHeadTerminal = terminal
        assertThat(lvFeeder.normalHeadTerminal, equalTo(terminal))

        lvFeeder.normalHeadTerminal = terminal2
        assertThat(lvFeeder.normalHeadTerminal, equalTo(terminal2))
    }

    @Test
    internal fun `can set feeder head terminal on feeder with equipment but no head terminal`() {
        val terminal = Terminal()

        lvFeeder.addEquipment(PowerTransformer())
        lvFeeder.normalHeadTerminal = terminal

        assertThat(lvFeeder.normalHeadTerminal, equalTo(terminal))
    }

    @Test
    internal fun `can set feeder head terminal on feeder with current equipment but no head terminal`() {
        val terminal = Terminal()

        lvFeeder.addCurrentEquipment(PowerTransformer())
        lvFeeder.normalHeadTerminal = terminal

        assertThat(lvFeeder.normalHeadTerminal, equalTo(terminal))
    }

    @Test
    internal fun `cannot set feeder head terminal on feeder with equipment and head terminal assigned`() {
        lvFeeder.apply {
            normalHeadTerminal = Terminal()
            addEquipment(PowerTransformer())
        }

        ExpectException.expect { lvFeeder.normalHeadTerminal = Terminal() }.toThrowAny()
            .withMessage("LvFeeder ${lvFeeder.mRID} has equipment assigned to it. Cannot update normalHeadTerminal on a feeder with equipment assigned.")
    }

    @Test
    internal fun `cannot set feeder head terminal on feeder with current equipment and head terminal assigned`() {
        lvFeeder.apply {
            normalHeadTerminal = Terminal()
            addCurrentEquipment(PowerTransformer())
        }

        ExpectException.expect { lvFeeder.normalHeadTerminal = Terminal() }.toThrowAny()
            .withMessage("LvFeeder ${lvFeeder.mRID} has equipment assigned to it. Cannot update normalHeadTerminal on a feeder with equipment assigned.")
    }

}
