/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.network

import com.zepben.cimbend.cim.iec61968.assetinfo.CableInfo
import com.zepben.cimbend.cim.iec61968.assetinfo.OverheadWireInfo
import com.zepben.cimbend.cim.iec61968.assets.AssetOwner
import com.zepben.cimbend.cim.iec61968.assets.Pole
import com.zepben.cimbend.cim.iec61968.assets.Streetlight
import com.zepben.cimbend.cim.iec61968.common.Location
import com.zepben.cimbend.cim.iec61968.common.Organisation
import com.zepben.cimbend.cim.iec61968.metering.Meter
import com.zepben.cimbend.cim.iec61968.metering.UsagePoint
import com.zepben.cimbend.cim.iec61968.operations.OperationalRestriction
import com.zepben.cimbend.cim.iec61970.base.auxiliaryequipment.FaultIndicator
import com.zepben.cimbend.cim.iec61970.base.core.*
import com.zepben.cimbend.cim.iec61970.base.meas.*
import com.zepben.cimbend.cim.iec61970.base.scada.RemoteControl
import com.zepben.cimbend.cim.iec61970.base.scada.RemoteSource
import com.zepben.cimbend.cim.iec61970.base.wires.*
import com.zepben.cimbend.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.cimbend.cim.iec61970.infiec61970.feeder.Loop
import com.zepben.cimbend.common.BaseService
import com.zepben.cimbend.network.tracing.ConnectivityResult

/**
 * Maintains an in-memory model of the network.
 */
class NetworkService : BaseService("network") {
    private enum class ProcessStatus {
        PROCESSED, SKIPPED, INVALID
    }

    private var autoConnectivityNodeIndex = 0

    @Suppress("UNCHECKED_CAST")
    private val _connectivityNodes: MutableMap<String, ConnectivityNode> = objectsByType.computeIfAbsent(ConnectivityNode::class) {
        mutableMapOf()
    } as MutableMap<String, ConnectivityNode>

    private val _measurements: MutableMap<String, MutableList<Measurement>> = mutableMapOf()

    fun add(acLineSegment: AcLineSegment): Boolean = super.add(acLineSegment)
    fun remove(acLineSegment: AcLineSegment): Boolean = super.remove(acLineSegment)

    fun add(assetOwner: AssetOwner): Boolean = super.add(assetOwner)
    fun remove(assetOwner: AssetOwner): Boolean = super.remove(assetOwner)

    fun add(baseVoltage: BaseVoltage): Boolean = super.add(baseVoltage)
    fun remove(baseVoltage: BaseVoltage): Boolean = super.remove(baseVoltage)

    fun add(breaker: Breaker): Boolean = super.add(breaker)
    fun remove(breaker: Breaker): Boolean = super.remove(breaker)

    fun add(cableInfo: CableInfo): Boolean = super.add(cableInfo)
    fun remove(cableInfo: CableInfo): Boolean = super.remove(cableInfo)

    fun add(circuit: Circuit): Boolean = super.add(circuit)
    fun remove(circuit: Circuit): Boolean = super.remove(circuit)

    fun add(connectivityNode: ConnectivityNode): Boolean = super.add(connectivityNode)
    fun remove(connectivityNode: ConnectivityNode): Boolean = super.remove(connectivityNode)

    fun add(disconnector: Disconnector): Boolean = super.add(disconnector)
    fun remove(disconnector: Disconnector): Boolean = super.remove(disconnector)

    fun add(energyConsumer: EnergyConsumer): Boolean = super.add(energyConsumer)
    fun remove(energyConsumer: EnergyConsumer): Boolean = super.remove(energyConsumer)

    fun add(energyConsumerPhase: EnergyConsumerPhase): Boolean = super.add(energyConsumerPhase)
    fun remove(energyConsumerPhase: EnergyConsumerPhase): Boolean = super.remove(energyConsumerPhase)

    fun add(energySource: EnergySource): Boolean = super.add(energySource)
    fun remove(energySource: EnergySource): Boolean = super.remove(energySource)

    fun add(energySourcePhase: EnergySourcePhase): Boolean = super.add(energySourcePhase)
    fun remove(energySourcePhase: EnergySourcePhase): Boolean = super.remove(energySourcePhase)

    fun add(faultIndicator: FaultIndicator): Boolean = super.add(faultIndicator)
    fun remove(faultIndicator: FaultIndicator): Boolean = super.remove(faultIndicator)

    fun add(feeder: Feeder): Boolean = super.add(feeder)
    fun remove(feeder: Feeder): Boolean = super.remove(feeder)

    fun add(fuse: Fuse): Boolean = super.add(fuse)
    fun remove(fuse: Fuse): Boolean = super.remove(fuse)

