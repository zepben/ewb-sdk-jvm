/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.cimbend.database.sqlite.readers

import com.zepben.cimbend.cim.iec61968.assetinfo.CableInfo
import com.zepben.cimbend.cim.iec61968.assetinfo.OverheadWireInfo
import com.zepben.cimbend.cim.iec61968.assetinfo.WireInfo
import com.zepben.cimbend.cim.iec61968.assetinfo.WireMaterialKind
import com.zepben.cimbend.cim.iec61968.assets.*
import com.zepben.cimbend.cim.iec61968.common.Location
import com.zepben.cimbend.cim.iec61968.common.PositionPoint
import com.zepben.cimbend.cim.iec61968.common.StreetAddress
import com.zepben.cimbend.cim.iec61968.common.TownDetail
import com.zepben.cimbend.cim.iec61968.metering.EndDevice
import com.zepben.cimbend.cim.iec61968.metering.Meter
import com.zepben.cimbend.cim.iec61968.metering.UsagePoint
import com.zepben.cimbend.cim.iec61968.operations.OperationalRestriction
import com.zepben.cimbend.cim.iec61970.base.auxiliaryequipment.AuxiliaryEquipment
import com.zepben.cimbend.cim.iec61970.base.auxiliaryequipment.FaultIndicator
import com.zepben.cimbend.cim.iec61970.base.core.*
import com.zepben.cimbend.cim.iec61970.base.domain.UnitSymbol
import com.zepben.cimbend.cim.iec61970.base.meas.*
import com.zepben.cimbend.cim.iec61970.base.scada.RemoteControl
import com.zepben.cimbend.cim.iec61970.base.scada.RemotePoint
import com.zepben.cimbend.cim.iec61970.base.scada.RemoteSource
import com.zepben.cimbend.cim.iec61970.base.wires.*
import com.zepben.cimbend.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.cimbend.cim.iec61970.infiec61970.feeder.Loop
import com.zepben.cimbend.common.extensions.*
import com.zepben.cimbend.database.sqlite.extensions.getNullableDouble
import com.zepben.cimbend.database.sqlite.extensions.getNullableString
import com.zepben.cimbend.database.sqlite.tables.associations.*
import com.zepben.cimbend.database.sqlite.tables.iec61968.assetinfo.TableCableInfo
import com.zepben.cimbend.database.sqlite.tables.iec61968.assetinfo.TableOverheadWireInfo
import com.zepben.cimbend.database.sqlite.tables.iec61968.assetinfo.TableWireInfo
import com.zepben.cimbend.database.sqlite.tables.iec61968.assets.*
import com.zepben.cimbend.database.sqlite.tables.iec61968.common.*
import com.zepben.cimbend.database.sqlite.tables.iec61968.metering.TableEndDevices
import com.zepben.cimbend.database.sqlite.tables.iec61968.metering.TableMeters
import com.zepben.cimbend.database.sqlite.tables.iec61968.metering.TableUsagePoints
import com.zepben.cimbend.database.sqlite.tables.iec61968.operations.TableOperationalRestrictions
import com.zepben.cimbend.database.sqlite.tables.iec61970.base.auxiliaryequipment.TableAuxiliaryEquipment
import com.zepben.cimbend.database.sqlite.tables.iec61970.base.auxiliaryequipment.TableFaultIndicators
import com.zepben.cimbend.database.sqlite.tables.iec61970.base.core.*
import com.zepben.cimbend.database.sqlite.tables.iec61970.base.meas.*
import com.zepben.cimbend.database.sqlite.tables.iec61970.base.scada.TableRemoteControls
import com.zepben.cimbend.database.sqlite.tables.iec61970.base.scada.TableRemotePoints
import com.zepben.cimbend.database.sqlite.tables.iec61970.base.scada.TableRemoteSources
import com.zepben.cimbend.database.sqlite.tables.iec61970.base.wires.*
import com.zepben.cimbend.database.sqlite.tables.iec61970.infiec61970.feeder.TableCircuits
import com.zepben.cimbend.database.sqlite.tables.iec61970.infiec61970.feeder.TableLoops
import com.zepben.cimbend.network.NetworkService
import com.zepben.cimbend.network.model.LoopSubstationRelationship
import java.sql.ResultSet

@Suppress("SameParameterValue")
class NetworkCIMReader(private val networkService: NetworkService) : BaseCIMReader(networkService) {

    /************ IEC61968 ASSET INFO ************/

    fun load(table: TableCableInfo, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val cableInfo = CableInfo(setLastMRID(resultSet.getString(table.MRID.queryIndex)))

        return loadWireInfo(cableInfo, table, resultSet) && networkService.addOrThrow(cableInfo)
    }

    fun load(table: TableOverheadWireInfo, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val overheadWireInfo = OverheadWireInfo(setLastMRID(resultSet.getString(table.MRID.queryIndex)))

        return loadWireInfo(overheadWireInfo, table, resultSet) && networkService.addOrThrow(overheadWireInfo)
    }

    private fun loadWireInfo(wireInfo: WireInfo, table: TableWireInfo, resultSet: ResultSet): Boolean {
        wireInfo.apply {
            ratedCurrent = resultSet.getInt(table.RATED_CURRENT.queryIndex)
            material = WireMaterialKind.valueOf(resultSet.getString(table.MATERIAL.queryIndex))
        }

        return loadAssetInfo(wireInfo, table, resultSet)
    }

    /************ IEC61968 ASSETS ************/

