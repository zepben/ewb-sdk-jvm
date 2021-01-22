/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.common

import com.zepben.evolve.cim.iec61968.assetinfo.PowerTransformerInfo
import com.zepben.evolve.cim.iec61968.assetinfo.WireInfo
import com.zepben.evolve.cim.iec61968.assets.Asset
import com.zepben.evolve.cim.iec61968.assets.AssetOrganisationRole
import com.zepben.evolve.cim.iec61968.assets.Pole
import com.zepben.evolve.cim.iec61968.assets.Streetlight
import com.zepben.evolve.cim.iec61968.common.Location
import com.zepben.evolve.cim.iec61968.common.Organisation
import com.zepben.evolve.cim.iec61968.common.OrganisationRole
import com.zepben.evolve.cim.iec61968.customers.Customer
import com.zepben.evolve.cim.iec61968.customers.CustomerAgreement
import com.zepben.evolve.cim.iec61968.customers.PricingStructure
import com.zepben.evolve.cim.iec61968.customers.Tariff
import com.zepben.evolve.cim.iec61968.metering.EndDevice
import com.zepben.evolve.cim.iec61968.metering.UsagePoint
import com.zepben.evolve.cim.iec61968.operations.OperationalRestriction
import com.zepben.evolve.cim.iec61970.base.auxiliaryequipment.AuxiliaryEquipment
import com.zepben.evolve.cim.iec61970.base.core.*
import com.zepben.evolve.cim.iec61970.base.diagramlayout.Diagram
import com.zepben.evolve.cim.iec61970.base.diagramlayout.DiagramObject
import com.zepben.evolve.cim.iec61970.base.meas.Control
import com.zepben.evolve.cim.iec61970.base.meas.Measurement
import com.zepben.evolve.cim.iec61970.base.scada.RemoteControl
import com.zepben.evolve.cim.iec61970.base.scada.RemoteSource
import com.zepben.evolve.cim.iec61970.base.wires.*
import com.zepben.evolve.cim.iec61970.base.wires.generation.production.PowerElectronicsUnit
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Loop

/**
 * These should be used to access [ReferenceResolver] instances for use with [BaseService.resolveOrDeferReference] and
 * [BaseService.getUnresolvedReferenceMrids].
 *
 * The naming pattern for the resolver matches the property name of the reference. E.g. to get the resolver for
 * base voltage for conducting equipment: `Resolvers.baseVoltage(conductingEquip)`.
 */
@Suppress("UNUSED_PARAMETER")
object Resolvers {

    @JvmStatic
    fun perLengthSequenceImpedance(acLineSegment: AcLineSegment): BoundReferenceResolver<AcLineSegment, PerLengthSequenceImpedance> =
        BoundReferenceResolver(acLineSegment, AcLineSegmentToPerLengthSequenceImpedanceResolver, null)

    @JvmStatic
    fun organisationRoles(asset: Asset): BoundReferenceResolver<Asset, AssetOrganisationRole> =
        BoundReferenceResolver(asset, AssetToAssetOrganisationRoleResolver, null)

    @JvmStatic
    fun location(asset: Asset): BoundReferenceResolver<Asset, Location> =
        BoundReferenceResolver(asset, AssetToLocationResolver, null)

    @JvmStatic
    fun terminal(auxiliaryEquipment: AuxiliaryEquipment): BoundReferenceResolver<AuxiliaryEquipment, Terminal> =
        BoundReferenceResolver(auxiliaryEquipment, AuxiliaryEquipmentToTerminalResolver, null)

    @JvmStatic
    fun baseVoltage(conductingEquipment: ConductingEquipment): BoundReferenceResolver<ConductingEquipment, BaseVoltage> =
        BoundReferenceResolver(conductingEquipment, ConductingEquipmentToBaseVoltageResolver, null)

    @JvmStatic
    fun terminals(conductingEquipment: ConductingEquipment): BoundReferenceResolver<ConductingEquipment, Terminal> =
        BoundReferenceResolver(conductingEquipment, ConductingEquipmentToTerminalsResolver, TerminalToConductingEquipmentResolver)

