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

import com.zepben.cimbend.cim.iec61968.common.Organisation
import com.zepben.cimbend.cim.iec61968.customers.Customer
import com.zepben.cimbend.cim.iec61968.customers.CustomerAgreement
import com.zepben.cimbend.cim.iec61968.customers.PricingStructure
import com.zepben.cimbend.cim.iec61968.customers.Tariff
import com.zepben.test.util.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class CustomerServiceTest {

    @JvmField
    @RegisterExtension
    var systemOut: SystemLogExtension = SystemLogExtension.SYSTEM_OUT.captureLog().muteOnSuccess()

    private val service = CustomerService()

    @Test
    internal fun supportsCustomer() {
        val customer = Customer()
        assertThat(service.add(customer), equalTo(true))
        assertThat(service.remove(customer), equalTo(true))
    }

    @Test
    internal fun supportsCustomerAgreement() {
        val customerAgreement = CustomerAgreement()
        assertThat(service.add(customerAgreement), equalTo(true))
        assertThat(service.remove(customerAgreement), equalTo(true))
    }

    @Test
    internal fun supportsOrganisation() {
        val organisation = Organisation()
        assertThat(service.add(organisation), equalTo(true))
        assertThat(service.remove(organisation), equalTo(true))
    }

    @Test
    internal fun supportsPricingStructure() {
        val pricingStructure = PricingStructure()
        assertThat(service.add(pricingStructure), equalTo(true))
        assertThat(service.remove(pricingStructure), equalTo(true))
    }

    @Test
    internal fun supportsTariff() {
        val tariff = Tariff()
        assertThat(service.add(tariff), equalTo(true))
        assertThat(service.remove(tariff), equalTo(true))
    }
}
