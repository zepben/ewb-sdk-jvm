/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.common.translator

import com.zepben.evolve.cim.iec61968.operations.OperationalRestriction
import com.zepben.evolve.cim.iec61970.base.core.*
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.LvFeeder
import com.zepben.evolve.database.sqlite.cim.CimDatabaseTables
import com.zepben.evolve.database.sqlite.cim.tables.SqliteTable
import com.zepben.evolve.database.sqlite.cim.tables.TableMetadataDataSources
import com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.core.TableNameTypes
import com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.core.TableNames
import com.zepben.evolve.database.sqlite.common.SqliteTableVersion
import com.zepben.evolve.services.common.BaseService
import com.zepben.evolve.services.common.BaseServiceComparator
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.api.fail
import kotlin.reflect.KClass
import com.zepben.protobuf.cim.iec61970.base.core.NameType as PBNameType

internal abstract class TranslatorTestBase<S : BaseService>(
    private val createService: () -> S,
    private val comparator: BaseServiceComparator,
    private val databaseTables: CimDatabaseTables,
    private val addFromPb: S.(PBNameType) -> NameType,
    private val createServiceIdentifiedObject: (IdentifiedObject) -> Any
) {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    /**
     * The list of things to validate.
     */
    protected abstract val validationInfo: List<ValidationInfo<*>>

    /**
     * A collection of abstract object creators.
     *
     * We need to create concrete types that are supported by the service so the references to abstract classes can be resolved.
     */
    protected open val abstractCreators: Map<Class<*>, (String) -> IdentifiedObject> = mapOf()

    /**
     * A list of tables that are not used directly via protobuf and should be excluded from the validation. This will include all
     * association and array value tables.
     *
     * You should add any tables for each specific database type via an appropriate override calling this base method.
     */
    protected open val excludedTables: Set<KClass<out SqliteTable>> = setOf(
        SqliteTableVersion::class,
        TableMetadataDataSources::class,
        TableNameTypes::class,
        TableNames::class
    )

    @Test
    internal fun `converts all types correctly`() {
        if (validationInfo.size != (databaseTables.tables.keys - excludedTables).size) {
            val actual = validationInfo.map { it.cim::class.simpleName!! }.toSet()
            val expected = (databaseTables.tables.keys - excludedTables).map { it.simpleName!!.removePrefix("Table").removeSuffix("s") }.toSet()
            fail(
                "The number of items being validated did not match the number of items writen to the database. Did you forget to validate an item, " +
                    "or to exclude the table if it was an association or array data?" +
                    formatValidationError("Unexpected", actual - expected) +
                    formatValidationError("Missing", expected - actual)
            )
        }

        validationInfo.forEach { it.validate() }
    }

    private fun formatValidationError(description: String, classes: Set<String>): String =
        classes.takeUnless { it.isEmpty() }?.let { "\n\n$description: $it" } ?: ""

    //
    // NOTE: NameType is not sent via any grpc messages at this stage, so test it separately
    //

    @Test
    internal fun createsNewNameType() {
        val pb = NameType("nt1 name").apply {
            description = "nt1 desc"
        }.toPb()

        val cim = createService().addFromPb(pb)

        assertThat(cim.name, equalTo(pb.name))
        assertThat(cim.description, equalTo(pb.description))
    }

    @Test
    internal fun updatesExistingNameType() {
        val pb = NameType("nt1 name").apply {
            description = "nt1 desc"
        }.toPb()

        val nt = NameType("nt1 name")
        val cim = createService().apply { addNameType(nt) }.addFromPb(pb)

        assertThat(cim, sameInstance(nt))
        assertThat(cim.description, equalTo(pb.description))
    }

    /**
     * Information required to validate the translation to/from cim/protobuf.
     *
     * @param T The class to test.
     * @property cim The object of type [T] under test.
     * @property filler The callback that is used to populate [cim].
     * @property translate The callback that performs the translation from CIM to protobuf and back.
     */
    protected inner class ValidationInfo<T : IdentifiedObject>(
        val cim: T,
        val filler: T.(S) -> Unit,
        val translate: S.(T) -> T?
    ) {

        override fun toString(): String = "ValidationInfo<${cim::class.simpleName}>"

        fun validate() {
            val blankDifferences = comparator.compare(cim, translate(createService(), cim)!!).differences
            assertThat("Failed to convert blank ${cim::class.simpleName}:${blankDifferences}", blankDifferences, anEmptyMap())

            cim.filler(createService())
            removeUnsentReferences()

            val populatedDifferences = comparator.compare(cim, addWithUnresolvedReferences()).differences
            assertThat("Failed to convert populated ${cim::class.simpleName}:${populatedDifferences}", populatedDifferences, anEmptyMap())

            assertThat(createServiceIdentifiedObject(cim), notNullValue())
        }

        private fun removeUnsentReferences() {
            if (cim is EquipmentContainer)
                cim.clearEquipment()

            if (cim is OperationalRestriction)
                cim.clearEquipment()

            if (cim is Feeder)
                cim.clearCurrentEquipment()

            if (cim is ConnectivityNode)
                cim.clearTerminals()

            if (cim is LvFeeder)
                cim.clearCurrentEquipment()
        }

        private fun addWithUnresolvedReferences(): T {
            // We need to convert the populated item before we check the differences, so we can complete the unresolved references.
            val service = createService()
            val convertedCim = translate(service, cim)!!
            service.unresolvedReferences().toList().forEach { (_, toMrid, resolver, _) ->
                try {
                    val io = abstractCreators[resolver.toClass]?.invoke(toMrid) ?: resolver.toClass.getDeclaredConstructor(String::class.java)
                        .newInstance(toMrid)
                    io.also { service.tryAdd(it) }
                } catch (e: Exception) {
                    // If this fails you need to add a concrete type mapping to the abstractCreators map at the top of this class.
                    fail("Failed to create unresolved reference for ${resolver.toClass}.", e)
                }
            }
            return convertedCim
        }

    }

}