    @JvmStatic
    fun assetInfo(conductor: Conductor): BoundReferenceResolver<Conductor, WireInfo> =
        BoundReferenceResolver(conductor, ConductorToWireInfoResolver, null)

    @JvmStatic
    fun assetInfo(powerTransformer: PowerTransformer): BoundReferenceResolver<PowerTransformer, PowerTransformerInfo> =
        BoundReferenceResolver(powerTransformer, PowerTransformerToPowerTransformerInfoResolver, null)

    @JvmStatic
    fun streetlights(pole: Pole): BoundReferenceResolver<Pole, Streetlight> =
        BoundReferenceResolver(pole, PoleToStreetlightResolver, StreetlightToPoleResolver)

    @JvmStatic
    fun pole(streetlight: Streetlight): BoundReferenceResolver<Streetlight, Pole> =
        BoundReferenceResolver(streetlight, StreetlightToPoleResolver, PoleToStreetlightResolver)

    @JvmStatic
    fun terminals(connectivityNode: ConnectivityNode): BoundReferenceResolver<ConnectivityNode, Terminal> =
        BoundReferenceResolver(connectivityNode, ConnectivityNodeToTerminalResolver, TerminalToConnectivityNodeResolver)

    @JvmStatic
    fun remoteControl(control: Control): BoundReferenceResolver<Control, RemoteControl> =
        BoundReferenceResolver(control, ControlToRemoteControlResolver, RemoteControlToControlResolver)

    @JvmStatic
    fun agreements(customer: Customer): BoundReferenceResolver<Customer, CustomerAgreement> =
        BoundReferenceResolver(customer, CustomerToCustomerAgreementResolver, CustomerAgreementToCustomerResolver)

    @JvmStatic
    fun customer(customerAgreement: CustomerAgreement): BoundReferenceResolver<CustomerAgreement, Customer> =
        BoundReferenceResolver(customerAgreement, CustomerAgreementToCustomerResolver, CustomerToCustomerAgreementResolver)

    @JvmStatic
    fun pricingStructures(customerAgreement: CustomerAgreement): BoundReferenceResolver<CustomerAgreement, PricingStructure> =
        BoundReferenceResolver(customerAgreement, CustomerAgreementToPricingStructureResolver, null)

    @JvmStatic
    fun diagramObjects(diagram: Diagram): BoundReferenceResolver<Diagram, DiagramObject> =
        BoundReferenceResolver(diagram, DiagramToDiagramObjectResolver, DiagramObjectToDiagramResolver)

    @JvmStatic
    fun diagram(diagramObject: DiagramObject): BoundReferenceResolver<DiagramObject, Diagram> =
        BoundReferenceResolver(diagramObject, DiagramObjectToDiagramResolver, DiagramToDiagramObjectResolver)

    @JvmStatic
    fun serviceLocation(endDevice: EndDevice): BoundReferenceResolver<EndDevice, Location> =
        BoundReferenceResolver(endDevice, EndDeviceToServiceLocationResolver, null)

    @JvmStatic
    fun usagePoints(endDevice: EndDevice): BoundReferenceResolver<EndDevice, UsagePoint> =
        BoundReferenceResolver(endDevice, EndDeviceToUsagePointResolver, UsagePointToEndDeviceResolver)

    @JvmStatic
    fun containers(equipment: Equipment): BoundReferenceResolver<Equipment, EquipmentContainer> =
        BoundReferenceResolver(equipment, EquipmentToEquipmentContainerResolver, EquipmentContainerToEquipmentResolver)

    @JvmStatic
    fun currentFeeders(equipment: Equipment): BoundReferenceResolver<Equipment, Feeder> =
        BoundReferenceResolver(equipment, EquipmentToCurrentFeedersResolver, CurrentFeederToEquipmentResolver)

    @JvmStatic
    fun operationalRestrictions(equipment: Equipment): BoundReferenceResolver<Equipment, OperationalRestriction> =
        BoundReferenceResolver(equipment, EquipmentToOperationalRestrictionResolver, OperationalRestrictionToEquipmentResolver)

    @JvmStatic
    fun usagePoints(equipment: Equipment): BoundReferenceResolver<Equipment, UsagePoint> =
        BoundReferenceResolver(equipment, EquipmentToUsagePointResolver, UsagePointToEquipmentResolver)

