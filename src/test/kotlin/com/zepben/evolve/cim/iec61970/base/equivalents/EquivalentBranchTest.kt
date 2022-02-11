/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.equivalents

import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.testdata.fillFields
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class EquivalentBranchTest {

    @JvmField
    @RegisterExtension
    var systemOut: SystemLogExtension = SystemLogExtension.SYSTEM_OUT.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(EquivalentBranch().mRID, not(equalTo("")))
        assertThat(EquivalentBranch("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val equivalentBranch = EquivalentBranch()

        assertThat(equivalentBranch.negativeR12, nullValue())
        assertThat(equivalentBranch.negativeR21, nullValue())
        assertThat(equivalentBranch.negativeX12, nullValue())
        assertThat(equivalentBranch.negativeX21, nullValue())
        assertThat(equivalentBranch.positiveR12, nullValue())
        assertThat(equivalentBranch.positiveR21, nullValue())
        assertThat(equivalentBranch.positiveX12, nullValue())
        assertThat(equivalentBranch.positiveX21, nullValue())
        assertThat(equivalentBranch.r, nullValue())
        assertThat(equivalentBranch.r21, nullValue())
        assertThat(equivalentBranch.x, nullValue())
        assertThat(equivalentBranch.x21, nullValue())
        assertThat(equivalentBranch.zeroR12, nullValue())
        assertThat(equivalentBranch.zeroR21, nullValue())
        assertThat(equivalentBranch.zeroX12, nullValue())
        assertThat(equivalentBranch.zeroX21, nullValue())

        equivalentBranch.fillFields(NetworkService())

        assertThat(equivalentBranch.negativeR12, equalTo(1.1))
        assertThat(equivalentBranch.negativeR21, equalTo(2.2))
        assertThat(equivalentBranch.negativeX12, equalTo(3.3))
        assertThat(equivalentBranch.negativeX21, equalTo(4.4))
        assertThat(equivalentBranch.positiveR12, equalTo(5.5))
        assertThat(equivalentBranch.positiveR21, equalTo(6.6))
        assertThat(equivalentBranch.positiveX12, equalTo(7.7))
        assertThat(equivalentBranch.positiveX21, equalTo(8.8))
        assertThat(equivalentBranch.r, equalTo(9.9))
        assertThat(equivalentBranch.r21, equalTo(10.01))
        assertThat(equivalentBranch.x, equalTo(11.11))
        assertThat(equivalentBranch.x21, equalTo(12.21))
        assertThat(equivalentBranch.zeroR12, equalTo(13.31))
        assertThat(equivalentBranch.zeroR21, equalTo(14.41))
        assertThat(equivalentBranch.zeroX12, equalTo(15.51))
        assertThat(equivalentBranch.zeroX21, equalTo(16.61))
    }

}