    private fun loadAsset(asset: Asset, table: TableAssets, resultSet: ResultSet): Boolean {
        asset.apply {
            location =
                networkService.ensureGet(resultSet.getNullableString(table.LOCATION_MRID.queryIndex), typeNameAndMRID())
        }
        return loadIdentifiedObject(asset, table, resultSet)
    }

    private fun loadAssetContainer(assetContainer: AssetContainer, table: TableAssetContainers, resultSet: ResultSet): Boolean {
        return loadAsset(assetContainer, table, resultSet)
    }

    private fun loadAssetInfo(assetInfo: AssetInfo, table: TableAssetInfo, resultSet: ResultSet): Boolean {
        return loadIdentifiedObject(assetInfo, table, resultSet)
    }

    private fun loadAssetOrganisationRole(
        assetOrganisationRole: AssetOrganisationRole,
        table: TableAssetOrganisationRoles,
        resultSet: ResultSet
    ): Boolean {
        return loadOrganisationRole(assetOrganisationRole, table, resultSet)
    }

    private fun loadStructure(
        structure: Structure,
        table: TableStructures,
        resultSet: ResultSet
    ): Boolean {
        return loadAssetContainer(structure, table, resultSet)
    }

    fun load(table: TableAssetOwners, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val assetOwner = AssetOwner(setLastMRID(resultSet.getString(table.MRID.queryIndex)))

        return loadAssetOrganisationRole(assetOwner, table, resultSet) && networkService.addOrThrow(assetOwner)
    }

    fun load(table: TablePoles, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val pole = Pole(setLastMRID(resultSet.getString(table.MRID.queryIndex)))

        pole.classification = resultSet.getString(table.CLASSIFICATION.queryIndex).emptyIfNull().internEmpty()

        return loadStructure(pole, table, resultSet) && networkService.addOrThrow(pole)
    }

    fun load(table: TableStreetlights, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val streetlight = Streetlight(setLastMRID(resultSet.getString(table.MRID.queryIndex))).apply {
            lampKind = StreetlightLampKind.valueOf(resultSet.getString(table.LAMP_KIND.queryIndex))
            lightRating = resultSet.getInt(table.LIGHT_RATING.queryIndex)
            pole = networkService.ensureGet(resultSet.getString(table.POLE_MRID.queryIndex), typeNameAndMRID())
            pole?.addStreetlight(this)
        }

        return loadAsset(streetlight, table, resultSet) && networkService.addOrThrow(streetlight)
    }

    /************ IEC61968 COMMON ************/

    fun load(table: TableLocations, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val location = Location(setLastMRID(resultSet.getString(table.MRID.queryIndex)))

        return loadIdentifiedObject(location, table, resultSet) && networkService.addOrThrow(location)
    }

    fun load(table: TableLocationStreetAddresses, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val locationMRID = setLastMRID(resultSet.getString(table.LOCATION_MRID.queryIndex))
        val field = TableLocationStreetAddressField.valueOf(resultSet.getString(table.ADDRESS_FIELD.queryIndex))

        val id = setLastMRID("$locationMRID-to-$field")
        val location = networkService.getOrThrow<Location>(locationMRID, "Location to StreetAddress association $id")

        when (field) {
            TableLocationStreetAddressField.mainAddress -> location.mainAddress = loadStreetAddress(table, resultSet)
        }

        return true
    }

    fun load(table: TablePositionPoints, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val locationMRID = setLastMRID(resultSet.getString(table.LOCATION_MRID.queryIndex))
        val sequenceNumber = resultSet.getInt(table.SEQUENCE_NUMBER.queryIndex)

        val id = setLastMRID("$locationMRID-point$sequenceNumber")
        val location = networkService.getOrThrow<Location>(locationMRID, "Location to PositionPoint association $id")

        location.addPoint(
            PositionPoint(
                resultSet.getDouble(table.X_POSITION.queryIndex),
                resultSet.getDouble(table.Y_POSITION.queryIndex)
            ),
            sequenceNumber
        )

        return true
    }

    private fun loadStreetAddress(table: TableStreetAddresses, resultSet: ResultSet): StreetAddress {
        return StreetAddress(
            resultSet.getString(table.POSTAL_CODE.queryIndex).emptyIfNull().internEmpty(),
            loadTownDetail(table, resultSet)
        )
    }

    private fun loadTownDetail(table: TableTownDetails, resultSet: ResultSet): TownDetail? {
        val townName = resultSet.getNullableString(table.TOWN_NAME.queryIndex)
        val stateOrProvince = resultSet.getNullableString(table.STATE_OR_PROVINCE.queryIndex)

        if ((townName == null) && (stateOrProvince == null))
            return null

        return TownDetail(townName ?: "", stateOrProvince ?: "")
    }

    /************ IEC61968 METERING ************/

    private fun loadEndDevice(endDevice: EndDevice, table: TableEndDevices, resultSet: ResultSet): Boolean {
        endDevice.apply {
            customerMRID = resultSet.getNullableString(table.CUSTOMER_MRID.queryIndex)
            serviceLocation = networkService.ensureGet(
                resultSet.getNullableString(table.SERVICE_LOCATION_MRID.queryIndex),
                typeNameAndMRID()
            )
        }

        return loadAssetContainer(endDevice, table, resultSet)
    }

    fun load(table: TableMeters, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val meter = Meter(setLastMRID(resultSet.getString(table.MRID.queryIndex)))

        return loadEndDevice(meter, table, resultSet) && networkService.addOrThrow(meter)
    }

