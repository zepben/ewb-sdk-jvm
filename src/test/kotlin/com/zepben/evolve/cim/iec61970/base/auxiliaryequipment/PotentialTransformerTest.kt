/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.auxiliaryequipment

import com.zepben.evolve.cim.iec61968.infiec61968.infassetinfo.PotentialTransformerInfo
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.testdata.fillFields
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
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

        assertThat(potentialTransformer.assetInfo, nullValue())
        assertThat(potentialTransformer.type, equalTo(PotentialTransformerKind.UNKNOWN))

        potentialTransformer.fillFields(NetworkService(), true)

        assertThat(potentialTransformer.assetInfo, instanceOf(PotentialTransformerInfo::class.java))
        assertThat(potentialTransformer.type, equalTo(PotentialTransformerKind.capacitiveCoupling))
    }

}
