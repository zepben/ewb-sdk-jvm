/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.customer

import com.zepben.ewb.cim.iec61968.common.Agreement
import com.zepben.ewb.cim.iec61968.customers.Customer
import com.zepben.ewb.cim.iec61968.customers.CustomerAgreement
import com.zepben.ewb.cim.iec61968.customers.PricingStructure
import com.zepben.ewb.cim.iec61968.customers.Tariff
import com.zepben.ewb.services.common.BaseServiceComparator
import com.zepben.ewb.services.common.ObjectDifference

/**
 * A class for comparing the contents of a [CustomerService].
 *
 * NOTE: Unused functions have been suppressed for this class as they are access by reflection rather than directly. This
 *       means they are always flagged as unused. By suppressing the warning it also means you might not be testing every
 *       function, so make sure you check the code coverage
 */
@Suppress("unused")
class CustomerServiceComparator : BaseServiceComparator() {

    private fun ObjectDifference<out Agreement>.compareAgreement(): ObjectDifference<out Agreement> =
        apply { compareDocument() }

    private fun compareCustomer(source: Customer, target: Customer): ObjectDifference<Customer> =
        ObjectDifference(source, target).apply {
            compareOrganisationRole()

            compareValues(Customer::kind, Customer::numEndDevices, Customer::specialNeed)
            compareIdReferenceCollections(Customer::agreements)
        }

    private fun compareCustomerAgreement(source: CustomerAgreement, target: CustomerAgreement): ObjectDifference<CustomerAgreement> =
        ObjectDifference(source, target).apply {
            compareAgreement()

            compareIdReferences(CustomerAgreement::customer)
            compareIdReferenceCollections(CustomerAgreement::pricingStructures)
        }

    private fun comparePricingStructure(source: PricingStructure, target: PricingStructure): ObjectDifference<PricingStructure> =
        ObjectDifference(source, target).apply {
            compareDocument()

            compareIdReferenceCollections(PricingStructure::tariffs)
        }

    private fun compareTariff(source: Tariff, target: Tariff): ObjectDifference<Tariff> =
        ObjectDifference(source, target).apply { compareDocument() }

}
