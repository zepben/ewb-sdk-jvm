/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.auxiliaryequipment

import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class PotentialTransformerTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(CurrentTransformer().mRID, not(equalTo("")))
        assertThat(CurrentTransformer("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val potentialTransformer = PotentialTransformer()

        assertThat(potentialTransformer.type, equalTo(PotentialTransformerKind.UNKNOWN))

        potentialTransformer.type = PotentialTransformerKind.capacitiveCoupling

        assertThat(potentialTransformer.type, equalTo(PotentialTransformerKind.capacitiveCoupling))
    }

}
