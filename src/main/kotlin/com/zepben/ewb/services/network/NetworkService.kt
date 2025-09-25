/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network

import com.zepben.ewb.cim.extensions.iec61968.assetinfo.RelayInfo
import com.zepben.ewb.cim.extensions.iec61968.metering.PanDemandResponseFunction
import com.zepben.ewb.cim.extensions.iec61970.base.core.Site
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.Loop
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvFeeder
import com.zepben.ewb.cim.extensions.iec61970.base.generation.production.EvChargingUnit
import com.zepben.ewb.cim.extensions.iec61970.base.protection.*
import com.zepben.ewb.cim.extensions.iec61970.base.wires.BatteryControl
import com.zepben.ewb.cim.iec61968.assetinfo.*
import com.zepben.ewb.cim.iec61968.assets.AssetOwner
import com.zepben.ewb.cim.iec61968.assets.Streetlight
import com.zepben.ewb.cim.iec61968.common.Location
import com.zepben.ewb.cim.iec61968.common.Organisation
import com.zepben.ewb.cim.iec61968.infiec61968.infassetinfo.CurrentTransformerInfo
import com.zepben.ewb.cim.iec61968.infiec61968.infassetinfo.PotentialTransformerInfo
import com.zepben.ewb.cim.iec61968.infiec61968.infassets.Pole
import com.zepben.ewb.cim.iec61968.metering.Meter
import com.zepben.ewb.cim.iec61968.metering.UsagePoint
import com.zepben.ewb.cim.iec61968.operations.OperationalRestriction
import com.zepben.ewb.cim.iec61970.base.auxiliaryequipment.CurrentTransformer
import com.zepben.ewb.cim.iec61970.base.auxiliaryequipment.FaultIndicator
import com.zepben.ewb.cim.iec61970.base.auxiliaryequipment.PotentialTransformer
import com.zepben.ewb.cim.iec61970.base.core.*
import com.zepben.ewb.cim.iec61970.base.equivalents.EquivalentBranch
import com.zepben.ewb.cim.iec61970.base.generation.production.BatteryUnit
import com.zepben.ewb.cim.iec61970.base.generation.production.PhotoVoltaicUnit
import com.zepben.ewb.cim.iec61970.base.generation.production.PowerElectronicsWindUnit
import com.zepben.ewb.cim.iec61970.base.meas.*
import com.zepben.ewb.cim.iec61970.base.protection.CurrentRelay
import com.zepben.ewb.cim.iec61970.base.scada.RemoteControl
import com.zepben.ewb.cim.iec61970.base.scada.RemoteSource
import com.zepben.ewb.cim.iec61970.base.wires.*
import com.zepben.ewb.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.ewb.services.common.BaseService
import com.zepben.ewb.services.common.meta.MetadataCollection
import com.zepben.ewb.services.network.tracing.connectivity.ConnectivityResult
import com.zepben.ewb.services.network.tracing.connectivity.TerminalConnectivityConnected
import kotlin.reflect.KClass

/**
 * Maintains an in-memory model of the network.
 */
class NetworkService(metadata: MetadataCollection = MetadataCollection()) : BaseService("network", metadata) {

    private enum class ProcessStatus {
        PROCESSED, SKIPPED, INVALID
    }

    private var autoConnectivityNodeIndex = 0

    @Suppress("UNCHECKED_CAST")
    private val _connectivityNodes: MutableMap<String, ConnectivityNode> = objectsByType.computeIfAbsent(ConnectivityNode::class) {
        mutableMapOf()
    } as MutableMap<String, ConnectivityNode>

    private val _measurements: MutableMap<String, MutableList<Measurement>> = mutableMapOf()

    // ##################################
    // # Extensions IEC61968 Asset Info #
    // ##################################

