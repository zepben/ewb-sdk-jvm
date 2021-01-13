/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network

import com.zepben.evolve.cim.iec61968.assetinfo.CableInfo
import com.zepben.evolve.cim.iec61968.assetinfo.OverheadWireInfo
import com.zepben.evolve.cim.iec61968.assetinfo.PowerTransformerInfo
import com.zepben.evolve.cim.iec61968.assets.AssetOwner
import com.zepben.evolve.cim.iec61968.assets.Pole
import com.zepben.evolve.cim.iec61968.assets.Streetlight
import com.zepben.evolve.cim.iec61968.common.Location
import com.zepben.evolve.cim.iec61968.common.Organisation
import com.zepben.evolve.cim.iec61968.metering.Meter
import com.zepben.evolve.cim.iec61968.metering.UsagePoint
import com.zepben.evolve.cim.iec61968.operations.OperationalRestriction
import com.zepben.evolve.cim.iec61970.base.auxiliaryequipment.FaultIndicator
import com.zepben.evolve.cim.iec61970.base.core.*
import com.zepben.evolve.cim.iec61970.base.meas.*
import com.zepben.evolve.cim.iec61970.base.scada.RemoteControl
import com.zepben.evolve.cim.iec61970.base.scada.RemoteSource
import com.zepben.evolve.cim.iec61970.base.wires.*
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Loop
import com.zepben.evolve.services.network.NetworkService.Companion.connectedEquipment
import com.zepben.evolve.services.network.NetworkService.Companion.connectedTerminals
import com.zepben.evolve.services.network.testdata.TestDataCreators.createNodeForConnecting
import com.zepben.evolve.services.network.testdata.TestNetworks
import com.zepben.evolve.services.network.tracing.ConnectivityResult
import com.zepben.evolve.services.network.tracing.phases.NominalPhasePath
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.util.*
import java.util.stream.Collectors
import java.util.stream.IntStream

