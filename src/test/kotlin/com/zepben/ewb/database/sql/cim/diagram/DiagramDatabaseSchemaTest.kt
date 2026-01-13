/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.diagram

import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.cim.iec61970.base.diagramlayout.Diagram
import com.zepben.ewb.cim.iec61970.base.diagramlayout.DiagramObject
import com.zepben.ewb.database.sql.cim.CimDatabaseSchemaTest
import com.zepben.ewb.services.common.Resolvers
import com.zepben.ewb.services.common.testdata.SchemaServices
import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.ewb.services.diagram.DiagramService
import com.zepben.ewb.services.diagram.DiagramServiceComparator
import com.zepben.ewb.services.diagram.testdata.fillFields
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.sql.Connection
import java.sql.DriverManager

class DiagramDatabaseSchemaTest : CimDatabaseSchemaTest<DiagramService, DiagramDatabaseWriter, DiagramDatabaseReader, DiagramServiceComparator>() {

    override fun createService(): DiagramService = DiagramService()

    override fun createWriter(filename: String): DiagramDatabaseWriter =
        DiagramDatabaseWriter(filename)

    override fun createReader(connection: Connection, databaseDescription: String): DiagramDatabaseReader =
        DiagramDatabaseReader(connection, databaseDescription)

    override fun createComparator(): DiagramServiceComparator = DiagramServiceComparator()

    override fun createIdentifiedObject(): IdentifiedObject = Diagram(generateId())

    @Test
    @Disabled
    fun readRealFile() {
        systemErr.unmute()

        // Put the name of the database you want to read in src/test/resources/test-diagram-database.txt
        val databaseFile = Files.readString(Path.of("src", "test", "resources", "test-diagram-database.txt")).trim().trim('"')

        assertThat("database must exist", Files.exists(Paths.get(databaseFile)))

        val diagramService = DiagramService()

        DriverManager.getConnection("jdbc:sqlite:$databaseFile").use { connection ->
            assertThat("Database should have read", DiagramDatabaseReader(connection, databaseFile).read(diagramService))
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
        // ################################
        // # IEC61970 Base Diagram Layout #
        // ################################

        validateSchema(SchemaServices.diagramServicesOf(::Diagram, Diagram::fillFields))
        validateSchema(SchemaServices.diagramServicesOf(::DiagramObject, DiagramObject::fillFields))
    }

    @Test
    internal fun `test Name and NameType schema`() {
        validateSchema(SchemaServices.createNameTestService<DiagramService, Diagram>())
    }

    @Test
    internal fun `post process fails with unresolved references`() {
        validateUnresolvedFailure("DiagramObject do1", "Diagram diagram1") {
            resolveOrDeferReference(Resolvers.diagram(DiagramObject("do1")), "diagram1")
        }
    }

}
