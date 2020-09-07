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