    fun load(table: TableUsagePoints, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val usagePoint = UsagePoint(setLastMRID(resultSet.getString(table.MRID.queryIndex))).apply {
            usagePointLocation =
                networkService.ensureGet(resultSet.getNullableString(table.LOCATION_MRID.queryIndex), typeNameAndMRID())
        }

        return loadIdentifiedObject(usagePoint, table, resultSet) && networkService.addOrThrow(usagePoint)
    }

    /************ IEC61968 OPERATIONS ************/

    fun load(table: TableOperationalRestrictions, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val operationalRestriction = OperationalRestriction(setLastMRID(resultSet.getString(table.MRID.queryIndex)))

        return loadDocument(operationalRestriction, table, resultSet) && networkService.addOrThrow(operationalRestriction)
    }

    /************ IEC61970 AUXILIARY EQUIPMENT ************/

    private fun loadAuxiliaryEquipment(
        auxiliaryEquipment: AuxiliaryEquipment,
        table: TableAuxiliaryEquipment,
        resultSet: ResultSet
    ): Boolean {
        auxiliaryEquipment.apply {
            terminal =
                networkService.ensureGet(resultSet.getNullableString(table.TERMINAL_MRID.queryIndex), typeNameAndMRID())
        }

        return loadEquipment(auxiliaryEquipment, table, resultSet)
    }

    fun load(table: TableFaultIndicators, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val faultIndicator = FaultIndicator(setLastMRID(resultSet.getString(table.MRID.queryIndex)))

        return loadAuxiliaryEquipment(faultIndicator, table, resultSet) && networkService.addOrThrow(faultIndicator)
    }

    /************ IEC61970 CORE ************/

    private fun loadAcDcTerminal(acDcTerminal: AcDcTerminal, table: TableAcDcTerminals, resultSet: ResultSet): Boolean {
        return loadIdentifiedObject(acDcTerminal, table, resultSet)
    }

    fun load(table: TableBaseVoltages, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val baseVoltage = BaseVoltage(setLastMRID(resultSet.getString(table.MRID.queryIndex))).apply {
            nominalVoltage = resultSet.getInt(table.NOMINAL_VOLTAGE.queryIndex)
        }

        return loadIdentifiedObject(baseVoltage, table, resultSet) && networkService.addOrThrow(baseVoltage)
    }

    private fun loadConductingEquipment(
        conductingEquipment: ConductingEquipment,
        table: TableConductingEquipment,
        resultSet: ResultSet
    ): Boolean {
        conductingEquipment.apply {
            baseVoltage = networkService.ensureGet(
                resultSet.getNullableString(table.BASE_VOLTAGE_MRID.queryIndex),
                typeNameAndMRID()
            )
        }

        return loadEquipment(conductingEquipment, table, resultSet)
    }

    fun load(table: TableConnectivityNodes, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val connectivityNode = ConnectivityNode(setLastMRID(resultSet.getString(table.MRID.queryIndex)))

        return loadIdentifiedObject(connectivityNode, table, resultSet) && networkService.addOrThrow(connectivityNode)
    }

    private fun loadConnectivityNodeContainer(
        connectivityNodeContainer: ConnectivityNodeContainer,
        table: TableConnectivityNodeContainers,
        resultSet: ResultSet
    ): Boolean {
        return loadPowerSystemResource(connectivityNodeContainer, table, resultSet)
    }

    private fun loadEquipment(equipment: Equipment, table: TableEquipment, resultSet: ResultSet): Boolean {
        equipment.apply {
            normallyInService = resultSet.getBoolean(table.NORMALLY_IN_SERVICE.queryIndex)
            inService = resultSet.getBoolean(table.IN_SERVICE.queryIndex)
        }

        return loadPowerSystemResource(equipment, table, resultSet)
    }

    private fun loadEquipmentContainer(equipmentContainer: EquipmentContainer, table: TableEquipmentContainers, resultSet: ResultSet): Boolean {
        return loadConnectivityNodeContainer(equipmentContainer, table, resultSet)
    }

    fun load(table: TableFeeders, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val feeder = Feeder(setLastMRID(resultSet.getString(table.MRID.queryIndex))).apply {
            normalHeadTerminal = networkService.ensureGet(
                resultSet.getNullableString(table.NORMAL_HEAD_TERMINAL_MRID.queryIndex),
                typeNameAndMRID()
            )
            normalEnergizingSubstation =
                networkService.ensureGet(
                    resultSet.getNullableString(table.NORMAL_ENERGIZING_SUBSTATION_MRID.queryIndex),
                    typeNameAndMRID()
                )
            normalEnergizingSubstation?.addFeeder(this)
        }

        return loadEquipmentContainer(feeder, table, resultSet) && networkService.addOrThrow(feeder)
    }

    fun load(table: TableGeographicalRegions, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val geographicalRegion = GeographicalRegion(setLastMRID(resultSet.getString(table.MRID.queryIndex)))

        return loadIdentifiedObject(geographicalRegion, table, resultSet) && networkService.addOrThrow(geographicalRegion)
    }

    private fun loadPowerSystemResource(
        powerSystemResource: PowerSystemResource,
        table: TablePowerSystemResources,
        resultSet: ResultSet
    ): Boolean {
        powerSystemResource.apply {
            location =
                networkService.ensureGet(resultSet.getNullableString(table.LOCATION_MRID.queryIndex), typeNameAndMRID())
            numControls = resultSet.getInt(table.NUM_CONTROLS.queryIndex)
        }

        return loadIdentifiedObject(powerSystemResource, table, resultSet)
    }

    fun load(table: TableSites, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val site = Site(setLastMRID(resultSet.getString(table.MRID.queryIndex)))

        return loadEquipmentContainer(site, table, resultSet) && networkService.addOrThrow(site)
    }