    /**
     * Add the [RelayInfo] to this service.
     *
     * @param relayInfo The [RelayInfo] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(relayInfo: RelayInfo): Boolean = super.add(relayInfo)

    /**
     * Remove the [RelayInfo] from this service.
     *
     * @param relayInfo The [RelayInfo] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(relayInfo: RelayInfo): Boolean = super.remove(relayInfo)

    // ################################
    // # Extensions IEC61968 Metering #
    // ################################

    /**
     * Add the [PanDemandResponseFunction] to this service.
     *
     * @param panDemandResponseFunction The [PanDemandResponseFunction] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(panDemandResponseFunction: PanDemandResponseFunction): Boolean = super.add(panDemandResponseFunction)

    /**
     * Remove the [PanDemandResponseFunction] from this service.
     *
     * @param panDemandResponseFunction The [PanDemandResponseFunction] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(panDemandResponseFunction: PanDemandResponseFunction): Boolean = super.remove(panDemandResponseFunction)

    // #################################
    // # Extensions IEC61970 Base Core #
    // #################################

    /**
     * Add the [Site] to this service.
     *
     * @param site The [Site] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(site: Site): Boolean = super.add(site)

    /**
     * Remove the [Site] from this service.
     *
     * @param site The [Site] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(site: Site): Boolean = super.remove(site)

    // ###################################
    // # Extensions IEC61970 Base Feeder #
    // ###################################

    /**
     * Add the [Loop] to this service.
     *
     * @param loop The [Loop] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(loop: Loop): Boolean = super.add(loop)

    /**
     * Remove the [Loop] from this service.
     *
     * @param loop The [Loop] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(loop: Loop): Boolean = super.remove(loop)

    /**
     * Add the [LvFeeder] to this service.
     *
     * @param lvFeeder The [LvFeeder] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(lvFeeder: LvFeeder): Boolean = super.add(lvFeeder)

    /**
     * Remove the [LvFeeder] from this service.
     *
     * @param lvFeeder The [LvFeeder] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(lvFeeder: LvFeeder): Boolean = super.remove(lvFeeder)

    // ##################################################
    // # Extensions IEC61970 Base Generation Production #
    // ##################################################

    /**
     * Add the [EvChargingUnit] to this service.
     *
     * @param evChargingUnit The [EvChargingUnit] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(evChargingUnit: EvChargingUnit): Boolean = super.add(evChargingUnit)

    /**
     * Remove the [EvChargingUnit] from this service.
     *
     * @param evChargingUnit The [EvChargingUnit] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(evChargingUnit: EvChargingUnit): Boolean = super.remove(evChargingUnit)

    // #######################################
    // # Extensions IEC61970 Base Protection #
    // #######################################

    /**
     * Add the [DirectionalCurrentRelay] to this service.
     *
     * @param directionalCurrentRelay The [DirectionalCurrentRelay] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(directionalCurrentRelay: DirectionalCurrentRelay): Boolean = super.add(directionalCurrentRelay)

    /**
     * Remove the [DirectionalCurrentRelay] from this service.
     *
     * @param directionalCurrentRelay The [DirectionalCurrentRelay] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(directionalCurrentRelay: DirectionalCurrentRelay): Boolean = super.remove(directionalCurrentRelay)

    /**
     * Add the [DistanceRelay] to this service.
     *
     * @param distanceRelay The [DistanceRelay] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(distanceRelay: DistanceRelay): Boolean = super.add(distanceRelay)

    /**
     * Remove the [DistanceRelay] from this service.
     *
     * @param distanceRelay The [DistanceRelay] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(distanceRelay: DistanceRelay): Boolean = super.remove(distanceRelay)

    /**
     * Add the [ProtectionRelayScheme] to this service.
     *
     * @param protectionRelayScheme The [ProtectionRelayScheme] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(protectionRelayScheme: ProtectionRelayScheme): Boolean = super.add(protectionRelayScheme)

    /**
     * Remove the [ProtectionRelayScheme] from this service.
     *
     * @param protectionRelayScheme The [ProtectionRelayScheme] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(protectionRelayScheme: ProtectionRelayScheme): Boolean = super.remove(protectionRelayScheme)

    /**
     * Add the [ProtectionRelaySystem] to this service.
     *
     * @param protectionRelaySystem The [ProtectionRelaySystem] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(protectionRelaySystem: ProtectionRelaySystem): Boolean = super.add(protectionRelaySystem)

    /**
     * Remove the [ProtectionRelaySystem] from this service.
     *
     * @param protectionRelaySystem The [ProtectionRelaySystem] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(protectionRelaySystem: ProtectionRelaySystem): Boolean = super.remove(protectionRelaySystem)

    /**
     * Add the [VoltageRelay] to this service.
     *
     * @param voltageRelay The [VoltageRelay] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(voltageRelay: VoltageRelay): Boolean = super.add(voltageRelay)

    /**
     * Remove the [VoltageRelay] from this service.
     *
     * @param voltageRelay The [VoltageRelay] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(voltageRelay: VoltageRelay): Boolean = super.remove(voltageRelay)

    // ##################################
    // # Extensions IEC61970 Base Wires #
    // ##################################

    /**
     * Add the [BatteryControl] to this service.
     *
     * @param batteryControl The [BatteryControl] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(batteryControl: BatteryControl): Boolean = super.add(batteryControl)

    /**
     * Remove the [BatteryControl] from this service.
     *
     * @param batteryControl The [BatteryControl] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(batteryControl: BatteryControl): Boolean = super.remove(batteryControl)

    // #######################
    // # IEC61968 Asset Info #
    // #######################

    /**
     * Add the [CableInfo] to this service.
     *
     * @param cableInfo The [CableInfo] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(cableInfo: CableInfo): Boolean = super.add(cableInfo)

    /**
     * Remove the [CableInfo] from this service.
     *
     * @param cableInfo The [CableInfo] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(cableInfo: CableInfo): Boolean = super.remove(cableInfo)

    /**
     * Add the [NoLoadTest] to this service.
     *
     * @param noLoadTest The [NoLoadTest] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(noLoadTest: NoLoadTest): Boolean = super.add(noLoadTest)

    /**
     * Remove the [NoLoadTest] from this service.
     *
     * @param noLoadTest The [NoLoadTest] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(noLoadTest: NoLoadTest): Boolean = super.remove(noLoadTest)

    /**
     * Add the [OpenCircuitTest] to this service.
     *
     * @param openCircuitTest The [OpenCircuitTest] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(openCircuitTest: OpenCircuitTest): Boolean = super.add(openCircuitTest)

    /**
     * Remove the [OpenCircuitTest] from this service.
     *
     * @param openCircuitTest The [OpenCircuitTest] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(openCircuitTest: OpenCircuitTest): Boolean = super.remove(openCircuitTest)

    /**
     * Add the [OverheadWireInfo] to this service.
     *
     * @param overheadWireInfo The [OverheadWireInfo] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(overheadWireInfo: OverheadWireInfo): Boolean = super.add(overheadWireInfo)

    /**
     * Remove the [OverheadWireInfo] from this service.
     *
     * @param overheadWireInfo The [OverheadWireInfo] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(overheadWireInfo: OverheadWireInfo): Boolean = super.remove(overheadWireInfo)

    /**
     * Add the [PowerTransformerInfo] to this service.
     *
     * @param powerTransformerInfo The [PowerTransformerInfo] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(powerTransformerInfo: PowerTransformerInfo): Boolean = super.add(powerTransformerInfo)

    /**
     * Remove the [PowerTransformerInfo] from this service.
     *
     * @param powerTransformerInfo The [PowerTransformerInfo] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(powerTransformerInfo: PowerTransformerInfo): Boolean = super.remove(powerTransformerInfo)

    /**
     * Add the [ShortCircuitTest] to this service.
     *
     * @param shortCircuitTest The [ShortCircuitTest] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(shortCircuitTest: ShortCircuitTest): Boolean = super.add(shortCircuitTest)

    /**
     * Remove the [ShortCircuitTest] from this service.
     *
     * @param shortCircuitTest The [ShortCircuitTest] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(shortCircuitTest: ShortCircuitTest): Boolean = super.remove(shortCircuitTest)

    /**
     * Add the [ShuntCompensatorInfo] to this service.
     *
     * @param shuntCompensatorInfo The [ShuntCompensatorInfo] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(shuntCompensatorInfo: ShuntCompensatorInfo): Boolean = super.add(shuntCompensatorInfo)

    /**
     * Remove the [ShuntCompensatorInfo] from this service.
     *
     * @param shuntCompensatorInfo The [ShuntCompensatorInfo] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(shuntCompensatorInfo: ShuntCompensatorInfo): Boolean = super.remove(shuntCompensatorInfo)

    /**
     * Add the [SwitchInfo] to this service.
     *
     * @param switchInfo The [SwitchInfo] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(switchInfo: SwitchInfo): Boolean = super.add(switchInfo)

    /**
     * Remove the [SwitchInfo] from this service.
     *
     * @param switchInfo The [SwitchInfo] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(switchInfo: SwitchInfo): Boolean = super.remove(switchInfo)

    /**
     * Add the [TransformerEndInfo] to this service.
     *
     * @param transformerEndInfo The [TransformerEndInfo] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(transformerEndInfo: TransformerEndInfo): Boolean = super.add(transformerEndInfo)

    /**
     * Remove the [TransformerEndInfo] from this service.
     *
     * @param transformerEndInfo The [TransformerEndInfo] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(transformerEndInfo: TransformerEndInfo): Boolean = super.remove(transformerEndInfo)

    /**
     * Add the [TransformerTankInfo] to this service.
     *
     * @param transformerTankInfo The [TransformerTankInfo] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(transformerTankInfo: TransformerTankInfo): Boolean = super.add(transformerTankInfo)

    /**
     * Remove the [TransformerTankInfo] from this service.
     *
     * @param transformerTankInfo The [TransformerTankInfo] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(transformerTankInfo: TransformerTankInfo): Boolean = super.remove(transformerTankInfo)

    // ###################
    // # IEC61968 Assets #
    // ###################

    /**
     * Add the [AssetOwner] to this service.
     *
     * @param assetOwner The [AssetOwner] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(assetOwner: AssetOwner): Boolean = super.add(assetOwner)

    /**
     * Remove the [AssetOwner] from this service.
     *
     * @param assetOwner The [AssetOwner] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(assetOwner: AssetOwner): Boolean = super.remove(assetOwner)

    /**
     * Add the [Streetlight] to this service.
     *
     * @param streetlight The [Streetlight] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(streetlight: Streetlight): Boolean = super.add(streetlight)

    /**
     * Remove the [Streetlight] from this service.
     *
     * @param streetlight The [Streetlight] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(streetlight: Streetlight): Boolean = super.remove(streetlight)

    // ###################
    // # IEC61968 Common #
    // ###################

    /**
     * Add the [Location] to this service.
     *
     * @param location The [Location] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(location: Location): Boolean = super.add(location)

    /**
     * Remove the [Location] from this service.
     *
     * @param location The [Location] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(location: Location): Boolean = super.remove(location)

    /**
     * Add the [Organisation] to this service.
     *
     * @param organisation The [Organisation] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(organisation: Organisation): Boolean = super.add(organisation)

    /**
     * Remove the [Organisation] from this service.
     *
     * @param organisation The [Organisation] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(organisation: Organisation): Boolean = super.remove(organisation)

    // #####################################
    // # IEC61968 infIEC61968 InfAssetInfo #
    // #####################################

    /**
     * Add the [CurrentTransformerInfo] to this service.
     *
     * @param currentTransformerInfo The [CurrentTransformerInfo] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(currentTransformerInfo: CurrentTransformerInfo): Boolean = super.add(currentTransformerInfo)

    /**
     * Remove the [CurrentTransformerInfo] from this service.
     *
     * @param currentTransformerInfo The [CurrentTransformerInfo] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(currentTransformerInfo: CurrentTransformerInfo): Boolean = super.remove(currentTransformerInfo)

    /**
     * Add the [PotentialTransformerInfo] to this service.
     *
     * @param potentialTransformerInfo The [PotentialTransformerInfo] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(potentialTransformerInfo: PotentialTransformerInfo): Boolean = super.add(potentialTransformerInfo)

    /**
     * Remove the [PotentialTransformerInfo] from this service.
     *
     * @param potentialTransformerInfo The [PotentialTransformerInfo] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(potentialTransformerInfo: PotentialTransformerInfo): Boolean = super.remove(potentialTransformerInfo)

    // ##################################
    // # IEC61968 infIEC61968 InfAssets #
    // ##################################

    /**
     * Add the [Pole] to this service.
     *
     * @param pole The [Pole] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(pole: Pole): Boolean = super.add(pole)

    /**
     * Remove the [Pole] from this service.
     *
     * @param pole The [Pole] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(pole: Pole): Boolean = super.remove(pole)

    // #####################
    // # IEC61968 Metering #
    // #####################

    /**
     * Add the [Meter] to this service.
     *
     * @param meter The [Meter] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(meter: Meter): Boolean = super.add(meter)

    /**
     * Remove the [Meter] from this service.
     *
     * @param meter The [Meter] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(meter: Meter): Boolean = super.remove(meter)

    /**
     * Add the [UsagePoint] to this service.
     *
     * @param usagePoint The [UsagePoint] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(usagePoint: UsagePoint): Boolean = super.add(usagePoint)

    /**
     * Remove the [UsagePoint] from this service.
     *
     * @param usagePoint The [UsagePoint] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(usagePoint: UsagePoint): Boolean = super.remove(usagePoint)

    // #######################
    // # IEC61968 Operations #
    // #######################

    /**
     * Add the [OperationalRestriction] to this service.
     *
     * @param operationalRestriction The [OperationalRestriction] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(operationalRestriction: OperationalRestriction): Boolean = super.add(operationalRestriction)

    /**
     * Remove the [OperationalRestriction] from this service.
     *
     * @param operationalRestriction The [OperationalRestriction] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(operationalRestriction: OperationalRestriction): Boolean = super.remove(operationalRestriction)

    // #####################################
    // # IEC61970 Base Auxiliary Equipment #
    // #####################################

    /**
     * Add the [CurrentTransformer] to this service.
     *
     * @param currentTransformer The [CurrentTransformer] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(currentTransformer: CurrentTransformer): Boolean = super.add(currentTransformer)

    /**
     * Remove the [CurrentTransformer] from this service.
     *
     * @param currentTransformer The [CurrentTransformer] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(currentTransformer: CurrentTransformer): Boolean = super.remove(currentTransformer)

    /**
     * Add the [FaultIndicator] to this service.
     *
     * @param faultIndicator The [FaultIndicator] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(faultIndicator: FaultIndicator): Boolean = super.add(faultIndicator)

    /**
     * Remove the [FaultIndicator] from this service.
     *
     * @param faultIndicator The [FaultIndicator] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(faultIndicator: FaultIndicator): Boolean = super.remove(faultIndicator)

    /**
     * Add the [PotentialTransformer] to this service.
     *
     * @param potentialTransformer The [PotentialTransformer] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(potentialTransformer: PotentialTransformer): Boolean = super.add(potentialTransformer)

    /**
     * Remove the [PotentialTransformer] from this service.
     *
     * @param potentialTransformer The [PotentialTransformer] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(potentialTransformer: PotentialTransformer): Boolean = super.remove(potentialTransformer)

    // ######################
    // # IEC61970 Base Core #
    // ######################

    /**
     * Add the [BaseVoltage] to this service.
     *
     * @param baseVoltage The [BaseVoltage] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(baseVoltage: BaseVoltage): Boolean = super.add(baseVoltage)

    /**
     * Remove the [BaseVoltage] from this service.
     *
     * @param baseVoltage The [BaseVoltage] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(baseVoltage: BaseVoltage): Boolean = super.remove(baseVoltage)

    /**
     * Add the [ConnectivityNode] to this service.
     *
     * @param connectivityNode The [ConnectivityNode] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(connectivityNode: ConnectivityNode): Boolean = super.add(connectivityNode)

    /**
     * Remove the [ConnectivityNode] from this service.
     *
     * @param connectivityNode The [ConnectivityNode] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(connectivityNode: ConnectivityNode): Boolean = super.remove(connectivityNode)

    /**
     * Add the [Feeder] to this service.
     *
     * @param feeder The [Feeder] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(feeder: Feeder): Boolean = super.add(feeder)

    /**
     * Remove the [Feeder] from this service.
     *
     * @param feeder The [Feeder] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(feeder: Feeder): Boolean = super.remove(feeder)

    /**
     * Add the [GeographicalRegion] to this service.
     *
     * @param geographicalRegion The [GeographicalRegion] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(geographicalRegion: GeographicalRegion): Boolean = super.add(geographicalRegion)

    /**
     * Remove the [GeographicalRegion] from this service.
     *
     * @param geographicalRegion The [GeographicalRegion] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(geographicalRegion: GeographicalRegion): Boolean = super.remove(geographicalRegion)

    /**
     * Add the [SubGeographicalRegion] to this service.
     *
     * @param subGeographicalRegion The [SubGeographicalRegion] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(subGeographicalRegion: SubGeographicalRegion): Boolean = super.add(subGeographicalRegion)

    /**
     * Remove the [SubGeographicalRegion] from this service.
     *
     * @param subGeographicalRegion The [SubGeographicalRegion] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(subGeographicalRegion: SubGeographicalRegion): Boolean = super.remove(subGeographicalRegion)

    /**
     * Add the [Substation] to this service.
     *
     * @param substation The [Substation] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(substation: Substation): Boolean = super.add(substation)

    /**
     * Remove the [Substation] from this service.
     *
     * @param substation The [Substation] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(substation: Substation): Boolean = super.remove(substation)

    /**
     * Add the [Terminal] to this service.
     *
     * @param terminal The [Terminal] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(terminal: Terminal): Boolean = super.add(terminal)

    /**
     * Remove the [Terminal] from this service.
     *
     * @param terminal The [Terminal] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(terminal: Terminal): Boolean = super.remove(terminal)

    // #############################
    // # IEC61970 Base Equivalents #
    // #############################

    /**
     * Add the [EquivalentBranch] to this service.
     *
     * @param equivalentBranch The [EquivalentBranch] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(equivalentBranch: EquivalentBranch): Boolean = super.add(equivalentBranch)

    /**
     * Remove the [EquivalentBranch] from this service.
     *
     * @param equivalentBranch The [EquivalentBranch] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(equivalentBranch: EquivalentBranch): Boolean = super.remove(equivalentBranch)

    // ######################
    // # IEC61970 Base Meas #
    // ######################

    /**
     * Add the [Accumulator] to this service.
     *
     * @param accumulator The [Accumulator] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(accumulator: Accumulator): Boolean = indexMeasurement(accumulator) && super.add(accumulator)

    /**
     * Remove the [Accumulator] from this service.
     *
     * @param accumulator The [Accumulator] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(accumulator: Accumulator): Boolean {
        removeMeasurementIndex(accumulator)
        return super.remove(accumulator)
    }

    /**
     * Add the [Analog] to this service.
     *
     * @param analog The [Analog] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(analog: Analog): Boolean = indexMeasurement(analog) && super.add(analog)

    /**
     * Remove the [Analog] from this service.
     *
     * @param analog The [Analog] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(analog: Analog): Boolean {
        removeMeasurementIndex(analog)
        return super.remove(analog)
    }

    /**
     * Add the [Control] to this service.
     *
     * @param control The [Control] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(control: Control): Boolean = super.add(control)

    /**
     * Remove the [Control] from this service.
     *
     * @param control The [Control] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(control: Control): Boolean = super.remove(control)

    /**
     * Add the [Discrete] to this service.
     *
     * @param discrete The [Discrete] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(discrete: Discrete): Boolean = indexMeasurement(discrete) && super.add(discrete)

    /**
     * Remove the [Discrete] from this service.
     *
     * @param discrete The [Discrete] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(discrete: Discrete): Boolean {
        removeMeasurementIndex(discrete)
        return super.remove(discrete)
    }

    // ############################
    // # IEC61970 Base Protection #
    // ############################

    /**
     * Add the [CurrentRelay] to this service.
     *
     * @param currentRelay The [CurrentRelay] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(currentRelay: CurrentRelay): Boolean = super.add(currentRelay)

    /**
     * Remove the [CurrentRelay] from this service.
     *
     * @param currentRelay The [CurrentRelay] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(currentRelay: CurrentRelay): Boolean = super.remove(currentRelay)

    // #######################
    // # IEC61970 Base Scada #
    // #######################

    /**
     * Add the [RemoteControl] to this service.
     *
     * @param remoteControl The [RemoteControl] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(remoteControl: RemoteControl): Boolean = super.add(remoteControl)

    /**
     * Remove the [RemoteControl] from this service.
     *
     * @param remoteControl The [RemoteControl] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(remoteControl: RemoteControl): Boolean = super.remove(remoteControl)

    /**
     * Add the [RemoteSource] to this service.
     *
     * @param remoteSource The [RemoteSource] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(remoteSource: RemoteSource): Boolean = super.add(remoteSource)

    /**
     * Remove the [RemoteSource] from this service.
     *
     * @param remoteSource The [RemoteSource] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(remoteSource: RemoteSource): Boolean = super.remove(remoteSource)

    // #######################################
    // # IEC61970 Base Generation Production #
    // #######################################

    /**
     * Add the [BatteryUnit] to this service.
     *
     * @param batteryUnit The [BatteryUnit] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(batteryUnit: BatteryUnit): Boolean = super.add(batteryUnit)

    /**
     * Remove the [BatteryUnit] from this service.
     *
     * @param batteryUnit The [BatteryUnit] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(batteryUnit: BatteryUnit): Boolean = super.remove(batteryUnit)

    /**
     * Add the [PhotoVoltaicUnit] to this service.
     *
     * @param photoVoltaicUnit The [PhotoVoltaicUnit] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(photoVoltaicUnit: PhotoVoltaicUnit): Boolean = super.add(photoVoltaicUnit)

    /**
     * Remove the [PhotoVoltaicUnit] from this service.
     *
     * @param photoVoltaicUnit The [PhotoVoltaicUnit] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(photoVoltaicUnit: PhotoVoltaicUnit): Boolean = super.remove(photoVoltaicUnit)

    /**
     * Add the [PowerElectronicsWindUnit] to this service.
     *
     * @param powerElectronicsWindUnit The [PowerElectronicsWindUnit] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(powerElectronicsWindUnit: PowerElectronicsWindUnit): Boolean = super.add(powerElectronicsWindUnit)

    /**
     * Remove the [PowerElectronicsWindUnit] from this service.
     *
     * @param powerElectronicsWindUnit The [PowerElectronicsWindUnit] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(powerElectronicsWindUnit: PowerElectronicsWindUnit): Boolean = super.remove(powerElectronicsWindUnit)

    // #######################
    // # IEC61970 Base Wires #
    // #######################

    /**
     * Add the [AcLineSegment] to this service.
     *
     * @param acLineSegment The [AcLineSegment] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(acLineSegment: AcLineSegment): Boolean = super.add(acLineSegment)

    /**
     * Remove the [AcLineSegment] from this service.
     *
     * @param acLineSegment The [AcLineSegment] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(acLineSegment: AcLineSegment): Boolean = super.remove(acLineSegment)

    /**
     * Add the [Breaker] to this service.
     *
     * @param breaker The [Breaker] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(breaker: Breaker): Boolean = super.add(breaker)

    /**
     * Remove the [Breaker] from this service.
     *
     * @param breaker The [Breaker] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(breaker: Breaker): Boolean = super.remove(breaker)

    /**
     * Add the [BusbarSection] to this service.
     *
     * @param busbarSection The [BusbarSection] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(busbarSection: BusbarSection): Boolean = super.add(busbarSection)

    /**
     * Remove the [BusbarSection] from this service.
     *
     * @param busbarSection The [BusbarSection] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(busbarSection: BusbarSection): Boolean = super.remove(busbarSection)

    /**
     * Add the [Disconnector] to this service.
     *
     * @param disconnector The [Disconnector] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(disconnector: Disconnector): Boolean = super.add(disconnector)

    /**
     * Remove the [Disconnector] from this service.
     *
     * @param disconnector The [Disconnector] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(disconnector: Disconnector): Boolean = super.remove(disconnector)

    /**
     * Add the [Clamp] to this service.
     *
     * @param clamp The [Clamp] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(clamp: Clamp): Boolean = super.add(clamp)

    /**
     * Remove the [Clamp] from this service.
     *
     * @param clamp The [Clamp] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(clamp: Clamp): Boolean = super.remove(clamp)

    /**
     * Add the [Cut] to this service.
     *
     * @param cut The [Cut] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(cut: Cut): Boolean = super.add(cut)

    /**
     * Remove the [Cut] from this service.
     *
     * @param cut The [Cut] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(cut: Cut): Boolean = super.remove(cut)

    /**
     * Add the [EnergyConsumer] to this service.
     *
     * @param energyConsumer The [EnergyConsumer] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(energyConsumer: EnergyConsumer): Boolean = super.add(energyConsumer)

    /**
     * Remove the [EnergyConsumer] from this service.
     *
     * @param energyConsumer The [EnergyConsumer] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(energyConsumer: EnergyConsumer): Boolean = super.remove(energyConsumer)

    /**
     * Add the [EnergyConsumerPhase] to this service.
     *
     * @param energyConsumerPhase The [EnergyConsumerPhase] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(energyConsumerPhase: EnergyConsumerPhase): Boolean = super.add(energyConsumerPhase)

    /**
     * Remove the [EnergyConsumerPhase] from this service.
     *
     * @param energyConsumerPhase The [EnergyConsumerPhase] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(energyConsumerPhase: EnergyConsumerPhase): Boolean = super.remove(energyConsumerPhase)

    /**
     * Add the [EnergySource] to this service.
     *
     * @param energySource The [EnergySource] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(energySource: EnergySource): Boolean = super.add(energySource)

    /**
     * Remove the [EnergySource] from this service.
     *
     * @param energySource The [EnergySource] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(energySource: EnergySource): Boolean = super.remove(energySource)

    /**
     * Add the [EnergySourcePhase] to this service.
     *
     * @param energySourcePhase The [EnergySourcePhase] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(energySourcePhase: EnergySourcePhase): Boolean = super.add(energySourcePhase)

    /**
     * Remove the [EnergySourcePhase] from this service.
     *
     * @param energySourcePhase The [EnergySourcePhase] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(energySourcePhase: EnergySourcePhase): Boolean = super.remove(energySourcePhase)

    /**
     * Add the [Fuse] to this service.
     *
     * @param fuse The [Fuse] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(fuse: Fuse): Boolean = super.add(fuse)

    /**
     * Remove the [Fuse] from this service.
     *
     * @param fuse The [Fuse] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(fuse: Fuse): Boolean = super.remove(fuse)

    /**
     * Add the [Ground] to this service.
     *
     * @param ground The [Ground] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(ground: Ground): Boolean = super.add(ground)

    /**
     * Remove the [Ground] from this service.
     *
     * @param ground The [Ground] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(ground: Ground): Boolean = super.remove(ground)

    /**
     * Add the [GroundDisconnector] to this service.
     *
     * @param groundDisconnector The [GroundDisconnector] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(groundDisconnector: GroundDisconnector): Boolean = super.add(groundDisconnector)

    /**
     * Remove the [GroundDisconnector] from this service.
     *
     * @param groundDisconnector The [GroundDisconnector] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(groundDisconnector: GroundDisconnector): Boolean = super.remove(groundDisconnector)

    /**
     * Add the [GroundingImpedance] to this service.
     *
     * @param groundingImpedance The [GroundingImpedance] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(groundingImpedance: GroundingImpedance): Boolean = super.add(groundingImpedance)

    /**
     * Remove the [GroundingImpedance] from this service.
     *
     * @param groundingImpedance The [GroundingImpedance] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(groundingImpedance: GroundingImpedance): Boolean = super.remove(groundingImpedance)

    /**
     * Add the [Jumper] to this service.
     *
     * @param jumper The [Jumper] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(jumper: Jumper): Boolean = super.add(jumper)

    /**
     * Remove the [Jumper] from this service.
     *
     * @param jumper The [Jumper] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(jumper: Jumper): Boolean = super.remove(jumper)

    /**
     * Add the [Junction] to this service.
     *
     * @param junction The [Junction] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(junction: Junction): Boolean = super.add(junction)

    /**
     * Remove the [Junction] from this service.
     *
     * @param junction The [Junction] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(junction: Junction): Boolean = super.remove(junction)

    /**
     * Add the [LinearShuntCompensator] to this service.
     *
     * @param linearShuntCompensator The [LinearShuntCompensator] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(linearShuntCompensator: LinearShuntCompensator): Boolean = super.add(linearShuntCompensator)

    /**
     * Remove the [LinearShuntCompensator] from this service.
     *
     * @param linearShuntCompensator The [LinearShuntCompensator] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(linearShuntCompensator: LinearShuntCompensator): Boolean = super.remove(linearShuntCompensator)

    /**
     * Add the [LoadBreakSwitch] to this service.
     *
     * @param loadBreakSwitch The [LoadBreakSwitch] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(loadBreakSwitch: LoadBreakSwitch): Boolean = super.add(loadBreakSwitch)

    /**
     * Remove the [LoadBreakSwitch] from this service.
     *
     * @param loadBreakSwitch The [LoadBreakSwitch] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(loadBreakSwitch: LoadBreakSwitch): Boolean = super.remove(loadBreakSwitch)

    /**
     * Add the [PerLengthPhaseImpedance] to this service.
     *
     * @param perLengthPhaseImpedance The [PerLengthPhaseImpedance] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(perLengthPhaseImpedance: PerLengthPhaseImpedance): Boolean = super.add(perLengthPhaseImpedance)

    /**
     * Remove the [PerLengthPhaseImpedance] from this service.
     *
     * @param perLengthPhaseImpedance The [PerLengthPhaseImpedance] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(perLengthPhaseImpedance: PerLengthPhaseImpedance): Boolean = super.remove(perLengthPhaseImpedance)

    /**
     * Add the [PerLengthSequenceImpedance] to this service.
     *
     * @param perLengthSequenceImpedance The [PerLengthSequenceImpedance] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(perLengthSequenceImpedance: PerLengthSequenceImpedance): Boolean = super.add(perLengthSequenceImpedance)

    /**
     * Remove the [PerLengthSequenceImpedance] from this service.
     *
     * @param perLengthSequenceImpedance The [PerLengthSequenceImpedance] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(perLengthSequenceImpedance: PerLengthSequenceImpedance): Boolean = super.remove(perLengthSequenceImpedance)

    /**
     * Add the [PetersenCoil] to this service.
     *
     * @param petersenCoil The [PetersenCoil] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(petersenCoil: PetersenCoil): Boolean = super.add(petersenCoil)

    /**
     * Remove the [PetersenCoil] from this service.
     *
     * @param petersenCoil The [PetersenCoil] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(petersenCoil: PetersenCoil): Boolean = super.remove(petersenCoil)

    /**
     * Add the [PowerElectronicsConnection] to this service.
     *
     * @param powerElectronicsConnection The [PowerElectronicsConnection] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(powerElectronicsConnection: PowerElectronicsConnection): Boolean = super.add(powerElectronicsConnection)

    /**
     * Remove the [PowerElectronicsConnection] from this service.
     *
     * @param powerElectronicsConnection The [PowerElectronicsConnection] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(powerElectronicsConnection: PowerElectronicsConnection): Boolean = super.remove(powerElectronicsConnection)

    /**
     * Add the [PowerElectronicsConnectionPhase] to this service.
     *
     * @param powerElectronicsConnectionPhase The [PowerElectronicsConnectionPhase] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(powerElectronicsConnectionPhase: PowerElectronicsConnectionPhase): Boolean = super.add(powerElectronicsConnectionPhase)

    /**
     * Remove the [PowerElectronicsConnectionPhase] from this service.
     *
     * @param powerElectronicsConnectionPhase The [PowerElectronicsConnectionPhase] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(powerElectronicsConnectionPhase: PowerElectronicsConnectionPhase): Boolean = super.remove(powerElectronicsConnectionPhase)

    /**
     * Add the [PowerTransformer] to this service.
     *
     * @param powerTransformer The [PowerTransformer] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(powerTransformer: PowerTransformer): Boolean = super.add(powerTransformer)

    /**
     * Remove the [PowerTransformer] from this service.
     *
     * @param powerTransformer The [PowerTransformer] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(powerTransformer: PowerTransformer): Boolean = super.remove(powerTransformer)

    /**
     * Add the [PowerTransformerEnd] to this service.
     *
     * @param powerTransformerEnd The [PowerTransformerEnd] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(powerTransformerEnd: PowerTransformerEnd): Boolean = super.add(powerTransformerEnd)

    /**
     * Remove the [PowerTransformerEnd] from this service.
     *
     * @param powerTransformerEnd The [PowerTransformerEnd] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(powerTransformerEnd: PowerTransformerEnd): Boolean = super.remove(powerTransformerEnd)

    /**
     * Add the [RatioTapChanger] to this service.
     *
     * @param ratioTapChanger The [RatioTapChanger] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(ratioTapChanger: RatioTapChanger): Boolean = super.add(ratioTapChanger)

    /**
     * Remove the [RatioTapChanger] from this service.
     *
     * @param ratioTapChanger The [RatioTapChanger] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(ratioTapChanger: RatioTapChanger): Boolean = super.remove(ratioTapChanger)

    /**
     * Add the [ReactiveCapabilityCurve] to this service.
     *
     * @param reactiveCapabilityCurve The [ReactiveCapabilityCurve] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(reactiveCapabilityCurve: ReactiveCapabilityCurve): Boolean = super.add(reactiveCapabilityCurve)

    /**
     * Remove the [ReactiveCapabilityCurve] from this service.
     *
     * @param reactiveCapabilityCurve The [ReactiveCapabilityCurve] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(reactiveCapabilityCurve: ReactiveCapabilityCurve): Boolean = super.remove(reactiveCapabilityCurve)

    /**
     * Add the [Recloser] to this service.
     *
     * @param recloser The [Recloser] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(recloser: Recloser): Boolean = super.add(recloser)

    /**
     * Remove the [Recloser] from this service.
     *
     * @param recloser The [Recloser] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(recloser: Recloser): Boolean = super.remove(recloser)

    /**
     * Add the [SeriesCompensator] to this service.
     *
     * @param seriesCompensator The [SeriesCompensator] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(seriesCompensator: SeriesCompensator): Boolean = super.add(seriesCompensator)

    /**
     * Remove the [SeriesCompensator] from this service.
     *
     * @param seriesCompensator The [SeriesCompensator] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(seriesCompensator: SeriesCompensator): Boolean = super.remove(seriesCompensator)

    /**
     * Add the [StaticVarCompensator] to this service.
     *
     * @param staticVarCompensator The [StaticVarCompensator] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(staticVarCompensator: StaticVarCompensator): Boolean = super.add(staticVarCompensator)

    /**
     * Remove the [StaticVarCompensator] from this service.
     *
     * @param staticVarCompensator The [StaticVarCompensator] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(staticVarCompensator: StaticVarCompensator): Boolean = super.remove(staticVarCompensator)

    /**
     * Add the [SynchronousMachine] to this service.
     *
     * @param synchronousMachine The [SynchronousMachine] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(synchronousMachine: SynchronousMachine): Boolean = super.add(synchronousMachine)

    /**
     * Remove the [SynchronousMachine] from this service.
     *
     * @param synchronousMachine The [SynchronousMachine] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(synchronousMachine: SynchronousMachine): Boolean = super.remove(synchronousMachine)

    /**
     * Add the [TapChangerControl] to this service.
     *
     * @param tapChangerControl The [TapChangerControl] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(tapChangerControl: TapChangerControl): Boolean = super.add(tapChangerControl)

    /**
     * Remove the [TapChangerControl] from this service.
     *
     * @param tapChangerControl The [TapChangerControl] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(tapChangerControl: TapChangerControl): Boolean = super.remove(tapChangerControl)

    /**
     * Add the [TransformerStarImpedance] to this service.
     *
     * @param transformerStarImpedance The [TransformerStarImpedance] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(transformerStarImpedance: TransformerStarImpedance): Boolean = super.add(transformerStarImpedance)

    /**
     * Remove the [TransformerStarImpedance] from this service.
     *
     * @param transformerStarImpedance The [TransformerStarImpedance] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(transformerStarImpedance: TransformerStarImpedance): Boolean = super.remove(transformerStarImpedance)

    // ###############################
    // # IEC61970 InfIEC61970 Feeder #
    // ###############################

    /**
     * Add the [Circuit] to this service.
     *
     * @param circuit The [Circuit] to add.
     * @return `true` if the item was added to the service, otherwise false.
     */
    fun add(circuit: Circuit): Boolean = super.add(circuit)

