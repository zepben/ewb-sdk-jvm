/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.readers

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
class CustomerServiceReader(getStatement: () -> Statement) : BaseServiceReader(getStatement) {

    fun load(reader: CustomerCIMReader): Boolean {
        var status = loadNameTypes(reader)

        status = status and loadEach<TableOrganisations>("organisations", reader::load)
        status = status and loadEach<TableCustomers>("customers", reader::load)
        status = status and loadEach<TableCustomerAgreements>("customer agreements", reader::load)
        status = status and loadEach<TablePricingStructures>("pricing structures", reader::load)
        status = status and loadEach<TableTariffs>("tariffs", reader::load)
        status = status and loadEach<TableCustomerAgreementsPricingStructures>("customer agreement to pricing structure associations", reader::load)
        status = status and loadEach<TablePricingStructuresTariffs>("pricing structure to tariff associations", reader::load)

        status = status and loadNames(reader)

        return status
    }

}
