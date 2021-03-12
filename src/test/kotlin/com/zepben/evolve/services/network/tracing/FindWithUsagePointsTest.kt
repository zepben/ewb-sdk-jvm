/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing

import com.zepben.evolve.cim.iec61968.metering.UsagePoint
import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.wires.EnergySource
import com.zepben.evolve.cim.iec61970.base.wires.PowerTransformer
import com.zepben.evolve.services.network.testdata.SingleTransformerNetwork
import com.zepben.evolve.services.network.testdata.WithUsagePointsNetwork
import com.zepben.evolve.services.network.tracing.FindWithUsagePoints.Result.Status.*
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.util.function.Consumer


class FindWithUsagePointsTest {

    @JvmField
    @RegisterExtension
    var systemOutRule: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val findWithUsagePoints = FindWithUsagePoints()

    //
    //      c0   c1   c2             c3   c4   c5
    //  es ----+----+----+- sw1 iso ----+----+---- tx1
    //         |    |    |              |    |
    //         |c6  |c7  |c8            |c9  |c10
    //         |    |    |              |    |
    //        tx2  tx3   |             tx4  tx5
    //                   |
    //                   |       c11  c12  c13
    //                   +- sw2 ----+----+---- tx6
    //                              |    |
    //                              |c14 |c15
    //                              |    |
    //                             tx7  tx8
    //
    // sw1: normally close, currently open
    // sw2: normally open, currently closed
    //
    private val network = WithUsagePointsNetwork.create()

    private val es = ce("es")
    private val c1 = ce("c1")
    private val c3 = ce("c3")
    private val c5 = ce("c5")
    private val c11 = ce("c11")
    private val c12 = ce("c12")
    private val tx6 = ce("tx6")

    @Test
    fun normalStateSingleTrace() {
        validate(findWithUsagePoints.runNormal(es, null), NO_ERROR, listOf("tx1", "tx2", "tx3", "tx4", "tx5", "iso"))
        validate(findWithUsagePoints.runNormal(c3, c5), NO_ERROR, listOf("tx4", "tx5"))
        validate(findWithUsagePoints.runNormal(c5, c3), NO_ERROR, listOf("tx4", "tx5"))
        validate(findWithUsagePoints.runNormal(es, c11), NO_PATH, emptyList())
    }

    @Test
    fun normalStateMultiTrace() {
        val results = findWithUsagePoints.runNormal(listOf(es, c3, c5, es), listOf(null, c5, c3, c11))

        assertThat(results.size, equalTo(4))
        validate(results[0], NO_ERROR, listOf("tx1", "tx2", "tx3", "tx4", "tx5", "iso"))
        validate(results[1], NO_ERROR, listOf("tx4", "tx5"))
        validate(results[2], NO_ERROR, listOf("tx4", "tx5"))
        validate(results[3], NO_PATH, emptyList())

        validateMismatch(findWithUsagePoints.runNormal(listOf(es, es), listOf(null)), 2)
        validateMismatch(findWithUsagePoints.runNormal(listOf(es, es), listOf(c1, c11, c3)), 3)
    }

    @Test
    fun currentStateSingleTrace() {
        validate(findWithUsagePoints.runCurrent(es, null), NO_ERROR, listOf("tx2", "tx3", "tx6", "tx7", "tx8"))
        validate(findWithUsagePoints.runCurrent(c1, c12), NO_ERROR, listOf("tx3", "tx7"))
        validate(findWithUsagePoints.runCurrent(c12, c1), NO_ERROR, listOf("tx3", "tx7"))
        validate(findWithUsagePoints.runCurrent(es, c5), NO_PATH, emptyList())
    }

    @Test
    fun currentStateMultiTrace() {
        val results = findWithUsagePoints.runCurrent(listOf(es, c1, c12, es), listOf(null, c12, c1, c5))

        assertThat(results.size, equalTo(4))
        validate(results[0], NO_ERROR, listOf("tx2", "tx3", "tx6", "tx7", "tx8"))
        validate(results[1], NO_ERROR, listOf("tx3", "tx7"))
        validate(results[2], NO_ERROR, listOf("tx3", "tx7"))
        validate(results[3], NO_PATH, emptyList())

        validateMismatch(findWithUsagePoints.runCurrent(listOf(es, es), listOf(null)), 2)
        validateMismatch(findWithUsagePoints.runCurrent(listOf(es, es), listOf(c1, c11, c3)), 3)
    }

    @Test
    fun sameFromAndTo() {
        validate(findWithUsagePoints.runNormal(c3, c3), NO_ERROR, emptyList())
        validate(findWithUsagePoints.runNormal(tx6, tx6), NO_ERROR, listOf("tx6"))
    }

    @Test
    fun worksWithNoTerminals() {
        val tx1 = PowerTransformer()
        val tx2 = PowerTransformer()
        val usagePoint = UsagePoint()

        usagePoint.addEquipment(tx1)
        tx1.addUsagePoint(usagePoint)

        validate(findWithUsagePoints.runNormal(tx1, null), NO_ERROR, listOf(tx1.mRID))
        validate(findWithUsagePoints.runNormal(tx1, tx1), NO_ERROR, listOf(tx1.mRID))
        validate(findWithUsagePoints.runNormal(tx2, null), NO_ERROR, emptyList())
        validate(findWithUsagePoints.runNormal(tx2, tx2), NO_ERROR, emptyList())
    }

    @Test
    fun doesntRelyOnTerminalSequenceNumbers() {
        val validateSingleTransformerNetwork = { sequenceNumber: Int ->
            val networkService1 = SingleTransformerNetwork.create(sequenceNumber)

            val s: EnergySource = networkService1["s"]!!
            val tx: PowerTransformer = networkService1["tx"]!!

            // Validate both `from` and `to` terminals.
            validate(findWithUsagePoints.runNormal(tx, null), NO_ERROR, listOf(tx.mRID))
            validate(findWithUsagePoints.runNormal(tx, s), NO_ERROR, listOf(tx.mRID))
        }

        validateSingleTransformerNetwork(1)
        validateSingleTransformerNetwork(2)
    }

    private fun ce(mRID: String): ConductingEquipment {
        return network.get(ConductingEquipment::class.java, mRID)!!
    }

    private fun validate(result: FindWithUsagePoints.Result, expectedStatus: FindWithUsagePoints.Result.Status, expectedMRIDs: List<String>) {
        assertThat(result.status(), equalTo(expectedStatus))
        assertThat(result.conductingEquipment().keys, containsInAnyOrder(*expectedMRIDs.toTypedArray()))
    }

    private fun validateMismatch(results: List<FindWithUsagePoints.Result>, expectedResults: Int) {
        assertThat(results.size, equalTo(expectedResults))

        results.forEach(Consumer { result: FindWithUsagePoints.Result ->
            assertThat(result.status(), equalTo(MISMATCHED_FROM_TO))
            assertThat(result.conductingEquipment().keys, empty())
        })
    }
}
