/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.extensions.iec61970.base.feeder

import com.zepben.ewb.cim.iec61970.base.core.Feeder
import com.zepben.ewb.utils.PrivateCollectionValidator
import org.hamcrest.MatcherAssert.assertThat
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


}