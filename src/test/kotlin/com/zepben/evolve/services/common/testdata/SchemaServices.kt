/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.common.testdata

import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject
import com.zepben.evolve.cim.iec61970.base.core.NameType
import com.zepben.evolve.cim.iec61970.base.wires.EnergyConsumer
import com.zepben.evolve.cim.iec61970.base.wires.EnergyConsumerPhase
import com.zepben.evolve.cim.iec61970.base.wires.EnergySource
import com.zepben.evolve.cim.iec61970.base.wires.EnergySourcePhase
import com.zepben.evolve.services.common.BaseService
import com.zepben.evolve.services.common.meta.DataSource
import com.zepben.evolve.services.common.meta.MetadataCollection
import com.zepben.evolve.services.customer.CustomerService
import com.zepben.evolve.services.diagram.DiagramService
import com.zepben.evolve.services.network.NetworkService
import java.time.Instant
import kotlin.reflect.full.createInstance

@Suppress("SameParameterValue")
object SchemaServices {

    inline fun <reified S : BaseService, reified IO : IdentifiedObject> createNameTestService(): S =
        S::class.createInstance().apply {
            val nameType = NameType("type1").apply {
                description = "type description"
            }.also {
                addNameType(it)
            }

            try {
                IO::class.constructors.first().call("obj1")
            } catch (ex: Exception) {
                throw IllegalArgumentException("Class should have a single mRID constructor: ${IO::class}", ex)
            }.apply {
                addName(nameType, "name1")
            }.also {
                tryAdd(it)
            }
        }

    fun createDataSourceTestServices(): MetadataCollection =
        MetadataCollection().apply {
            add(DataSource("source1", "v1", Instant.EPOCH))
            add(DataSource("source2", "v2", Instant.now()))
        }

    fun <T : IdentifiedObject> customerServicesOf(factory: (mRID: String) -> T, filler: (T, CustomerService, Boolean) -> T): CustomerService =
        CustomerService().also { customerService ->
            customerService.tryAdd(factory("empty"))
            customerService.tryAdd(filler(factory("filled"), customerService, false))
        }

    fun <T : IdentifiedObject> diagramServicesOf(factory: (mRID: String) -> T, filler: (T, DiagramService, Boolean) -> T): DiagramService =
        DiagramService().also { diagramService ->
            diagramService.tryAdd(factory("empty"))
            diagramService.tryAdd(filler(factory("filled"), diagramService, false))
        }

    fun <T : IdentifiedObject> networkServicesOf(factory: (mRID: String) -> T, filler: (T, NetworkService, Boolean) -> T): NetworkService =
        NetworkService().also { networkService ->
            networkService.tryAdd(factory("empty").also { fillRequired(networkService, it) })
            networkService.tryAdd(filler(factory("filled"), networkService, false))
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
