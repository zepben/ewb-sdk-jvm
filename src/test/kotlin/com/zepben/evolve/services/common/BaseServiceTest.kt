/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.common

import com.zepben.evolve.cim.iec61968.assetinfo.CableInfo
import com.zepben.evolve.cim.iec61968.common.Location
import com.zepben.evolve.cim.iec61968.operations.OperationalRestriction
import com.zepben.evolve.cim.iec61970.base.core.BaseVoltage
import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.AcLineSegment
import com.zepben.evolve.cim.iec61970.base.wires.Breaker
import com.zepben.evolve.cim.iec61970.base.wires.Junction
import com.zepben.evolve.services.common.exceptions.UnsupportedIdentifiedObjectException
import com.zepben.evolve.services.common.meta.MetadataCollection
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.translator.addFromPb
import com.zepben.evolve.services.network.translator.toPb
import com.zepben.testutils.exception.ExpectException.Companion.expect
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class BaseServiceTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    internal class TestBaseService : BaseService("test", MetadataCollection()) {
        fun add(obj: Junction) = super.add(obj)
        fun remove(obj: Junction) = super.remove(obj)
        fun add(obj: Breaker) = super.add(obj)
        fun remove(obj: Breaker) = super.remove(obj)
        fun add(obj: AcLineSegment) = super.add(obj)
        fun remove(obj: AcLineSegment) = super.remove(obj)
        fun add(obj: Terminal) = super.add(obj)
        fun remove(obj: Terminal) = super.remove(obj)
        fun add(obj: Location) = super.add(obj)
        fun remove(obj: Location) = super.remove(obj)
        fun add(obj: BaseVoltage) = super.add(obj)
        fun remove(obj: BaseVoltage) = super.remove(obj)
    }

    private val service = TestBaseService()
    private val breaker1 = Breaker().also { service.add(it) }
    private val breaker2 = Breaker().also { service.add(it) }
    private val acLineSegment1 = AcLineSegment().also { service.add(it) }
    private val acLineSegment2 = AcLineSegment().also { service.add(it) }

    @BeforeEach
    internal fun beforeEach() {
        breaker2.name = "breaker"
        acLineSegment1.name = "acLineSegment2"
    }

    @Test
    internal fun supportedTypesCollectionMatch() {
        assertThat(service.supportedClasses, containsInAnyOrder(*service.supportedKClasses.map { it.java }.toTypedArray()))
    }


    @Test
    internal fun tryFunctions() {
        val junction = Junction()
        assertThat("Initial tryAdd should return true", service.tryAdd(junction))
        assertThat("tryRemove should return true for previously-added object", service.tryRemove(junction))
        expect { service.tryAdd(CableInfo()) }.toThrow<UnsupportedIdentifiedObjectException>()
        expect { service.tryRemove(CableInfo()) }.toThrow<UnsupportedIdentifiedObjectException>()
    }

    @Test
    internal fun getJavaInterop() {
        assertThat(service.get<IdentifiedObject>(breaker1.mRID), equalTo(breaker1))
        assertThat(service.get<IdentifiedObject>(breaker2.mRID), equalTo(breaker2))
        assertThat(service.get<IdentifiedObject>(acLineSegment1.mRID), equalTo(acLineSegment1))
        assertThat(service.get<IdentifiedObject>(acLineSegment2.mRID), equalTo(acLineSegment2))

        assertThat(service[breaker2.mRID], equalTo(breaker2))
        assertThat(service[breaker1.mRID], equalTo(breaker1))
        assertThat(service.get<Breaker>(acLineSegment1.mRID), nullValue())
        assertThat(service.get<Breaker>(acLineSegment2.mRID), nullValue())

        assertThat(service.get<AcLineSegment>(breaker2.mRID), nullValue())
        assertThat(service.get<AcLineSegment>(breaker1.mRID), nullValue())
        assertThat(service[acLineSegment1.mRID], equalTo(acLineSegment1))
        assertThat(service[acLineSegment2.mRID], equalTo(acLineSegment2))
    }

    @Test
    internal fun countJavaInterop() {
        assertThat(service.num<IdentifiedObject>(), equalTo(4))
        assertThat(service.num<Breaker>(), equalTo(2))
        assertThat(service.num<AcLineSegment>(), equalTo(2))
    }

    @Test
    internal fun forEachJavaInterop() {
        validateForEach<IdentifiedObject>(listOf(breaker1, breaker2, acLineSegment1, acLineSegment2))
        validateForEach<Breaker>(listOf(breaker1, breaker2))
        validateForEach<AcLineSegment>(listOf(acLineSegment1, acLineSegment2))

        validateForEachFiltered<IdentifiedObject>(
            { it.name.isEmpty() },
            listOf(breaker1, acLineSegment2)
        )
        validateForEachFiltered<Breaker>({ it.name.isEmpty() }, listOf(breaker1))
        validateForEachFiltered<AcLineSegment>({ it.name.isEmpty() }, listOf(acLineSegment2))
    }

    @Test
    internal fun asStreamJavaInterop() {
        assertThat(service.sequenceOf<IdentifiedObject>().toList(), containsInAnyOrder(breaker1, breaker2, acLineSegment1, acLineSegment2))
        assertThat(service.sequenceOf<Breaker>().toList(), containsInAnyOrder(breaker1, breaker2))
        assertThat(service.sequenceOf<AcLineSegment>().toList(), containsInAnyOrder(acLineSegment1, acLineSegment2))
    }

    @Test
    internal fun toListJavaInterop() {
        assertThat(service.listOf(), containsInAnyOrder<IdentifiedObject>(breaker1, breaker2, acLineSegment1, acLineSegment2))
        assertThat(service.listOf(), containsInAnyOrder(breaker1, breaker2))
        assertThat(service.listOf(), containsInAnyOrder(acLineSegment1, acLineSegment2))
        assertThat(service.listOf { it.name.isEmpty() }, containsInAnyOrder<IdentifiedObject>(breaker1, acLineSegment2))
        assertThat(service.listOf { it.name.isEmpty() }, containsInAnyOrder(breaker1))
        assertThat(service.listOf { it.name.isEmpty() }, containsInAnyOrder(acLineSegment2))
    }

    @Test
    internal fun toSetJavaInterop() {
        assertThat(service.setOf(), containsInAnyOrder<IdentifiedObject>(breaker1, breaker2, acLineSegment1, acLineSegment2))
        assertThat(service.setOf(), containsInAnyOrder(breaker1, breaker2))
        assertThat(service.setOf(), containsInAnyOrder(acLineSegment1, acLineSegment2))
        assertThat(service.setOf { it.name.isEmpty() }, containsInAnyOrder<IdentifiedObject>(breaker1, acLineSegment2))
        assertThat(service.setOf { it.name.isEmpty() }, containsInAnyOrder(breaker1))
        assertThat(service.setOf { it.name.isEmpty() }, containsInAnyOrder(acLineSegment2))
    }

    @Test
    internal fun toMapJavaInterop() {
        assertThat(service.mapOf<IdentifiedObject>().keys, containsInAnyOrder(breaker1.mRID, breaker2.mRID, acLineSegment1.mRID, acLineSegment2.mRID))
        assertThat(
            service.mapOf<IdentifiedObject>().values,
            containsInAnyOrder(breaker1, breaker2, acLineSegment1, acLineSegment2)
        )

        assertThat(service.mapOf<Breaker>().keys, containsInAnyOrder(breaker1.mRID, breaker2.mRID))
        assertThat(service.mapOf<Breaker>().values, containsInAnyOrder(breaker1, breaker2))

        assertThat(service.mapOf<AcLineSegment>().keys, containsInAnyOrder(acLineSegment1.mRID, acLineSegment2.mRID))
        assertThat(service.mapOf<AcLineSegment>().values, containsInAnyOrder(acLineSegment1, acLineSegment2))

        assertThat(service.mapOf<IdentifiedObject> { it.name.isEmpty() }.keys, containsInAnyOrder(breaker1.mRID, acLineSegment2.mRID))
        assertThat(service.mapOf<IdentifiedObject> { it.name.isEmpty() }.values, containsInAnyOrder(breaker1, acLineSegment2))

        assertThat(service.mapOf<Breaker> { it.name.isEmpty() }.keys, containsInAnyOrder(breaker1.mRID))
        assertThat(service.mapOf<Breaker> { it.name.isEmpty() }.values, containsInAnyOrder(breaker1))

        assertThat(service.mapOf<AcLineSegment> { it.name.isEmpty() }.keys, containsInAnyOrder(acLineSegment2.mRID))
        assertThat(service.mapOf<AcLineSegment> { it.name.isEmpty() }.values, containsInAnyOrder(acLineSegment2))
    }

    @Test
    internal fun testUnresolvedBidirectionalReferences() {
        val terminal = Terminal("t1")
        assertThat("Initial add should return true", service.add(terminal))
        assertThat(
            "resolveOrDeferReference should return false for mRID not in service",
            !service.resolveOrDeferReference(Resolvers.conductingEquipment(terminal), "j1")
        )

        val resolver = Resolvers.conductingEquipment(terminal)
        val unresolvedReference = UnresolvedReference(terminal, "j1", resolver.resolver, resolver.reverseResolver)

        assertThat(service.unresolvedReferences().toList(), contains(unresolvedReference))
        assertThat(service.getUnresolvedReferenceMrids(Resolvers.conductingEquipment(terminal)), contains("j1"))
        assertThat(service.getUnresolvedReferencesFrom(terminal.mRID).toList(), contains(unresolvedReference))
        assertThat(service.getUnresolvedReferencesTo("j1").toList(), contains(unresolvedReference))

        val junction = Junction("j1")
        assertThat(
            "resolveOrDeferReference should return true for mRID in service",
            service.resolveOrDeferReference(Resolvers.terminals(junction), terminal.mRID)
        )

        assertThat(service.unresolvedReferences().toList(), empty())
        assertThat(service.getUnresolvedReferenceMrids(Resolvers.conductingEquipment(terminal)), empty())
        assertThat(service.getUnresolvedReferenceMrids(Resolvers.terminals(junction)), empty())
        assertThat(service.getUnresolvedReferencesFrom(terminal.mRID).toList(), empty())
        assertThat(service.getUnresolvedReferencesTo("j1").toList(), empty())

        assertThat(terminal.conductingEquipment, equalTo(junction))
        assertThat(junction.getTerminal(terminal.mRID), equalTo(terminal))

        assertThat("Initial add should return true", service.add(junction))
    }

    @Test
    internal fun testUnresolvedUnidirectionalReferences() {
        val junction = Junction("j1")
        assertThat("Initial add should return true", service.add(junction))
        assertThat(
            "resolveOrDeferReference should return false for mRID not in service",
            !service.resolveOrDeferReference(Resolvers.baseVoltage(junction), "bv1")
        )

        assertThat(service.unresolvedReferences().toList(), equalTo(listOf(UnresolvedReference(junction, "bv1", Resolvers.baseVoltage(junction).resolver))))
        assertThat(service.getUnresolvedReferenceMrids(Resolvers.baseVoltage(junction)), contains("bv1"))

        val baseVoltage = BaseVoltage("bv1")
        assertThat("Initial add should return true", service.add(baseVoltage))

        assertThat(service.unresolvedReferences().toList(), empty())
        assertThat(service.getUnresolvedReferenceMrids(Resolvers.baseVoltage(junction)), empty())

        assertThat(junction.baseVoltage, equalTo(baseVoltage))
    }

    @Test
    internal fun `test add resolves reverse relationship`() {
        val ns = NetworkService()

        OperationalRestriction("or1").apply {
            addEquipment(AcLineSegment("eq1").also {
                it.addOperationalRestriction(this)
                ns.addFromPb(it.toPb())
            })
            ns.addFromPb(this.toPb())
        }

        assertThat(ns.get<OperationalRestriction>("or1")!!.equipment, contains(ns.get<AcLineSegment>("eq1")))
        assertThat(ns.get<AcLineSegment>("eq1")!!.operationalRestrictions, contains(ns.get<OperationalRestriction>("or1")))
    }

    @Test
    internal fun `add returns true when object already in service`() {
        val ns = NetworkService()

        AcLineSegment("acls1").apply {
            assertThat("Initial add should return true", ns.add(this))
            // Re-adding the same object should return true
            assertThat("Adding the same instance should return true", ns.add(this))
        }

        // a new ACLS with the same mRID should fail
        AcLineSegment("acls1").apply {
            assertThat("Adding a different instance of the same class with the same mRID should return false", !ns.add(this))
        }

        // A completely different object with the same mRID should fail
        Junction("acls1").apply {
            assertThat("Adding a different instance of a different class with the same mRID should return false", !ns.add(this))
        }
    }

    @Test
    internal fun `add only returns true when object is the same instance, not just equal`() {
        val ms = MyService()
        val obj1 = MyIdentifiedObject("1")
        val obj1Dup = MyIdentifiedObject("1")

        assertThat("Initial add should return true", ms.add(obj1))
        assertThat("Adding same instance should return true", ms.add(obj1))
        assertThat("Adding equivalent but different instance should return false", !ms.add(obj1Dup))
    }

    @Test
    internal fun `throws cast exception when getting wrong type`() {
        expect { service.get(Junction::class, breaker1.mRID) }.toThrow<ClassCastException>()
    }

    @Test
    internal fun `get returns null when id not in service`() {
        assertThat(service.get(Breaker::class, "no breaker"), nullValue())
    }

    @Test
    internal fun `mrid must be unique`() {
        val junction = Junction("id1")
        assertThat("Initial add should return true", service.add(junction))

        val location = Location(junction.mRID)
        assertThat("Adding different object with same mRID should return false", !service.add(location))
    }

    private inline fun <reified T : IdentifiedObject> validateForEach(expected: List<ConductingEquipment>) {
        val visited = mutableListOf<IdentifiedObject>()
        service.sequenceOf<T>().forEach { visited.add(it) }
        assertThat(visited, containsInAnyOrder<Any>(*expected.toTypedArray()))
    }

    private inline fun <reified T : IdentifiedObject> validateForEachFiltered(noinline filter: (T) -> Boolean, expected: List<ConductingEquipment>) {
        val visited = mutableListOf<IdentifiedObject>()
        service.sequenceOf<T>().filter(filter).forEach { visited.add(it) }
        assertThat(visited, containsInAnyOrder<Any>(*expected.toTypedArray()))
    }

    private class MyIdentifiedObject(mrid: String) : IdentifiedObject(mrid) {
        override fun equals(other: Any?): Boolean = true
        override fun hashCode(): Int = 0
    }

    private class MyService : BaseService("", MetadataCollection()) {
        fun add(cableInfo: MyIdentifiedObject): Boolean = super.add(cableInfo)

        // required, since BaseService checks that each add method has a matching remove method
        @Suppress("Unused")
        fun remove(cableInfo: MyIdentifiedObject): Boolean = super.remove(cableInfo)
    }

}
