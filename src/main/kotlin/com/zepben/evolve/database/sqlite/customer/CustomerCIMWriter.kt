/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.customer

import com.zepben.evolve.cim.iec61968.common.Agreement
import com.zepben.evolve.cim.iec61968.customers.Customer
import com.zepben.evolve.cim.iec61968.customers.CustomerAgreement
import com.zepben.evolve.cim.iec61968.customers.PricingStructure
import com.zepben.evolve.cim.iec61968.customers.Tariff
import com.zepben.evolve.database.sqlite.common.BaseCIMWriter
import com.zepben.evolve.database.sqlite.common.DatabaseTables
import com.zepben.evolve.database.sqlite.extensions.setNullableInt
import com.zepben.evolve.database.sqlite.extensions.setNullableString
import com.zepben.evolve.database.sqlite.tables.associations.TableCustomerAgreementsPricingStructures
import com.zepben.evolve.database.sqlite.tables.associations.TablePricingStructuresTariffs
import com.zepben.evolve.database.sqlite.tables.iec61968.common.TableAgreements
import com.zepben.evolve.database.sqlite.tables.iec61968.customers.TableCustomerAgreements
import com.zepben.evolve.database.sqlite.tables.iec61968.customers.TableCustomers
import com.zepben.evolve.database.sqlite.tables.iec61968.customers.TablePricingStructures
import com.zepben.evolve.database.sqlite.tables.iec61968.customers.TableTariffs
import java.sql.PreparedStatement


@Suppress("SameParameterValue")
class CustomerCIMWriter(databaseTables: DatabaseTables) : BaseCIMWriter(databaseTables) {

    /************ IEC61968 COMMON ************/
    private fun saveAgreement(table: TableAgreements, inset: PreparedStatement, agreement: Agreement, description: String): Boolean {
        return saveDocument(table, inset, agreement, description)
    }

    /************ IEC61968 CUSTOMERS ************/
    fun save(customer: Customer): Boolean {
        val table = databaseTables.getTable<TableCustomers>()
        val insert = databaseTables.getInsert<TableCustomers>()

        insert.setNullableString(table.KIND.queryIndex, customer.kind.name)
        insert.setNullableInt(table.NUM_END_DEVICES.queryIndex, customer.numEndDevices)

        return saveOrganisationRole(table, insert, customer, "customer")
    }

    fun save(customerAgreement: CustomerAgreement): Boolean {
        val table = databaseTables.getTable<TableCustomerAgreements>()
        val insert = databaseTables.getInsert<TableCustomerAgreements>()

        var status = true
        customerAgreement.pricingStructures.forEach { status = status and saveAssociation(customerAgreement, it) }

        insert.setNullableString(table.CUSTOMER_MRID.queryIndex, customerAgreement.customer?.mRID)

        return status and saveAgreement(table, insert, customerAgreement, "customer agreement")
    }

    fun save(pricingStructure: PricingStructure): Boolean {
        val table = databaseTables.getTable<TablePricingStructures>()
        val insert = databaseTables.getInsert<TablePricingStructures>()

        var status = true
        pricingStructure.tariffs.forEach { status = status and saveAssociation(pricingStructure, it) }

        return status and saveDocument(table, insert, pricingStructure, "pricing structure")
    }

    fun save(tariff: Tariff): Boolean {
        val table = databaseTables.getTable<TableTariffs>()
        val insert = databaseTables.getInsert<TableTariffs>()

        return saveDocument(table, insert, tariff, "tariff")
    }

    /************ ASSOCIATIONS ************/
    private fun saveAssociation(customerAgreement: CustomerAgreement, pricingStructure: PricingStructure): Boolean {
        val table = databaseTables.getTable<TableCustomerAgreementsPricingStructures>()
        val insert = databaseTables.getInsert<TableCustomerAgreementsPricingStructures>()

        insert.setNullableString(table.CUSTOMER_AGREEMENT_MRID.queryIndex, customerAgreement.mRID)
        insert.setNullableString(table.PRICING_STRUCTURE_MRID.queryIndex, pricingStructure.mRID)

        return tryExecuteSingleUpdate(
            insert,
            "${customerAgreement.mRID}-to-${pricingStructure.mRID}",
            "customer agreement to pricing structure association"
        )
    }

    private fun saveAssociation(pricingStructure: PricingStructure, tariff: Tariff): Boolean {
        val table = databaseTables.getTable<TablePricingStructuresTariffs>()
        val insert = databaseTables.getInsert<TablePricingStructuresTariffs>()

        insert.setNullableString(table.PRICING_STRUCTURE_MRID.queryIndex, pricingStructure.mRID)
        insert.setNullableString(table.TARIFF_MRID.queryIndex, tariff.mRID)

        return tryExecuteSingleUpdate(
            insert,
            "${pricingStructure.mRID}-to-${tariff.mRID}",
            "pricing structure to tariff association"
        )
    }
}
