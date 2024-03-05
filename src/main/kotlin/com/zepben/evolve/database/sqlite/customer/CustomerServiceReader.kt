/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.customer

import com.zepben.evolve.database.sqlite.common.BaseServiceReader
import com.zepben.evolve.database.sqlite.tables.associations.TableCustomerAgreementsPricingStructures
import com.zepben.evolve.database.sqlite.tables.associations.TablePricingStructuresTariffs
import com.zepben.evolve.database.sqlite.tables.iec61968.common.TableOrganisations
import com.zepben.evolve.database.sqlite.tables.iec61968.customers.TableCustomerAgreements
import com.zepben.evolve.database.sqlite.tables.iec61968.customers.TableCustomers
import com.zepben.evolve.database.sqlite.tables.iec61968.customers.TablePricingStructures
import com.zepben.evolve.database.sqlite.tables.iec61968.customers.TableTariffs
import com.zepben.evolve.services.customer.CustomerService
import java.sql.Statement


/**
 * Class for reading a [CustomerService] from the database.
 *
 * @property getStatement provider of statements for the connection.
 */
class CustomerServiceReader(
    reader: CustomerCIMReader,
    databaseTables: CustomerDatabaseTables = CustomerDatabaseTables(),
    getStatement: () -> Statement,
) : BaseServiceReader<CustomerCIMReader>(databaseTables, getStatement, reader) {

    override fun doLoad(): Boolean =
        loadEach<TableOrganisations>(reader::load)
            .andLoadEach<TableCustomers>(reader::load)
            .andLoadEach<TableCustomerAgreements>(reader::load)
            .andLoadEach<TablePricingStructures>(reader::load)
            .andLoadEach<TableTariffs>(reader::load)
            .andLoadEach<TableCustomerAgreementsPricingStructures>(reader::load)
            .andLoadEach<TablePricingStructuresTariffs>(reader::load)

}
