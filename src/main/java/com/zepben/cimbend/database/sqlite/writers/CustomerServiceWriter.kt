/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.writers

import com.zepben.cimbend.cim.iec61968.common.Organisation
import com.zepben.cimbend.cim.iec61968.customers.Customer
import com.zepben.cimbend.cim.iec61968.customers.CustomerAgreement
import com.zepben.cimbend.cim.iec61968.customers.PricingStructure
import com.zepben.cimbend.cim.iec61968.customers.Tariff
import com.zepben.cimbend.customer.CustomerService

class CustomerServiceWriter(hasCommon: (String) -> Boolean, addCommon: (String) -> Boolean) : BaseServiceWriter<CustomerService, CustomerCIMWriter>(hasCommon, addCommon) {

    override fun save(service: CustomerService, writer: CustomerCIMWriter): Boolean {
        var status = true

        service.sequenceOf<Organisation>().forEach { status = status and trySaveCommon(writer::save, it) }
        service.sequenceOf<Customer>().forEach { status = status and validateSave(it, writer::save, "customer") }
        service.sequenceOf<CustomerAgreement>().forEach { status = status and validateSave(it, writer::save, "customer agreement") }
        service.sequenceOf<PricingStructure>().forEach { status = status and validateSave(it, writer::save, "pricing structure") }
        service.sequenceOf<Tariff>().forEach { status = status and validateSave(it, writer::save, "tariff") }

        return status
    }

}
