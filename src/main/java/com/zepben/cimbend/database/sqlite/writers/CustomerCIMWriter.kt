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

import com.zepben.cimbend.cim.iec61968.common.Agreement
import com.zepben.cimbend.cim.iec61968.customers.Customer
import com.zepben.cimbend.cim.iec61968.customers.CustomerAgreement
import com.zepben.cimbend.cim.iec61968.customers.PricingStructure
import com.zepben.cimbend.cim.iec61968.customers.Tariff
import com.zepben.cimbend.database.sqlite.DatabaseTables
import com.zepben.cimbend.database.sqlite.extensions.setNullableString
import com.zepben.cimbend.database.sqlite.tables.associations.TableCustomerAgreementsPricingStructures
import com.zepben.cimbend.database.sqlite.tables.associations.TablePricingStructuresTariffs
import com.zepben.cimbend.database.sqlite.tables.iec61968.common.TableAgreements
import com.zepben.cimbend.database.sqlite.tables.iec61968.customers.TableCustomerAgreements
import com.zepben.cimbend.database.sqlite.tables.iec61968.customers.TableCustomers
import com.zepben.cimbend.database.sqlite.tables.iec61968.customers.TablePricingStructures
import com.zepben.cimbend.database.sqlite.tables.iec61968.customers.TableTariffs
import java.sql.PreparedStatement


@Suppress("SameParameterValue")
class CustomerCIMWriter(databaseTables: DatabaseTables) : BaseCIMWriter(databaseTables) {

    /************ IEC61968 COMMON ************/
    private fun saveAgreement(table: TableAgreements, inset: PreparedStatement, agreement: Agreement, description: String): Boolean {
        return saveDocument(table, inset, agreement, description)
    }

    /************ IEC61968 CUSTOMERS ************/
    fun save(customer: Customer): Boolean {
        val table = databaseTables.getTable(TableCustomers::class.java)
        val insert = databaseTables.getInsert(TableCustomers::class.java)

        insert.setNullableString(table.KIND.queryIndex(), customer.kind.name)
        insert.setInt(table.NUM_END_DEVICES.queryIndex(), customer.numEndDevices)

        return saveOrganisationRole(table, insert, customer, "customer")
    }

    fun save(customerAgreement: CustomerAgreement): Boolean {
        val table = databaseTables.getTable(TableCustomerAgreements::class.java)
        val insert = databaseTables.getInsert(TableCustomerAgreements::class.java)

        var status = true
        customerAgreement.pricingStructures.forEach { status = status and saveAssociation(customerAgreement, it) }

        insert.setNullableString(table.CUSTOMER_MRID.queryIndex(), customerAgreement.customer?.mRID)

        return status and saveAgreement(table, insert, customerAgreement, "customer agreement")
    }

    fun save(pricingStructure: PricingStructure): Boolean {
        val table = databaseTables.getTable(TablePricingStructures::class.java)
        val insert = databaseTables.getInsert(TablePricingStructures::class.java)

        var status = true
        pricingStructure.tariffs.forEach { status = status and saveAssociation(pricingStructure, it) }

        return status and saveDocument(table, insert, pricingStructure, "pricing structure")
    }

    fun save(tariff: Tariff): Boolean {
        val table = databaseTables.getTable(TableTariffs::class.java)
        val insert = databaseTables.getInsert(TableTariffs::class.java)

        return saveDocument(table, insert, tariff, "tariff")
    }

    /************ ASSOCIATIONS ************/
    private fun saveAssociation(customerAgreement: CustomerAgreement, pricingStructure: PricingStructure): Boolean {
        val table = databaseTables.getTable(TableCustomerAgreementsPricingStructures::class.java)
        val insert = databaseTables.getInsert(TableCustomerAgreementsPricingStructures::class.java)

        insert.setNullableString(table.CUSTOMER_AGREEMENT_MRID.queryIndex(), customerAgreement.mRID)
        insert.setNullableString(table.PRICING_STRUCTURE_MRID.queryIndex(), pricingStructure.mRID)

        return tryExecuteSingleUpdate(
            insert,
            "${customerAgreement.mRID}-to-${pricingStructure.mRID}",
            "Failed to save customer agreement to pricing structure association."
        )
    }

    private fun saveAssociation(pricingStructure: PricingStructure, tariff: Tariff): Boolean {
        val table = databaseTables.getTable(TablePricingStructuresTariffs::class.java)
        val insert = databaseTables.getInsert(TablePricingStructuresTariffs::class.java)

        insert.setNullableString(table.PRICING_STRUCTURE_MRID.queryIndex(), pricingStructure.mRID)
        insert.setNullableString(table.TARIFF_MRID.queryIndex(), tariff.mRID)

        return tryExecuteSingleUpdate(
            insert,
            "${pricingStructure.mRID}-to-${tariff.mRID}",
            "Failed to save pricing structure to tariff association."
        )
    }
}
