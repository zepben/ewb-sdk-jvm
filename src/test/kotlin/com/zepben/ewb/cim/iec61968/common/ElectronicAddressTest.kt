/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.common

import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class ElectronicAddressTest {
    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.Companion.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(ElectronicAddress(), notNullValue())
    }

    @Test
    internal fun accessorCoverage() {
        val address = ElectronicAddress(
            email1 = "email1",
            isPrimary = true,
            description = "description",
        )

        assertThat(address.email1, equalTo("email1"))
        assertThat(address.isPrimary, equalTo(true))
        assertThat(address.description, equalTo("description"))
    }

}