    fun load(table: TableSubGeographicalRegions, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val subGeographicalRegion =
            SubGeographicalRegion(setLastMRID(resultSet.getString(table.MRID.queryIndex))).apply {
                geographicalRegion = networkService.ensureGet(
                    resultSet.getNullableString(table.GEOGRAPHICAL_REGION_MRID.queryIndex),
                    typeNameAndMRID()
                )
                geographicalRegion?.addSubGeographicalRegion(this)
            }

        return loadIdentifiedObject(subGeographicalRegion, table, resultSet) && networkService.addOrThrow(
            subGeographicalRegion
        )
    }

    fun load(table: TableSubstations, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val substation = Substation(setLastMRID(resultSet.getString(table.MRID.queryIndex))).apply {
            subGeographicalRegion = networkService.ensureGet(
                resultSet.getNullableString(table.SUB_GEOGRAPHICAL_REGION_MRID.queryIndex),
                typeNameAndMRID()
            )
            subGeographicalRegion?.addSubstation(this)
        }

        return loadEquipmentContainer(substation, table, resultSet) && networkService.addOrThrow(substation)
    }

    fun load(table: TableTerminals, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val terminal = Terminal(setLastMRID(resultSet.getString(table.MRID.queryIndex))).apply {
            sequenceNumber = resultSet.getInt(table.SEQUENCE_NUMBER.queryIndex)
            conductingEquipment = networkService.ensureGet(
                resultSet.getNullableString(table.CONDUCTING_EQUIPMENT_MRID.queryIndex),
                typeNameAndMRID()
            )
            conductingEquipment?.addTerminal(this)
            phases = PhaseCode.valueOf(resultSet.getString(table.PHASES.queryIndex))
        }

        networkService.connect(terminal, resultSet.getNullableString(table.CONNECTIVITY_NODE_MRID.queryIndex))

        return loadAcDcTerminal(terminal, table, resultSet) && networkService.addOrThrow(terminal)
    }

    /************ IEC61970 WIRES ************/

    fun load(table: TableAcLineSegments, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val acLineSegment = AcLineSegment(setLastMRID(resultSet.getString(table.MRID.queryIndex))).apply {
            perLengthSequenceImpedance =
                networkService.ensureGet(
                    resultSet.getNullableString(table.PER_LENGTH_SEQUENCE_IMPEDANCE_MRID.queryIndex),
                    typeNameAndMRID()
                )
        }

        return loadConductor(acLineSegment, table, resultSet) && networkService.addOrThrow(acLineSegment)
    }

    fun load(table: TableBreakers, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val breaker = Breaker(setLastMRID(resultSet.getString(table.MRID.queryIndex)))

        return loadProtectedSwitch(breaker, table, resultSet) && networkService.addOrThrow(breaker)
    }

    private fun loadConductor(conductor: Conductor, table: TableConductors, resultSet: ResultSet): Boolean {
        conductor.apply {
            length = resultSet.getNullableDouble(table.LENGTH.queryIndex)
            assetInfo = networkService.ensureGet(
                resultSet.getNullableString(table.WIRE_INFO_MRID.queryIndex),
                typeNameAndMRID()
            )
        }

        return loadConductingEquipment(conductor, table, resultSet)
    }

    private fun loadConnector(connector: Connector, table: TableConnectors, resultSet: ResultSet): Boolean {
        return loadConductingEquipment(connector, table, resultSet)
    }

    fun load(table: TableDisconnectors, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val disconnector = Disconnector(setLastMRID(resultSet.getString(table.MRID.queryIndex)))

        return loadSwitch(disconnector, table, resultSet) && networkService.addOrThrow(disconnector)
    }

    private fun loadEnergyConnection(
        energyConnection: EnergyConnection,
        table: TableEnergyConnections,
        resultSet: ResultSet
    ): Boolean {
        return loadConductingEquipment(energyConnection, table, resultSet)
    }

    fun load(table: TableEnergyConsumers, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val energyConsumer = EnergyConsumer(setLastMRID(resultSet.getString(table.MRID.queryIndex))).apply {
            customerCount = resultSet.getInt(table.CUSTOMER_COUNT.queryIndex)
            grounded = resultSet.getBoolean(table.GROUNDED.queryIndex)
            p = resultSet.getDouble(table.P.queryIndex)
            q = resultSet.getDouble(table.Q.queryIndex)
            pFixed = resultSet.getDouble(table.P_FIXED.queryIndex)
            qFixed = resultSet.getDouble(table.Q_FIXED.queryIndex)
            phaseConnection = PhaseShuntConnectionKind.valueOf(resultSet.getString(table.PHASE_CONNECTION.queryIndex))
        }

        return loadEnergyConnection(energyConsumer, table, resultSet) && networkService.addOrThrow(energyConsumer)
    }

    fun load(table: TableEnergyConsumerPhases, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val energyConsumerPhase = EnergyConsumerPhase(setLastMRID(resultSet.getString(table.MRID.queryIndex))).apply {
            energyConsumer =
                networkService.ensureGet(resultSet.getString(table.ENERGY_CONSUMER_MRID.queryIndex), typeNameAndMRID())
            energyConsumer?.addPhase(this)

            phase = SinglePhaseKind.valueOf(resultSet.getString(table.PHASE.queryIndex))
            p = resultSet.getDouble(table.P.queryIndex)
            q = resultSet.getDouble(table.Q.queryIndex)
            pFixed = resultSet.getDouble(table.P_FIXED.queryIndex)
            qFixed = resultSet.getDouble(table.Q_FIXED.queryIndex)
        }

        return loadPowerSystemResource(energyConsumerPhase, table, resultSet) && networkService.addOrThrow(
            energyConsumerPhase
        )
    }