    @JvmStatic
    fun equipment(equipmentContainer: EquipmentContainer): BoundReferenceResolver<EquipmentContainer, Equipment> =
        BoundReferenceResolver(equipmentContainer, EquipmentContainerToEquipmentResolver, EquipmentToEquipmentContainerResolver)

    @JvmStatic
    fun phases(energyConsumer: EnergyConsumer): BoundReferenceResolver<EnergyConsumer, EnergyConsumerPhase> =
        BoundReferenceResolver(energyConsumer, EnergyConsumerToEnergyConsumerPhaseResolver, EnergyConsumerPhaseToEnergyConsumerResolver)

    @JvmStatic
    fun energyConsumer(energyConsumerPhase: EnergyConsumerPhase): BoundReferenceResolver<EnergyConsumerPhase, EnergyConsumer> =
        BoundReferenceResolver(energyConsumerPhase, EnergyConsumerPhaseToEnergyConsumerResolver, EnergyConsumerToEnergyConsumerPhaseResolver)

    @JvmStatic
    fun phases(energySource: EnergySource): BoundReferenceResolver<EnergySource, EnergySourcePhase> =
        BoundReferenceResolver(energySource, EnergySourceToEnergySourcePhaseResolver, EnergySourcePhaseToEnergySourceResolver)

    @JvmStatic
    fun energySource(energySourcePhase: EnergySourcePhase): BoundReferenceResolver<EnergySourcePhase, EnergySource> =
        BoundReferenceResolver(energySourcePhase, EnergySourcePhaseToEnergySourceResolver, EnergySourceToEnergySourcePhaseResolver)

    @JvmStatic
    fun currentEquipment(feeder: Feeder): BoundReferenceResolver<Feeder, Equipment> =
        BoundReferenceResolver(feeder, CurrentFeederToEquipmentResolver, EquipmentToCurrentFeedersResolver)

    @JvmStatic
    fun normalEnergizingSubstation(feeder: Feeder): BoundReferenceResolver<Feeder, Substation> =
        BoundReferenceResolver(feeder, FeederToNormalEnergizingSubstationResolver, SubstationToNormalEnergizingFeedersResolver)

    @JvmStatic
    fun normalHeadTerminal(feeder: Feeder): BoundReferenceResolver<Feeder, Terminal> =
        BoundReferenceResolver(feeder, FeederToNormalHeadTerminalResolver, null)

    @JvmStatic
    fun subGeographicalRegions(geographicalRegion: GeographicalRegion): BoundReferenceResolver<GeographicalRegion, SubGeographicalRegion> =
        BoundReferenceResolver(geographicalRegion, GeographicalRegionToSubGeographicalRegionResolver, SubGeographicalRegionToGeographicalRegionResolver)

    @JvmStatic
    fun remoteSource(measurement: Measurement): BoundReferenceResolver<Measurement, RemoteSource> =
        BoundReferenceResolver(measurement, MeasurementToRemoteSourceResolver, RemoteSourceToMeasurementResolver)

    @JvmStatic
    fun equipment(operationalRestriction: OperationalRestriction): BoundReferenceResolver<OperationalRestriction, Equipment> =
        BoundReferenceResolver(operationalRestriction, OperationalRestrictionToEquipmentResolver, EquipmentToOperationalRestrictionResolver)

    @JvmStatic
    fun organisation(organisationRole: OrganisationRole): BoundReferenceResolver<OrganisationRole, Organisation> =
        BoundReferenceResolver(organisationRole, OrganisationRoleToOrganisationResolver, null)

    @JvmStatic
    fun location(powerSystemResource: PowerSystemResource): BoundReferenceResolver<PowerSystemResource, Location> =
        BoundReferenceResolver(powerSystemResource, PowerSystemResourceToLocationResolver, null)

    @JvmStatic
    fun ends(powerTransformer: PowerTransformer): BoundReferenceResolver<PowerTransformer, PowerTransformerEnd> =
        BoundReferenceResolver(powerTransformer, PowerTransformerToPowerTransformerEndResolver, PowerTransformerEndToPowerTransformerResolver)

