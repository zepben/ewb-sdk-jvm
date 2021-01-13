/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61968.assetinfo

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.not
import org.junit.Test

internal class PowerTransformerInfoTest {

    @Test
    internal fun constructorCoverage() {
        assertThat(CableInfo().mRID, not(equalTo("")))
        assertThat(CableInfo("id").mRID, equalTo("id"))
    }

}
