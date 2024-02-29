/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.ConnectivityNode
import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.cim.iec61970.base.meas.Accumulator
import com.zepben.evolve.cim.iec61970.base.meas.Analog
import com.zepben.evolve.cim.iec61970.base.meas.Discrete
import com.zepben.evolve.cim.iec61970.base.meas.Measurement
import com.zepben.evolve.cim.iec61970.base.wires.AcLineSegment
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.services.network.NetworkService.Companion.connectedEquipment
import com.zepben.evolve.services.network.NetworkService.Companion.connectedTerminals
import com.zepben.evolve.services.network.testdata.PhaseSwapLoopNetwork
import com.zepben.evolve.services.network.testdata.SplitIndividualPhasesFromJunctionNetwork
import com.zepben.evolve.services.network.testdata.SplitSinglePhasesFromJunctionNetwork
import com.zepben.evolve.services.network.testdata.createJunctionForConnecting
import com.zepben.evolve.services.network.tracing.connectivity.ConnectivityResult
import com.zepben.evolve.services.network.tracing.phases.NominalPhasePath
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.util.stream.Collectors
import java.util.stream.IntStream

internal class NetworkServiceTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val service = NetworkService()

    @Test
    internal fun `can add and remove supported types`() {
        service.supportedClasses
            .asSequence()
            .map { it.getDeclaredConstructor().newInstance() }
            .forEach {
                assertThat("Initial tryAdd should return true", service.tryAdd(it))
                assertThat(service[it.mRID], equalTo(it))
                assertThat("tryRemove should return true for previously-added object", service.tryRemove(it))
                assertThat(service[it.mRID], nullValue())
            }
    }

    @Test
    internal fun `indexes measurements on terminal`() {
        assertThat(service.getMeasurements<Measurement>("t1"), empty())

        val discrete = Discrete().apply { terminalMRID = "t1" }
        val accumulator = Accumulator().apply { terminalMRID = "t2" }
        val analog1 = Analog().apply { terminalMRID = "t1" }
        val analog2 = Analog().apply { terminalMRID = "t1" }

        service.add(discrete)
        service.add(accumulator)
        service.add(analog1)
        service.add(analog2)

        assertThat(service.getMeasurements("t1"), containsInAnyOrder(discrete, analog1, analog2))
        assertThat(service.getMeasurements("t2"), containsInAnyOrder(accumulator))

        assertThat(service.getMeasurements("t1", Analog::class), containsInAnyOrder(analog1, analog2))
        service.remove(analog1)
        assertThat(service.getMeasurements("t1", Analog::class), containsInAnyOrder(analog2))
    }

    @Test
    internal fun `indexes measurements on power system resource`() {
        assertThat(service.getMeasurements<Measurement>("psr1"), empty())

        val discrete = Discrete().apply { powerSystemResourceMRID = "psr1" }
        val accumulator = Accumulator().apply { powerSystemResourceMRID = "psr2" }
        val analog1 = Analog().apply { powerSystemResourceMRID = "psr1" }
        val analog2 = Analog().apply { powerSystemResourceMRID = "psr1" }

        service.add(discrete)
        service.add(accumulator)
        service.add(analog1)
        service.add(analog2)

        assertThat(service.getMeasurements("psr1"), containsInAnyOrder(discrete, analog1, analog2))
        assertThat(service.getMeasurements("psr2"), containsInAnyOrder(accumulator))

        assertThat(service.getMeasurements("psr1", Analog::class), containsInAnyOrder(analog1, analog2))
        service.remove(analog1)
        assertThat(service.getMeasurements("psr1", Analog::class), containsInAnyOrder(analog2))
    }

    @Test
    internal fun testNetworkConnect() {
        val network = NetworkService()
        val j0 = createJunctionForConnecting(network, "j0", 2)
        val j1 = createJunctionForConnecting(network, "j1", 2)
        val j2 = createJunctionForConnecting(network, "j2", 2)
        val j3 = createJunctionForConnecting(network, "j3", 2)
        val j4 = createJunctionForConnecting(network, "j4", 2)
        val j5 = createJunctionForConnecting(network, "j5", 2)

        // Connect 2 terminals.
        assertThat(
            "Two disconnected terminals should connect successfully",
            network.connect(j0.getTerminal(1)!!, j1.getTerminal(1)!!)
        )
        var connectivityNode = j0.getTerminal(1)!!.connectivityNode!!
        assertThat(connectivityNode, notNullValue())
        assertThat(connectivityNode.mRID, containsString("generated_cn_"))
        assertThat(j1.getTerminal(1)!!.connectivityNode, equalTo(connectivityNode))
        assertThat(connectivityNode.numTerminals(), equalTo(2))

        // Add third terminal by linking to first.
        assertThat(
            "Should be able to connect disconnected terminal to connected terminal",
            network.connect(j0.getTerminal(1)!!, j2.getTerminal(1)!!)
        )
        assertThat(j0.getTerminal(1)!!.connectivityNode, equalTo(connectivityNode))
        assertThat(j2.getTerminal(1)!!.connectivityNode, equalTo(connectivityNode))
        assertThat(connectivityNode.numTerminals(), equalTo(3))

        // Add fourth terminal by linking to the cn.
        assertThat(
            "Should be able to connect terminal to connectivity node",
            network.connect(j3.getTerminal(1)!!, connectivityNode.mRID)
        )
        assertThat(j0.getTerminal(1)!!.connectivityNode, equalTo(connectivityNode))
        assertThat(j3.getTerminal(1)!!.connectivityNode, equalTo(connectivityNode))
        assertThat(connectivityNode.numTerminals(), equalTo(4))

        // Create a single connection.
        assertThat(
            "Should be able to connect terminal to connectivity node by mRID",
            network.connect(j4.getTerminal(1)!!, "test_cn_1")
        )
        connectivityNode = j4.getTerminal(1)!!.connectivityNode!!
        assertThat(connectivityNode, notNullValue())
        assertThat(connectivityNode.mRID, equalTo("test_cn_1"))
        assertThat(connectivityNode.numTerminals(), equalTo(1))

        // Attempt to create a single connection with no connectivity nodes.
        assertThat(
            "Attempting to connect terminal to connectivity node with blank mRID should fail",
            !network.connect(j5.getTerminal(1)!!, "")
        )

        // Join 2 nodes that are already linked.
        val beforeConnectivityNode = j0.getTerminal(1)!!.connectivityNode!!
        assertThat(beforeConnectivityNode, notNullValue())

        val beforeSize = beforeConnectivityNode.numTerminals()
        assertThat(
            "Connecting already-connected terminals should return true",
            network.connect(j0.getTerminal(1)!!, j1.getTerminal(1)!!)
        )

        connectivityNode = j0.getTerminal(1)!!.connectivityNode!!
        assertThat(connectivityNode, notNullValue())
        assertThat(connectivityNode, equalTo(beforeConnectivityNode))
        assertThat(j1.getTerminal(1)!!.connectivityNode, equalTo(connectivityNode))
        assertThat(connectivityNode.numTerminals(), equalTo(beforeSize))

        // Attempt to join 2 nodes that are linked to different cn's.
        assertThat(
            "Should be able to connect two terminals that have different connectivity nodes",
            !network.connect(j0.getTerminal(1)!!, j4.getTerminal(1)!!)
        )
    }

    @Test
    internal fun testNetworkDisconnect() {
        val network = NetworkService()
        val j0 = createJunctionForConnecting(network, "j0", 2)
        val j1 = createJunctionForConnecting(network, "j1", 2)
        val j2 = createJunctionForConnecting(network, "j2", 2)
        val j3 = createJunctionForConnecting(network, "j3", 2)
        val j4 = createJunctionForConnecting(network, "j4", 2)
        val j5 = createJunctionForConnecting(network, "j5", 2)

        // Connect up a network, so we can disconnect it :)
        network.connect(j0.getTerminal(1)!!, "cn_1")
        network.connect(j1.getTerminal(1)!!, "cn_1")
        network.connect(j2.getTerminal(1)!!, "cn_1")
        network.connect(j3.getTerminal(1)!!, "cn_2")
        network.connect(j4.getTerminal(1)!!, "cn_2")
        network.connect(j5.getTerminal(1)!!, "cn_2")

        var connectivityNode = j0.getTerminal(1)!!.connectivityNode!!
        assertThat(connectivityNode, notNullValue())
        assertThat("Network should contain connectivity node ${connectivityNode.mRID}", network.containsConnectivityNode(connectivityNode.mRID))
        assertThat(connectivityNode.numTerminals(), equalTo(3))

        network.disconnect(j0.getTerminal(1)!!)
        assertThat(j0.getTerminal(1)!!.connectivityNode, nullValue())
        assertThat(connectivityNode.numTerminals(), equalTo(2))

        network.disconnect(j1.getTerminal(1)!!)
        network.disconnect(j2.getTerminal(1)!!)
        assertThat("Connectivity node should be removed after all its terminals are disconnected", !network.containsConnectivityNode(connectivityNode.mRID))
        assertThat(connectivityNode.numTerminals(), equalTo(0))

        connectivityNode = j3.getTerminal(1)!!.connectivityNode!!
        assertThat(connectivityNode, notNullValue())
        assertThat("Network should contain connectivity node ${connectivityNode.mRID}", network.containsConnectivityNode(connectivityNode.mRID))
        assertThat(connectivityNode.numTerminals(), equalTo(3))
        network.disconnect(connectivityNode.mRID)
        assertThat(j3.getTerminal(1)!!.connectivityNode, nullValue())
        assertThat("Connectivity node should be removed after being disconnected", !network.containsConnectivityNode(connectivityNode.mRID))
        assertThat(connectivityNode.numTerminals(), equalTo(0))
    }

    @Test
    internal fun testNetworkConnectedTerminals() {
        val network = PhaseSwapLoopNetwork.create()
        val j0 = network.get<ConductingEquipment>("j0")!!
        val j3 = network.get<ConductingEquipment>("j3")!!
        val j5 = network.get<ConductingEquipment>("j5")!!
        val acLineSegment0 = network.get<AcLineSegment>("acLineSegment0")!!
        val acLineSegment1 = network.get<AcLineSegment>("acLineSegment1")!!
        val acLineSegment2 = network.get<AcLineSegment>("acLineSegment2")!!
        val acLineSegment3 = network.get<AcLineSegment>("acLineSegment3")!!
        val acLineSegment4 = network.get<AcLineSegment>("acLineSegment4")!!
        val acLineSegment5 = network.get<AcLineSegment>("acLineSegment5")!!
        val acLineSegment9 = network.get<AcLineSegment>("acLineSegment9")!!
        val acLineSegment11 = network.get<AcLineSegment>("acLineSegment11")!!
        val expectedTerminals = mutableListOf<ConnectivityResult>()

        expectedTerminals.add(ConnectivityResult.between(j0.getTerminal(1)!!, acLineSegment0.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.ABCN)))
        assertThat(connectedTerminals(j0.getTerminal(1)!!, PhaseCode.ABCN), containsInAnyOrder(*expectedTerminals.toTypedArray()))
        assertThat(connectedTerminals(j0.getTerminal(1)!!), containsInAnyOrder(*expectedTerminals.toTypedArray()))
        assertThat(connectedTerminals(j0.getTerminal(1)!!, PhaseCode.ABCN.toSet()), containsInAnyOrder(*expectedTerminals.toTypedArray()))

        expectedTerminals.clear()
        expectedTerminals.add(
            ConnectivityResult.between(
                acLineSegment0.getTerminal(2)!!,
                acLineSegment1.getTerminal(1)!!,
                PhasePathSet.implicit(PhaseCode.ABCN)
            )
        )
        expectedTerminals.add(ConnectivityResult.between(acLineSegment0.getTerminal(2)!!, acLineSegment4.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.AB)))
        assertThat(connectedTerminals(acLineSegment0.getTerminal(2)!!, PhaseCode.ABCN), containsInAnyOrder<Any>(*expectedTerminals.toTypedArray()))

        expectedTerminals.clear()
        expectedTerminals.add(
            ConnectivityResult.between(
                acLineSegment2.getTerminal(2)!!,
                acLineSegment3.getTerminal(1)!!,
                PhasePathSet.implicit(PhaseCode.ABCN)
            )
        )
        expectedTerminals.add(ConnectivityResult.between(acLineSegment2.getTerminal(2)!!, acLineSegment9.getTerminal(2)!!, PhasePathSet.implicit(PhaseCode.BC)))
        assertThat(connectedTerminals(acLineSegment2.getTerminal(2)!!, PhaseCode.ABCN), containsInAnyOrder<Any>(*expectedTerminals.toTypedArray()))

        expectedTerminals.clear()
        expectedTerminals.add(ConnectivityResult.between(acLineSegment9.getTerminal(2)!!, acLineSegment2.getTerminal(2)!!, PhasePathSet.implicit(PhaseCode.BC)))
        expectedTerminals.add(ConnectivityResult.between(acLineSegment9.getTerminal(2)!!, acLineSegment3.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.BC)))
        assertThat(connectedTerminals(acLineSegment9.getTerminal(2)!!, PhaseCode.BC), containsInAnyOrder<Any>(*expectedTerminals.toTypedArray()))

        expectedTerminals.clear()
        expectedTerminals.add(ConnectivityResult.between(acLineSegment9.getTerminal(2)!!, acLineSegment2.getTerminal(2)!!, PhasePathSet.implicit(PhaseCode.B)))
        expectedTerminals.add(ConnectivityResult.between(acLineSegment9.getTerminal(2)!!, acLineSegment3.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.B)))
        assertThat(connectedTerminals(acLineSegment9.getTerminal(2)!!, PhaseCode.AB), containsInAnyOrder<Any>(*expectedTerminals.toTypedArray()))

        expectedTerminals.clear()
        expectedTerminals.add(
            ConnectivityResult.between(
                j3.getTerminal(2)!!,
                acLineSegment5.getTerminal(1)!!,
                PhasePathSet.from(PhaseCode.A).to(PhaseCode.X)
            )
        )
        assertThat(connectedTerminals(j3.getTerminal(2)!!, PhaseCode.A), containsInAnyOrder<Any>(*expectedTerminals.toTypedArray()))

        expectedTerminals.clear()
        expectedTerminals.add(
            ConnectivityResult.between(
                j3.getTerminal(2)!!,
                acLineSegment5.getTerminal(1)!!,
                PhasePathSet.from(PhaseCode.B).to(PhaseCode.Y)
            )
        )
        assertThat(connectedTerminals(j3.getTerminal(2)!!, PhaseCode.B), containsInAnyOrder<Any>(*expectedTerminals.toTypedArray()))
        assertThat(connectedTerminals(j5.getTerminal(3)!!, PhaseCode.X), empty())

        expectedTerminals.clear()
        expectedTerminals.add(ConnectivityResult.between(j5.getTerminal(3)!!, acLineSegment11.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.Y)))
        assertThat(connectedTerminals(j5.getTerminal(3)!!, PhaseCode.Y), containsInAnyOrder<Any>(*expectedTerminals.toTypedArray()))
    }

    @Test
    internal fun testNetworkConnectedEquipment() {
        val network = PhaseSwapLoopNetwork.create()
        val j0 = network.get<ConductingEquipment>("j0")!!
        val j1 = network.get<ConductingEquipment>("j1")!!
        val j3 = network.get<ConductingEquipment>("j3")!!
        val j4 = network.get<ConductingEquipment>("j4")!!
        val j5 = network.get<ConductingEquipment>("j5")!!
        val j6 = network.get<ConductingEquipment>("j6")!!
        val j7 = network.get<ConductingEquipment>("j7")!!
        val acLineSegment0 = network.get<AcLineSegment>("acLineSegment0")!!
        val acLineSegment1 = network.get<AcLineSegment>("acLineSegment1")!!
        val acLineSegment2 = network.get<AcLineSegment>("acLineSegment2")!!
        val acLineSegment3 = network.get<AcLineSegment>("acLineSegment3")!!
        val acLineSegment4 = network.get<AcLineSegment>("acLineSegment4")!!
        val acLineSegment5 = network.get<AcLineSegment>("acLineSegment5")!!
        val acLineSegment6 = network.get<AcLineSegment>("acLineSegment6")!!
        val acLineSegment7 = network.get<AcLineSegment>("acLineSegment7")!!
        val acLineSegment8 = network.get<AcLineSegment>("acLineSegment8")!!
        val acLineSegment9 = network.get<AcLineSegment>("acLineSegment9")!!
        val acLineSegment10 = network.get<AcLineSegment>("acLineSegment10")!!
        val acLineSegment11 = network.get<AcLineSegment>("acLineSegment11")!!
        val expectedAssets = mutableListOf<ConnectivityResult>()

        expectedAssets.add(ConnectivityResult.between(acLineSegment0.getTerminal(1)!!, j0.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.ABCN)))
        expectedAssets.add(ConnectivityResult.between(acLineSegment0.getTerminal(2)!!, acLineSegment1.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.ABCN)))
        expectedAssets.add(ConnectivityResult.between(acLineSegment0.getTerminal(2)!!, acLineSegment4.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.AB)))
        assertThat(connectedEquipment(acLineSegment0, PhaseCode.ABCN), containsInAnyOrder(*expectedAssets.toTypedArray()))
        assertThat(connectedEquipment(acLineSegment0), containsInAnyOrder(*expectedAssets.toTypedArray()))
        assertThat(connectedEquipment(acLineSegment0, PhaseCode.ABCN.toSet()), containsInAnyOrder(*expectedAssets.toTypedArray()))

        expectedAssets.clear()
        expectedAssets.add(ConnectivityResult.between(j4.getTerminal(1)!!, acLineSegment5.getTerminal(2)!!, PhasePathSet.implicit(PhaseCode.XY)))
        expectedAssets.add(ConnectivityResult.between(j4.getTerminal(2)!!, acLineSegment6.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.XY)))
        expectedAssets.add(ConnectivityResult.between(j4.getTerminal(3)!!, acLineSegment10.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.X)))
        assertThat(connectedEquipment(j4, PhaseCode.XY), containsInAnyOrder(*expectedAssets.toTypedArray()))

        expectedAssets.clear()
        expectedAssets.add(ConnectivityResult.between(j5.getTerminal(1)!!, acLineSegment6.getTerminal(2)!!, PhasePathSet.implicit(PhaseCode.XY)))
        expectedAssets.add(ConnectivityResult.between(j5.getTerminal(2)!!, acLineSegment7.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.XY)))
        expectedAssets.add(ConnectivityResult.between(j5.getTerminal(3)!!, acLineSegment11.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.Y)))
        assertThat(connectedEquipment(j5, PhaseCode.XY), containsInAnyOrder(*expectedAssets.toTypedArray()))

        expectedAssets.clear()
        expectedAssets.add(ConnectivityResult.between(acLineSegment2.getTerminal(1)!!, j1.getTerminal(2)!!, PhasePathSet.implicit(PhaseCode.ABCN)))
        expectedAssets.add(ConnectivityResult.between(acLineSegment2.getTerminal(2)!!, acLineSegment3.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.ABCN)))
        expectedAssets.add(ConnectivityResult.between(acLineSegment2.getTerminal(2)!!, acLineSegment9.getTerminal(2)!!, PhasePathSet.implicit(PhaseCode.BC)))
        assertThat(connectedEquipment(acLineSegment2, PhaseCode.ABCN), containsInAnyOrder(*expectedAssets.toTypedArray()))

        expectedAssets.clear()
        expectedAssets.add(ConnectivityResult.between(acLineSegment9.getTerminal(1)!!, j7.getTerminal(2)!!, PhasePathSet.implicit(PhaseCode.BC)))
        expectedAssets.add(ConnectivityResult.between(acLineSegment9.getTerminal(2)!!, acLineSegment2.getTerminal(2)!!, PhasePathSet.implicit(PhaseCode.BC)))
        expectedAssets.add(ConnectivityResult.between(acLineSegment9.getTerminal(2)!!, acLineSegment3.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.BC)))
        assertThat(connectedEquipment(acLineSegment9, PhaseCode.BC), containsInAnyOrder(*expectedAssets.toTypedArray()))

        expectedAssets.clear()
        expectedAssets.add(
            ConnectivityResult.between(
                j7.getTerminal(1)!!,
                acLineSegment8.getTerminal(2)!!,
                PhasePathSet.from(PhaseCode.BC).to(PhaseCode.XY)
            )
        )
        expectedAssets.add(ConnectivityResult.between(j7.getTerminal(2)!!, acLineSegment9.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.BC)))
        assertThat(connectedEquipment(j7, PhaseCode.BC), containsInAnyOrder(*expectedAssets.toTypedArray()))

        expectedAssets.clear()
        expectedAssets.add(ConnectivityResult.between(acLineSegment8.getTerminal(1)!!, j6.getTerminal(2)!!, PhasePathSet.implicit(PhaseCode.XY)))
        expectedAssets.add(
            ConnectivityResult.between(
                acLineSegment8.getTerminal(2)!!,
                j7.getTerminal(1)!!,
                PhasePathSet.from(PhaseCode.XY).to(PhaseCode.BC)
            )
        )
        assertThat(connectedEquipment(acLineSegment8, PhaseCode.XY), containsInAnyOrder(*expectedAssets.toTypedArray()))

        expectedAssets.clear()
        expectedAssets.add(ConnectivityResult.between(j3.getTerminal(1)!!, acLineSegment4.getTerminal(2)!!, PhasePathSet.implicit(PhaseCode.A)))
        expectedAssets.add(ConnectivityResult.between(j3.getTerminal(2)!!, acLineSegment5.getTerminal(1)!!, PhasePathSet.from(PhaseCode.A).to(PhaseCode.X)))
        assertThat(connectedEquipment(j3, PhaseCode.A), containsInAnyOrder(*expectedAssets.toTypedArray()))

        expectedAssets.clear()
        expectedAssets.add(ConnectivityResult.between(j3.getTerminal(1)!!, acLineSegment4.getTerminal(2)!!, PhasePathSet.implicit(PhaseCode.B)))
        expectedAssets.add(ConnectivityResult.between(j3.getTerminal(2)!!, acLineSegment5.getTerminal(1)!!, PhasePathSet.from(PhaseCode.B).to(PhaseCode.Y)))
        assertThat(connectedEquipment(j3, PhaseCode.B), containsInAnyOrder(*expectedAssets.toTypedArray()))

        expectedAssets.clear()
        expectedAssets.add(ConnectivityResult.between(acLineSegment5.getTerminal(1)!!, j3.getTerminal(2)!!, PhasePathSet.from(PhaseCode.X).to(PhaseCode.A)))
        expectedAssets.add(ConnectivityResult.between(acLineSegment5.getTerminal(2)!!, j4.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.X)))
        assertThat(connectedEquipment(acLineSegment5, PhaseCode.X), containsInAnyOrder(*expectedAssets.toTypedArray()))

        expectedAssets.clear()
        expectedAssets.add(ConnectivityResult.between(acLineSegment5.getTerminal(1)!!, j3.getTerminal(2)!!, PhasePathSet.from(PhaseCode.Y).to(PhaseCode.B)))
        expectedAssets.add(ConnectivityResult.between(acLineSegment5.getTerminal(2)!!, j4.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.Y)))
        assertThat(connectedEquipment(acLineSegment5, PhaseCode.Y), containsInAnyOrder(*expectedAssets.toTypedArray()))
    }

    @Test
    internal fun testNetworkConnected2() {
        val network = SplitIndividualPhasesFromJunctionNetwork.create()

        val j1 = network.get<ConductingEquipment>("j1")!!
        val acLineSegment1 = network.get<AcLineSegment>("acLineSegment1")!!
        val acLineSegment2 = network.get<AcLineSegment>("acLineSegment2")!!
        val acLineSegment3 = network.get<AcLineSegment>("acLineSegment3")!!
        val acLineSegment4 = network.get<AcLineSegment>("acLineSegment4")!!
        val acLineSegment5 = network.get<AcLineSegment>("acLineSegment5")!!
        val expectedAssets = mutableListOf<ConnectivityResult>()

        expectedAssets.add(ConnectivityResult.between(j1.getTerminal(1)!!, acLineSegment1.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.ABCN)))
        expectedAssets.add(ConnectivityResult.between(j1.getTerminal(2)!!, acLineSegment2.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.A)))
        expectedAssets.add(ConnectivityResult.between(j1.getTerminal(3)!!, acLineSegment3.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.B)))
        expectedAssets.add(ConnectivityResult.between(j1.getTerminal(4)!!, acLineSegment4.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.N)))
        expectedAssets.add(ConnectivityResult.between(j1.getTerminal(5)!!, acLineSegment5.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.CN)))
        assertThat(connectedEquipment(j1, PhaseCode.ABCN), containsInAnyOrder(*expectedAssets.toTypedArray()))

        expectedAssets.clear()
        expectedAssets.add(ConnectivityResult.between(j1.getTerminal(1)!!, acLineSegment1.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.AC)))
        expectedAssets.add(ConnectivityResult.between(j1.getTerminal(2)!!, acLineSegment2.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.A)))
        expectedAssets.add(ConnectivityResult.between(j1.getTerminal(5)!!, acLineSegment5.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.C)))
        assertThat(connectedEquipment(j1, PhaseCode.AC), containsInAnyOrder(*expectedAssets.toTypedArray()))
    }

    @Test
    internal fun testNetworkConnected3() {
        val network = SplitSinglePhasesFromJunctionNetwork.create()
        val acLineSegment1 = network.get<AcLineSegment>("acLineSegment1")!!
        val acLineSegment2 = network.get<AcLineSegment>("acLineSegment2")!!
        val acLineSegment3 = network.get<AcLineSegment>("acLineSegment3")!!
        val acLineSegment4 = network.get<AcLineSegment>("acLineSegment4")!!
        val expectedAssets = mutableListOf<ConnectivityResult>()

        expectedAssets.add(ConnectivityResult.between(acLineSegment1.getTerminal(1)!!, acLineSegment2.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.AB)))
        expectedAssets.add(ConnectivityResult.between(acLineSegment1.getTerminal(1)!!, acLineSegment3.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.AC)))
        expectedAssets.add(ConnectivityResult.between(acLineSegment1.getTerminal(1)!!, acLineSegment4.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.BC)))
        assertThat(connectedEquipment(acLineSegment1, PhaseCode.ABCN), containsInAnyOrder(*expectedAssets.toTypedArray()))

        expectedAssets.clear()
        expectedAssets.add(ConnectivityResult.between(acLineSegment2.getTerminal(1)!!, acLineSegment1.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.AB)))
        expectedAssets.add(ConnectivityResult.between(acLineSegment2.getTerminal(1)!!, acLineSegment3.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.A)))
        expectedAssets.add(ConnectivityResult.between(acLineSegment2.getTerminal(1)!!, acLineSegment4.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.B)))
        assertThat(connectedEquipment(acLineSegment2, PhaseCode.AB), containsInAnyOrder(*expectedAssets.toTypedArray()))

        expectedAssets.clear()
        expectedAssets.add(ConnectivityResult.between(acLineSegment3.getTerminal(1)!!, acLineSegment1.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.AC)))
        expectedAssets.add(ConnectivityResult.between(acLineSegment3.getTerminal(1)!!, acLineSegment2.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.A)))
        expectedAssets.add(ConnectivityResult.between(acLineSegment3.getTerminal(1)!!, acLineSegment4.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.C)))
        assertThat(connectedEquipment(acLineSegment3, PhaseCode.AC), containsInAnyOrder(*expectedAssets.toTypedArray()))

        expectedAssets.clear()
        expectedAssets.add(ConnectivityResult.between(acLineSegment4.getTerminal(1)!!, acLineSegment1.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.BC)))
        expectedAssets.add(ConnectivityResult.between(acLineSegment4.getTerminal(1)!!, acLineSegment2.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.B)))
        expectedAssets.add(ConnectivityResult.between(acLineSegment4.getTerminal(1)!!, acLineSegment3.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.C)))
        assertThat(connectedEquipment(acLineSegment4, PhaseCode.BC), containsInAnyOrder(*expectedAssets.toTypedArray()))
    }

    @Test
    internal fun getOrPutConnectivityNode() {
        val network = NetworkService()

        val mRID = "cn1"
        assertThat(network.get<ConnectivityNode>(mRID), nullValue())

        val first = network.getOrPutConnectivityNode(mRID)
        val second = network.getOrPutConnectivityNode(mRID)

        assertThat(first, equalTo(second))
        assertThat(network[mRID], equalTo(first))
    }

    private fun PhaseCode.toSet(): Set<SinglePhaseKind> = singlePhases.toSet()

    private class PhasePathSet private constructor(private val fromPhases: PhaseCode) {
        fun to(toPhases: PhaseCode): Set<NominalPhasePath> {
            return IntStream.range(0, fromPhases.singlePhases.size)
                .mapToObj { i: Int -> NominalPhasePath(fromPhases.singlePhases[i], toPhases.singlePhases[i]) }
                .collect(Collectors.toSet())
        }

        companion object {
            fun from(fromPhases: PhaseCode): PhasePathSet {
                return PhasePathSet(fromPhases)
            }

            fun implicit(phases: PhaseCode) = phases.singlePhases
                .asSequence()
                .map { NominalPhasePath(it, it) }
                .toSet()
        }
    }
}
