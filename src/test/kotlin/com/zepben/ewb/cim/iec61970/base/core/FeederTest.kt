/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.core

import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvSubstation
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvFeeder
import com.zepben.ewb.cim.iec61970.base.wires.PowerTransformer
import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.ewb.utils.PrivateCollectionValidator
import com.zepben.testutils.exception.ExpectException.Companion.expect
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class FeederTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val feeder = Feeder(generateId())

    @Test
    internal fun constructorCoverage() {
        assertThat(Feeder("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val terminal = Terminal(generateId())
        val substation = Substation(generateId())

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
        PrivateCollectionValidator.validateUnordered(
            ::Feeder,
            { id -> object : Equipment(id) {} },
            Feeder::currentEquipment,
            Feeder::numCurrentEquipment,
            Feeder::getCurrentEquipment,
            Feeder::addCurrentEquipment,
            Feeder::removeCurrentEquipment,
            Feeder::clearCurrentEquipment
        )
    }

    @Test
    internal fun normalEnergizedLvFeeders() {
        PrivateCollectionValidator.validateUnordered(
            ::Feeder,
            ::LvFeeder,
            Feeder::normalEnergizedLvFeeders,
            Feeder::numNormalEnergizedLvFeeders,
            Feeder::getNormalEnergizedLvFeeder,
            Feeder::addNormalEnergizedLvFeeder,
            Feeder::removeNormalEnergizedLvFeeder,
            Feeder::clearNormalEnergizedLvFeeders
        )
    }

    @Test
    internal fun currentEnergizedLvFeeders() {
        PrivateCollectionValidator.validateUnordered(
            ::Feeder,
            ::LvFeeder,
            Feeder::currentEnergizedLvFeeders,
            Feeder::numCurrentEnergizedLvFeeders,
            Feeder::getCurrentEnergizedLvFeeder,
            Feeder::addCurrentEnergizedLvFeeder,
            Feeder::removeCurrentEnergizedLvFeeder,
            Feeder::clearCurrentEnergizedLvFeeders
        )
    }

    @Test
    internal fun normalEnergizedLvSubstations() {
        PrivateCollectionValidator.validateUnordered(
            ::Feeder,
            ::LvSubstation,
            Feeder::normalEnergizedLvSubstations,
            Feeder::numNormalEnergizedLvSubstations,
            Feeder::getNormalEnergizedLvSubstation,
            Feeder::addNormalEnergizedLvSubstation,
            Feeder::removeNormalEnergizedLvSubstation,
            Feeder::clearNormalEnergizedLvSubstations
        )
    }

    @Test
    internal fun currentEnergizedLvSubstations() {
        PrivateCollectionValidator.validateUnordered(
            ::Feeder,
            ::LvFeeder,
            Feeder::currentEnergizedLvFeeders,
            Feeder::numCurrentEnergizedLvFeeders,
            Feeder::getCurrentEnergizedLvFeeder,
            Feeder::addCurrentEnergizedLvFeeder,
            Feeder::removeCurrentEnergizedLvFeeder,
            Feeder::clearCurrentEnergizedLvFeeders
        )
    }

    @Test
    internal fun `can set feeder head terminal on feeder without equipment`() {
        val terminal = Terminal(generateId())
        val terminal2 = Terminal(generateId())

        feeder.normalHeadTerminal = terminal
        assertThat(feeder.normalHeadTerminal, equalTo(terminal))

        feeder.normalHeadTerminal = terminal2
        assertThat(feeder.normalHeadTerminal, equalTo(terminal2))
    }

    @Test
    internal fun `can set feeder head terminal on feeder with equipment but no head terminal`() {
        val terminal = Terminal(generateId())

        feeder.addEquipment(PowerTransformer(generateId()))
        feeder.normalHeadTerminal = terminal

        assertThat(feeder.normalHeadTerminal, equalTo(terminal))
    }

    @Test
    internal fun `can set feeder head terminal on feeder with current equipment but no head terminal`() {
        val terminal = Terminal(generateId())

        feeder.addCurrentEquipment(PowerTransformer(generateId()))
        feeder.normalHeadTerminal = terminal

        assertThat(feeder.normalHeadTerminal, equalTo(terminal))
    }

    @Test
    internal fun `cannot set feeder head terminal on feeder with equipment and head terminal assigned`() {
        feeder.apply {
            normalHeadTerminal = Terminal(generateId())
            addEquipment(PowerTransformer(generateId()))
        }

        expect { feeder.normalHeadTerminal = Terminal(generateId()) }.toThrowAny()
            .withMessage("Feeder ${feeder.mRID} has equipment assigned to it. Cannot update normalHeadTerminal on a feeder with equipment assigned.")
    }

    @Test
    internal fun `cannot set feeder head terminal on feeder with current equipment and head terminal assigned`() {
        feeder.apply {
            normalHeadTerminal = Terminal(generateId())
            addCurrentEquipment(PowerTransformer(generateId()))
        }

        expect { feeder.normalHeadTerminal = Terminal(generateId()) }.toThrowAny()
            .withMessage("Feeder ${feeder.mRID} has equipment assigned to it. Cannot update normalHeadTerminal on a feeder with equipment assigned.")
    }

}
