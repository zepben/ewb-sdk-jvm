/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.customer

import com.zepben.ewb.database.sqlite.cim.BaseServiceReader
import com.zepben.ewb.database.sqlite.cim.tables.associations.TableCustomerAgreementsPricingStructures
import com.zepben.ewb.database.sqlite.cim.tables.associations.TablePricingStructuresTariffs
import com.zepben.ewb.database.sqlite.cim.tables.iec61968.common.TableOrganisations
import com.zepben.ewb.database.sqlite.cim.tables.iec61968.customers.TableCustomerAgreements
import com.zepben.ewb.database.sqlite.cim.tables.iec61968.customers.TableCustomers
import com.zepben.ewb.database.sqlite.cim.tables.iec61968.customers.TablePricingStructures
import com.zepben.ewb.database.sqlite.cim.tables.iec61968.customers.TableTariffs
import com.zepben.ewb.services.customer.CustomerService
import java.sql.Connection

/**
 * A class for reading a [CustomerService] from the database.
 *
 * @param databaseTables The tables available in the database.
 * @param connection A connection to the database.
 */
internal class CustomerServiceReader(
    databaseTables: CustomerDatabaseTables,
    connection: Connection,
    override val reader: CustomerCimReader = CustomerCimReader()
) : BaseServiceReader<CustomerService>(databaseTables, connection, reader) {

    override fun readService(service: CustomerService): Boolean =
        readEach<TableOrganisations>(service, reader::read) and
            readEach<TableCustomers>(service, reader::read) and
            readEach<TableCustomerAgreements>(service, reader::read) and
            readEach<TablePricingStructures>(service, reader::read) and
            readEach<TableTariffs>(service, reader::read) and
            readEach<TableCustomerAgreementsPricingStructures>(service, reader::read) and
            readEach<TablePricingStructuresTariffs>(service, reader::read)

}