internal class NetworkServiceTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val service = NetworkService()

    @Test
    internal fun supportsAcLineSegment() {
        val acLineSegment = AcLineSegment()
        assertThat(service.add(acLineSegment), equalTo(true))
        assertThat(service.remove(acLineSegment), equalTo(true))
    }

    @Test
    internal fun supportsAssetOwner() {
        val assetOwner = AssetOwner()
        assertThat(service.add(assetOwner), equalTo(true))
        assertThat(service.remove(assetOwner), equalTo(true))
    }

    @Test
    internal fun supportsBaseVoltage() {
        val baseVoltage = BaseVoltage()
        assertThat(service.add(baseVoltage), equalTo(true))
        assertThat(service.remove(baseVoltage), equalTo(true))
    }

    @Test
    internal fun supportsBreaker() {
        val breaker = Breaker()
        assertThat(service.add(breaker), equalTo(true))
        assertThat(service.remove(breaker), equalTo(true))
    }

    @Test
    internal fun supportsCableInfo() {
        val cableInfo = CableInfo()
        assertThat(service.add(cableInfo), equalTo(true))
        assertThat(service.remove(cableInfo), equalTo(true))
    }

    @Test
    internal fun supportsPowerTransformerInfo() {
        val powerTransformerInfo = PowerTransformerInfo()
        assertThat(service.add(powerTransformerInfo), equalTo(true))
        assertThat(service.remove(powerTransformerInfo), equalTo(true))
    }

    @Test
    internal fun supportsCircuit() {
        val circuit = Circuit()
        assertThat(service.add(circuit), equalTo(true))
        assertThat(service.remove(circuit), equalTo(true))
    }

    @Test
    internal fun supportsConnectivityNode() {
        val connectivityNode = ConnectivityNode()
        assertThat(service.add(connectivityNode), equalTo(true))
        assertThat(service.remove(connectivityNode), equalTo(true))
    }

    @Test
    internal fun supportsDisconnector() {
        val disconnector = Disconnector()
        assertThat(service.add(disconnector), equalTo(true))
        assertThat(service.remove(disconnector), equalTo(true))
    }

    @Test
    internal fun supportsEnergyConsumer() {
        val energyConsumer = EnergyConsumer()
        assertThat(service.add(energyConsumer), equalTo(true))
        assertThat(service.remove(energyConsumer), equalTo(true))
    }

    @Test
    internal fun supportsEnergyConsumerPhase() {
        val energyConsumerPhase = EnergyConsumerPhase()
        assertThat(service.add(energyConsumerPhase), equalTo(true))
        assertThat(service.remove(energyConsumerPhase), equalTo(true))
    }

    @Test
    internal fun supportsEnergySource() {
        val energySource = EnergySource()
        assertThat(service.add(energySource), equalTo(true))
        assertThat(service.remove(energySource), equalTo(true))
    }

    @Test
    internal fun supportsEnergySourcePhase() {
        val energySourcePhase = EnergySourcePhase()
        assertThat(service.add(energySourcePhase), equalTo(true))
        assertThat(service.remove(energySourcePhase), equalTo(true))
    }

    @Test
    internal fun supportsFaultIndicator() {
        val faultIndicator = FaultIndicator()
        assertThat(service.add(faultIndicator), equalTo(true))
        assertThat(service.remove(faultIndicator), equalTo(true))
    }

    @Test
    internal fun supportsFeeder() {
        val feeder = Feeder()
        assertThat(service.add(feeder), equalTo(true))
        assertThat(service.remove(feeder), equalTo(true))
    }

    @Test
    internal fun supportsFuse() {
        val fuse = Fuse()
        assertThat(service.add(fuse), equalTo(true))
        assertThat(service.remove(fuse), equalTo(true))
    }

    @Test
    internal fun supportsGeographicalRegion() {
        val geographicalRegion = GeographicalRegion()
        assertThat(service.add(geographicalRegion), equalTo(true))
        assertThat(service.remove(geographicalRegion), equalTo(true))
    }

    @Test
    internal fun supportsJumper() {
        val jumper = Jumper()
        assertThat(service.add(jumper), equalTo(true))
        assertThat(service.remove(jumper), equalTo(true))
    }

    @Test
    internal fun supportsJunction() {
        val junction = Junction()
        assertThat(service.add(junction), equalTo(true))
        assertThat(service.remove(junction), equalTo(true))
    }

    @Test
    internal fun supportsLinearShuntCompensator() {
        val linearShuntCompensator = LinearShuntCompensator()
        assertThat(service.add(linearShuntCompensator), equalTo(true))
        assertThat(service.remove(linearShuntCompensator), equalTo(true))
    }

    @Test
    internal fun supportsLocation() {
        val location = Location()
        assertThat(service.add(location), equalTo(true))
        assertThat(service.remove(location), equalTo(true))
    }

    @Test
    internal fun supportsLoop() {
        val loop = Loop()
        assertThat(service.add(loop), equalTo(true))
        assertThat(service.remove(loop), equalTo(true))
    }

    @Test
    internal fun supportsMeter() {
        val meter = Meter()
        assertThat(service.add(meter), equalTo(true))
        assertThat(service.remove(meter), equalTo(true))
    }

    @Test
    internal fun supportsOperationalRestriction() {
        val operationalRestriction = OperationalRestriction()
        assertThat(service.add(operationalRestriction), equalTo(true))
        assertThat(service.remove(operationalRestriction), equalTo(true))
    }

    @Test
    internal fun supportsOrganisation() {
        val organisation = Organisation()
        assertThat(service.add(organisation), equalTo(true))
        assertThat(service.remove(organisation), equalTo(true))
    }

    @Test
    internal fun supportsOverheadWireInfo() {
        val overheadWireInfo = OverheadWireInfo()
        assertThat(service.add(overheadWireInfo), equalTo(true))
        assertThat(service.remove(overheadWireInfo), equalTo(true))
    }

    @Test
    internal fun supportsPerLengthSequenceImpedance() {
        val perLengthSequenceImpedance = PerLengthSequenceImpedance()
        assertThat(service.add(perLengthSequenceImpedance), equalTo(true))
        assertThat(service.remove(perLengthSequenceImpedance), equalTo(true))
    }

    @Test
    internal fun supportsPole() {
        val pole = Pole()
        assertThat(service.add(pole), equalTo(true))
        assertThat(service.remove(pole), equalTo(true))
    }

    @Test
    internal fun supportsPowerTransformer() {
        val powerTransformer = PowerTransformer()
        assertThat(service.add(powerTransformer), equalTo(true))
        assertThat(service.remove(powerTransformer), equalTo(true))
    }

    @Test
    internal fun supportsPowerTransformerEnd() {
        val powerTransformerEnd = PowerTransformerEnd()
        assertThat(service.add(powerTransformerEnd), equalTo(true))
        assertThat(service.remove(powerTransformerEnd), equalTo(true))
    }

    @Test
    internal fun supportsRatioTapChanger() {
        val ratioTapChanger = RatioTapChanger()
        assertThat(service.add(ratioTapChanger), equalTo(true))
        assertThat(service.remove(ratioTapChanger), equalTo(true))
    }

    @Test
    internal fun supportsRecloser() {
        val recloser = Recloser()
        assertThat(service.add(recloser), equalTo(true))
        assertThat(service.remove(recloser), equalTo(true))
    }

    @Test
    internal fun supportsSite() {
        val site = Site()
        assertThat(service.add(site), equalTo(true))
        assertThat(service.remove(site), equalTo(true))
    }

    @Test
    internal fun supportsStreetlight() {
        val streetlight = Streetlight()
        assertThat(service.add(streetlight), equalTo(true))
        assertThat(service.remove(streetlight), equalTo(true))
    }

    @Test
    internal fun supportsSubGeographicalRegion() {
        val subGeographicalRegion = SubGeographicalRegion()
        assertThat(service.add(subGeographicalRegion), equalTo(true))
        assertThat(service.remove(subGeographicalRegion), equalTo(true))
    }

    @Test
    internal fun supportsSubstation() {
        val substation = Substation()
        assertThat(service.add(substation), equalTo(true))
        assertThat(service.remove(substation), equalTo(true))
    }

    @Test
    internal fun supportsTerminal() {
        val terminal = Terminal()
        assertThat(service.add(terminal), equalTo(true))
        assertThat(service.remove(terminal), equalTo(true))
    }

    @Test
    internal fun supportsUsagePoint() {
        val usagePoint = UsagePoint()
        assertThat(service.add(usagePoint), equalTo(true))
        assertThat(service.remove(usagePoint), equalTo(true))
    }

    @Test
    internal fun supportsControl() {
        val control = Control()
        assertThat(service.add(control), equalTo(true))
        assertThat(service.remove(control), equalTo(true))
    }

    @Test
    internal fun supportsRemoteControl() {
        val remoteControl = RemoteControl()
        assertThat(service.add(remoteControl), equalTo(true))
        assertThat(service.remove(remoteControl), equalTo(true))
    }

    @Test
    internal fun supportsRemoteSource() {
        val remoteSource = RemoteSource()
        assertThat(service.add(remoteSource), equalTo(true))
        assertThat(service.remove(remoteSource), equalTo(true))
    }

    @Test
    internal fun supportsAnalog() {
        val measurement = Analog()

        assertThat(service.add(measurement), equalTo(true))
        assertThat(service.get(measurement.mRID), equalTo(measurement))
        assertThat(service.remove(measurement), equalTo(true))
    }

    @Test
    internal fun supportsAccumulator() {
        val measurement = Accumulator()

        assertThat(service.add(measurement), equalTo(true))
        assertThat(service.get(measurement.mRID), equalTo(measurement))
        assertThat(service.remove(measurement), equalTo(true))
    }

    @Test
    internal fun supportsDiscrete() {
        val measurement = Discrete()

        assertThat(service.add(measurement), equalTo(true))
        assertThat(service.get(measurement.mRID), equalTo(measurement))
        assertThat(service.remove(measurement), equalTo(true))
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
        val node0 = createNodeForConnecting(network, "node0", 2)
        val node1 = createNodeForConnecting(network, "node1", 2)
        val node2 = createNodeForConnecting(network, "node2", 2)
        val node3 = createNodeForConnecting(network, "node3", 2)
        val node4 = createNodeForConnecting(network, "node4", 2)
        val node5 = createNodeForConnecting(network, "node5", 2)

        // Connect 2 terminals.
        assertThat(network.connect(node0.getTerminal(1)!!, node1.getTerminal(1)!!), equalTo(true))
        var connectivityNode = node0.getTerminal(1)!!.connectivityNode!!
        assertThat(connectivityNode, notNullValue())
        assertThat(connectivityNode.mRID, containsString("generated_cn_"))
        assertThat(node1.getTerminal(1)!!.connectivityNode, equalTo(connectivityNode))
        assertThat(connectivityNode.numTerminals(), equalTo(2))

        // Add third terminals by linking to first.
        assertThat(network.connect(node0.getTerminal(1)!!, node2.getTerminal(1)!!), equalTo(true))
        assertThat(node0.getTerminal(1)!!.connectivityNode, equalTo(connectivityNode))
        assertThat(node2.getTerminal(1)!!.connectivityNode, equalTo(connectivityNode))
        assertThat(connectivityNode.numTerminals(), equalTo(3))

        // Add forth terminals by linking to the cn.
        assertThat(network.connect(node3.getTerminal(1)!!, connectivityNode.mRID), equalTo(true))
        assertThat(node0.getTerminal(1)!!.connectivityNode, equalTo(connectivityNode))
        assertThat(node3.getTerminal(1)!!.connectivityNode, equalTo(connectivityNode))
        assertThat(connectivityNode.numTerminals(), equalTo(4))

        // Create a single connection.
        assertThat(network.connect(node4.getTerminal(1)!!, "test_cn_1"), equalTo(true))
        connectivityNode = node4.getTerminal(1)!!.connectivityNode!!
        assertThat(connectivityNode, notNullValue())
        assertThat(connectivityNode.mRID, equalTo("test_cn_1"))
        assertThat(connectivityNode.numTerminals(), equalTo(1))

        // Attempt to create a single connection with no connectivity nodes.
        assertThat(network.connect(node5.getTerminal(1)!!, ""), equalTo(false))

        // Join 2 nodes that are already linked.
        val beforeConnectivityNode = node0.getTerminal(1)!!.connectivityNode!!
        assertThat(beforeConnectivityNode, notNullValue())

        val beforeSize = beforeConnectivityNode.numTerminals()
        assertThat(network.connect(node0.getTerminal(1)!!, node1.getTerminal(1)!!), equalTo(true))

        connectivityNode = node0.getTerminal(1)!!.connectivityNode!!
        assertThat(connectivityNode, notNullValue())
        assertThat(connectivityNode, equalTo(beforeConnectivityNode))
        assertThat(node1.getTerminal(1)!!.connectivityNode, equalTo(connectivityNode))
        assertThat(connectivityNode.numTerminals(), equalTo(beforeSize))

        // Attempt to join 2 nodes that are linked to different cn's.
        assertThat(network.connect(node0.getTerminal(1)!!, node4.getTerminal(1)!!), equalTo(false))
    }

    @Test
    internal fun testNetworkDisconnect() {
        val network = NetworkService()
        val node0 = createNodeForConnecting(network, "node0", 2)
        val node1 = createNodeForConnecting(network, "node1", 2)
        val node2 = createNodeForConnecting(network, "node2", 2)
        val node3 = createNodeForConnecting(network, "node3", 2)
        val node4 = createNodeForConnecting(network, "node4", 2)
        val node5 = createNodeForConnecting(network, "node5", 2)

        // Connect up a network so we can disconnect it :)
        network.connect(node0.getTerminal(1)!!, "cn_1")
        network.connect(node1.getTerminal(1)!!, "cn_1")
        network.connect(node2.getTerminal(1)!!, "cn_1")
        network.connect(node3.getTerminal(1)!!, "cn_2")
        network.connect(node4.getTerminal(1)!!, "cn_2")
        network.connect(node5.getTerminal(1)!!, "cn_2")

        var connectivityNode = node0.getTerminal(1)!!.connectivityNode!!
        assertThat(connectivityNode, notNullValue())
        assertThat(network.containsConnectivityNode(connectivityNode.mRID), equalTo(true))
        assertThat(connectivityNode.numTerminals(), equalTo(3))

        network.disconnect(node0.getTerminal(1)!!)
        assertThat(node0.getTerminal(1)!!.connectivityNode, nullValue())
        assertThat(connectivityNode.numTerminals(), equalTo(2))

        network.disconnect(node1.getTerminal(1)!!)
        network.disconnect(node2.getTerminal(1)!!)
        assertThat(network.containsConnectivityNode(connectivityNode.mRID), equalTo(false))
        assertThat(connectivityNode.numTerminals(), equalTo(0))

        connectivityNode = node3.getTerminal(1)!!.connectivityNode!!
        assertThat(connectivityNode, notNullValue())
        assertThat(network.containsConnectivityNode(connectivityNode.mRID), equalTo(true))
        assertThat(connectivityNode.numTerminals(), equalTo(3))
        network.disconnect(connectivityNode.mRID)
        assertThat(node3.getTerminal(1)!!.connectivityNode, nullValue())
        assertThat(network.containsConnectivityNode(connectivityNode.mRID), equalTo(false))
        assertThat(connectivityNode.numTerminals(), equalTo(0))
    }

    @Test
    internal fun testNetworkConnectedTerminals() {
        val network = TestNetworks.getNetwork(1)
        val node0 = network.get<ConductingEquipment>("node0")!!
        val node3 = network.get<ConductingEquipment>("node3")!!
        val node5 = network.get<ConductingEquipment>("node5")!!
        val acLineSegment0 = network.get<AcLineSegment>("acLineSegment0")!!
        val acLineSegment1 = network.get<AcLineSegment>("acLineSegment1")!!
        val acLineSegment2 = network.get<AcLineSegment>("acLineSegment2")!!
        val acLineSegment3 = network.get<AcLineSegment>("acLineSegment3")!!
        val acLineSegment4 = network.get<AcLineSegment>("acLineSegment4")!!
        val acLineSegment5 = network.get<AcLineSegment>("acLineSegment5")!!
        val acLineSegment9 = network.get<AcLineSegment>("acLineSegment9")!!
        val acLineSegment11 = network.get<AcLineSegment>("acLineSegment11")!!
        val expectedTerminals: MutableList<ConnectivityResult> = ArrayList()

        expectedTerminals.add(ConnectivityResult.between(node0.getTerminal(1)!!, acLineSegment0.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.ABCN)))
        assertThat(connectedTerminals(node0.getTerminal(1)!!, PhaseCode.ABCN), containsInAnyOrder(*expectedTerminals.toTypedArray()))
        assertThat(connectedTerminals(node0.getTerminal(1)!!), containsInAnyOrder(*expectedTerminals.toTypedArray()))
        assertThat(connectedTerminals(node0.getTerminal(1)!!, PhaseCode.ABCN.toSet()), containsInAnyOrder(*expectedTerminals.toTypedArray()))

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
                node3.getTerminal(2)!!,
                acLineSegment5.getTerminal(1)!!,
                PhasePathSet.from(PhaseCode.A).to(PhaseCode.X)
            )
        )
        assertThat(connectedTerminals(node3.getTerminal(2)!!, PhaseCode.A), containsInAnyOrder<Any>(*expectedTerminals.toTypedArray()))

        expectedTerminals.clear()
        expectedTerminals.add(
            ConnectivityResult.between(
                node3.getTerminal(2)!!,
                acLineSegment5.getTerminal(1)!!,
                PhasePathSet.from(PhaseCode.B).to(PhaseCode.Y)
            )
        )
        assertThat(connectedTerminals(node3.getTerminal(2)!!, PhaseCode.B), containsInAnyOrder<Any>(*expectedTerminals.toTypedArray()))
        assertThat(connectedTerminals(node5.getTerminal(3)!!, PhaseCode.X), empty())

        expectedTerminals.clear()
        expectedTerminals.add(ConnectivityResult.between(node5.getTerminal(3)!!, acLineSegment11.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.Y)))
        assertThat(connectedTerminals(node5.getTerminal(3)!!, PhaseCode.Y), containsInAnyOrder<Any>(*expectedTerminals.toTypedArray()))
    }

    @Test
    internal fun testNetworkConnectedEquipment() {
        val network = TestNetworks.getNetwork(1)
        val node0 = network.get<ConductingEquipment>("node0")!!
        val node1 = network.get<ConductingEquipment>("node1")!!
        val node3 = network.get<ConductingEquipment>("node3")!!
        val node4 = network.get<ConductingEquipment>("node4")!!
        val node5 = network.get<ConductingEquipment>("node5")!!
        val node6 = network.get<ConductingEquipment>("node6")!!
        val node7 = network.get<ConductingEquipment>("node7")!!
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
        val expectedAssets: MutableList<ConnectivityResult> = ArrayList()

        expectedAssets.add(ConnectivityResult.between(acLineSegment0.getTerminal(1)!!, node0.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.ABCN)))
        expectedAssets.add(ConnectivityResult.between(acLineSegment0.getTerminal(2)!!, acLineSegment1.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.ABCN)))
        expectedAssets.add(ConnectivityResult.between(acLineSegment0.getTerminal(2)!!, acLineSegment4.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.AB)))
        assertThat(connectedEquipment(acLineSegment0, PhaseCode.ABCN), containsInAnyOrder(*expectedAssets.toTypedArray()))
        assertThat(connectedEquipment(acLineSegment0), containsInAnyOrder(*expectedAssets.toTypedArray()))
        assertThat(connectedEquipment(acLineSegment0, PhaseCode.ABCN.toSet()), containsInAnyOrder(*expectedAssets.toTypedArray()))

        expectedAssets.clear()
        expectedAssets.add(ConnectivityResult.between(node4.getTerminal(1)!!, acLineSegment5.getTerminal(2)!!, PhasePathSet.implicit(PhaseCode.XY)))
        expectedAssets.add(ConnectivityResult.between(node4.getTerminal(2)!!, acLineSegment6.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.XY)))
        expectedAssets.add(ConnectivityResult.between(node4.getTerminal(3)!!, acLineSegment10.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.X)))
        assertThat(connectedEquipment(node4, PhaseCode.XY), containsInAnyOrder(*expectedAssets.toTypedArray()))

        expectedAssets.clear()
        expectedAssets.add(ConnectivityResult.between(node5.getTerminal(1)!!, acLineSegment6.getTerminal(2)!!, PhasePathSet.implicit(PhaseCode.XY)))
        expectedAssets.add(ConnectivityResult.between(node5.getTerminal(2)!!, acLineSegment7.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.XY)))
        expectedAssets.add(ConnectivityResult.between(node5.getTerminal(3)!!, acLineSegment11.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.Y)))
        assertThat(connectedEquipment(node5, PhaseCode.XY), containsInAnyOrder(*expectedAssets.toTypedArray()))

        expectedAssets.clear()
        expectedAssets.add(ConnectivityResult.between(acLineSegment2.getTerminal(1)!!, node1.getTerminal(2)!!, PhasePathSet.implicit(PhaseCode.ABCN)))
        expectedAssets.add(ConnectivityResult.between(acLineSegment2.getTerminal(2)!!, acLineSegment3.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.ABCN)))
        expectedAssets.add(ConnectivityResult.between(acLineSegment2.getTerminal(2)!!, acLineSegment9.getTerminal(2)!!, PhasePathSet.implicit(PhaseCode.BC)))
        assertThat(connectedEquipment(acLineSegment2, PhaseCode.ABCN), containsInAnyOrder(*expectedAssets.toTypedArray()))

        expectedAssets.clear()
        expectedAssets.add(ConnectivityResult.between(acLineSegment9.getTerminal(1)!!, node7.getTerminal(2)!!, PhasePathSet.implicit(PhaseCode.BC)))
        expectedAssets.add(ConnectivityResult.between(acLineSegment9.getTerminal(2)!!, acLineSegment2.getTerminal(2)!!, PhasePathSet.implicit(PhaseCode.BC)))
        expectedAssets.add(ConnectivityResult.between(acLineSegment9.getTerminal(2)!!, acLineSegment3.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.BC)))
        assertThat(connectedEquipment(acLineSegment9, PhaseCode.BC), containsInAnyOrder(*expectedAssets.toTypedArray()))

        expectedAssets.clear()
        expectedAssets.add(
            ConnectivityResult.between(
                node7.getTerminal(1)!!,
                acLineSegment8.getTerminal(2)!!,
                PhasePathSet.from(PhaseCode.BC).to(PhaseCode.XY)
            )
        )
        expectedAssets.add(ConnectivityResult.between(node7.getTerminal(2)!!, acLineSegment9.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.BC)))
        assertThat(connectedEquipment(node7, PhaseCode.BC), containsInAnyOrder(*expectedAssets.toTypedArray()))

        expectedAssets.clear()
        expectedAssets.add(ConnectivityResult.between(acLineSegment8.getTerminal(1)!!, node6.getTerminal(2)!!, PhasePathSet.implicit(PhaseCode.XY)))
        expectedAssets.add(
            ConnectivityResult.between(
                acLineSegment8.getTerminal(2)!!,
                node7.getTerminal(1)!!,
                PhasePathSet.from(PhaseCode.XY).to(PhaseCode.BC)
            )
        )
        assertThat(connectedEquipment(acLineSegment8, PhaseCode.XY), containsInAnyOrder(*expectedAssets.toTypedArray()))

        expectedAssets.clear()
        expectedAssets.add(ConnectivityResult.between(node3.getTerminal(1)!!, acLineSegment4.getTerminal(2)!!, PhasePathSet.implicit(PhaseCode.A)))
        expectedAssets.add(ConnectivityResult.between(node3.getTerminal(2)!!, acLineSegment5.getTerminal(1)!!, PhasePathSet.from(PhaseCode.A).to(PhaseCode.X)))
        assertThat(connectedEquipment(node3, PhaseCode.A), containsInAnyOrder(*expectedAssets.toTypedArray()))

        expectedAssets.clear()
        expectedAssets.add(ConnectivityResult.between(node3.getTerminal(1)!!, acLineSegment4.getTerminal(2)!!, PhasePathSet.implicit(PhaseCode.B)))
        expectedAssets.add(ConnectivityResult.between(node3.getTerminal(2)!!, acLineSegment5.getTerminal(1)!!, PhasePathSet.from(PhaseCode.B).to(PhaseCode.Y)))
        assertThat(connectedEquipment(node3, PhaseCode.B), containsInAnyOrder(*expectedAssets.toTypedArray()))

        expectedAssets.clear()
        expectedAssets.add(ConnectivityResult.between(acLineSegment5.getTerminal(1)!!, node3.getTerminal(2)!!, PhasePathSet.from(PhaseCode.X).to(PhaseCode.A)))
        expectedAssets.add(ConnectivityResult.between(acLineSegment5.getTerminal(2)!!, node4.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.X)))
        assertThat(connectedEquipment(acLineSegment5, PhaseCode.X), containsInAnyOrder(*expectedAssets.toTypedArray()))

        expectedAssets.clear()
        expectedAssets.add(ConnectivityResult.between(acLineSegment5.getTerminal(1)!!, node3.getTerminal(2)!!, PhasePathSet.from(PhaseCode.Y).to(PhaseCode.B)))
        expectedAssets.add(ConnectivityResult.between(acLineSegment5.getTerminal(2)!!, node4.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.Y)))
        assertThat(connectedEquipment(acLineSegment5, PhaseCode.Y), containsInAnyOrder(*expectedAssets.toTypedArray()))
    }

    @Test
    internal fun testNetworkConnected2() {
        val network = TestNetworks.getNetwork(2)

        val node1 = network.get<ConductingEquipment>("node1")!!
        val acLineSegment1 = network.get<AcLineSegment>("acLineSegment1")!!
        val acLineSegment2 = network.get<AcLineSegment>("acLineSegment2")!!
        val acLineSegment3 = network.get<AcLineSegment>("acLineSegment3")!!
        val acLineSegment4 = network.get<AcLineSegment>("acLineSegment4")!!
        val acLineSegment5 = network.get<AcLineSegment>("acLineSegment5")!!
        val expectedAssets: MutableList<ConnectivityResult> = ArrayList()

        expectedAssets.add(ConnectivityResult.between(node1.getTerminal(1)!!, acLineSegment1.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.ABCN)))
        expectedAssets.add(ConnectivityResult.between(node1.getTerminal(2)!!, acLineSegment2.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.A)))
        expectedAssets.add(ConnectivityResult.between(node1.getTerminal(3)!!, acLineSegment3.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.B)))
        expectedAssets.add(ConnectivityResult.between(node1.getTerminal(4)!!, acLineSegment4.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.N)))
        expectedAssets.add(ConnectivityResult.between(node1.getTerminal(5)!!, acLineSegment5.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.CN)))
        assertThat(connectedEquipment(node1, PhaseCode.ABCN), containsInAnyOrder(*expectedAssets.toTypedArray()))

        expectedAssets.clear()
        expectedAssets.add(ConnectivityResult.between(node1.getTerminal(1)!!, acLineSegment1.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.AC)))
        expectedAssets.add(ConnectivityResult.between(node1.getTerminal(2)!!, acLineSegment2.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.A)))
        expectedAssets.add(ConnectivityResult.between(node1.getTerminal(5)!!, acLineSegment5.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.C)))
        assertThat(connectedEquipment(node1, PhaseCode.AC), containsInAnyOrder(*expectedAssets.toTypedArray()))
    }

    @Test
    internal fun testNetworkConnected3() {
        val network = TestNetworks.getNetwork(3)
        val acLineSegment1 = network.get<AcLineSegment>("acLineSegment1")!!
        val acLineSegment2 = network.get<AcLineSegment>("acLineSegment2")!!
        val acLineSegment3 = network.get<AcLineSegment>("acLineSegment3")!!
        val acLineSegment4 = network.get<AcLineSegment>("acLineSegment4")!!
        val expectedAssets: MutableList<ConnectivityResult> = ArrayList()

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

    private fun validateReplacementNetwork(network: NetworkService, included: ConductingEquipment, excluded: ConductingEquipment) {
        assertThat(network[included.mRID], equalTo(included))
        included.terminals.forEach { t ->
            assertThat(network[t.mRID], equalTo(t))
        }

        var terminalIndex = 1
        var terminal = included.getTerminal(terminalIndex++)!!
        assertThat(
            connectedTerminals(terminal, PhaseCode.A),
            containsInAnyOrder(ConnectivityResult.between(terminal, network.get<AcLineSegment>("acls1")!!.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.A)))
        )

        if (included.numTerminals() > 2)
            assertThat(included.getTerminal(terminalIndex++)!!.isConnected(), equalTo(false))

        terminal = included.getTerminal(terminalIndex++)!!
        assertThat(
            connectedTerminals(terminal, PhaseCode.A),
            containsInAnyOrder(ConnectivityResult.between(terminal, network.get<Junction>("j2")!!.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.A)))
        )

        while (terminalIndex < included.numTerminals())
            assertThat(included.getTerminal(terminalIndex++)!!.isConnected(), equalTo(false))

        assertThat(network[excluded.mRID], nullValue())
        excluded.terminals.forEach { t ->
            assertThat(network[t.mRID], nullValue())
        }
    }

    private fun validateReplacementWithMultiDisconnectNetwork(network: NetworkService, included: ConductingEquipment, excluded: ConductingEquipment) {
        assertThat(network[included.mRID], equalTo(included))
        included.terminals.forEach { t ->
            assertThat(network[t.mRID], equalTo(t))
        }

        var terminalIndex = 1
        var terminal = included.getTerminal(terminalIndex++)!!  // 2
        assertThat(
            connectedTerminals(terminal, PhaseCode.A),
            containsInAnyOrder(ConnectivityResult.between(terminal, network.get<AcLineSegment>("acls1")!!.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.A)))
        )


        if (included.numTerminals() > 3) {
            assertThat(included.getTerminal(terminalIndex++)!!.isConnected(), equalTo(false)) // 3
        }

        terminal = included.getTerminal(terminalIndex++)!!
        assertThat(
            connectedTerminals(terminal, PhaseCode.A),
            containsInAnyOrder(ConnectivityResult.between(terminal, network.get<Junction>("j2")!!.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.A)))
        )

        if (included.numTerminals() > 3) {
            assertThat(included.getTerminal(terminalIndex++)!!.isConnected(), equalTo(false)) // 4
        }

        terminal = included.getTerminal(terminalIndex++)!!
        assertThat(
            connectedTerminals(terminal, PhaseCode.A),
            containsInAnyOrder(ConnectivityResult.between(terminal, network.get<AcLineSegment>("acls2")!!.getTerminal(1)!!, PhasePathSet.implicit(PhaseCode.A)))
        )

        while (terminalIndex < included.numTerminals())
            assertThat(included.getTerminal(terminalIndex++)!!.isConnected(), equalTo(false))

        assertThat(network[excluded.mRID], nullValue())
        excluded.terminals.forEach { t ->
            assertThat(network[t.mRID], nullValue())
        }
    }

    private fun PhaseCode.toSet(): Set<SinglePhaseKind> = singlePhases().toSet()

    private class PhasePathSet private constructor(private val fromPhases: PhaseCode) {
        fun to(toPhases: PhaseCode): Set<NominalPhasePath> {
            return IntStream.range(0, fromPhases.singlePhases().size)
                .mapToObj { i: Int -> NominalPhasePath(fromPhases.singlePhases()[i], toPhases.singlePhases()[i]) }
                .collect(Collectors.toSet())
        }

        companion object {
            fun from(fromPhases: PhaseCode): PhasePathSet {
                return PhasePathSet(fromPhases)
            }

            fun implicit(phases: PhaseCode) = phases.singlePhases()
                .asSequence()
                .map { NominalPhasePath(it, it) }
                .toSet()
        }
    }
}
