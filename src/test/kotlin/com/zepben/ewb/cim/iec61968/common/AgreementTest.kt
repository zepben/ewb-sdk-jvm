/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.common

import com.zepben.ewb.cim.iec61970.base.domain.DateTimeInterval
import com.zepben.ewb.services.customer.CustomerService
import com.zepben.ewb.services.customer.testdata.fillFields
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.time.Instant

internal class AgreementTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(object : Agreement() {}.mRID, not(equalTo("")))
        assertThat(object : Agreement("id") {}.mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val agreement = object : Agreement() {}

        assertThat(agreement.validityInterval, nullValue())

        agreement.fillFields(CustomerService())

        assertThat(
            agreement.validityInterval,
            equalTo(DateTimeInterval(start = Instant.ofEpochSecond(1), end = Instant.ofEpochSecond(2)))
        )
    }

}