    @JvmStatic
    fun powerTransformer(powerTransformerEnd: PowerTransformerEnd): BoundReferenceResolver<PowerTransformerEnd, PowerTransformer> =
        BoundReferenceResolver(powerTransformerEnd, PowerTransformerEndToPowerTransformerResolver, PowerTransformerToPowerTransformerEndResolver)

    @JvmStatic
    fun tariffs(pricingStructure: PricingStructure): BoundReferenceResolver<PricingStructure, Tariff> =
        BoundReferenceResolver(pricingStructure, PricingStructureToTariffResolver, null)

    @JvmStatic
    fun transformerEnd(ratioTapChanger: RatioTapChanger): BoundReferenceResolver<RatioTapChanger, TransformerEnd> =
        BoundReferenceResolver(ratioTapChanger, RatioTapChangerToTransformerEndResolver, TransformerEndToRatioTapChangerResolver)

    @JvmStatic
    fun control(remoteControl: RemoteControl): BoundReferenceResolver<RemoteControl, Control> =
        BoundReferenceResolver(remoteControl, RemoteControlToControlResolver, ControlToRemoteControlResolver)

    @JvmStatic
    fun measurement(remoteSource: RemoteSource): BoundReferenceResolver<RemoteSource, Measurement> =
        BoundReferenceResolver(remoteSource, RemoteSourceToMeasurementResolver, MeasurementToRemoteSourceResolver)

    @JvmStatic
    fun geographicalRegion(subGeographicalRegion: SubGeographicalRegion): BoundReferenceResolver<SubGeographicalRegion, GeographicalRegion> =
        BoundReferenceResolver(subGeographicalRegion, SubGeographicalRegionToGeographicalRegionResolver, GeographicalRegionToSubGeographicalRegionResolver)

    @JvmStatic
    fun substations(subGeographicalRegion: SubGeographicalRegion): BoundReferenceResolver<SubGeographicalRegion, Substation> =
        BoundReferenceResolver(subGeographicalRegion, SubGeographicalRegionToSubstationResolver, SubstationToSubGeographicalRegionResolver)

    @JvmStatic
    fun normalEnergizingFeeders(substation: Substation): BoundReferenceResolver<Substation, Feeder> =
        BoundReferenceResolver(substation, SubstationToNormalEnergizingFeedersResolver, FeederToNormalEnergizingSubstationResolver)

    @JvmStatic
    fun subGeographicalRegion(substation: Substation): BoundReferenceResolver<Substation, SubGeographicalRegion> =
        BoundReferenceResolver(substation, SubstationToSubGeographicalRegionResolver, SubGeographicalRegionToSubstationResolver)

    @JvmStatic
    fun circuits(substation: Substation): BoundReferenceResolver<Substation, Circuit> =
        BoundReferenceResolver(substation, SubstationToCircuitResolver, CircuitToSubstationResolver)

    @JvmStatic
    fun loops(substation: Substation): BoundReferenceResolver<Substation, Loop> =
        BoundReferenceResolver(substation, SubstationToLoopResolver, LoopToSubstationResolver)

    @JvmStatic
    fun normalEnergizedLoops(substation: Substation): BoundReferenceResolver<Substation, Loop> =
        BoundReferenceResolver(substation, SubstationToEnergizedLoopResolver, LoopToEnergizingSubstationResolver)

    @JvmStatic
    fun conductingEquipment(terminal: Terminal): BoundReferenceResolver<Terminal, ConductingEquipment> =
        BoundReferenceResolver(terminal, TerminalToConductingEquipmentResolver, ConductingEquipmentToTerminalsResolver)

    @JvmStatic
    fun connectivityNode(terminal: Terminal): BoundReferenceResolver<Terminal, ConnectivityNode> =
        BoundReferenceResolver(terminal, TerminalToConnectivityNodeResolver, ConnectivityNodeToTerminalResolver)

    @JvmStatic
    fun baseVoltage(transformerEnd: TransformerEnd): BoundReferenceResolver<TransformerEnd, BaseVoltage> =
        BoundReferenceResolver(transformerEnd, TransformerEndToBaseVoltageResolver, null)

