/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.customers

import com.zepben.ewb.utils.PrivateCollectionValidator
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class CustomerTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun hasEndDevicesReflectsNumEndDevices() {
        val customer = Customer()
        assertThat("Customer has no end devices by default", !customer.hasEndDevices())

        customer.numEndDevices = 1
        assertThat("Customer with 1 end device has end devices", customer.hasEndDevices())

        customer.numEndDevices = 0
        assertThat("Customer with 0 end devices has no end devices", !customer.hasEndDevices())
    }

    @Test
    internal fun constructorCoverage() {
        assertThat(Customer().mRID, not(equalTo("")))
        assertThat(Customer("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val customer = Customer()

        assertThat(customer.kind, equalTo(CustomerKind.UNKNOWN))
        assertThat(customer.numEndDevices, nullValue())
        assertThat(customer.specialNeed, nullValue())

        customer.apply {
            kind = CustomerKind.enterprise
            numEndDevices = 4
            specialNeed = "my need"
        }

        assertThat(customer.kind, equalTo(CustomerKind.enterprise))
        assertThat(customer.numEndDevices, equalTo(4))
        assertThat(customer.specialNeed, equalTo("my need"))
    }

    @Test
    internal fun customerAgreements() {
        PrivateCollectionValidator.validateUnordered(
            ::Customer,
            ::CustomerAgreement,
            Customer::agreements,
            Customer::numAgreements,
            Customer::getAgreement,
            Customer::addAgreement,
            Customer::removeAgreement,
            Customer::clearAgreements
        )
    }

}