    fun load(table: TableEnergySources, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val energySource = EnergySource(setLastMRID(resultSet.getString(table.MRID.queryIndex))).apply {
            activePower = resultSet.getDouble(table.ACTIVE_POWER.queryIndex)
            reactivePower = resultSet.getDouble(table.REACTIVE_POWER.queryIndex)
            voltageAngle = resultSet.getDouble(table.VOLTAGE_ANGLE.queryIndex)
            voltageMagnitude = resultSet.getDouble(table.VOLTAGE_MAGNITUDE.queryIndex)
            pMax = resultSet.getDouble(table.P_MAX.queryIndex)
            pMin = resultSet.getDouble(table.P_MIN.queryIndex)
            r = resultSet.getDouble(table.R.queryIndex)
            r0 = resultSet.getDouble(table.R0.queryIndex)
            rn = resultSet.getDouble(table.RN.queryIndex)
            x = resultSet.getDouble(table.X.queryIndex)
            x0 = resultSet.getDouble(table.X0.queryIndex)
            xn = resultSet.getDouble(table.XN.queryIndex)
        }

        return loadEnergyConnection(energySource, table, resultSet) && networkService.addOrThrow(energySource)
    }

    fun load(table: TableEnergySourcePhases, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val energySourcePhase = EnergySourcePhase(setLastMRID(resultSet.getString(table.MRID.queryIndex))).apply {
            energySource =
                networkService.ensureGet(resultSet.getString(table.ENERGY_SOURCE_MRID.queryIndex), typeNameAndMRID())
            energySource?.addPhase(this)

            phase = SinglePhaseKind.valueOf(resultSet.getString(table.PHASE.queryIndex))
        }

        return loadPowerSystemResource(energySourcePhase, table, resultSet) && networkService.addOrThrow(
            energySourcePhase
        )
    }

    fun load(table: TableFuses, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val fuse = Fuse(setLastMRID(resultSet.getString(table.MRID.queryIndex)))

        return loadSwitch(fuse, table, resultSet) && networkService.addOrThrow(fuse)
    }

    fun load(table: TableJumpers, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val jumper = Jumper(setLastMRID(resultSet.getString(table.MRID.queryIndex)))

        return loadSwitch(jumper, table, resultSet) && networkService.addOrThrow(jumper)
    }

    fun load(table: TableJunctions, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val junction = Junction(setLastMRID(resultSet.getString(table.MRID.queryIndex)))

        return loadConnector(junction, table, resultSet) && networkService.addOrThrow(junction)
    }

    private fun loadLine(line: Line, table: TableLines, resultSet: ResultSet): Boolean {
        return loadEquipmentContainer(line, table, resultSet)
    }

    fun load(table: TableLinearShuntCompensators, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val linearShuntCompensator =
            LinearShuntCompensator(setLastMRID(resultSet.getString(table.MRID.queryIndex))).apply {
                b0PerSection = resultSet.getDouble(table.B0_PER_SECTION.queryIndex)
                bPerSection = resultSet.getDouble(table.B_PER_SECTION.queryIndex)
                g0PerSection = resultSet.getDouble(table.G0_PER_SECTION.queryIndex)
                gPerSection = resultSet.getDouble(table.G_PER_SECTION.queryIndex)
            }

        return loadShuntCompensator(linearShuntCompensator, table, resultSet) && networkService.addOrThrow(
            linearShuntCompensator
        )
    }

    private fun loadPerLengthImpedance(perLengthImpedance: PerLengthImpedance, table: TablePerLengthImpedances, resultSet: ResultSet): Boolean {
        return loadPerLengthLineParameter(perLengthImpedance, table, resultSet)
    }

    private fun loadPerLengthLineParameter(perLengthLineParameter: PerLengthLineParameter, table: TablePerLengthLineParameters, resultSet: ResultSet): Boolean {
        return loadIdentifiedObject(perLengthLineParameter, table, resultSet)
    }

    fun load(table: TablePerLengthSequenceImpedances, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val perLengthSequenceImpedance =
            PerLengthSequenceImpedance(setLastMRID(resultSet.getString(table.MRID.queryIndex))).apply {
                r = resultSet.getDouble(table.R.queryIndex)
                x = resultSet.getDouble(table.X.queryIndex)
                r0 = resultSet.getDouble(table.R0.queryIndex)
                x0 = resultSet.getDouble(table.X0.queryIndex)
                bch = resultSet.getDouble(table.BCH.queryIndex)
                gch = resultSet.getDouble(table.GCH.queryIndex)
                b0ch = resultSet.getDouble(table.B0CH.queryIndex)
                g0ch = resultSet.getDouble(table.G0CH.queryIndex)
            }

        return loadPerLengthImpedance(perLengthSequenceImpedance, table, resultSet) && networkService.addOrThrow(
            perLengthSequenceImpedance
        )
    }

    fun load(table: TablePowerTransformers, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val powerTransformer = PowerTransformer(setLastMRID(resultSet.getString(table.MRID.queryIndex))).apply {
            vectorGroup = VectorGroup.valueOf(resultSet.getString(table.VECTOR_GROUP.queryIndex))
        }

        return loadConductingEquipment(
            powerTransformer,
            table,
            resultSet
        ) && networkService.addOrThrow(powerTransformer)
    }

