/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.cim.customer

import com.zepben.evolve.cim.iec61968.common.Organisation
import com.zepben.evolve.cim.iec61968.customers.Customer
import com.zepben.evolve.cim.iec61968.customers.CustomerAgreement
import com.zepben.evolve.cim.iec61968.customers.PricingStructure
import com.zepben.evolve.cim.iec61968.customers.Tariff
import com.zepben.evolve.database.sqlite.cim.BaseServiceWriter
import com.zepben.evolve.services.customer.CustomerService

/**
 * A class for writing a [CustomerService] into the database.
 *
 * @param databaseTables The [CustomerDatabaseTables] to add to the database.
 */
internal class CustomerServiceWriter(
    databaseTables: CustomerDatabaseTables,
    override val writer: CustomerCimWriter = CustomerCimWriter(databaseTables)
) : BaseServiceWriter<CustomerService>(writer) {

    override fun CustomerService.writeService(): Boolean =
        writeEach<Organisation>(writer::write) and
            writeEach<Customer>(writer::write) and
            writeEach<CustomerAgreement>(writer::write) and
            writeEach<PricingStructure>(writer::write) and
            writeEach<Tariff>(writer::write)

}
