/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.upgrade
import com.zepben.cimbend.cim.iec61970.base.core.Terminal
import com.zepben.cimbend.cim.iec61970.base.meas.Analog
import com.zepben.cimbend.cim.iec61970.base.wires.PowerTransformerEnd
import com.zepben.cimbend.customer.CustomerService
import com.zepben.cimbend.database.sqlite.DatabaseReader
import com.zepben.cimbend.diagram.DiagramService
import com.zepben.cimbend.measurement.MeasurementService
import com.zepben.cimbend.network.NetworkService
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.api.fail
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
import java.sql.DriverManager

class ChangeSetTest {


    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()


    @Test
    @Throws(Exception::class)
    fun `updates sequence and end numbers`(@TempDir tempDir: Path) {
        val dbFile = File("src/test/data/changeset20.sqlite").copyTo(Path.of(tempDir.toString(), "changeset20.sqlite").toFile()).toPath()
        val runner = UpgradeRunner()
        val cr = runner.connectAndUpgrade("jdbc:sqlite:$dbFile", dbFile)

        // Ensure index was recreated, as changeset drops it to update numbers
        cr.connection.use { connection ->
            connection.createStatement().use { statement ->
                statement.executeQuery("pragma index_info('power_transformer_ends_power_transformer_mrid_end_number')").use {rs ->
                    assertThat(rs.next(), equalTo(true))
                }
            }
        }

        val reader = DatabaseReader(dbFile.toString())
        val network = NetworkService()
        reader.load(network, DiagramService(), CustomerService())

        assertThat(network.get<Terminal>("t1")!!.sequenceNumber, equalTo(1))
        assertThat(network.get<Terminal>("t2")!!.sequenceNumber, equalTo(2))
        assertThat(network.get<Terminal>("t3")!!.sequenceNumber, equalTo(3))
        assertThat(network.get<Terminal>("t4")!!.sequenceNumber, equalTo(11))

        assertThat(network.get<PowerTransformerEnd>("e1")!!.endNumber, equalTo(1))
        assertThat(network.get<PowerTransformerEnd>("e2")!!.endNumber, equalTo(2))
        assertThat(network.get<PowerTransformerEnd>("e3")!!.endNumber, equalTo(11))
    }

    @Test
    @Throws(Exception::class)
    fun `migrates measurement to analog`(@TempDir tempDir: Path) {
        val dbFile = File("src/test/data/changeset21.sqlite").copyTo(Path.of(tempDir.toString(), "changeset21.sqlite").toFile()).toPath()
        val runner = UpgradeRunner()
        val cr = runner.connectAndUpgrade("jdbc:sqlite:$dbFile", dbFile)
        cr.connection.close()

        val reader = DatabaseReader(dbFile.toString())
        val network = NetworkService()
        reader.load(network, DiagramService(), CustomerService())

        var meas1 = network.get<Analog>("meas1")
        assertThat(meas1?.name, `is`("meas1"))
        assertThat(meas1?.description, `is`("meas1"))
        assertThat(meas1?.numDiagramObjects, `is`(1))
        assertThat(meas1?.powerSystemResourceMRID, `is`("psr1"))
        var meas2 = network.get<Analog>("meas2")
        assertThat(meas2?.name, `is`("meas2"))
        assertThat(meas2?.description, `is`("meas2"))
        assertThat(meas2?.numDiagramObjects, `is`(2))
        assertThat(meas2?.powerSystemResourceMRID, `is`("psr2"))
        var meas3 = network.get<Analog>("meas3")
        assertThat(meas3?.name, `is`("meas3"))
        assertThat(meas3?.description, `is`("meas3"))
        assertThat(meas3?.numDiagramObjects, `is`(3))
        assertThat(meas3?.powerSystemResourceMRID, `is`("psr3"))


    }

}
