/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.cim.customer

import com.zepben.evolve.cim.iec61968.common.Agreement
import com.zepben.evolve.cim.iec61968.customers.Customer
import com.zepben.evolve.cim.iec61968.customers.CustomerAgreement
import com.zepben.evolve.cim.iec61968.customers.PricingStructure
import com.zepben.evolve.cim.iec61968.customers.Tariff
import com.zepben.evolve.database.sqlite.cim.CimWriter
import com.zepben.evolve.database.sqlite.cim.tables.associations.TableCustomerAgreementsPricingStructures
import com.zepben.evolve.database.sqlite.cim.tables.associations.TablePricingStructuresTariffs
import com.zepben.evolve.database.sqlite.cim.tables.iec61968.common.TableAgreements
import com.zepben.evolve.database.sqlite.cim.tables.iec61968.customers.TableCustomerAgreements
import com.zepben.evolve.database.sqlite.cim.tables.iec61968.customers.TableCustomers
import com.zepben.evolve.database.sqlite.cim.tables.iec61968.customers.TablePricingStructures
import com.zepben.evolve.database.sqlite.cim.tables.iec61968.customers.TableTariffs
import com.zepben.evolve.database.sql.extensions.setNullableInt
import com.zepben.evolve.database.sql.extensions.setNullableString
import com.zepben.evolve.services.customer.CustomerService
import java.sql.PreparedStatement
import java.sql.SQLException

/**
 * A class for writing the [CustomerService] tables to the database.
 *
 * @property databaseTables The tables available in the database.
 */
internal class CustomerCimWriter(
    override val databaseTables: CustomerDatabaseTables
) : CimWriter(databaseTables) {

    // ###################
    // # IEC61968 Common #
    // ###################

    @Suppress("SameParameterValue")
    @Throws(SQLException::class)
    private fun writeAgreement(table: TableAgreements, insert: PreparedStatement, agreement: Agreement, description: String): Boolean {
        return writeDocument(table, insert, agreement, description)
    }

    // ######################
    // # IEC61968 Customers #
    // ######################

    /**
     * Write the [Customer] fields to [TableCustomers].
     *
     * @param customer The [Customer] instance to write to the database.
     *
     * @return true if the [Customer] was successfully written to the database, otherwise false.
     * @throws SQLException For any errors encountered writing to the database.
     */
    @Throws(SQLException::class)
    fun write(customer: Customer): Boolean {
        val table = databaseTables.getTable<TableCustomers>()
        val insert = databaseTables.getInsert<TableCustomers>()

        insert.setNullableString(table.KIND.queryIndex, customer.kind.name)
        insert.setNullableInt(table.NUM_END_DEVICES.queryIndex, customer.numEndDevices)
        insert.setNullableString(table.SPECIAL_NEED.queryIndex, customer.specialNeed)

        return writeOrganisationRole(table, insert, customer, "customer")
    }

    /**
     * Write the [CustomerAgreement] fields to [TableCustomerAgreements].
     *
     * @param customerAgreement The [CustomerAgreement] instance to write to the database.
     *
     * @return true if the [CustomerAgreement] was successfully written to the database, otherwise false.
     * @throws SQLException For any errors encountered writing to the database.
     */
    @Throws(SQLException::class)
    fun write(customerAgreement: CustomerAgreement): Boolean {
        val table = databaseTables.getTable<TableCustomerAgreements>()
        val insert = databaseTables.getInsert<TableCustomerAgreements>()

        var status = true
        customerAgreement.pricingStructures.forEach { status = status and writeAssociation(customerAgreement, it) }

        insert.setNullableString(table.CUSTOMER_MRID.queryIndex, customerAgreement.customer?.mRID)

        return status and writeAgreement(table, insert, customerAgreement, "customer agreement")
    }

    /**
     * Write the [PricingStructure] fields to [TablePricingStructures].
     *
     * @param pricingStructure The [PricingStructure] instance to write to the database.
     *
     * @return true if the [PricingStructure] was successfully written to the database, otherwise false.
     * @throws SQLException For any errors encountered writing to the database.
     */
    @Throws(SQLException::class)
    fun write(pricingStructure: PricingStructure): Boolean {
        val table = databaseTables.getTable<TablePricingStructures>()
        val insert = databaseTables.getInsert<TablePricingStructures>()

        var status = true
        pricingStructure.tariffs.forEach { status = status and writeAssociation(pricingStructure, it) }

        return status and writeDocument(table, insert, pricingStructure, "pricing structure")
    }

    /**
     * Write the [Tariff] fields to [TableTariffs].
     *
     * @param tariff The [Tariff] instance to write to the database.
     *
     * @return true if the [Tariff] was successfully written to the database, otherwise false.
     * @throws SQLException For any errors encountered writing to the database.
     */
    @Throws(SQLException::class)
    fun write(tariff: Tariff): Boolean {
        val table = databaseTables.getTable<TableTariffs>()
        val insert = databaseTables.getInsert<TableTariffs>()

        return writeDocument(table, insert, tariff, "tariff")
    }

    // ################
    // # Associations #
    // ################

    @Throws(SQLException::class)
    private fun writeAssociation(customerAgreement: CustomerAgreement, pricingStructure: PricingStructure): Boolean {
        val table = databaseTables.getTable<TableCustomerAgreementsPricingStructures>()
        val insert = databaseTables.getInsert<TableCustomerAgreementsPricingStructures>()

        insert.setNullableString(table.CUSTOMER_AGREEMENT_MRID.queryIndex, customerAgreement.mRID)
        insert.setNullableString(table.PRICING_STRUCTURE_MRID.queryIndex, pricingStructure.mRID)

        return insert.tryExecuteSingleUpdate("customer agreement to pricing structure association")
    }

    @Throws(SQLException::class)
    private fun writeAssociation(pricingStructure: PricingStructure, tariff: Tariff): Boolean {
        val table = databaseTables.getTable<TablePricingStructuresTariffs>()
        val insert = databaseTables.getInsert<TablePricingStructuresTariffs>()

        insert.setNullableString(table.PRICING_STRUCTURE_MRID.queryIndex, pricingStructure.mRID)
        insert.setNullableString(table.TARIFF_MRID.queryIndex, tariff.mRID)

        return insert.tryExecuteSingleUpdate("pricing structure to tariff association")
    }

}