    fun load(table: TablePowerTransformerEnds, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val powerTransformerEnd = PowerTransformerEnd(setLastMRID(resultSet.getString(table.MRID.queryIndex))).apply {
            endNumber = resultSet.getInt(table.END_NUMBER.queryIndex)
            powerTransformer = networkService.ensureGet(
                resultSet.getString(table.POWER_TRANSFORMER_MRID.queryIndex),
                typeNameAndMRID()
            )
            powerTransformer?.addEnd(this)

            connectionKind = WindingConnection.valueOf(resultSet.getString(table.CONNECTION_KIND.queryIndex))
            phaseAngleClock = resultSet.getInt(table.PHASE_ANGLE_CLOCK.queryIndex)
            b = resultSet.getDouble(table.B.queryIndex)
            b0 = resultSet.getDouble(table.B0.queryIndex)
            g = resultSet.getDouble(table.G.queryIndex)
            g0 = resultSet.getDouble(table.G0.queryIndex)
            r = resultSet.getDouble(table.R.queryIndex)
            r0 = resultSet.getDouble(table.R0.queryIndex)
            ratedS = resultSet.getInt(table.RATED_S.queryIndex)
            ratedU = resultSet.getInt(table.RATED_U.queryIndex)
            x = resultSet.getDouble(table.X.queryIndex)
            x0 = resultSet.getDouble(table.X0.queryIndex)
        }

        return loadTransformerEnd(powerTransformerEnd, table, resultSet) && networkService.addOrThrow(powerTransformerEnd)
    }

    private fun loadProtectedSwitch(protectedSwitch: ProtectedSwitch, table: TableProtectedSwitches, resultSet: ResultSet): Boolean {
        return loadSwitch(protectedSwitch, table, resultSet)
    }

    fun load(table: TableRatioTapChangers, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val ratioTapChanger = RatioTapChanger(setLastMRID(resultSet.getString(table.MRID.queryIndex))).apply {
            transformerEnd = networkService.ensureGet(
                resultSet.getNullableString(table.TRANSFORMER_END_MRID.queryIndex),
                typeNameAndMRID()
            )
            transformerEnd?.ratioTapChanger = this

            stepVoltageIncrement = resultSet.getDouble(table.STEP_VOLTAGE_INCREMENT.queryIndex)
        }

        return loadTapChanger(ratioTapChanger, table, resultSet) && networkService.addOrThrow(ratioTapChanger)
    }

    fun load(table: TableReclosers, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val recloser = Recloser(setLastMRID(resultSet.getString(table.MRID.queryIndex)))

        return loadProtectedSwitch(recloser, table, resultSet) && networkService.addOrThrow(recloser)
    }

    private fun loadRegulatingCondEq(
        regulatingCondEq: RegulatingCondEq,
        table: TableRegulatingCondEq,
        resultSet: ResultSet
    ): Boolean {
        regulatingCondEq.apply {
            controlEnabled = resultSet.getBoolean(table.CONTROL_ENABLED.queryIndex)
        }

        return loadEnergyConnection(regulatingCondEq, table, resultSet)
    }

    private fun loadShuntCompensator(
        shuntCompensator: ShuntCompensator,
        table: TableShuntCompensators,
        resultSet: ResultSet
    ): Boolean {
        shuntCompensator.apply {
            grounded = resultSet.getBoolean(table.GROUNDED.queryIndex)
            nomU = resultSet.getInt(table.NOM_U.queryIndex)
            phaseConnection = PhaseShuntConnectionKind.valueOf(resultSet.getString(table.PHASE_CONNECTION.queryIndex))
            sections = resultSet.getDouble(table.SECTIONS.queryIndex)
        }

        return loadRegulatingCondEq(shuntCompensator, table, resultSet)
    }

    private fun loadSwitch(switch: Switch, table: TableSwitches, resultSet: ResultSet): Boolean {
        switch.apply {
            normalOpen = resultSet.getInt(table.NORMAL_OPEN.queryIndex)
            open = resultSet.getInt(table.OPEN.queryIndex)
        }

        return loadConductingEquipment(switch, table, resultSet)
    }

    private fun loadTapChanger(tapChanger: TapChanger, table: TableTapChangers, resultSet: ResultSet): Boolean {
        tapChanger.apply {
            controlEnabled = resultSet.getBoolean(table.CONTROL_ENABLED.queryIndex)
            highStep = resultSet.getInt(table.HIGH_STEP.queryIndex)
            lowStep = resultSet.getInt(table.LOW_STEP.queryIndex)
            neutralStep = resultSet.getInt(table.NEUTRAL_STEP.queryIndex)
            neutralU = resultSet.getInt(table.NEUTRAL_U.queryIndex)
            normalStep = resultSet.getInt(table.NORMAL_STEP.queryIndex)
            step = resultSet.getDouble(table.STEP.queryIndex)
        }

        return loadPowerSystemResource(tapChanger, table, resultSet)
    }

    private fun loadTransformerEnd(transformerEnd: TransformerEnd, table: TableTransformerEnds, resultSet: ResultSet): Boolean {
        transformerEnd.apply {
            terminal =
                networkService.ensureGet(resultSet.getNullableString(table.TERMINAL_MRID.queryIndex), typeNameAndMRID())
            baseVoltage = networkService.ensureGet(
                resultSet.getNullableString(table.BASE_VOLTAGE_MRID.queryIndex),
                typeNameAndMRID()
            )
            grounded = resultSet.getBoolean(table.GROUNDED.queryIndex)
            rGround = resultSet.getDouble(table.R_GROUND.queryIndex)
            xGround = resultSet.getDouble(table.X_GROUND.queryIndex)
        }

        return loadIdentifiedObject(transformerEnd, table, resultSet)
    }