    /**
     * Remove the [Circuit] from this service.
     *
     * @param circuit The [Circuit] to remove.
     * @return `true` if the item was removed from the service, otherwise false.
     */
    fun remove(circuit: Circuit): Boolean = super.remove(circuit)

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

    /**
     * Connect two terminals together. This will create a connectivity node if one is required.
     *
     * @param terminal1 The first [Terminal] to connect.
     * @param terminal2 The second [Terminal] to connect.
     * @return `true` if the terminals could be connected. `false` if both terminals were already connected to different [ConnectivityNode]s.
     */
    fun connect(terminal1: Terminal, terminal2: Terminal): Boolean {
        val status = attemptToReuseConnection(terminal1, terminal2)
        if (status == ProcessStatus.PROCESSED) return true
        else if (status == ProcessStatus.INVALID) return false

        val connectivityNode = _connectivityNodes.computeIfAbsent(generateConnectivityNodeId()) { mRID: String -> createConnectivityNode(mRID) }
        connect(terminal2, connectivityNode)
        connect(terminal1, connectivityNode)

        return true
    }

    /**
     * Connect a [Terminal] to a [ConnectivityNode] with the specified mRID. This will create a connectivity node if one is required.
     *
     * @param terminal The [Terminal] to connect.
     * @param connectivityNodeId The mRID of the [ConnectivityNode] to connect.
     * @return `true` if the terminal was connected. `false` if the terminal was already connected to different [ConnectivityNode].
     */
    fun connect(terminal: Terminal, connectivityNodeId: String?): Boolean {
        if (connectivityNodeId.isNullOrBlank()) return false

        var connectivityNode = terminal.connectivityNode
        if (connectivityNode != null) return connectivityNodeId == connectivityNode.mRID

        connectivityNode = _connectivityNodes.computeIfAbsent(connectivityNodeId) { mRID: String -> createConnectivityNode(mRID) }
        connect(terminal, connectivityNode)
        return true
    }

