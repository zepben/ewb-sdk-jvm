/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.equivalents

import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvFeeder
import com.zepben.ewb.cim.iec61968.common.Location
import com.zepben.ewb.cim.iec61970.base.core.*
import com.zepben.ewb.cim.iec61970.base.equivalents.EquivalentBranch
import com.zepben.ewb.cim.iec61970.base.wires.*
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.equivalents.EdgeDetectionDetails.Companion.between
import com.zepben.ewb.services.network.equivalents.EquivalentNetworkCreator.addToEdgeBetween
import com.zepben.ewb.services.network.equivalents.EquivalentNetworkCreator.singleEquipmentCreator
import com.zepben.ewb.services.network.equivalents.EquivalentNetworkCreator.singleEquivalentBranchCreator
import com.zepben.ewb.services.network.testdata.createTerminal
import com.zepben.ewb.services.network.testdata.createTerminals
import io.mockk.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

internal class EquivalentNetworkCreatorTest {

    @AfterEach
    internal fun afterEach() {
        // We need to clear the `mockkObject(EquivalentNetworkUtils)` calls. We do it here to make sure it runs even if there is a failure.
        unmockkAll()
    }

    @Test
    internal fun `check that calling convenience functions calls underlying function`() {
        val lvFeeder = LvFeeder()
        val hvFeeder = Feeder()
        val substation = Substation()
        val classes = setOf(Substation::class, Feeder::class, LvFeeder::class)
        val containers = setOf<EquipmentContainer>(mockk(), mockk(), mockk())

        validateConvenience(setOf(LvFeeder::class, Feeder::class), setOf(lvFeeder, hvFeeder), null) { addToEdgeBetween<Breaker>(lvFeeder, hvFeeder) }
        validateConvenience(setOf(LvFeeder::class, Feeder::class), setOf(lvFeeder, hvFeeder), 1) {
            addToEdgeBetween<Breaker>(lvFeeder, hvFeeder, maxNumber = 1)
        }

        validateConvenience(setOf(Substation::class, Feeder::class), setOf(substation), null) { addToEdgeBetween<Feeder, Junction>(substation) }
        validateConvenience(setOf(Substation::class, Feeder::class), setOf(substation), 2) {
            addToEdgeBetween<Feeder, Junction>(substation, maxNumber = 2)
        }

        validateConvenience(setOf(Substation::class, LvFeeder::class), emptySet(), null) { addToEdgeBetween<Substation, LvFeeder, AcLineSegment>() }
        validateConvenience(setOf(Substation::class, LvFeeder::class), emptySet(), 3) {
            addToEdgeBetween<Substation, LvFeeder, AcLineSegment>(maxNumber = 3)
        }

        validateConvenience(classes, containers, null) { addToEdgeBetween<Breaker>(this, EdgeDetectionDetails(classes, containers)) }
        validateConvenience(classes, containers, 4) { addToEdgeBetween<Breaker>(this, EdgeDetectionDetails(classes, containers), maxNumber = 4) }
    }

    @Test
    internal fun `check that calling convenience functions allows customisation`() {
        val lvFeeder = LvFeeder()
        val hvFeeder = Feeder()
        val substation = Substation()
        val classes = setOf(Substation::class, Feeder::class, LvFeeder::class)
        val containers = setOf<EquipmentContainer>(mockk(), mockk(), mockk())

        val branchMrid = spyk<BranchMridSupplier>()
        val equipmentMrid = spyk<EquipmentMridSupplier>()
        val initBranch = spyk<BranchInitialisation>()
        val initEquipment = spyk<EquipmentInitialisation<Junction>>()

        validateConvenienceCustomisation(branchMrid, equipmentMrid, initBranch, initEquipment) {
            addToEdgeBetween(lvFeeder, hvFeeder, branchMrid, equipmentMrid, null, initBranch, initEquipment)
        }
        validateConvenienceCustomisation(branchMrid, equipmentMrid, initBranch, initEquipment) {
            addToEdgeBetween<Feeder, Junction>(substation, branchMrid, equipmentMrid, null, initBranch, initEquipment)
        }
        validateConvenienceCustomisation(branchMrid, equipmentMrid, initBranch, initEquipment) {
            addToEdgeBetween<Substation, LvFeeder, Junction>(branchMrid, equipmentMrid, null, initBranch, initEquipment)
        }
        validateConvenienceCustomisation(branchMrid, equipmentMrid, initBranch, initEquipment) {
            addToEdgeBetween(this, EdgeDetectionDetails(classes, containers), branchMrid, equipmentMrid, initBranch, initEquipment)
        }
    }

