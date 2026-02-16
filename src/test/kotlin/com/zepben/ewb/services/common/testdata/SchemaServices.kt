/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.common.testdata

import com.zepben.ewb.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProjectComponent
import com.zepben.ewb.cim.iec61968.common.StreetAddress
import com.zepben.ewb.cim.iec61968.common.StreetDetail
import com.zepben.ewb.cim.iec61968.common.TownDetail
import com.zepben.ewb.cim.iec61968.infiec61968.infcommon.Ratio
import com.zepben.ewb.cim.iec61968.metering.ControlledAppliance
import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.cim.iec61970.base.core.NameType
import com.zepben.ewb.cim.iec61970.base.domain.DateTimeInterval
import com.zepben.ewb.cim.iec61970.base.wires.*
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ChangeSet
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.DataSet
import com.zepben.ewb.services.common.BaseService
import com.zepben.ewb.services.common.meta.DataSource
import com.zepben.ewb.services.common.meta.MetadataCollection
import com.zepben.ewb.services.customer.CustomerService
import com.zepben.ewb.services.diagram.DiagramService
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.variant.VariantService
import java.time.Instant
import javax.annotation.Nullable
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

    fun <T : IdentifiedObject> variantServicesOf(factory: (mRID: String) -> T, filler: (T, VariantService, Boolean) -> T): VariantService =
        VariantService().also { variantService ->
            variantService.tryAdd(factory("empty"))
            variantService.tryAdd(filler(factory("filled"), variantService, false))
            variantService.tryAdd(factory("emptyNotNull").also { fillEmptys(it)})
        }

    fun variantServicesOfChangeSets(factory: (mRID: String) -> ChangeSet, filler: (ChangeSet, VariantService, Boolean) -> ChangeSet): VariantService =
        VariantService().also { variantService ->
            variantService.add(factory("empty"))
            variantService.add(filler(factory("filled"), variantService, false))
            variantService.add(factory("emptyNotNull").also { fillEmptys(it) })
        }

    private fun fillRequired(service: NetworkService, io: Any) {
        when (io) {
            is EnergyConsumerPhase -> {
                io.energyConsumer = EnergyConsumer(generateId()).also {
                    it.addPhase(io)
                    service.add(it)
                }
            }

            is EnergySourcePhase -> {
                io.energySource = EnergySource(generateId()).also {
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
     * created with an empty string (""), and any nullable boolean will be created with false etc.
     *
     * Any exception in setting the value to the empty value means there are constraints in place, and the old null
     * replacements won't be an issue, so can safely be ignored.
     */
    fun fillEmptys(io: Any) {
        io::class.memberProperties
            .filter { it.visibility == KVisibility.PUBLIC }
            .filter { it.returnType.isMarkedNullable }
            .filterNot { it.name.uppercase().endsWith("MRID") } // Ignore identifiedObjectMRID, customerMRID, etc
            .filterIsInstance<KMutableProperty<*>>()
            .filter { it.getter.call(io) == null }
            .forEach { prop ->
                val value = prop.findKnownNonEmpty(io) ?: when {
                    prop.isNullableOf<String>() -> ""
                    prop.isNullableOf<Boolean>() -> false
                    prop.isNullableOf<Int>() -> 0
                    prop.isNullableOf<Long>() -> 0L
                    prop.isNullableOf<Float>() -> 0.0f
                    prop.isNullableOf<Double>() -> 0.0
                    // We don't need to worry about references to other objects, they are covered by `filled`.
                    prop.isNullableOf<IdentifiedObject>() -> null
                    prop.isNullableOf<ControlledAppliance>() -> ControlledAppliance(0)
                    prop.isNullableOf<DateTimeInterval>() -> DateTimeInterval(Instant.ofEpochSecond(0), Instant.ofEpochSecond(1))
                    prop.isNullableOf<Instant>() -> Instant.ofEpochSecond(0)
                    prop.isNullableOf<Ratio>() -> Ratio(0.0, 0.0)
                    prop.isNullableOf<StreetAddress>() -> StreetAddress(
                        postalCode = "",
                        townDetail = TownDetail(name = "", stateOrProvince = "", country = ""),
                        poBox = "",
                        streetDetail = StreetDetail(
                            buildingName = "",
                            floorIdentification = "",
                            name = "",
                            number = "",
                            suiteNumber = "",
                            type = "",
                            displayAddress = "",
                            buildingNumber = ""
                        ),
                    )
                    // FIXME: IDK how, also not much thought has gone in, but this just bit me with an accidental public var (NetworkModelProject._children), maybe we should check for that? ie: isPublic && name.startswith("_") ?
                    else -> throw IllegalStateException("INTERNAL ERROR: You forgot to add an empty value mapper for ${prop.returnType} - used by ${io::class.simpleName}.${prop.name}")
                }

//                try {
                prop.setter.call(io, value)
//                } catch (_: Exception) {
//                    // Any exception in setting the value to the empty value means there are constraints in place, and the old null
//                    // replacements won't be an issue, so can safely be ignored.
//                }
            }
    }

    private inline fun <reified T> KMutableProperty<*>.isNullableOf(): Boolean =
        returnType.withNullability(false).isSubtypeOf(T::class.createType())

    private fun KMutableProperty<*>.findKnownNonEmpty(io: Any): Any? {
        return tryPowerElectronicsConnectionNonEmpty(io)
    }

    private fun KMutableProperty<*>.tryPowerElectronicsConnectionNonEmpty(io: Any): Any? =
        when (io) {
            is PowerElectronicsConnection -> {
                when (name) {
                    "invWattRespV1", "invWattRespV3", "invWattRespV4" -> 244
                    "invWattRespV2" -> 216 // 244 is invalid for invWattRespV2.
                    "invVarRespV1", "invVarRespV2", "invVarRespV3", "invVarRespV4" -> 200
                    else -> null
                }
            }

            is TapChanger -> {
                when (name) {
                    "lowStep" -> -1
                    else -> null
                }
            }

            else -> null
        }

}
