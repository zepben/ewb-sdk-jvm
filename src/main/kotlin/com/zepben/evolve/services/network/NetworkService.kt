/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network

import com.zepben.evolve.cim.iec61968.assetinfo.*
import com.zepben.evolve.cim.iec61968.assets.AssetOwner
import com.zepben.evolve.cim.iec61968.assets.Pole
import com.zepben.evolve.cim.iec61968.assets.Streetlight
import com.zepben.evolve.cim.iec61968.common.Location
import com.zepben.evolve.cim.iec61968.common.Organisation
import com.zepben.evolve.cim.iec61968.infiec61968.infassetinfo.CurrentTransformerInfo
import com.zepben.evolve.cim.iec61968.infiec61968.infassetinfo.PotentialTransformerInfo
import com.zepben.evolve.cim.iec61968.metering.Meter
import com.zepben.evolve.cim.iec61968.metering.UsagePoint
import com.zepben.evolve.cim.iec61968.operations.OperationalRestriction
import com.zepben.evolve.cim.iec61970.base.auxiliaryequipment.CurrentTransformer
import com.zepben.evolve.cim.iec61970.base.auxiliaryequipment.FaultIndicator
import com.zepben.evolve.cim.iec61970.base.auxiliaryequipment.PotentialTransformer
import com.zepben.evolve.cim.iec61970.base.core.*
import com.zepben.evolve.cim.iec61970.base.equivalents.EquivalentBranch
import com.zepben.evolve.cim.iec61970.base.meas.*
import com.zepben.evolve.cim.iec61970.base.scada.RemoteControl
import com.zepben.evolve.cim.iec61970.base.scada.RemoteSource
import com.zepben.evolve.cim.iec61970.base.wires.*
import com.zepben.evolve.cim.iec61970.base.wires.generation.production.BatteryUnit
import com.zepben.evolve.cim.iec61970.base.wires.generation.production.PhotoVoltaicUnit
import com.zepben.evolve.cim.iec61970.base.wires.generation.production.PowerElectronicsWindUnit
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Loop
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.LvFeeder
import com.zepben.evolve.services.common.BaseService
import com.zepben.evolve.services.network.tracing.connectivity.ConnectivityResult
import com.zepben.evolve.services.network.tracing.connectivity.TerminalConnectivityConnected
import kotlin.reflect.KClass

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

    // #######################
    // # IEC61968 ASSET INFO #
    // #######################

    fun add(cableInfo: CableInfo): Boolean = super.add(cableInfo)
    fun remove(cableInfo: CableInfo): Boolean = super.remove(cableInfo)

    fun add(noLoadTest: NoLoadTest): Boolean = super.add(noLoadTest)
    fun remove(noLoadTest: NoLoadTest): Boolean = super.remove(noLoadTest)

    fun add(openCircuitTest: OpenCircuitTest): Boolean = super.add(openCircuitTest)
    fun remove(openCircuitTest: OpenCircuitTest): Boolean = super.remove(openCircuitTest)

    fun add(overheadWireInfo: OverheadWireInfo): Boolean = super.add(overheadWireInfo)
    fun remove(overheadWireInfo: OverheadWireInfo): Boolean = super.remove(overheadWireInfo)

    fun add(powerTransformerInfo: PowerTransformerInfo): Boolean = super.add(powerTransformerInfo)
    fun remove(powerTransformerInfo: PowerTransformerInfo): Boolean = super.remove(powerTransformerInfo)

    fun add(shortCircuitTest: ShortCircuitTest): Boolean = super.add(shortCircuitTest)
    fun remove(shortCircuitTest: ShortCircuitTest): Boolean = super.remove(shortCircuitTest)

    fun add(shuntCompensatorInfo: ShuntCompensatorInfo): Boolean = super.add(shuntCompensatorInfo)
    fun remove(shuntCompensatorInfo: ShuntCompensatorInfo): Boolean = super.remove(shuntCompensatorInfo)

    fun add(transformerEndInfo: TransformerEndInfo): Boolean = super.add(transformerEndInfo)
    fun remove(transformerEndInfo: TransformerEndInfo): Boolean = super.remove(transformerEndInfo)

    fun add(transformerTankInfo: TransformerTankInfo): Boolean = super.add(transformerTankInfo)
    fun remove(transformerTankInfo: TransformerTankInfo): Boolean = super.remove(transformerTankInfo)

    // ###################
    // # IEC61968 ASSETS #
    // ###################

    fun add(assetOwner: AssetOwner): Boolean = super.add(assetOwner)
    fun remove(assetOwner: AssetOwner): Boolean = super.remove(assetOwner)

    fun add(pole: Pole): Boolean = super.add(pole)
    fun remove(pole: Pole): Boolean = super.remove(pole)

    fun add(streetlight: Streetlight): Boolean = super.add(streetlight)
    fun remove(streetlight: Streetlight): Boolean = super.remove(streetlight)

    // ###################
    // # IEC61968 COMMON #
    // ###################

    fun add(location: Location): Boolean = super.add(location)
    fun remove(location: Location): Boolean = super.remove(location)

    fun add(organisation: Organisation): Boolean = super.add(organisation)
    fun remove(organisation: Organisation): Boolean = super.remove(organisation)

    // #####################################
    // # IEC61968 infIEC61968 InfAssetInfo #
    // #####################################

    fun add(currentTransformerInfo: CurrentTransformerInfo): Boolean = super.add(currentTransformerInfo)
    fun remove(currentTransformerInfo: CurrentTransformerInfo): Boolean = super.remove(currentTransformerInfo)

    fun add(potentialTransformerInfo: PotentialTransformerInfo): Boolean = super.add(potentialTransformerInfo)
    fun remove(potentialTransformerInfo: PotentialTransformerInfo): Boolean = super.remove(potentialTransformerInfo)

    // #####################
    // # IEC61968 METERING #
    // #####################

    fun add(meter: Meter): Boolean = super.add(meter)
    fun remove(meter: Meter): Boolean = super.remove(meter)

    fun add(usagePoint: UsagePoint): Boolean = super.add(usagePoint)
    fun remove(usagePoint: UsagePoint): Boolean = super.remove(usagePoint)

    // #######################
    // # IEC61968 OPERATIONS #
    // #######################

    fun add(operationalRestriction: OperationalRestriction): Boolean = super.add(operationalRestriction)
    fun remove(operationalRestriction: OperationalRestriction): Boolean = super.remove(operationalRestriction)

    // #####################################
    // # IEC61970 BASE AUXILIARY EQUIPMENT #
    // #####################################

    fun add(currentTransformer: CurrentTransformer): Boolean = super.add(currentTransformer)
    fun remove(currentTransformer: CurrentTransformer): Boolean = super.remove(currentTransformer)

    fun add(faultIndicator: FaultIndicator): Boolean = super.add(faultIndicator)
    fun remove(faultIndicator: FaultIndicator): Boolean = super.remove(faultIndicator)

    fun add(potentialTransformer: PotentialTransformer): Boolean = super.add(potentialTransformer)
    fun remove(potentialTransformer: PotentialTransformer): Boolean = super.remove(potentialTransformer)

    // ######################
    // # IEC61970 BASE CORE #
    // ######################

    fun add(baseVoltage: BaseVoltage): Boolean = super.add(baseVoltage)
    fun remove(baseVoltage: BaseVoltage): Boolean = super.remove(baseVoltage)

    fun add(connectivityNode: ConnectivityNode): Boolean = super.add(connectivityNode)
    fun remove(connectivityNode: ConnectivityNode): Boolean = super.remove(connectivityNode)

    fun add(feeder: Feeder): Boolean = super.add(feeder)
    fun remove(feeder: Feeder): Boolean = super.remove(feeder)

    fun add(geographicalRegion: GeographicalRegion): Boolean = super.add(geographicalRegion)
    fun remove(geographicalRegion: GeographicalRegion): Boolean = super.remove(geographicalRegion)

    fun add(site: Site): Boolean = super.add(site)
    fun remove(site: Site): Boolean = super.remove(site)

    fun add(subGeographicalRegion: SubGeographicalRegion): Boolean = super.add(subGeographicalRegion)
    fun remove(subGeographicalRegion: SubGeographicalRegion): Boolean = super.remove(subGeographicalRegion)

    fun add(substation: Substation): Boolean = super.add(substation)
    fun remove(substation: Substation): Boolean = super.remove(substation)

    fun add(terminal: Terminal): Boolean = super.add(terminal)
    fun remove(terminal: Terminal): Boolean = super.remove(terminal)

    // #############################
    // # IEC61970 BASE EQUIVALENTS #
    // #############################

    fun add(equivalentBranch: EquivalentBranch): Boolean = super.add(equivalentBranch)
    fun remove(equivalentBranch: EquivalentBranch): Boolean = super.remove(equivalentBranch)

    // ######################
    // # IEC61970 BASE MEAS #
    // ######################

    fun add(accumulator: Accumulator): Boolean = indexMeasurement(accumulator) && super.add(accumulator)
    fun remove(accumulator: Accumulator): Boolean {
        removeMeasurementIndex(accumulator)
        return super.remove(accumulator)
    }

    fun add(analog: Analog): Boolean = indexMeasurement(analog) && super.add(analog)
    fun remove(analog: Analog): Boolean {
        removeMeasurementIndex(analog)
        return super.remove(analog)
    }

    fun add(control: Control): Boolean = super.add(control)
    fun remove(control: Control): Boolean = super.remove(control)

    fun add(discrete: Discrete): Boolean = indexMeasurement(discrete) && super.add(discrete)
    fun remove(discrete: Discrete): Boolean {
        removeMeasurementIndex(discrete)
        return super.remove(discrete)
    }

    // #######################
    // # IEC61970 BASE SCADA #
    // #######################

    fun add(remoteControl: RemoteControl): Boolean = super.add(remoteControl)
    fun remove(remoteControl: RemoteControl): Boolean = super.remove(remoteControl)

    fun add(remoteSource: RemoteSource): Boolean = super.add(remoteSource)
    fun remove(remoteSource: RemoteSource): Boolean = super.remove(remoteSource)

    // #############################################
    // # IEC61970 BASE WIRES GENERATION PRODUCTION #
    // #############################################

    fun add(batteryUnit: BatteryUnit): Boolean = super.add(batteryUnit)
    fun remove(batteryUnit: BatteryUnit): Boolean = super.remove(batteryUnit)

    fun add(photoVoltaicUnit: PhotoVoltaicUnit): Boolean = super.add(photoVoltaicUnit)
    fun remove(photoVoltaicUnit: PhotoVoltaicUnit): Boolean = super.remove(photoVoltaicUnit)

    fun add(powerElectronicsWindUnit: PowerElectronicsWindUnit): Boolean = super.add(powerElectronicsWindUnit)
    fun remove(powerElectronicsWindUnit: PowerElectronicsWindUnit): Boolean = super.remove(powerElectronicsWindUnit)

    // #######################
    // # IEC61970 BASE WIRES #
    // #######################

    fun add(acLineSegment: AcLineSegment): Boolean = super.add(acLineSegment)
    fun remove(acLineSegment: AcLineSegment): Boolean = super.remove(acLineSegment)

    fun add(breaker: Breaker): Boolean = super.add(breaker)
    fun remove(breaker: Breaker): Boolean = super.remove(breaker)

    fun add(busbarSection: BusbarSection): Boolean = super.add(busbarSection)
    fun remove(busbarSection: BusbarSection): Boolean = super.remove(busbarSection)

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

    fun add(fuse: Fuse): Boolean = super.add(fuse)
    fun remove(fuse: Fuse): Boolean = super.remove(fuse)

    fun add(jumper: Jumper): Boolean = super.add(jumper)
    fun remove(jumper: Jumper): Boolean = super.remove(jumper)

    fun add(junction: Junction): Boolean = super.add(junction)
    fun remove(junction: Junction): Boolean = super.remove(junction)

    fun add(linearShuntCompensator: LinearShuntCompensator): Boolean = super.add(linearShuntCompensator)
    fun remove(linearShuntCompensator: LinearShuntCompensator): Boolean = super.remove(linearShuntCompensator)

    fun add(loadBreakSwitch: LoadBreakSwitch): Boolean = super.add(loadBreakSwitch)
    fun remove(loadBreakSwitch: LoadBreakSwitch): Boolean = super.remove(loadBreakSwitch)

    fun add(perLengthSequenceImpedance: PerLengthSequenceImpedance): Boolean = super.add(perLengthSequenceImpedance)
    fun remove(perLengthSequenceImpedance: PerLengthSequenceImpedance): Boolean = super.remove(perLengthSequenceImpedance)

    fun add(powerElectronicsConnection: PowerElectronicsConnection): Boolean = super.add(powerElectronicsConnection)
    fun remove(powerElectronicsConnection: PowerElectronicsConnection): Boolean = super.remove(powerElectronicsConnection)

    fun add(powerElectronicsConnectionPhase: PowerElectronicsConnectionPhase): Boolean = super.add(powerElectronicsConnectionPhase)
    fun remove(powerElectronicsConnectionPhase: PowerElectronicsConnectionPhase): Boolean = super.remove(powerElectronicsConnectionPhase)

    fun add(powerTransformer: PowerTransformer): Boolean = super.add(powerTransformer)
    fun remove(powerTransformer: PowerTransformer): Boolean = super.remove(powerTransformer)

    fun add(powerTransformerEnd: PowerTransformerEnd): Boolean = super.add(powerTransformerEnd)
    fun remove(powerTransformerEnd: PowerTransformerEnd): Boolean = super.remove(powerTransformerEnd)

    fun add(ratioTapChanger: RatioTapChanger): Boolean = super.add(ratioTapChanger)
    fun remove(ratioTapChanger: RatioTapChanger): Boolean = super.remove(ratioTapChanger)

    fun add(recloser: Recloser): Boolean = super.add(recloser)
    fun remove(recloser: Recloser): Boolean = super.remove(recloser)

    fun add(transformerStarImpedance: TransformerStarImpedance): Boolean = super.add(transformerStarImpedance)
    fun remove(transformerStarImpedance: TransformerStarImpedance): Boolean = super.remove(transformerStarImpedance)

    // ###############################
    // # IEC61970 InfIEC61970 Feeder #
    // ###############################

    fun add(circuit: Circuit): Boolean = super.add(circuit)
    fun remove(circuit: Circuit): Boolean = super.remove(circuit)

    fun add(loop: Loop): Boolean = super.add(loop)
    fun remove(loop: Loop): Boolean = super.remove(loop)

    fun add(lvFeeder: LvFeeder): Boolean = super.add(lvFeeder)
    fun remove(lvFeeder: LvFeeder): Boolean = super.remove(lvFeeder)

    /**
     * Get all measurements of type [T] associated with the given [mRID].
     *
     * The [mRID] should be either a [PowerSystemResource] or a [Terminal] MRID that is assigned to the corresponding
     * fields on the measurements.
     */
    inline fun <reified T : Measurement> getMeasurements(mRID: String): List<T> = getMeasurements(mRID, T::class)

    /**
     * Get all measurements of type [measurementClass] associated with the given [mRID].
     *
     * The [mRID] should be either a [PowerSystemResource] or a [Terminal] MRID that is assigned to the corresponding
     * fields on the measurements.
     */
    fun <T : Measurement> getMeasurements(mRID: String, measurementClass: KClass<T>): List<T> =
        getMeasurements(mRID, measurementClass.java)

    /**
     * Get all measurements of type [measurementClass] associated with the given [mRID].
     *
     * The [mRID] should be either a [PowerSystemResource] or a [Terminal] MRID that is assigned to the corresponding
     * fields on the measurements.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun <T : Measurement> getMeasurements(mRID: String, measurementClass: Class<T>): List<T> =
        _measurements[mRID]?.filterIsInstance(measurementClass) ?: emptyList()

    private fun indexMeasurement(measurement: Measurement, mRID: String?): Boolean {
        if (mRID.isNullOrEmpty())
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

    private fun indexMeasurement(measurement: Measurement) =
        indexMeasurement(measurement, measurement.terminalMRID) && indexMeasurement(measurement, measurement.powerSystemResourceMRID)

    private fun removeMeasurementIndex(measurement: Measurement) {
        _measurements[measurement.terminalMRID]?.remove(measurement)
        _measurements[measurement.powerSystemResourceMRID]?.remove(measurement)
    }

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

        connectivityNode = _connectivityNodes.computeIfAbsent(connectivityNodeId) { mRID: String -> createConnectivityNode(mRID) }
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

        /**
         * Find the connected [ConductingEquipment] for each [Terminal] of [conductingEquipment] using only the phases of the specified [phaseCode].
         *
         * @param conductingEquipment The [ConductingEquipment] to process.
         * @param phaseCode The [PhaseCode] specifying which phases should be used for the connectivity check.
         * @return A list of [ConnectivityResult] specifying the connections between [conductingEquipment] and the connected [ConductingEquipment]
         */
        @JvmStatic
        fun connectedEquipment(conductingEquipment: ConductingEquipment, phaseCode: PhaseCode): List<ConnectivityResult> =
            connectedEquipment(conductingEquipment, phaseCode.singlePhases.toSet())

        /**
         * Find the connected [ConductingEquipment] for each [Terminal] of [conductingEquipment] using only the specified [phases].
         *
         * @param conductingEquipment The [ConductingEquipment] to process.
         * @param phases A collection of [SinglePhaseKind] specifying which phases should be used for the connectivity check. If omitted,
         *               all valid phases will be used.
         * @return A list of [ConnectivityResult] specifying the connections between [conductingEquipment] and the connected [ConductingEquipment]
         */
        @JvmStatic
        @JvmOverloads
        fun connectedEquipment(conductingEquipment: ConductingEquipment, phases: Set<SinglePhaseKind>? = null): List<ConnectivityResult> =
            conductingEquipment.terminals.flatMap { connectedTerminals(it, phases ?: it.phases.singlePhases) }

        /**
         * Find the connected [Terminal]s for the specified [terminal] using only the phases of the specified [phaseCode].
         *
         * @param terminal The [Terminal] to process.
         * @param phaseCode The [PhaseCode] specifying which phases should be used for the connectivity check.
         * @return A list of [ConnectivityResult] specifying the connections between [terminal] and the connected [Terminal]s
         */
        @JvmStatic
        fun connectedTerminals(terminal: Terminal, phaseCode: PhaseCode): List<ConnectivityResult> =
            connectedTerminals(terminal, phaseCode.singlePhases)

        /**
         * Find the connected [Terminal]s for the specified [terminal] using only the specified [phases].
         *
         * @param terminal The [Terminal] to process.
         * @param phases A collection of [SinglePhaseKind] specifying which phases should be used for the connectivity check. If omitted,
         *               all valid phases will be used.
         * @return A list of [ConnectivityResult] specifying the connections between [terminal] and the connected [Terminal]s
         */
        @JvmStatic
        @JvmOverloads
        fun connectedTerminals(terminal: Terminal, phases: Iterable<SinglePhaseKind> = terminal.phases.singlePhases): List<ConnectivityResult> =
            TerminalConnectivityConnected().connectedTerminals(terminal, phases)

    }

}
