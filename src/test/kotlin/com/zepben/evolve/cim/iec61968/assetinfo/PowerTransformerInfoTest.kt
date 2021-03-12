/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61968.assetinfo

import com.zepben.evolve.utils.PrivateCollectionValidator
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test

internal class PowerTransformerInfoTest {

    @Test
    internal fun constructorCoverage() {
        assertThat(PowerTransformerInfo().mRID, not(equalTo("")))
        assertThat(PowerTransformerInfo("id").mRID, equalTo("id"))
    }

    @Test
    internal fun transformerTankInfo() {
        PrivateCollectionValidator.validate(
            { PowerTransformerInfo() },
            { id, info -> TransformerTankInfo(id).apply { powerTransformerInfo = info } },
            PowerTransformerInfo::numTransformerTankInfos,
            PowerTransformerInfo::getTransformerTankInfo,
            PowerTransformerInfo::transformerTankInfos,
            PowerTransformerInfo::addTransformerTankInfo,
            PowerTransformerInfo::removeTransformerTankInfo,
            PowerTransformerInfo::clearTransformerTankInfos
        )
    }

}