    fun add(geographicalRegion: GeographicalRegion): Boolean = super.add(geographicalRegion)
    fun remove(geographicalRegion: GeographicalRegion): Boolean = super.remove(geographicalRegion)

    fun add(jumper: Jumper): Boolean = super.add(jumper)
    fun remove(jumper: Jumper): Boolean = super.remove(jumper)

    fun add(junction: Junction): Boolean = super.add(junction)
    fun remove(junction: Junction): Boolean = super.remove(junction)

    fun add(linearShuntCompensator: LinearShuntCompensator): Boolean = super.add(linearShuntCompensator)
    fun remove(linearShuntCompensator: LinearShuntCompensator): Boolean = super.remove(linearShuntCompensator)

    fun add(location: Location): Boolean = super.add(location)
    fun remove(location: Location): Boolean = super.remove(location)

    fun add(loop: Loop): Boolean = super.add(loop)
    fun remove(loop: Loop): Boolean = super.remove(loop)

    fun add(meter: Meter): Boolean = super.add(meter)
    fun remove(meter: Meter): Boolean = super.remove(meter)

    fun add(operationalRestriction: OperationalRestriction): Boolean = super.add(operationalRestriction)
    fun remove(operationalRestriction: OperationalRestriction): Boolean = super.remove(operationalRestriction)

    fun add(organisation: Organisation): Boolean = super.add(organisation)
    fun remove(organisation: Organisation): Boolean = super.remove(organisation)

    fun add(overheadWireInfo: OverheadWireInfo): Boolean = super.add(overheadWireInfo)
    fun remove(overheadWireInfo: OverheadWireInfo): Boolean = super.remove(overheadWireInfo)

    fun add(perLengthSequenceImpedance: PerLengthSequenceImpedance): Boolean = super.add(perLengthSequenceImpedance)
    fun remove(perLengthSequenceImpedance: PerLengthSequenceImpedance): Boolean = super.remove(perLengthSequenceImpedance)

    fun add(pole: Pole): Boolean = super.add(pole)
    fun remove(pole: Pole): Boolean = super.remove(pole)

    fun add(powerTransformer: PowerTransformer): Boolean = super.add(powerTransformer)
    fun remove(powerTransformer: PowerTransformer): Boolean = super.remove(powerTransformer)

    fun add(powerTransformerEnd: PowerTransformerEnd): Boolean = super.add(powerTransformerEnd)
    fun remove(powerTransformerEnd: PowerTransformerEnd): Boolean = super.remove(powerTransformerEnd)

    fun add(ratioTapChanger: RatioTapChanger): Boolean = super.add(ratioTapChanger)
    fun remove(ratioTapChanger: RatioTapChanger): Boolean = super.remove(ratioTapChanger)

    fun add(recloser: Recloser): Boolean = super.add(recloser)
    fun remove(recloser: Recloser): Boolean = super.remove(recloser)

    fun add(site: Site): Boolean = super.add(site)
    fun remove(site: Site): Boolean = super.remove(site)

    fun add(streetlight: Streetlight): Boolean = super.add(streetlight)
    fun remove(streetlight: Streetlight): Boolean = super.remove(streetlight)

    fun add(subGeographicalRegion: SubGeographicalRegion): Boolean = super.add(subGeographicalRegion)
    fun remove(subGeographicalRegion: SubGeographicalRegion): Boolean = super.remove(subGeographicalRegion)

    fun add(substation: Substation): Boolean = super.add(substation)
    fun remove(substation: Substation): Boolean = super.remove(substation)

    fun add(terminal: Terminal): Boolean = super.add(terminal)
    fun remove(terminal: Terminal): Boolean = super.remove(terminal)

    fun add(usagePoint: UsagePoint): Boolean = super.add(usagePoint)
    fun remove(usagePoint: UsagePoint): Boolean = super.remove(usagePoint)

    fun add(control: Control): Boolean = super.add(control)
    fun remove(control: Control): Boolean = super.remove(control)

    fun add(remoteControl: RemoteControl): Boolean = super.add(remoteControl)
    fun remove(remoteControl: RemoteControl): Boolean = super.remove(remoteControl)

    fun add(remoteSource: RemoteSource): Boolean = super.add(remoteSource)
    fun remove(remoteSource: RemoteSource): Boolean = super.remove(remoteSource)

    private fun indexMeasurement(measurement: Measurement, mRID: String?): Boolean {
        if (mRID == null || mRID.isEmpty())
            return true

        _measurements[mRID]?.let { measurements ->
            measurements.find { m -> m.mRID == measurement.mRID } ?: run {
                measurements.add(measurement)
                return true
            }
        } ?: run {
            _measurements[mRID] = mutableListOf(measurement)
            return true
        }

        return false
    }

