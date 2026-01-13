/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.customer

import com.zepben.ewb.database.sql.cim.CimDatabaseTables
import com.zepben.ewb.database.sql.cim.tables.associations.TableCustomerAgreementsPricingStructures
import com.zepben.ewb.database.sql.cim.tables.associations.TablePricingStructuresTariffs
import com.zepben.ewb.database.sql.cim.tables.iec61968.common.TableOrganisations
import com.zepben.ewb.database.sql.cim.tables.iec61968.customers.TableCustomerAgreements
import com.zepben.ewb.database.sql.cim.tables.iec61968.customers.TableCustomers
import com.zepben.ewb.database.sql.cim.tables.iec61968.customers.TablePricingStructures
import com.zepben.ewb.database.sql.cim.tables.iec61968.customers.TableTariffs
import com.zepben.ewb.database.sql.common.tables.SqlTable
import com.zepben.ewb.database.sql.generators.SqlGenerator
import com.zepben.ewb.database.sql.generators.SqliteGenerator

/**
 * The collection of tables for our customer databases.
 */
class CustomerDatabaseTables(
    override val sqlGenerator: SqlGenerator = SqliteGenerator
) : CimDatabaseTables() {

    override val includedTables: Sequence<SqlTable> =
        super.includedTables + sequenceOf(
            TableCustomerAgreements(),
            TableCustomerAgreementsPricingStructures(),
            TableCustomers(),
            TableOrganisations(),
            TablePricingStructures(),
            TablePricingStructuresTariffs(),
            TableTariffs(),
        )

}
