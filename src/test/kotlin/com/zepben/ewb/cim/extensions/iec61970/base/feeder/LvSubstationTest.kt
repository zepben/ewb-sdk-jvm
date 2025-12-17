/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.extensions.iec61970.base.feeder

import com.zepben.ewb.cim.iec61970.base.core.Feeder
import com.zepben.ewb.cim.iec61970.base.core.Terminal
import com.zepben.ewb.cim.iec61970.base.wires.Fuse
import com.zepben.ewb.cim.iec61970.base.wires.PowerTransformer
import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.ewb.utils.PrivateCollectionValidator
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class LvSubstationTest {
    @Test
    internal fun constructorCoverage() {
        assertThat(LvSubstation("id").mRID, equalTo("id"))
    }

    @Test
    internal fun normalEnergizedLvFeeders() {
        PrivateCollectionValidator.validateUnordered(
            ::LvSubstation,
            ::LvFeeder,
            LvSubstation::normalEnergizedLvFeeders,
            LvSubstation::numNormalEnergizedLvFeeders,
            LvSubstation::getNormalEnergizedLvFeeder,
            LvSubstation::addNormalEnergizedLvFeeder,
            LvSubstation::removeNormalEnergizedLvFeeder,
            LvSubstation::clearNormalEnergizedLvFeeders
        )
    }

    @Test
    internal fun currentEnergizedLvFeeders() {
        PrivateCollectionValidator.validateUnordered(
            ::LvSubstation,
            ::LvFeeder,
            LvSubstation::currentEnergizedLvFeeders,
            LvSubstation::numCurrentEnergizedLvFeeders,
            LvSubstation::getCurrentEnergizedLvFeeder,
            LvSubstation::addCurrentEnergizedLvFeeder,
            LvSubstation::removeCurrentEnergizedLvFeeder,
            LvSubstation::clearCurrentEnergizedLvFeeders
        )
    }

    @Test
    internal fun normalEnergizingFeeders() {
        PrivateCollectionValidator.validateUnordered(
            ::LvSubstation,
            ::Feeder,
            LvSubstation::normalEnergizingFeeders,
            LvSubstation::numNormalEnergizingFeeders,
            LvSubstation::getNormalEnergizingFeeder,
            LvSubstation::addNormalEnergizingFeeder,
            LvSubstation::removeNormalEnergizingFeeder,
            LvSubstation::clearNormalEnergizingFeeders
        )
    }

    @Test
    internal fun currentEnergizingFeeders() {
        PrivateCollectionValidator.validateUnordered(
            ::LvSubstation,
            ::Feeder,
            LvSubstation::currentEnergizingFeeders,
            LvSubstation::numCurrentEnergizingFeeders,
            LvSubstation::getCurrentEnergizingFeeder,
            LvSubstation::addCurrentEnergizingFeeder,
            LvSubstation::removeCurrentEnergizingFeeder,
            LvSubstation::clearCurrentEnergizingFeeders
        )
    }

    @Test
    internal fun lvSwitchFeeders() {
        val ptt = Terminal(generateId())
        val ft = Terminal(generateId())
        val pt = PowerTransformer(generateId()).apply { addTerminal(ptt) }
        val fuse = Fuse(generateId()).apply { addTerminal(ft) }
        val lvf1 = LvFeeder(generateId()).apply { normalHeadTerminal = ptt }
        val lvf2 = LvFeeder(generateId()).apply { normalHeadTerminal = ft }
        val lvSub = LvSubstation(generateId()).apply {
            addNormalEnergizedLvFeeder(lvf1)
            addNormalEnergizedLvFeeder(lvf2)
            addCurrentEnergizedLvFeeder(lvf1)
            addCurrentEnergizedLvFeeder(lvf2)
        }

        assertThat(lvSub.normalEnergizedLvSwitchFeeders(), contains(lvf2))
        assertThat(lvSub.currentEnergizedLvSwitchFeeders(), contains(lvf2))
    }

}