/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.testdata

import com.zepben.evolve.cim.iec61968.assetinfo.PowerTransformerInfo
import com.zepben.evolve.cim.iec61968.assetinfo.TransformerEndInfo
import com.zepben.evolve.cim.iec61968.assetinfo.TransformerTankInfo
import com.zepben.evolve.cim.iec61968.assets.Pole
import com.zepben.evolve.cim.iec61968.assets.Streetlight
import com.zepben.evolve.cim.iec61968.common.Organisation
import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject
import com.zepben.evolve.cim.iec61970.base.core.NameType
import com.zepben.evolve.cim.iec61970.base.wires.*
import com.zepben.evolve.cim.iec61970.base.wires.generation.production.BatteryUnit
import com.zepben.evolve.cim.iec61970.base.wires.generation.production.PhotoVoltaicUnit
import com.zepben.evolve.cim.iec61970.base.wires.generation.production.PowerElectronicsWindUnit
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Loop
import com.zepben.evolve.services.common.meta.DataSource
import com.zepben.evolve.services.common.meta.MetadataCollection
import com.zepben.evolve.services.customer.CustomerService
import com.zepben.evolve.services.diagram.DiagramService
import com.zepben.evolve.services.measurement.MeasurementService
import com.zepben.evolve.services.network.NetworkModelTestUtil
import com.zepben.evolve.services.network.NetworkService
import java.time.Instant

@Suppress("SameParameterValue", "BooleanLiteralArgument")
object SchemaNetworks {

    fun createNameTestServices(): NetworkModelTestUtil.Services {
        val networkService = NetworkService().apply {
            val nameType = NameType("type1").apply {
                description = "type description"
                val identifiedObject = Organisation("org1")
                val name = getOrAddName("name1", identifiedObject)
                identifiedObject.addName(name)
            }

            addNameType(nameType)
        }

        val customerService = CustomerService().apply {
            val nameType = NameType("type1").apply {
                description = "type description"
                val identifiedObject = Organisation("org1")
                val name = getOrAddName("name1", identifiedObject)
                identifiedObject.addName(name)
            }

            addNameType(nameType)
        }

        return NetworkModelTestUtil.Services(MetadataCollection(), networkService, DiagramService(), customerService, MeasurementService())
    }

    fun createDataSourceTestServices(): NetworkModelTestUtil.Services {
        val metadataCollection = MetadataCollection()

        metadataCollection.add(DataSource("source1", "v1", Instant.EPOCH))
        metadataCollection.add(DataSource("source2", "v2", Instant.now()))

        return NetworkModelTestUtil.Services(metadataCollection, NetworkService(), DiagramService(), CustomerService(), MeasurementService())
    }

    fun createBusbarSectionServices() = servicesOf(::BusbarSection, BusbarSection::fillFields)

    fun createBatteryUnitServices() = servicesOf(::BatteryUnit, BatteryUnit::fillFields)

    fun createPhotoVoltaicUnitServices() = servicesOf(::PhotoVoltaicUnit, PhotoVoltaicUnit::fillFields)

    fun createPowerElectronicsWindUnitServices() = servicesOf(::PowerElectronicsWindUnit, PowerElectronicsWindUnit::fillFields)

    fun createPowerElectronicsConnectionServices() = servicesOf(::PowerElectronicsConnection, PowerElectronicsConnection::fillFields)

    fun createPowerElectronicsConnectionPhaseServices() = servicesOf(::PowerElectronicsConnectionPhase, PowerElectronicsConnectionPhase::fillFields)

    fun createPoleServices() = servicesOf(::Pole, Pole::fillFields)

    fun createPowerTransformerServices() = servicesOf(::PowerTransformer, PowerTransformer::fillFields)

    fun createLoadBreakSwitchServices() = servicesOf(::LoadBreakSwitch, LoadBreakSwitch::fillFields)

    fun createBreakerServices() = servicesOf(::Breaker, Breaker::fillFields)

    fun createPowerTransformerInfoServices() = servicesOf(::PowerTransformerInfo, PowerTransformerInfo::fillFields)

    fun createStreetlightServices() = servicesOf(::Streetlight, Streetlight::fillFields)

    fun createCircuitServices() = servicesOf(::Circuit, Circuit::fillFields)

    fun createLoopServices() = servicesOf(::Loop, Loop::fillFields)

    fun createPowerTransformerEndServices() = servicesOf(::PowerTransformerEnd, PowerTransformerEnd::fillFields)

    fun createTransformerStarImpedanceServices() = servicesOf(::TransformerStarImpedance, TransformerStarImpedance::fillFields)

    fun createTransformerTankInfoServices() = servicesOf(::TransformerTankInfo, TransformerTankInfo::fillFields)

    fun createTransformerEndInfoServices() = servicesOf(::TransformerEndInfo, TransformerEndInfo::fillFields)

    private fun <T : IdentifiedObject> servicesOf(factory: (mRID: String) -> T, filler: (T, NetworkService, Boolean) -> T): NetworkModelTestUtil.Services {
        val networkService = NetworkService()

        networkService.tryAdd(factory("empty"))
        networkService.tryAdd(filler(factory("filled"), networkService, false))

        return NetworkModelTestUtil.Services(MetadataCollection(), networkService, DiagramService(), CustomerService(), MeasurementService())
    }

}