    @Test
    internal fun `check that 'addToEdgeBetween' creates default equivalent network`() {
        val network = NetworkService()
        val (edgeEquipment, lvFeeder, hvFeeder, _) = network.createEdgeBetween<LvFeeder, Feeder>()
        edgeEquipment.apply {
            location = Location()
            baseVoltage = BaseVoltage().apply { nominalVoltage = 1 }
            terminals.forEach { it.phases = PhaseCode.AB }
        }

        val results = network.addToEdgeBetween<EnergySource>(lvFeeder, hvFeeder)

        assertThat(results, hasSize(1))
        results.first().also { (otherEdgeEquipment, _, branchToEquipment) ->
            assertThat(otherEdgeEquipment, equalTo(edgeEquipment))

            assertThat(branchToEquipment, aMapWithSize(1))
            branchToEquipment.entries.first().also { (equivalentBranch, equivalentEquipment) ->
                assertThat(equivalentBranch.mRID, equalTo("${edgeEquipment.mRID}-eb"))
                assertThat(equivalentBranch.location, equalTo(edgeEquipment.location))
                assertThat(equivalentBranch.baseVoltage, equalTo(edgeEquipment.baseVoltage))
                assertThat(equivalentBranch.terminals.map { it.phases }, contains(PhaseCode.AB, PhaseCode.AB))
                assertThat(
                    equivalentBranch.terminals.map { t -> NetworkService.connectedTerminals(t).map { it.to }.toSet() },
                    contains(setOf(edgeEquipment), equivalentEquipment.toSet())
                )
                equivalentBranch.assertConnected(setOf(edgeEquipment), equivalentEquipment.toSet())

                assertThat(equivalentEquipment, hasSize(1))
                equivalentEquipment.first().also { equipment ->
                    assertThat(equipment, instanceOf(EnergySource::class.java))
                    assertThat(equipment.mRID, equalTo("${edgeEquipment.mRID}-eeq"))
                    assertThat(equipment.location, equalTo(edgeEquipment.location))
                    assertThat(equipment.baseVoltage, equalTo(edgeEquipment.baseVoltage))
                    assertThat(equipment.terminals.map { it.phases }, contains(PhaseCode.AB))
                    assertThat(
                        equipment.terminals.map { t -> NetworkService.connectedTerminals(t).map { it.to }.toSet() },
                        contains(setOf(equivalentBranch))
                    )
                    equipment.assertConnected(setOf(equivalentBranch))
                }
            }
        }

        assertThat(network.num<EquivalentBranch>(), equalTo(1))
        assertThat(network.num<EnergySource>(), equalTo(1))
        assertThat(network.num<Terminal>(), equalTo(6)) // The original 3 plus the new 3.
    }

    @Test
    internal fun `check that 'addToEdgeBetween' applies customisation`() {
        val network = NetworkService()
        val (edgeEquipment, lvFeeder, hvFeeder, edgeNode) = network.createEdgeBetween<LvFeeder, Feeder>()
        edgeEquipment.apply {
            location = Location()
            baseVoltage = BaseVoltage().apply { nominalVoltage = 1 }
        }

        val branchMrid = spyk<BranchMridSupplier>({ "branchMrid" })
        val equipmentMrid = spyk<EquipmentMridSupplier>({ "equipmentMrid" })
        val initBranch = spyk<BranchInitialisation>({ location = Location() })
        val initEquipment = spyk<EquipmentInitialisation<EnergySource>>({ baseVoltage = BaseVoltage() })

        val results = network.addToEdgeBetween(lvFeeder, hvFeeder, branchMrid, equipmentMrid, null, initBranch, initEquipment)

        results.first().also { result ->
            result.branchToEquipment.entries.first().also { (equivalentBranch, equivalentEquipment) ->
                verify(exactly = 1) { branchMrid.invoke(EquivalentBranchDetails(edgeEquipment, edgeNode)) }
                verify(exactly = 1) { initBranch.invoke(equivalentBranch, any()) }

                assertThat(equivalentBranch.mRID, equalTo("branchMrid"))
                assertThat(equivalentBranch.location, not(equalTo(edgeEquipment.location)))
                assertThat(equivalentBranch.baseVoltage, equalTo(edgeEquipment.baseVoltage))

                equivalentEquipment.first().also { equipment ->
                    verify(exactly = 1) { equipmentMrid.invoke(EquivalentEquipmentDetails(edgeEquipment, edgeNode, equivalentBranch)) }
                    assertThat(equipment, instanceOf(EnergySource::class.java))
                    assertThat(equipment.mRID, equalTo("equipmentMrid"))
                    assertThat(equipment.location, equalTo(edgeEquipment.location))
                    assertThat(equipment.baseVoltage, not(equalTo(edgeEquipment.baseVoltage)))
                    verify(exactly = 1) { initEquipment.invoke(equipment as EnergySource, any()) }
                }
            }
        }

        confirmVerified(branchMrid)
        confirmVerified(equipmentMrid)
        confirmVerified(initBranch)
        confirmVerified(initEquipment)
    }

