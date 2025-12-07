/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.customer

import com.zepben.ewb.database.sql.cim.CimDatabaseSchemaTest
import com.zepben.ewb.cim.iec61968.common.Organisation
import com.zepben.ewb.cim.iec61968.customers.Customer
import com.zepben.ewb.cim.iec61968.customers.CustomerAgreement
import com.zepben.ewb.cim.iec61968.customers.PricingStructure
import com.zepben.ewb.cim.iec61968.customers.Tariff
import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.database.sql.cim.CimDatabaseSchemaTest
import com.zepben.ewb.services.common.Resolvers
import com.zepben.ewb.services.common.extensions.typeNameAndMRID
import com.zepben.ewb.services.common.testdata.SchemaServices
import com.zepben.ewb.services.common.testdata.fillFieldsCommon
import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.ewb.services.customer.CustomerService
import com.zepben.ewb.services.customer.CustomerServiceComparator
import com.zepben.ewb.services.customer.testdata.fillFields
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.sql.Connection
import java.sql.DriverManager

class CustomerDatabaseSchemaTest : CimDatabaseSchemaTest<
    CustomerService,
    CustomerDatabaseWriter,
    CustomerDatabaseReader,
    CustomerServiceComparator,
    IdentifiedObject
>(
    describeObject = IdentifiedObject::typeNameAndMRID,
    addToService = BaseService::tryAdd
) {

    override fun createService(): CustomerService = CustomerService()

    override fun createWriter(filename: String): CustomerDatabaseWriter =
        CustomerDatabaseWriter(filename)

    override fun createReader(connection: Connection, databaseDescription: String): CustomerDatabaseReader =
        CustomerDatabaseReader(connection, databaseDescription)

    override fun createComparator(): CustomerServiceComparator = CustomerServiceComparator()

    override fun createIdentifiedObject(): IdentifiedObject = Customer(generateId())

    @Test
    @Disabled
    fun readRealFile() {
        systemErr.unmute()

        // Put the name of the database you want to read in src/test/resources/test-customer-database.txt
        val databaseFile = Files.readString(Path.of("src", "test", "resources", "test-customer-database.txt")).trim().trim('"')

        assertThat("database must exist", Files.exists(Paths.get(databaseFile)))

        val customerService = CustomerService()

        DriverManager.getConnection("jdbc:sqlite:$databaseFile").use { connection ->
            assertThat("Database should have read", CustomerDatabaseReader(connection, databaseFile).read(customerService))
        }

        logger.info("Sleeping...")
        try {
            Thread.sleep(Long.MAX_VALUE)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    @Test
    internal fun `test schema for each supported type`() {
        // ###################
        // # IEC61968 Common #
        // ###################

        validateSchema(SchemaServices.customerServicesOf(::Organisation, Organisation::fillFieldsCommon))

        // ######################
        // # IEC61968 Customers #
        // ######################

        validateSchema(SchemaServices.customerServicesOf(::Customer, Customer::fillFields))
        validateSchema(SchemaServices.customerServicesOf(::CustomerAgreement, CustomerAgreement::fillFields))
        validateSchema(SchemaServices.customerServicesOf(::PricingStructure, PricingStructure::fillFields))
        validateSchema(SchemaServices.customerServicesOf(::Tariff, Tariff::fillFields))
    }

    @Test
    internal fun `test Name and NameType schema`() {
        validateSchema(SchemaServices.createNameTestService<CustomerService, Customer>())
    }

    @Test
    internal fun `post process fails with unresolved references`() {
        validateUnresolvedFailure("PricingStructure ps1", "Tariff tar1") {
            resolveOrDeferReference(Resolvers.tariffs(PricingStructure("ps1")), "tar1")
        }
    }

}
