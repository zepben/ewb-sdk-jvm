/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.customer

import com.zepben.evolve.cim.iec61968.common.Organisation
import com.zepben.evolve.cim.iec61968.customers.Customer
import com.zepben.evolve.cim.iec61968.customers.CustomerAgreement
import com.zepben.evolve.cim.iec61968.customers.PricingStructure
import com.zepben.evolve.cim.iec61968.customers.Tariff
import com.zepben.evolve.database.sqlite.common.BaseServiceWriter
import com.zepben.evolve.services.customer.CustomerService

class CustomerServiceWriter(
    service: CustomerService,
    writer: CustomerCIMWriter,
    hasCommon: (String) -> Boolean,
    addCommon: (String) -> Boolean
) : BaseServiceWriter<CustomerService, CustomerCIMWriter>(service, writer, hasCommon, addCommon) {
    override fun doSave(): Boolean {
        var status = true

        status = status and saveEach<Organisation> { trySaveCommon(writer::save, it) }
        status = status and saveEach<Customer>(writer::save)
        status = status and saveEach<CustomerAgreement>(writer::save)
        status = status and saveEach<PricingStructure>(writer::save)
        status = status and saveEach<Tariff>(writer::save)

        return status
    }

}
