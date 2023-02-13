/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.customer

import com.zepben.evolve.database.sqlite.common.BaseServiceReader
import com.zepben.evolve.database.sqlite.common.DatabaseTables
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
    databaseTables: DatabaseTables,
    reader: CustomerCIMReader,
    getStatement: () -> Statement,
) : BaseServiceReader<CustomerCIMReader>(databaseTables, getStatement, reader) {

    override fun load(): Boolean {
        var status = loadNameTypes(reader)

        status = status and loadEach("organisations", TableOrganisations(), reader::load)
        status = status and loadEach("customers", TableCustomers(), reader::load)
        status = status and loadEach("customer agreements", TableCustomerAgreements(), reader::load)
        status = status and loadEach("pricing structures", TablePricingStructures(), reader::load)
        status = status and loadEach("tariffs", TableTariffs(), reader::load)
        status = status and loadEach("customer agreement to pricing structure associations", TableCustomerAgreementsPricingStructures(), reader::load)
        status = status and loadEach("pricing structure to tariff associations", TablePricingStructuresTariffs(), reader::load)

        status = status and loadNames(reader)

        return status
    }

}
