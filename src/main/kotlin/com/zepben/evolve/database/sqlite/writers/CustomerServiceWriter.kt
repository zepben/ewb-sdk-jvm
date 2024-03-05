/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.writers

import com.zepben.evolve.cim.iec61968.common.Organisation
import com.zepben.evolve.cim.iec61968.customers.Customer
import com.zepben.evolve.cim.iec61968.customers.CustomerAgreement
import com.zepben.evolve.cim.iec61968.customers.PricingStructure
import com.zepben.evolve.cim.iec61968.customers.Tariff
import com.zepben.evolve.services.customer.CustomerService

class CustomerServiceWriter(hasCommon: (String) -> Boolean, addCommon: (String) -> Boolean) :
    BaseServiceWriter<CustomerService, CustomerCIMWriter>(hasCommon, addCommon) {

    override fun save(service: CustomerService, writer: CustomerCIMWriter): Boolean {
        var status = super.save(service, writer)

        service.sequenceOf<Organisation>().forEach { status = status and trySaveCommon(writer::save, it) }
        service.sequenceOf<Customer>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<CustomerAgreement>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<PricingStructure>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<Tariff>().forEach { status = status and validateSave(it, writer::save) }

        return status
    }

}