    /**
     * Disconnect a [Terminal] from its [ConnectivityNode]. This will remove the connectivity node if this was the last connected terminal.
     *
     * @param terminal The [Terminal] to disconnect.
     */
    fun disconnect(terminal: Terminal) {
        val connectivityNode = terminal.connectivityNode ?: return
        connectivityNode.removeTerminal(terminal)
        terminal.disconnect()

        if (connectivityNode.numTerminals() == 0) _connectivityNodes.remove(connectivityNode.mRID)
    }

    /**
     * Disconnect all [Terminal]s from a [ConnectivityNode]. This will remove the connectivity node.
     *
     * @param connectivityNodeId The [ConnectivityNode] to disconnect and remove.
     */
    fun disconnect(connectivityNodeId: String) {
        val connectivityNode = _connectivityNodes[connectivityNodeId] ?: return

        connectivityNode.terminals.forEach { it.disconnect() }
        connectivityNode.clearTerminals()

        _connectivityNodes.remove(connectivityNode.mRID)
    }

    private fun createConnectivityNode(mRID: String = ""): ConnectivityNode {
        return ConnectivityNode(mRID)
    }

    /**
     * Check to see if a connectivity node is being used by this service.
     */
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
            TerminalConnectivityConnected.connectedTerminals(terminal, phases)

    }

}