    private fun indexMeasurement(measurement: Measurement) = indexMeasurement(measurement, measurement.terminalMRID) && indexMeasurement(measurement, measurement.powerSystemResourceMRID)

    private fun removeMeasurementIndex(measurement: Measurement): Set<Measurement>? {
        val termMeas = _measurements.remove(measurement.terminalMRID)
        val psrMeas = _measurements.remove(measurement.powerSystemResourceMRID)
        termMeas?.let {
            if (psrMeas != null)
                return it.union(psrMeas)
            return it.toSet()
        } ?: return psrMeas?.toSet()
    }

    fun add(analog: Analog): Boolean = indexMeasurement(analog) && super.add(analog)

    fun remove(analog: Analog): Boolean {
        removeMeasurementIndex(analog)
        return super.remove(analog)
    }

    fun getAnalog(mRID: String?): List<Analog>? = _measurements[mRID]?.filterIsInstance<Analog>()

    fun add(accumulator: Accumulator): Boolean = indexMeasurement(accumulator) && super.add(accumulator)

    fun remove(accumulator: Accumulator): Boolean {
        removeMeasurementIndex(accumulator)
        return super.remove(accumulator)
    }

    fun getAccumulator(mRID: String?): List<Accumulator>? = _measurements[mRID]?.filterIsInstance<Accumulator>()

    fun add(discrete: Discrete): Boolean = indexMeasurement(discrete) && super.add(discrete)

    fun remove(discrete: Discrete): Boolean {
        removeMeasurementIndex(discrete)
        return super.remove(discrete)
    }

    fun getDiscrete(mRID: String?): List<Discrete>? = _measurements[mRID]?.filterIsInstance<Discrete>()

    /**
     * Get a connectivityNode by its mRID, or create it if it doesn't already exist in the network.
     * @param mRID The mRID of the [ConnectivityNode]
     * @return The [ConnectivityNode]
     */
    fun getOrPutConnectivityNode(mRID: String): ConnectivityNode {
        return _connectivityNodes.getOrPut(mRID) { createConnectivityNode(mRID) }
    }

    fun connect(terminal1: Terminal, terminal2: Terminal): Boolean {
        val status = attemptToReuseConnection(terminal1, terminal2)
        if (status == ProcessStatus.PROCESSED) return true
        else if (status == ProcessStatus.INVALID) return false

        val connectivityNode = _connectivityNodes.computeIfAbsent(generateConnectivityNodeId()) { mRID: String -> createConnectivityNode(mRID) }
        connect(terminal2, connectivityNode)
        connect(terminal1, connectivityNode)

        return true
    }

    fun connect(terminal: Terminal, connectivityNodeId: String?): Boolean {
        if (connectivityNodeId.isNullOrBlank()) return false

        var connectivityNode = terminal.connectivityNode
        if (connectivityNode != null) return connectivityNodeId == connectivityNode.mRID

        connectivityNode = _connectivityNodes.computeIfAbsent(connectivityNodeId!!) { mRID: String -> createConnectivityNode(mRID) }
        connect(terminal, connectivityNode)
        return true
    }

    fun disconnect(terminal: Terminal) {
        val connectivityNode = terminal.connectivityNode ?: return
        connectivityNode.removeTerminal(terminal)
        terminal.disconnect()

        if (connectivityNode.numTerminals() == 0) _connectivityNodes.remove(connectivityNode.mRID)
    }

    fun disconnect(connectivityNodeId: String) {
        val connectivityNode = _connectivityNodes[connectivityNodeId] ?: return

        connectivityNode.terminals.forEach { it.disconnect() }
        connectivityNode.clearTerminals()

        _connectivityNodes.remove(connectivityNode.mRID)
    }

    private fun createConnectivityNode(mRID: String = ""): ConnectivityNode {
        return ConnectivityNode(mRID)
    }

    //
    // NOTE: This function is package private to allow test cases to check for connectivity nodes.
    //
    fun containsConnectivityNode(connectivityNode: String): Boolean {
        return _connectivityNodes.containsKey(connectivityNode)
    }

    private fun attemptToReuseConnection(terminal1: Terminal, terminal2: Terminal): ProcessStatus {
        val connectivityNode1 = terminal1.connectivityNode
        val connectivityNode2 = terminal2.connectivityNode
        if (connectivityNode1 != null) {
            if (connectivityNode2 != null) {
                if (connectivityNode1 == connectivityNode2) return ProcessStatus.PROCESSED
            } else if (connect(terminal2, connectivityNode1.mRID)) return ProcessStatus.PROCESSED
            return ProcessStatus.INVALID
        } else if (connectivityNode2 != null) {
            return if (connect(terminal1, connectivityNode2.mRID)) ProcessStatus.PROCESSED else ProcessStatus.INVALID
        }
        return ProcessStatus.SKIPPED
    }

