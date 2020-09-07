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
import com.zepben.cimbend.common.BaseService

/**
 * Maintains an in-memory model of customers and their organisations.
 */
class CustomerService : BaseService("customer") {
    fun add(customer: Customer): Boolean = super.add(customer)
    fun remove(customer: Customer): Boolean = super.remove(customer)

    fun add(customerAgreement: CustomerAgreement): Boolean = super.add(customerAgreement)
    fun remove(customerAgreement: CustomerAgreement): Boolean = super.remove(customerAgreement)

    fun add(organisation: Organisation): Boolean = super.add(organisation)
    fun remove(organisation: Organisation): Boolean = super.remove(organisation)

    fun add(pricingStructure: PricingStructure): Boolean = super.add(pricingStructure)
    fun remove(pricingStructure: PricingStructure): Boolean = super.remove(pricingStructure)

    fun add(tariff: Tariff): Boolean = super.add(tariff)
    fun remove(tariff: Tariff): Boolean = super.remove(tariff)
}