    @Test
    internal fun `check that 'addToEdgeBetween' can override branch phases by creating terminals`() {
        val network = NetworkService()
        val (edgeEquipment, lvFeeder, hvFeeder, _) = network.createEdgeBetween<LvFeeder, Feeder>()
        edgeEquipment.apply {
            location = Location()
            baseVoltage = BaseVoltage().apply { nominalVoltage = 1 }
        }

        val initBranch = spyk<BranchInitialisation>({ addTerminal(Terminal().apply { phases = PhaseCode.AN }) })

        val results = network.addToEdgeBetween<EnergySource>(lvFeeder, hvFeeder, initBranch = initBranch)

        results.first().also { result ->
            result.branchToEquipment.entries.first().also { (equivalentBranch, equivalentEquipment) ->
                assertThat(equivalentBranch.terminals.map { it.phases }, contains(PhaseCode.AN, PhaseCode.AN))

                equivalentEquipment.first().also { equipment ->
                    assertThat(equipment.terminals.map { it.phases }, contains(PhaseCode.AN))
                }
            }
        }
    }

    @Test
    internal fun `check that 'addToEdgeBetween' can override equipment phases by creating terminals`() {
        val network = NetworkService()
        val (edgeEquipment, lvFeeder, hvFeeder, _) = network.createEdgeBetween<LvFeeder, Feeder>()
        edgeEquipment.apply {
            location = Location()
            baseVoltage = BaseVoltage().apply { nominalVoltage = 1 }
        }

        val initEquipment = spyk<EquipmentInitialisation<EnergySource>>({ addTerminal(Terminal().apply { phases = PhaseCode.BN }) })

        val results = network.addToEdgeBetween(lvFeeder, hvFeeder, initEquipment = initEquipment)

        results.first().also { result ->
            result.branchToEquipment.entries.first().also { (equivalentBranch, equivalentEquipment) ->
                assertThat(equivalentBranch.terminals.map { it.phases }, contains(PhaseCode.ABC, PhaseCode.ABC))

                equivalentEquipment.first().also { equipment ->
                    assertThat(equipment.terminals.map { it.phases }, contains(PhaseCode.BN))
                }
            }
        }
    }

    @Test
    internal fun `check that 'addToEdgeBetween' can override equipment by returning different phases`() {
        val network = NetworkService()
        val (edgeEquipment, lvFeeder, hvFeeder, _) = network.createEdgeBetween<LvFeeder, Feeder>()
        edgeEquipment.apply {
            location = Location()
            baseVoltage = BaseVoltage().apply { nominalVoltage = 1 }
        }

        val results = addToEdgeBetween(network, between(lvFeeder, hvFeeder)) {
            sequenceOf(
                PhaseCode.A to Junction("1"),
                PhaseCode.B to Junction("2"),
                PhaseCode.C to Junction("3"),
                null to Junction("4")
            )
        }

        results.first().also { result ->
            result.branchToEquipment.entries.first().also { (equivalentBranch, equivalentEquipment) ->
                assertThat(equivalentBranch.terminals.map { it.phases }, contains(PhaseCode.ABC, PhaseCode.ABC))
                assertThat(equivalentEquipment.flatMap { it.terminals }.map { it.phases }, contains(PhaseCode.A, PhaseCode.B, PhaseCode.C, PhaseCode.ABC))
            }
        }
    }

