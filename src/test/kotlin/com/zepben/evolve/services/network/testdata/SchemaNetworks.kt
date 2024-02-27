/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.testdata

import com.zepben.evolve.cim.iec61968.common.Organisation
import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject
import com.zepben.evolve.cim.iec61970.base.core.NameType
import com.zepben.evolve.cim.iec61970.base.wires.EnergyConsumer
import com.zepben.evolve.cim.iec61970.base.wires.EnergyConsumerPhase
import com.zepben.evolve.cim.iec61970.base.wires.EnergySource
import com.zepben.evolve.cim.iec61970.base.wires.EnergySourcePhase
import com.zepben.evolve.services.common.meta.DataSource
import com.zepben.evolve.services.customer.CustomerService
import com.zepben.evolve.services.diagram.DiagramService
import com.zepben.evolve.services.network.NetworkModelTestUtil
import com.zepben.evolve.services.network.NetworkService
import java.time.Instant

@Suppress("SameParameterValue")
object SchemaNetworks {

    fun createNameTestServices(): NetworkModelTestUtil.Services = NetworkModelTestUtil.Services().apply {
        networkService.apply {
            val nameType = NameType("type1").apply {
                description = "type description"
                Organisation("org1").also {
                    it.addName(this, "name1")
                    add(it)
                }
            }

            addNameType(nameType)
        }

        customerService.apply {
            val nameType = NameType("type1").apply {
                description = "type description"
                Organisation("org1").also {
                    it.addName(this, "name1")
                    add(it)
                }
            }

            addNameType(nameType)
        }

        diagramService.apply {
            addNameType(NameType("type1").apply { description = "type description" })
        }
    }

    fun createDataSourceTestServices(): NetworkModelTestUtil.Services = NetworkModelTestUtil.Services().apply {
        metadataCollection.add(DataSource("source1", "v1", Instant.EPOCH))
        metadataCollection.add(DataSource("source2", "v2", Instant.now()))
    }

    fun <T : IdentifiedObject> customerServicesOf(factory: (mRID: String) -> T, filler: (T, CustomerService, Boolean) -> T): NetworkModelTestUtil.Services =
        NetworkModelTestUtil.Services().apply {
            customerService.tryAdd(factory("empty"))
            customerService.tryAdd(filler(factory("filled"), customerService, false))

            // Copy items to other services that get automatically loaded there.
            customerService.sequenceOf<Organisation>().forEach { networkService.add(it) }
            customerService.nameTypes.forEach {
                networkService.addNameType(NameType(it.name).apply {
                    description = it.description
                    it.names
                        .filter { name -> name.identifiedObject is Organisation }
                        .forEach { (name, _, identifiedObject) -> getOrAddName(name, identifiedObject) }
                })
                diagramService.addNameType(NameType(it.name).apply { description = it.description })
            }
        }

    fun <T : IdentifiedObject> diagramServicesOf(factory: (mRID: String) -> T, filler: (T, DiagramService, Boolean) -> T): NetworkModelTestUtil.Services =
        NetworkModelTestUtil.Services().apply {
            diagramService.tryAdd(factory("empty"))
            diagramService.tryAdd(filler(factory("filled"), diagramService, false))

            // Copy items to other services that get automatically loaded there.
            diagramService.nameTypes.forEach {
                customerService.addNameType(NameType(it.name).apply { description = it.description })
                networkService.addNameType(NameType(it.name).apply { description = it.description })
            }
        }

    fun <T : IdentifiedObject> networkServicesOf(factory: (mRID: String) -> T, filler: (T, NetworkService, Boolean) -> T): NetworkModelTestUtil.Services =
        NetworkModelTestUtil.Services().apply {
            networkService.tryAdd(factory("empty").also { fillRequired(networkService, it) })
            networkService.tryAdd(filler(factory("filled"), networkService, false))

            // Copy items to other services that get automatically loaded there.
            networkService.sequenceOf<Organisation>().forEach { customerService.add(it) }
            networkService.nameTypes.forEach {
                customerService.addNameType(NameType(it.name).apply {
                    description = it.description
                    it.names
                        .filter { name -> name.identifiedObject is Organisation }
                        .forEach { (name, _, identifiedObject) -> getOrAddName(name, identifiedObject) }
                })
                diagramService.addNameType(NameType(it.name).apply { description = it.description })
            }
        }

    private fun fillRequired(service: NetworkService, io: IdentifiedObject) {
        when (io) {
            is EnergyConsumerPhase -> {
                io.energyConsumer = EnergyConsumer().also {
                    it.addPhase(io)
                    service.add(it)
                }
            }

            is EnergySourcePhase -> {
                io.energySource = EnergySource().also {
                    it.addPhase(io)
                    service.add(it)
                }
            }
        }
    }

}
