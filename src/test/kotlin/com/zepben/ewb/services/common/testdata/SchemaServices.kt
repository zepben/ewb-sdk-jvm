/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.common.testdata

import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.cim.iec61970.base.core.NameType
import com.zepben.ewb.cim.iec61970.base.wires.EnergyConsumer
import com.zepben.ewb.cim.iec61970.base.wires.EnergyConsumerPhase
import com.zepben.ewb.cim.iec61970.base.wires.EnergySource
import com.zepben.ewb.cim.iec61970.base.wires.EnergySourcePhase
import com.zepben.ewb.services.common.BaseService
import com.zepben.ewb.services.common.meta.DataSource
import com.zepben.ewb.services.common.meta.MetadataCollection
import com.zepben.ewb.services.customer.CustomerService
import com.zepben.ewb.services.diagram.DiagramService
import com.zepben.ewb.services.network.NetworkService
import java.time.Instant
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KVisibility
import kotlin.reflect.full.*

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

            NameType("typeNullDescription").also {
                addNameType(it)
            }

            NameType("typeEmptyDescription").apply {
                description = ""
            }.also {
                addNameType(it)
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
            customerService.tryAdd(factory("emptyNotNull").also { fillEmptys(it) })
        }

    fun <T : IdentifiedObject> diagramServicesOf(factory: (mRID: String) -> T, filler: (T, DiagramService, Boolean) -> T): DiagramService =
        DiagramService().also { diagramService ->
            diagramService.tryAdd(factory("empty"))
            diagramService.tryAdd(filler(factory("filled"), diagramService, false))
            diagramService.tryAdd(factory("emptyNotNull").also { fillEmptys(it) })
        }

    fun <T : IdentifiedObject> networkServicesOf(factory: (mRID: String) -> T, filler: (T, NetworkService, Boolean) -> T): NetworkService =
        NetworkService().also { networkService ->
            networkService.tryAdd(factory("empty").also { fillRequired(networkService, it) })
            networkService.tryAdd(filler(factory("filled"), networkService, false))
            networkService.tryAdd(factory("emptyNotNull").also { fillRequired(networkService, it); fillEmptys(it) })
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

    /**
     * This functionality was created because there were a few strings that weren't nullable, and we were treating "" as null.
     * We then converted them to nullable and wanted to ensure that nothing got broken by writing/reading these from the DB/protos.
     *
     * Will create an 'empty' type like a default constructor, but any nullable string property will be
     * created with an empty string (""), and any nullable boolean will be created with false.
     *
     * We also specify numControls and numDiagramObjects explicitly, as these were previously not nullable but
     * now are, and we set them to 0. We don't do this for all integers as many (TapChanger and PowerElectronicsConnection) settings
     * have range restrictions on them where a default of 0 won't work.
     */
    fun fillEmptys(io: IdentifiedObject) {
        io::class.memberProperties
            .filter { it.visibility == KVisibility.PUBLIC }
            .filter { it.returnType.isMarkedNullable }
            .filterNot { it.name.uppercase().endsWith("MRID") }     // Ignore identifiedObjectMRID, customerMRID, etc
            .filterIsInstance<KMutableProperty<*>>()
            .forEach { prop ->
                if (prop.returnType.withNullability(false).isSubtypeOf(String::class.createType())) {
                    prop.setter.call(io, "")
                }

                if (prop.returnType.withNullability(false).isSubtypeOf(Boolean::class.createType())) {
                    prop.setter.call(io, false)
                }

                if (prop.name == "numDiagramObjects" || prop.name == "numControls" || prop.name == "numEndDevices")
                    prop.setter.call(io, 0)
            }
    }

}