    /************ IEC61970 InfIEC61970 ************/

    fun load(table: TableCircuits, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val circuit = Circuit(setLastMRID(resultSet.getString(table.MRID.queryIndex))).apply {
            loop = networkService.ensureGet(resultSet.getNullableString(table.LOOP_MRID.queryIndex), typeNameAndMRID())
            loop?.addCircuit(this)
        }

        return loadLine(circuit, table, resultSet) && networkService.addOrThrow(circuit)
    }

    fun load(table: TableLoops, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val loop = Loop(setLastMRID(resultSet.getString(table.MRID.queryIndex)))

        return loadIdentifiedObject(loop, table, resultSet) && networkService.addOrThrow(loop)
    }

    /************ IEC61970 MEAS ************/
    fun load(table: TableControls, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val control = Control(setLastMRID(resultSet.getString(table.MRID.queryIndex))).apply {
            powerSystemResourceMRID = resultSet.getNullableString(table.POWER_SYSTEM_RESOURCE_MRID.queryIndex)
        }

        return loadIoPoint(control, table, resultSet) && networkService.addOrThrow(control)
    }

    private fun loadIoPoint(ioPoint: IoPoint, table: TableIoPoints, resultSet: ResultSet): Boolean {
        return loadIdentifiedObject(ioPoint, table, resultSet)
    }

    private fun loadMeasurement(measurement: Measurement, table: TableMeasurements, resultSet: ResultSet): Boolean {
        measurement.apply {
            powerSystemResourceMRID = resultSet.getNullableString(table.POWER_SYSTEM_RESOURCE_MRID.queryIndex)
            remoteSource = networkService.ensureGet(
                resultSet.getNullableString(table.REMOTE_SOURCE_MRID.queryIndex),
                typeNameAndMRID()
            )
            remoteSource?.measurement = this
            terminalMRID = resultSet.getNullableString(table.TERMINAL_MRID.queryIndex)
            phases = PhaseCode.valueOf(resultSet.getString(table.PHASES.queryIndex))
            unitSymbol = UnitSymbol.valueOf(resultSet.getString(table.UNIT_SYMBOL.queryIndex))
        }
        return loadIdentifiedObject(measurement, table, resultSet)
    }

    fun load(table: TableAnalogs, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val meas = Analog(setLastMRID(resultSet.getString(table.MRID.queryIndex))).apply {
            positiveFlowIn = resultSet.getBoolean(table.POSITIVE_FLOW_IN.queryIndex)
        }

        return loadMeasurement(meas, table, resultSet) && networkService.addOrThrow(meas)
    }

    fun load(table: TableAccumulators, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val meas = Accumulator(setLastMRID(resultSet.getString(table.MRID.queryIndex)))

        return loadMeasurement(meas, table, resultSet) && networkService.addOrThrow(meas)
    }

    fun load(table: TableDiscretes, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val meas = Discrete(setLastMRID(resultSet.getString(table.MRID.queryIndex)))

        return loadMeasurement(meas, table, resultSet) && networkService.addOrThrow(meas)
    }

    /************ IEC61970 SCADA ************/
    fun load(table: TableRemoteControls, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val remoteControl = RemoteControl(setLastMRID(resultSet.getString(table.MRID.queryIndex))).apply {
            control =
                networkService.ensureGet(resultSet.getNullableString(table.CONTROL_MRID.queryIndex), typeNameAndMRID())
            control?.remoteControl = this
        }

        return loadRemotePoint(remoteControl, table, resultSet) && networkService.addOrThrow(remoteControl)
    }

    private fun loadRemotePoint(remotePoint: RemotePoint, table: TableRemotePoints, resultSet: ResultSet): Boolean {
        return loadIdentifiedObject(remotePoint, table, resultSet)
    }

    fun load(table: TableRemoteSources, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val remoteSource = RemoteSource(setLastMRID(resultSet.getString(table.MRID.queryIndex)))

        return loadRemotePoint(remoteSource, table, resultSet) && networkService.addOrThrow(remoteSource)
    }

    /************ ASSOCIATIONS ************/

    fun load(table: TableAssetOrganisationRolesAssets, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val assetOrganisationRoleMRID = setLastMRID(resultSet.getString(table.ASSET_ORGANISATION_ROLE_MRID.queryIndex))
        setLastMRID("${assetOrganisationRoleMRID}-to-UNKNOWN")

        val assetMRID = resultSet.getString(table.ASSET_MRID.queryIndex)
        val id = setLastMRID("${assetOrganisationRoleMRID}-to-${assetMRID}")

        val typeNameAndMRID = "AssetOrganisationRole to Asset association $id"
        val assetOrganisationRole = networkService.getOrThrow<AssetOrganisationRole>(assetOrganisationRoleMRID, typeNameAndMRID)
        val asset = networkService.getOrThrow<Asset>(assetMRID, typeNameAndMRID)

        asset.addOrganisationRole(assetOrganisationRole)

        return true
    }

    fun load(table: TableEquipmentEquipmentContainers, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val equipmentMRID = setLastMRID(resultSet.getString(table.EQUIPMENT_MRID.queryIndex))
        setLastMRID("${equipmentMRID}-to-UNKNOWN")

        val equipmentContainerMRID = resultSet.getString(table.EQUIPMENT_CONTAINER_MRID.queryIndex)
        val id = setLastMRID("${equipmentMRID}-to-${equipmentContainerMRID}")

        val typeNameAndMRID = "Equipment to EquipmentContainer association $id"
        val equipment = networkService.getOrThrow<Equipment>(equipmentMRID, typeNameAndMRID)
        val equipmentContainer = networkService.getOrThrow<EquipmentContainer>(equipmentContainerMRID, typeNameAndMRID)

        equipmentContainer.addEquipment(equipment)
        equipment.addContainer(equipmentContainer)

        return true
    }