    @Test
    internal fun `check that 'addToEdgeBetween' adds equivalent branch and equipment to correct container`() {
        NetworkService().also { network ->
            val (edgeEquipment, lvFeeder, hvFeeder, _) = network.createEdgeBetween<LvFeeder, Feeder>()
            val otherEquipment = network.sequenceOf<AcLineSegment>().first()

            network.addToEdgeBetween<EnergySource>(lvFeeder, hvFeeder)

            val equivalentBranch = network.sequenceOf<EquivalentBranch>().first()
            val equivalentEquipment = network.sequenceOf<EnergySource>().first()

            assertThat(equivalentBranch.containers, contains(hvFeeder))
            assertThat(equivalentEquipment.containers, contains(hvFeeder))

            assertThat(lvFeeder.equipment, containsInAnyOrder(edgeEquipment, otherEquipment))
            assertThat(hvFeeder.equipment, containsInAnyOrder(edgeEquipment, equivalentBranch, equivalentEquipment))
        }

        // Do the same check with the containers set in the reverse order.
        NetworkService().also { network ->
            val (edgeEquipment, lvFeeder, hvFeeder, _) = network.createEdgeBetween<LvFeeder, Feeder>(edgeIsOnFirstContainer = true)
            val otherEquipment = network.sequenceOf<AcLineSegment>().first()

            network.addToEdgeBetween<EnergySource>(lvFeeder, hvFeeder)

            val equivalentBranch = network.sequenceOf<EquivalentBranch>().first()
            val equivalentEquipment = network.sequenceOf<EnergySource>().first()

            assertThat(equivalentBranch.containers, contains(lvFeeder))
            assertThat(equivalentEquipment.containers, contains(lvFeeder))

            assertThat(lvFeeder.equipment, containsInAnyOrder(edgeEquipment, equivalentBranch, equivalentEquipment))
            assertThat(hvFeeder.equipment, containsInAnyOrder(edgeEquipment, otherEquipment))
        }
    }

    @Test
    internal fun `check that 'addToEdgeBetween' supports multiple edges`() {
        val (network, edges) = networkWithEdges(LvFeeder::class to Feeder::class, Feeder::class to LvFeeder::class)

        val results = network.addToEdgeBetween<LvFeeder, Feeder, Junction>()

        assertThat(results, hasSize(2))
        assertThat(results.map { it.edgeEquipment }, contains(*edges.map { it.conductingEquipment }.toTypedArray()))
        assertThat(results.first().branchToEquipment.keys.first(), not(equalTo(results.last().branchToEquipment.keys.first())))
    }

    @Test
    internal fun `check that 'addToEdgeBetween' can add multiple branches with multiple equipment`() {
        val network = NetworkService()
        val (edgeEquipment, _, _, _) = network.createEdgeBetween<LvFeeder, Feeder>()

        val equivalentBranchesCreator = spyk<EquivalentBranchesCreator>({
            sequenceOf(EquivalentBranch("${it.edgeEquipment.mRID}-b1"), EquivalentBranch("${it.edgeEquipment.mRID}-b2"))
        })

        val equivalentEquipmentCreator = spyk<EquivalentEquipmentCreator>({
            sequenceOf(null to EnergyConsumer("${it.equivalentBranch.mRID}-e1"), null to EnergySource("${it.equivalentBranch.mRID}-e2"))
        })

        val results = addToEdgeBetween(
            network,
            between<Feeder, LvFeeder>(),
            createEquivalentBranches = equivalentBranchesCreator,
            createEquivalentEquipment = equivalentEquipmentCreator
        )

        assertThat(results, hasSize(1))
        results.first().also { (_, _, branchToEquipment) ->
            assertThat(branchToEquipment.keys.map { it.mRID }, contains("${edgeEquipment.mRID}-b1", "${edgeEquipment.mRID}-b2"))
            assertThat(
                branchToEquipment.values.map { equipment -> equipment.map { it.mRID }.toSet() },
                contains(
                    setOf("${edgeEquipment.mRID}-b1-e1", "${edgeEquipment.mRID}-b1-e2"),
                    setOf("${edgeEquipment.mRID}-b2-e1", "${edgeEquipment.mRID}-b2-e2")
                )
            )
        }
    }

