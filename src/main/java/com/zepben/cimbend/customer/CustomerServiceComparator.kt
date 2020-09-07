/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.customer

import com.zepben.cimbend.cim.iec61968.common.Agreement
import com.zepben.cimbend.cim.iec61968.customers.Customer
import com.zepben.cimbend.cim.iec61968.customers.CustomerAgreement
import com.zepben.cimbend.cim.iec61968.customers.PricingStructure
import com.zepben.cimbend.cim.iec61968.customers.Tariff
import com.zepben.cimbend.common.BaseServiceComparator
import com.zepben.cimbend.common.ObjectDifference

//
// NOTE: Unused functions have been suppressed for this class as they are access by reflection rather than directly. This
//       means they are always flagged as unused. By suppressing the warning it also means you might not be testing every
//       function, so make sure you check the code coverage
//
@Suppress("unused")
class CustomerServiceComparator : BaseServiceComparator() {

    private fun ObjectDifference<out Agreement>.compareAgreement(): ObjectDifference<out Agreement> =
        apply { compareDocument() }

    private fun compareCustomer(source: Customer, target: Customer): ObjectDifference<Customer> =
        ObjectDifference(source, target).apply {
            compareOrganisationRole()

            compareValues(Customer::kind, Customer::numEndDevices)
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