    fun load(table: TableEquipmentOperationalRestrictions, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val equipmentMRID = setLastMRID(resultSet.getString(table.EQUIPMENT_MRID.queryIndex))
        setLastMRID("${equipmentMRID}-to-UNKNOWN")

        val operationalRestrictionMRID = resultSet.getString(table.OPERATIONAL_RESTRICTION_MRID.queryIndex)
        val id = setLastMRID("${equipmentMRID}-to-${operationalRestrictionMRID}")

        val typeNameAndMRID = "Equipment to OperationalRestriction association $id"
        val equipment = networkService.getOrThrow<Equipment>(equipmentMRID, typeNameAndMRID)
        val operationalRestriction = networkService.getOrThrow<OperationalRestriction>(operationalRestrictionMRID, typeNameAndMRID)

        operationalRestriction.addEquipment(equipment)
        equipment.addOperationalRestriction(operationalRestriction)

        return true
    }

    fun load(table: TableEquipmentUsagePoints, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val equipmentMRID = setLastMRID(resultSet.getString(table.EQUIPMENT_MRID.queryIndex))
        setLastMRID("${equipmentMRID}-to-UNKNOWN")

        val usagePointMRID = resultSet.getString(table.USAGE_POINT_MRID.queryIndex)
        val id = setLastMRID("${equipmentMRID}-to-${usagePointMRID}")

        val typeNameAndMRID = "Equipment to UsagePoint association $id"
        val equipment = networkService.getOrThrow<Equipment>(equipmentMRID, typeNameAndMRID)
        val usagePoint = networkService.getOrThrow<UsagePoint>(usagePointMRID, typeNameAndMRID)

        usagePoint.addEquipment(equipment)
        equipment.addUsagePoint(usagePoint)

        return true
    }

    fun load(table: TableUsagePointsEndDevices, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val usagePointMRID = setLastMRID(resultSet.getString(table.USAGE_POINT_MRID.queryIndex))
        setLastMRID("${usagePointMRID}-to-UNKNOWN")

        val endDeviceMRID = resultSet.getString(table.END_DEVICE_MRID.queryIndex)
        val id = setLastMRID("${usagePointMRID}-to-${endDeviceMRID}")

        val typeNameAndMRID = "UsagePoint to EndDevice association $id"
        val usagePoint = networkService.getOrThrow<UsagePoint>(usagePointMRID, typeNameAndMRID)
        val endDevice = networkService.getOrThrow<EndDevice>(endDeviceMRID, typeNameAndMRID)

        endDevice.addUsagePoint(usagePoint)
        usagePoint.addEndDevice(endDevice)

        return true
    }

    fun load(table: TableCircuitsSubstations, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val circuitMRID = setLastMRID(resultSet.getString(table.CIRCUIT_MRID.queryIndex))
        setLastMRID("${circuitMRID}-to-UNKNOWN")

        val substationMRID = resultSet.getString(table.SUBSTATION_MRID.queryIndex)
        val id = setLastMRID("${circuitMRID}-to-${substationMRID}")

        val typeNameAndMRID = "Circuit to Substation association $id"
        val circuit = networkService.getOrThrow<Circuit>(circuitMRID, typeNameAndMRID)
        val substation = networkService.getOrThrow<Substation>(substationMRID, typeNameAndMRID)

        substation.addCircuit(circuit)
        circuit.addEndSubstation(substation)

        return true
    }

    fun load(table: TableCircuitsTerminals, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val circuitMRID = setLastMRID(resultSet.getString(table.CIRCUIT_MRID.queryIndex))
        setLastMRID("${circuitMRID}-to-UNKNOWN")

        val terminalMRID = resultSet.getString(table.TERMINAL_MRID.queryIndex)
        val id = setLastMRID("${circuitMRID}-to-${terminalMRID}")

        val typeNameAndMRID = "Circuit to Terminal association $id"
        val circuit = networkService.getOrThrow<Circuit>(circuitMRID, typeNameAndMRID)
        val terminal = networkService.getOrThrow<Terminal>(terminalMRID, typeNameAndMRID)

        circuit.addEndTerminal(terminal)

        return true
    }

    fun load(table: TableLoopsSubstations, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val assetOrganisationRoleMRID = setLastMRID(resultSet.getString(table.LOOP_MRID.queryIndex))
        setLastMRID("${assetOrganisationRoleMRID}-to-UNKNOWN")

        val assetMRID = resultSet.getString(table.SUBSTATION_MRID.queryIndex)
        val id = setLastMRID("${assetOrganisationRoleMRID}-to-${assetMRID}")

        val typeNameAndMRID = "Loop to Substation association $id"
        val loop = networkService.getOrThrow<Loop>(assetOrganisationRoleMRID, typeNameAndMRID)
        val substation = networkService.getOrThrow<Substation>(assetMRID, typeNameAndMRID)

        when (LoopSubstationRelationship.valueOf(resultSet.getString(table.RELATIONSHIP.queryIndex))) {
            LoopSubstationRelationship.LOOP_ENERGIZES_SUBSTATION -> {
                substation.addLoop(loop)
                loop.addSubstation(substation)
            }
            LoopSubstationRelationship.SUBSTATION_ENERGIZES_LOOP -> {
                substation.addEnergizedLoop(loop)
                loop.addEnergizingSubstation(substation)
            }
        }

        return true
    }

}
