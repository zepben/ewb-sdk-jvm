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
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class CustomerAgreementTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(CustomerAgreement().mRID, not(equalTo("")))
        assertThat(CustomerAgreement("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val customerAgreement = CustomerAgreement()
        val customer = Customer()

        assertThat(customerAgreement.customer, nullValue())

        customerAgreement.customer = customer

        assertThat(customerAgreement.customer, equalTo(customer))
    }

    @Test
    internal fun pricingStructures() {
        PrivateCollectionValidator.validate(
            { CustomerAgreement() },
            { id, _ -> PricingStructure(id) },
            CustomerAgreement::numPricingStructures,
            CustomerAgreement::getPricingStructure,
            CustomerAgreement::pricingStructures,
            CustomerAgreement::addPricingStructure,
            CustomerAgreement::removePricingStructure,
            CustomerAgreement::clearPricingStructures
        )
    }
}