    private fun generateConnectivityNodeId(): String {
        var id: String
        do {
            id = "generated_cn_" + autoConnectivityNodeIndex++
        } while (_connectivityNodes.containsKey(id))
        return id
    }

    private fun connect(terminal: Terminal, connectivityNode: ConnectivityNode) {
        terminal.connect(connectivityNode)
        connectivityNode.addTerminal(terminal)
    }

    companion object {
        @JvmStatic
        fun connectedEquipment(conductingEquipment: ConductingEquipment, phaseCode: PhaseCode): List<ConnectivityResult> {
            return connectedEquipment(
                conductingEquipment,
                phaseCode.singlePhases().toSet()
            )
        }

        @JvmStatic
        @JvmOverloads
        fun connectedEquipment(conductingEquipment: ConductingEquipment, phases: Set<SinglePhaseKind>? = null): List<ConnectivityResult> {
            val results = mutableListOf<ConnectivityResult>()
            conductingEquipment.terminals.forEach { terminal ->
                results.addAll(connectedTerminals(terminal, phases ?: terminal.phases.singlePhases()))
            }
            return results
        }

        @JvmStatic
        fun connectedTerminals(terminal: Terminal, phaseCode: PhaseCode): List<ConnectivityResult> {
            return connectedTerminals(terminal, phaseCode.singlePhases())
        }

        @JvmStatic
        @JvmOverloads
        fun connectedTerminals(terminal: Terminal, phases: Iterable<SinglePhaseKind> = terminal.phases.singlePhases()): List<ConnectivityResult> {
            val tracePhases = phases.intersect(terminal.phases.singlePhases())
            val connectivityNode = terminal.connectivityNode ?: return emptyList()

            val results = mutableListOf<ConnectivityResult>()
            connectivityNode.terminals.forEach { connectedTerminal ->
                if (connectedTerminal != terminal) {
                    val cr = terminalConnectivity(terminal, connectedTerminal, tracePhases)
                    if (cr.nominalPhasePaths().isNotEmpty())
                        results.add(cr)
                }
            }
            return results
        }

        private fun terminalConnectivity(terminal: Terminal, connectedTerminal: Terminal, phases: Set<SinglePhaseKind>): ConnectivityResult {
            val connectivityResult = ConnectivityResult.between(terminal, connectedTerminal)
            phases
                .asSequence()
                .filter { connectedTerminal.phases.singlePhases().contains(it) }
                .forEach { connectivityResult.addNominalPhasePath(it, it) }

            if (connectivityResult.fromNominalPhases().isEmpty()) {
                val xyPhases = phases
                    .asSequence()
                    .filter { (it == SinglePhaseKind.X) || (it == SinglePhaseKind.Y) }
                    .toSet()

                val connectedXyPhases = connectedTerminal.phases.singlePhases()
                    .asSequence()
                    .filter { (it == SinglePhaseKind.X) || (it == SinglePhaseKind.Y) }
                    .toSet()

                tryProcessXyPhases(
                    terminal,
                    connectedTerminal,
                    phases,
                    xyPhases,
                    connectedXyPhases,
                    connectivityResult
                )
            }

            return connectivityResult
        }

        private fun tryProcessXyPhases(
            terminal: Terminal,
            connectedTerminal: Terminal,
            phases: Set<SinglePhaseKind>,
            xyPhases: Set<SinglePhaseKind>,
            connectedXyPhases: Set<SinglePhaseKind>,
            connectivityResult: ConnectivityResult
        ) {
            if ((xyPhases.isEmpty() && connectedXyPhases.isEmpty()) || (xyPhases.isNotEmpty() && connectedXyPhases.isNotEmpty()))
                return

            xyPhases.forEach {
                val index = terminal.phases.singlePhases().indexOf(it)
                if (index < connectedTerminal.phases.singlePhases().size)
                    connectivityResult.addNominalPhasePath(it, connectedTerminal.phases.singlePhases()[index])
            }

            connectedXyPhases.forEach {
                val index = connectedTerminal.phases.singlePhases().indexOf(it)
                if (index < terminal.phases.singlePhases().size) {
                    val phase = terminal.phases.singlePhases()[index]
                    if (phases.contains(phase))
                        connectivityResult.addNominalPhasePath(phase, it)
                }
            }
        }
    }
}

