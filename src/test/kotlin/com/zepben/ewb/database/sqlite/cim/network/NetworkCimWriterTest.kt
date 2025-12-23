/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.network

import com.google.common.reflect.ClassPath
import com.zepben.ewb.cim.extensions.iec61970.base.core.HvCustomer
import com.zepben.ewb.cim.extensions.iec61970.base.core.Site
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvFeeder
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvSubstation
import com.zepben.ewb.cim.iec61970.base.core.EquipmentContainer
import com.zepben.ewb.cim.iec61970.base.core.Feeder
import com.zepben.ewb.cim.iec61970.base.core.Substation
import com.zepben.ewb.cim.iec61970.base.wires.Junction
import com.zepben.ewb.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.ewb.database.sqlite.cim.tables.associations.TableEquipmentEquipmentContainers
import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.testutils.junit.SystemLogExtension
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.slf4j.LoggerFactory
import java.lang.reflect.Modifier
import java.sql.Connection
import java.sql.PreparedStatement

class NetworkCimWriterTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val logger = LoggerFactory.getLogger(javaClass)

    @Test
    internal fun `only exports equipment for expected equipment containers`() {
        val table = TableEquipmentEquipmentContainers()
        // We use a relaxed mock as we are only checking a fraction of the overall functionality, with the rest being checked in other test classes.
        val insert = createRelaxedPreparedStatement()
        val databaseTables = NetworkDatabaseTables().apply {
            val connection = mockk<Connection> {
                // We are not interested in validating any of the other interactions, so just use relaxed mocks by default.
                every { prepareStatement(any()) } returns createRelaxedPreparedStatement()

                // These are the interactions we are interested in:
                every { prepareStatement(table.preparedInsertSql) } answers { insert }
            }

            prepareInsertStatements(connection)
        }

        val writer = NetworkCimWriter(databaseTables)

        val allEquipmentContainerClasses = ClassPath.from(ClassLoader.getSystemClassLoader())
            .getTopLevelClassesRecursive("com.zepben.ewb.cim")
            .asSequence()
            .map { it.load() }
            .filter { !Modifier.isAbstract(it.modifiers) }
            .filter { EquipmentContainer::class.java.isAssignableFrom(it) }
            .toSet()

        val shouldExport = listOf(Site(generateId()), Substation(generateId()), Circuit(generateId()), HvCustomer(generateId()), LvSubstation(generateId()))
        val shouldIgnore = listOf(Feeder(generateId()), LvFeeder(generateId()))

        assertThat(
            "Should be checking all EquipmentContainer subclasses",
            (shouldExport + shouldIgnore).map { it::class.java }.toSet(),
            equalTo(allEquipmentContainerClasses)
        )

        val junction = Junction(generateId()).apply {
            shouldExport.forEach { addContainer(it) }
            shouldIgnore.forEach { addContainer(it) }
        }

        writer.write(junction)

        shouldExport.forEach {
            logger.info("Check collection entry for ${it::class.simpleName} was exported...")
            verify(exactly = 1) { insert.setString(table.EQUIPMENT_CONTAINER_MRID.queryIndex, it.mRID) }
        }
        shouldIgnore.forEach {
            logger.info("Check collection entry for ${it::class.simpleName} was ignored...")
            verify(exactly = 0) { insert.setString(table.EQUIPMENT_CONTAINER_MRID.queryIndex, it.mRID) }
        }
    }

    private fun createRelaxedPreparedStatement(): PreparedStatement = mockk(relaxed = true) { every { executeUpdate() } returns 1 }

}
