/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.upgrade

import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.meas.Analog
import com.zepben.evolve.cim.iec61970.base.wires.PowerTransformer
import com.zepben.evolve.cim.iec61970.base.wires.PowerTransformerEnd
import com.zepben.evolve.database.sqlite.DatabaseReader
import com.zepben.evolve.database.sqlite.tables.TableVersion
import com.zepben.evolve.database.sqlite.upgrade.changesets.changeSet28
import com.zepben.evolve.services.common.meta.MetadataCollection
import com.zepben.evolve.services.customer.CustomerService
import com.zepben.evolve.services.diagram.DiagramService
import com.zepben.evolve.services.network.NetworkService
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.api.io.TempDir
import org.mockito.Mockito.*
import java.io.File
import java.nio.file.Path
import java.sql.Connection
import java.sql.DriverManager.getConnection
import java.sql.Statement
import kotlin.test.fail

@Suppress("SqlResolve", "SameParameterValue")
class ChangeSetTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun `can upgrade to latest`(@TempDir tempDir: Path) {
        val dbFile = File("src/test/data/changeset20.sqlite").copyTo(Path.of(tempDir.toString(), "changeset20.sqlite").toFile()).toPath()
        val runner = UpgradeRunner()
        val cr = runner.connectAndUpgrade("jdbc:sqlite:$dbFile", dbFile)
        cr.connection.close()
    }

    @Test
    internal fun `updates sequence and end numbers`(@TempDir tempDir: Path) {
        val dbFile = File("src/test/data/changeset20.sqlite").copyTo(Path.of(tempDir.toString(), "changeset20.sqlite").toFile()).toPath()
        val runner = UpgradeRunner()
        val cr = runner.connectAndUpgrade("jdbc:sqlite:$dbFile", dbFile)

        // Ensure index was recreated, as changeset drops it to update numbers
        cr.connection.use { connection ->
            connection.createStatement().use { statement ->
                statement.executeQuery("pragma index_info('power_transformer_ends_power_transformer_mrid_end_number')").use { rs ->
                    assertThat(rs.next(), equalTo(true))
                }
            }
        }

        val reader = DatabaseReader(dbFile.toString())
        val network = NetworkService()
        reader.load(MetadataCollection(), network, DiagramService(), CustomerService())

        assertThat(network.get<Terminal>("t1")!!.sequenceNumber, equalTo(1))
        assertThat(network.get<Terminal>("t2")!!.sequenceNumber, equalTo(2))
        assertThat(network.get<Terminal>("t3")!!.sequenceNumber, equalTo(3))
        assertThat(network.get<Terminal>("t4")!!.sequenceNumber, equalTo(11))

        assertThat(network.get<PowerTransformerEnd>("e1")!!.endNumber, equalTo(1))
        assertThat(network.get<PowerTransformerEnd>("e2")!!.endNumber, equalTo(2))
        assertThat(network.get<PowerTransformerEnd>("e3")!!.endNumber, equalTo(11))
    }

    @Test
    internal fun `migrates measurement to analog`(@TempDir tempDir: Path) {
        val dbFile = File("src/test/data/changeset21.sqlite").copyTo(Path.of(tempDir.toString(), "changeset21.sqlite").toFile()).toPath()
        val runner = UpgradeRunner()
        val cr = runner.connectAndUpgrade("jdbc:sqlite:$dbFile", dbFile)
        cr.connection.close()

        val reader = DatabaseReader(dbFile.toString())
        val network = NetworkService()
        reader.load(MetadataCollection(), network, DiagramService(), CustomerService())

        val meas1 = network.get<Analog>("meas1")
        assertThat(meas1?.name, equalTo("meas1"))
        assertThat(meas1?.description, equalTo("meas1"))
        assertThat(meas1?.numDiagramObjects, equalTo(1))
        assertThat(meas1?.powerSystemResourceMRID, equalTo("psr1"))

        val meas2 = network.get<Analog>("meas2")
        assertThat(meas2?.name, equalTo("meas2"))
        assertThat(meas2?.description, equalTo("meas2"))
        assertThat(meas2?.numDiagramObjects, equalTo(2))
        assertThat(meas2?.powerSystemResourceMRID, equalTo("psr2"))

        val meas3 = network.get<Analog>("meas3")
        assertThat(meas3?.name, equalTo("meas3"))
        assertThat(meas3?.description, equalTo("meas3"))
        assertThat(meas3?.numDiagramObjects, equalTo(3))
        assertThat(meas3?.powerSystemResourceMRID, equalTo("psr3"))
    }

    @Test
    internal fun `removed null constraint from transformer_utilisation column in power_transformers table`(@TempDir tempDir: Path) {
        val dbFile = File("src/test/data/changeset24.sqlite").copyTo(Path.of(tempDir.toString(), "changeset24.sqlite").toFile()).toPath()
        val runner = UpgradeRunner()
        val cr = runner.connectAndUpgrade("jdbc:sqlite:$dbFile", dbFile)

        // Ensure transformer_utilisation column is nullable
        cr.connection.use { connection ->
            connection.createStatement().use { statement ->
                statement.executeQuery("pragma table_info('power_transformers');").use rs@{ rs ->
                    while (rs.next()) {
                        if (rs.getString("name") == "transformer_utilisation") {
                            assertThat(rs.getString("notnull"), equalTo("0"))
                            return@rs
                        }
                    }
                    fail()
                }
            }
        }
        cr.connection.close()

        val reader = DatabaseReader(dbFile.toString())
        val network = NetworkService()
        reader.load(MetadataCollection(), network, DiagramService(), CustomerService())

        val pt0 = network.get<PowerTransformer>("power_transformer_0")
        assertThat(pt0?.transformerUtilisation, equalTo(0.0))

        val pt1 = network.get<PowerTransformer>("power_transformer_1")
        assertThat(pt1?.transformerUtilisation, equalTo(1.2))
    }

    @Test
    internal fun `cs28 updates rated_e and stored_e to longs`() {
        validateChangeSet("src/test/data/changeset28.sql", changeSet28()) {
            val rs = executeQuery("SELECT rated_e, stored_e FROM battery_unit WHERE mrid = 'abc'")
            assertThat(rs.getLong("rated_e"), equalTo(1500L))
            assertThat(rs.getLong("stored_e"), equalTo(2500L))
        }
    }

    /**
     * Takes a path to an SQL dump and executes it against an in-memory sqlite database, returning the connection.
     * @param path The filesystem path to the SQL file.
     * @return A [Connection] to the in-memory database.
     */
    internal fun sqlDumpToDB(path: String): Connection {
        val f = File(path)
        val lines = f.readLines()
        val conn = getConnection("jdbc:sqlite::memory:")
        conn.createStatement().use { statement ->
            lines.forEach {
                statement.executeUpdate(it)
            }
        }
        return conn
    }

    private fun validateChangeSet(sqlPath: String, changeSet: ChangeSet, validation: Statement.() -> Unit) {
        sqlDumpToDB(sqlPath).use { conn ->
            conn.createStatement().use { statement ->
                conn.prepareStatement(TableVersion().preparedUpdateSql()).use { versionUpdateStatement ->
                    statement.executeUpdate("BEGIN TRANSACTION")

                    UpgradeRunner().runUpgrade(changeSet, statement, versionUpdateStatement)

                    validation(statement)
                }
            }
        }
    }

}
