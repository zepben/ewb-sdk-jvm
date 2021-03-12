/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61968.customers

import com.zepben.evolve.utils.PrivateCollectionValidator
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class CustomerTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(Customer().mRID, not(equalTo("")))
        assertThat(Customer("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val customer = Customer()

        assertThat(customer.kind, equalTo(CustomerKind.UNKNOWN))
        assertThat(customer.numEndDevices, equalTo(0))

        customer.apply {
            kind = CustomerKind.enterprise
            numEndDevices = 4
        }

        assertThat(customer.kind, equalTo(CustomerKind.enterprise))
        assertThat(customer.numEndDevices, equalTo(4))
    }

    @Test
    internal fun customerAgreements() {
        PrivateCollectionValidator.validate(
            { Customer() },
            { id, _ -> CustomerAgreement(id) },
            Customer::numAgreements,
            Customer::getAgreement,
            Customer::agreements,
            Customer::addAgreement,
            Customer::removeAgreement,
            Customer::clearAgreements
        )
    }
}
