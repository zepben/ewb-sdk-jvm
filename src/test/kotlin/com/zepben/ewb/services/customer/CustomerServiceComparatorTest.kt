/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.customer

import com.zepben.ewb.cim.iec61968.common.Agreement
import com.zepben.ewb.cim.iec61968.customers.*
import com.zepben.ewb.services.common.BaseServiceComparatorTest
import com.zepben.ewb.services.common.ObjectCollectionDifference
import com.zepben.ewb.utils.ServiceComparatorValidator
import org.junit.jupiter.api.Test

@Suppress("SameParameterValue")
internal class CustomerServiceComparatorTest : BaseServiceComparatorTest() {

    override val comparatorValidator: ServiceComparatorValidator<CustomerService, CustomerServiceComparator> = ServiceComparatorValidator(
        { CustomerService() },
        { CustomerServiceComparator() }
    )

    @Test
    internal fun testCompareCustomer() {
        compareOrganisationRole { Customer(mRID = it) }

        comparatorValidator.validateProperty(Customer::kind, { Customer(it) }, { CustomerKind.residential }, { CustomerKind.commercialIndustrial })
        comparatorValidator.validateCollection(
            Customer::agreements,
            Customer::addAgreement,
            { Customer(it) },
            { CustomerAgreement("1").apply { customer = it } },
            { CustomerAgreement("2").apply { customer = it } },
            ::ObjectCollectionDifference,
        )
        comparatorValidator.validateProperty(Customer::numEndDevices, { Customer(it) }, { 1 }, { 2 })
        comparatorValidator.validateProperty(Customer::specialNeed, { Customer(it) }, { "1" }, { "2" })
    }

    @Test
    internal fun testCompareCustomerAgreement() {
        compareAgreement { CustomerAgreement(mRID = it) }

        comparatorValidator.validateProperty(CustomerAgreement::customer, { CustomerAgreement(it) }, { Customer("1") }, { Customer("2") })
        comparatorValidator.validateCollection(
            CustomerAgreement::pricingStructures,
            CustomerAgreement::addPricingStructure,
            { CustomerAgreement(it) },
            { PricingStructure("1") },
            { PricingStructure("2") },
            ::ObjectCollectionDifference
        )
    }

    @Test
    internal fun testComparePricingStructure() {
        compareDocument { PricingStructure(mRID = it) }

        comparatorValidator.validateCollection(
            PricingStructure::tariffs,
            PricingStructure::addTariff,
            { PricingStructure(it) },
            { Tariff("1") },
            { Tariff("2") },
            ::ObjectCollectionDifference,
        )
        comparatorValidator.validateProperty(PricingStructure::code, { PricingStructure(it) }, { "1" }, { "2" })
    }

    @Test
    internal fun testCompareTariff() {
        compareDocument { Tariff(mRID = it) }
    }

    private fun compareAgreement(newAgreement: (mRID: String) -> Agreement) {
        compareDocument(newAgreement)
    }

}
