/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.customer

import com.zepben.ewb.cim.iec61968.common.Organisation
import com.zepben.ewb.cim.iec61968.customers.Customer
import com.zepben.ewb.cim.iec61968.customers.CustomerAgreement
import com.zepben.ewb.cim.iec61968.customers.PricingStructure
import com.zepben.ewb.cim.iec61968.customers.Tariff
import com.zepben.ewb.services.common.BaseService
import com.zepben.ewb.services.common.meta.MetadataCollection

/**
 * Maintains an in-memory model of customers and their organisations.
 */
class CustomerService(metadata: MetadataCollection = MetadataCollection()) : BaseService("customer", metadata) {

    // ###################
    // # IEC61968 Common #
    // ###################

    fun add(organisation: Organisation): Boolean = super.add(organisation)
    fun remove(organisation: Organisation): Boolean = super.remove(organisation)

    // ######################
    // # IEC61968 Customers #
    // ######################

    fun add(customer: Customer): Boolean = super.add(customer)
    fun remove(customer: Customer): Boolean = super.remove(customer)

    fun add(customerAgreement: CustomerAgreement): Boolean = super.add(customerAgreement)
    fun remove(customerAgreement: CustomerAgreement): Boolean = super.remove(customerAgreement)

    fun add(pricingStructure: PricingStructure): Boolean = super.add(pricingStructure)
    fun remove(pricingStructure: PricingStructure): Boolean = super.remove(pricingStructure)

    fun add(tariff: Tariff): Boolean = super.add(tariff)
    fun remove(tariff: Tariff): Boolean = super.remove(tariff)

}