    private fun networkWithEdges(
        vararg neighbours: Pair<KClass<out EquipmentContainer>, KClass<out EquipmentContainer>>
    ): Pair<NetworkService, List<NetworkEdge<out EquipmentContainer, out EquipmentContainer>>> {
        val network = NetworkService()
        return network to neighbours.map { (a, b) -> networkWithEdge(a, b, network) }
    }

    private inline fun <reified T : EquipmentContainer, reified U : EquipmentContainer> NetworkService.createEdgeBetween(edgeIsOnFirstContainer: Boolean = false) =
        networkWithEdge(T::class, U::class, this, edgeIsOnFirstContainer)

    private fun <T : EquipmentContainer, U : EquipmentContainer> networkWithEdge(
        aClass: KClass<T>,
        bClass: KClass<U>,
        network: NetworkService = NetworkService(),
        edgeIsOnFirstContainer: Boolean = false
    ): NetworkEdge<T, U> {
        val a = aClass.createInstance().also { network.tryAdd(it) }
        val b = bClass.createInstance().also { network.tryAdd(it) }

        val edgeEquipment = Junction().apply {
            location = Location()
            baseVoltage = BaseVoltage().apply { nominalVoltage = 1 }

            addContainer(a)
            addContainer(b)
        }.also {
            createTerminals(network, it, 2, PhaseCode.ABC)

            a.addEquipment(it)
            b.addEquipment(it)

            network.add(it)
        }

        val sharedContainer = if (edgeIsOnFirstContainer) b else a
        val equipment = AcLineSegment().apply {
            addContainer(sharedContainer)
        }.also {
            createTerminal(network, it, PhaseCode.ABC, sequenceNumber = 1)
            sharedContainer.addEquipment(it)
            network.add(it)
        }

        network.connect(edgeEquipment.terminals.first(), equipment.terminals.first())
        network.connect(edgeEquipment.terminals.last(), "${edgeEquipment.mRID}-edge-cn")

        return NetworkEdge(edgeEquipment, a, b, network["${edgeEquipment.mRID}-edge-cn"]!!)
    }

    private fun validateConvenience(
        expectedClasses: Set<KClass<*>>,
        expectedContainers: Set<EquipmentContainer>,
        expectedLimit: Int?,
        addEdges: NetworkService.() -> Set<EquivalentNetworkConnection>
    ) {
        val network = NetworkService()
        val edgeDetectionDetails = slot<EdgeDetectionDetails>()
        val expectedResults = setOf<EquivalentNetworkConnection>(mockk(), mockk())

        mockkObject(EquivalentNetworkCreator)
        every { addToEdgeBetween(network, capture(edgeDetectionDetails), expectedLimit, any(), any()) } returns expectedResults

        assertThat(network.addEdges(), equalTo(expectedResults))

        assertThat(edgeDetectionDetails.captured.edgeContainerClasses, equalTo(expectedClasses))
        assertThat(edgeDetectionDetails.captured.containers, equalTo(expectedContainers))
    }

    private fun validateConvenienceCustomisation(
        expectedBranchMrid: BranchMridSupplier,
        expectedEquipmentMrid: EquipmentMridSupplier,
        expectedInitBranch: BranchInitialisation,
        expectedInitEquipment: EquipmentInitialisation<Junction>,
        addEdges: NetworkService.() -> Set<EquivalentNetworkConnection>
    ) {
        val network = NetworkService()

        mockkObject(EquivalentNetworkCreator)
        every { addToEdgeBetween(network, any(), any(), any(), any()) } returns emptySet()
        every { singleEquivalentBranchCreator(expectedBranchMrid, expectedInitBranch) } answers { callOriginal() }
        every { singleEquipmentCreator(expectedEquipmentMrid, expectedInitEquipment) } answers { callOriginal() }

        network.addEdges()

        @Suppress("UnusedLambdaExpressionBody")
        verify(exactly = 1) { singleEquivalentBranchCreator(expectedBranchMrid, expectedInitBranch) }
        verify(exactly = 1) { singleEquipmentCreator(expectedEquipmentMrid, expectedInitEquipment) }
    }

    private fun ConductingEquipment.assertConnected(vararg expected: Set<ConductingEquipment>) {
        assertThat(terminals.map { t -> NetworkService.connectedTerminals(t).map { it.to }.toSet() }, contains(*expected))

    }

    private data class NetworkEdge<T, U>(
        val conductingEquipment: ConductingEquipment,
        val firstContainer: T,
        val secondContainer: U,
        val node: ConnectivityNode
    )

}