    @JvmStatic
    fun ratioTapChanger(transformerEnd: TransformerEnd): BoundReferenceResolver<TransformerEnd, RatioTapChanger> =
        BoundReferenceResolver(transformerEnd, TransformerEndToRatioTapChangerResolver, RatioTapChangerToTransformerEndResolver)

    @JvmStatic
    fun terminal(transformerEnd: TransformerEnd): BoundReferenceResolver<TransformerEnd, Terminal> =
        BoundReferenceResolver(transformerEnd, TransformerEndToTerminalResolver, null)

    @JvmStatic
    fun endDevices(usagePoint: UsagePoint): BoundReferenceResolver<UsagePoint, EndDevice> =
        BoundReferenceResolver(usagePoint, UsagePointToEndDeviceResolver, EndDeviceToUsagePointResolver)

    @JvmStatic
    fun equipment(usagePoint: UsagePoint): BoundReferenceResolver<UsagePoint, Equipment> =
        BoundReferenceResolver(usagePoint, UsagePointToEquipmentResolver, EquipmentToUsagePointResolver)

    @JvmStatic
    fun usagePointLocation(usagePoint: UsagePoint): BoundReferenceResolver<UsagePoint, Location> =
        BoundReferenceResolver(usagePoint, UsagePointToLocationResolver, null)

    @JvmStatic
    fun loop(circuit: Circuit): BoundReferenceResolver<Circuit, Loop> =
        BoundReferenceResolver(circuit, CircuitToLoopResolver, LoopToCircuitResolver)

    @JvmStatic
    fun endTerminal(circuit: Circuit): BoundReferenceResolver<Circuit, Terminal> =
        BoundReferenceResolver(circuit, CircuitToTerminalResolver, null)

    @JvmStatic
    fun endSubstation(circuit: Circuit): BoundReferenceResolver<Circuit, Substation> =
        BoundReferenceResolver(circuit, CircuitToSubstationResolver, SubstationToCircuitResolver)

    @JvmStatic
    fun circuits(loop: Loop): BoundReferenceResolver<Loop, Circuit> =
        BoundReferenceResolver(loop, LoopToCircuitResolver, CircuitToLoopResolver)

    @JvmStatic
    fun substations(loop: Loop): BoundReferenceResolver<Loop, Substation> =
        BoundReferenceResolver(loop, LoopToSubstationResolver, SubstationToLoopResolver)

    @JvmStatic
    fun normalEnergizingSubstations(loop: Loop): BoundReferenceResolver<Loop, Substation> =
        BoundReferenceResolver(loop, LoopToEnergizingSubstationResolver, SubstationToEnergizedLoopResolver)

    @JvmStatic
    fun powerElectronicsConnection(powerElectronicsUnit: PowerElectronicsUnit): BoundReferenceResolver<PowerElectronicsUnit, PowerElectronicsConnection> =
        BoundReferenceResolver(powerElectronicsUnit, PowerElectronicsUnitToPowerElectronicsConnectionResolver, null)

    @JvmStatic
    fun powerElectronicsConnection(powerElectronicsConnectionPhase: PowerElectronicsConnectionPhase): BoundReferenceResolver<PowerElectronicsConnectionPhase, PowerElectronicsConnection> =
        BoundReferenceResolver(powerElectronicsConnectionPhase, PowerElectronicsConnectionPhaseToPowerElectronicsConnectionResolver, null)

    @JvmStatic
    fun powerElectronicsUnit(powerElectronicsConnection: PowerElectronicsConnection): BoundReferenceResolver<PowerElectronicsConnection, PowerElectronicsUnit> =
        BoundReferenceResolver(powerElectronicsConnection, PowerElectronicsConnectionToPowerElectronicsUnitResolver, null)

    @JvmStatic
    fun powerElectronicsConnectionPhase(powerElectronicsConnection: PowerElectronicsConnection): BoundReferenceResolver<PowerElectronicsConnection, PowerElectronicsConnectionPhase> =
        BoundReferenceResolver(powerElectronicsConnection, PowerElectronicsConnectionToPowerElectronicsConnectionPhaseResolver, null)
}
