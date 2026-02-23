/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.variant

import com.zepben.ewb.database.sql.cim.CimDatabaseSchemaTest
import com.zepben.ewb.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProject
import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.AnnotatedProjectDependency
import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProjectStage
import com.zepben.ewb.database.sql.cim.variant.tables.tableVariantsVersion
import com.zepben.ewb.database.sql.initialisers.NoOpDatabaseInitialiser
import com.zepben.ewb.services.common.BaseService
import com.zepben.ewb.services.common.testdata.SchemaServices
import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.ewb.services.variant.VariantService
import com.zepben.ewb.services.variant.VariantServiceComparator
import com.zepben.ewb.services.variant.testdata.fillFields
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import java.sql.Connection
import java.sql.DriverManager
import kotlin.use

private fun getConnection() = DriverManager.getConnection("jdbc:h2:mem:metrics;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH")

class VariantDatabaseSchemaTest : CimDatabaseSchemaTest<
    VariantService,
    VariantDatabaseWriter,
    VariantDatabaseReader,
    VariantServiceComparator,
    IdentifiedObject
>(
    tableVariantsVersion,
    databaseInitialiser = NoOpDatabaseInitialiser(::getConnection),
    describeObject = IdentifiedObject::typeNameAndMRID,
    addToService = BaseService::tryAdd
) {

    override fun createService(): VariantService = VariantService()

    override fun createWriter(filename: String): VariantDatabaseWriter =
        VariantDatabaseWriter(::getConnection)

    override fun createReader(connection: Connection, databaseDescription: String): VariantDatabaseReader =
        VariantDatabaseReader(connection, databaseDescription)

    override fun createComparator(): VariantServiceComparator = VariantServiceComparator()

    override fun createIdentifiedObject(): IdentifiedObject = NetworkModelProject(generateId())

    // NOTE: this pattern must be used instead of `getConnection.use` to ensure the same in-memory DB is used for each of these tests.
    private val connection = getConnection()

    @BeforeEach
    // NOTE: this pattern must be used instead of `getConnection.use` to ensure the same in-memory DB is used for each of these tests.
    internal fun createSchema() {
        // The VariantDatabaseWriter assumes that the schema has been created already, so we create it here
        connection.createStatement().use { statement ->
            val tables = VariantDatabaseTables()
            tables.forEachTable {
                statement.executeUpdate(tables.sqlGenerator.createTableSql(it))
            }

            // Add the version number to the database.
            connection.prepareStatement(tableVariantsVersion.preparedInsertSql).use { insert ->
                insert.setInt(tableVariantsVersion.VERSION.queryIndex, tableVariantsVersion.supportedVersion)
                insert.executeUpdate()
            }
        }
    }


    @AfterEach
    // NOTE: this pattern must be used instead of `getConnection.use` to ensure the same in-memory DB is used for each of these tests.
    internal fun closeConnection() = connection.close()

    internal fun `test schema for each supported type`() {
        validateSchema(SchemaServices.variantServicesOf(::AnnotatedProjectDependency, AnnotatedProjectDependency::fillFields))
        validateSchema(SchemaServices.variantServicesOf(::NetworkModelProject, NetworkModelProject::fillFields))
        validateSchema(SchemaServices.variantServicesOf(::NetworkModelProjectStage, NetworkModelProjectStage::fillFields))
    }
}
