/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network

import com.zepben.evolve.cim.iec61968.common.Location
import com.zepben.evolve.cim.iec61970.base.core.*
import com.zepben.evolve.cim.iec61970.base.equivalents.EquivalentBranch
import com.zepben.evolve.cim.iec61970.base.wires.Breaker
import com.zepben.evolve.cim.iec61970.base.wires.Junction
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.LvFeeder
import com.zepben.evolve.services.network.EquivalentNetworkUtils.EquivalentBranchDetails
import com.zepben.evolve.services.network.EquivalentNetworkUtils.EquivalentEquipmentDetails
import com.zepben.evolve.services.network.EquivalentNetworkUtils.EquivalentNetworkConnection
import com.zepben.evolve.services.network.EquivalentNetworkUtils.addToEdgeBetween
import com.zepben.evolve.services.network.EquivalentNetworkUtils.addToEdgeBetweenContainers
import com.zepben.evolve.services.network.testdata.createTerminal
import com.zepben.evolve.services.network.testdata.createTerminals
import io.mockk.clearAllMocks
import io.mockk.mockkObject
import io.mockk.spyk
import io.mockk.verify
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

internal class EquivalentNetworkUtilsTest {

    @BeforeEach
    internal fun setup() {
        clearAllMocks()
    }

    @Test
    internal fun `check that calling convenience function with containers calls underlying function with default values`() {
        val (network, _) = networkWithEdges(LvFeeder::class to Feeder::class)
        val lvFeeder = network.sequenceOf<LvFeeder>().first()
        val hvFeeder = network.sequenceOf<Feeder>().first()

        mockkObject(EquivalentNetworkUtils)

        addToEdgeBetween<Breaker>(lvFeeder, hvFeeder, network)
        verify {
            addToEdgeBetweenContainers(
                container = eq(lvFeeder),
                otherContainer = eq(hvFeeder),
                network = eq(network),
                createEquivalentBranches = any(),
                createEquivalentEquipment = any(),
            )
        }
    }

    @Test
    internal fun `check that calling convenience extension function with containers calls underlying function with default values`() {
        val (network, _) = networkWithEdges(LvFeeder::class to Feeder::class)
        val lvFeeder = network.sequenceOf<LvFeeder>().first()
        val hvFeeder = network.sequenceOf<Feeder>().first()

        mockkObject(EquivalentNetworkUtils)

        network.addToEdgeBetween<Breaker>(lvFeeder, hvFeeder)
        verify {
            addToEdgeBetweenContainers(
                container = eq(lvFeeder),
                otherContainer = eq(hvFeeder),
                network = eq(network),
                createEquivalentBranches = any(),
                createEquivalentEquipment = any(),
            )
        }
    }

    @Test
    internal fun `check that calling convenience function with container and container class calls underlying function with default values`() {
        val (network, _) = networkWithEdges(LvFeeder::class to Feeder::class)
        val lvFeeder = network.sequenceOf<LvFeeder>().first()

        mockkObject(EquivalentNetworkUtils)

        addToEdgeBetween<Breaker>(lvFeeder, Feeder::class, network)
        verify {
            addToEdgeBetweenContainers(
                container = eq(lvFeeder),
                otherContainerClass = eq(Feeder::class),
                network = eq(network),
                createEquivalentBranches = any(),
                createEquivalentEquipment = any(),
            )
        }
    }

    @Test
    internal fun `check that calling convenience extension function with container and container class calls underlying function with default values`() {
        val (network, _) = networkWithEdges(LvFeeder::class to Feeder::class)
        val lvFeeder = network.sequenceOf<LvFeeder>().first()

        mockkObject(EquivalentNetworkUtils)

        network.addToEdgeBetween<Breaker>(lvFeeder, Feeder::class)
        verify {
            addToEdgeBetweenContainers(
                container = eq(lvFeeder),
                otherContainerClass = eq(Feeder::class),
                network = eq(network),
                createEquivalentBranches = any(),
                createEquivalentEquipment = any(),
            )
        }
    }

    @Test
    internal fun `check that calling convenience function passing equipment container classes calls underlying function with default values`() {
        val (network, _) = networkWithEdges(LvFeeder::class to Feeder::class)

        mockkObject(EquivalentNetworkUtils)

        addToEdgeBetween<Breaker>(LvFeeder::class, Feeder::class, network)
        verify {
            addToEdgeBetweenContainers(
                containerClass = eq(LvFeeder::class),
                otherContainerClass = eq(Feeder::class),
                network = eq(network),
                createEquivalentBranches = any(),
                createEquivalentEquipment = any(),
            )
        }
    }

    @Test
    internal fun `check that calling convenience extension function passing equipment container classes calls underlying function with default values`() {
        val (network, _) = networkWithEdges(LvFeeder::class to Feeder::class)

        mockkObject(EquivalentNetworkUtils)

        network.addToEdgeBetween<Breaker>(LvFeeder::class, Feeder::class)
        verify {
            addToEdgeBetweenContainers(
                containerClass = eq(LvFeeder::class),
                otherContainerClass = eq(Feeder::class),
                network = eq(network),
                createEquivalentBranches = any(),
                createEquivalentEquipment = any(),
            )
        }
    }

    @Test
    internal fun `check that 'addToEdgeBetween' with container and otherContainer instances calls 'addToEdgeBetweenContainers' with the appropriate arguments`() {
        val (network, edgeEqToCns) = networkWithEdges(LvFeeder::class to Feeder::class)
        val lvFeeder = network.sequenceOf<LvFeeder>().first()
        val hvFeeder = network.sequenceOf<Feeder>().first()
        val (edgeEquipment) = edgeEqToCns.first()
        edgeEquipment.apply {
            location = Location()
            baseVoltage = BaseVoltage().apply { nominalVoltage = 1 }
        }

        mockkObject(EquivalentNetworkUtils)

        val branchMrid: (EquivalentBranchDetails) -> String = spyk({ "branchMrid" })
        val equipmentMrid: (EquivalentEquipmentDetails) -> String = spyk({ "equipmentMrid" })
        val initBranch: EquivalentBranch.(EquivalentBranchDetails) -> Unit = spyk({})
        val initEquipment: Junction.(EquivalentEquipmentDetails) -> Unit = spyk({})
        val maxNumber = 1
        val connectionResult = network.addToEdgeBetween(
            lvFeeder,
            hvFeeder,
            branchMrid = branchMrid,
            equipmentMrid = equipmentMrid,
            initBranch = initBranch,
            initEquipment = initEquipment,
            maxNumber = maxNumber
        )

        val (equivalentBranch, equivalentEquipment) = connectionResult.first().branchToEquipment.entries.first()
        assertThat(equivalentBranch.mRID, equalTo("branchMrid"))
        assertThat(equivalentBranch.location, equalTo(edgeEquipment.location))
        assertThat(equivalentBranch.baseVoltage, equalTo(edgeEquipment.baseVoltage))
        assertThat(equivalentEquipment.first().mRID, equalTo("equipmentMrid"))
        assertThat(equivalentEquipment.first(), instanceOf(Junction::class.java))
        assertThat(equivalentEquipment.first().location, equalTo(edgeEquipment.location))
        assertThat(equivalentEquipment.first().baseVoltage, equalTo(edgeEquipment.baseVoltage))

        verify {
            addToEdgeBetweenContainers(
                container = eq(lvFeeder),
                otherContainer = eq(hvFeeder),
                network = eq(network),
                createEquivalentBranches = any(),
                createEquivalentEquipment = any(),
                maxNumber = eq(maxNumber)
            )
        }
    }

    @Test
    internal fun `check that 'addToEdgeBetween' with container instance and otherContainerClass calls 'addToEdgeBetweenContainers' with the appropriate arguments`() {
        val (network, edgeEqToCns) = networkWithEdges(LvFeeder::class to Feeder::class)
        val lvFeeder = network.sequenceOf<LvFeeder>().first()
        val (edgeEquipment) = edgeEqToCns.first()
        edgeEquipment.apply {
            location = Location()
            baseVoltage = BaseVoltage().apply { nominalVoltage = 1 }
        }

        mockkObject(EquivalentNetworkUtils)

        val branchMrid: (EquivalentBranchDetails) -> String = spyk({ "branchMrid" })
        val equipmentMrid: (EquivalentEquipmentDetails) -> String = spyk({ "equipmentMrid" })
        val initBranch: EquivalentBranch.(EquivalentBranchDetails) -> Unit = spyk({})
        val initEquipment: Junction.(EquivalentEquipmentDetails) -> Unit = spyk({})
        val maxNumber = 1
        val connectionResult = network.addToEdgeBetween(
            lvFeeder,
            Feeder::class,
            branchMrid = branchMrid,
            equipmentMrid = equipmentMrid,
            initBranch = initBranch,
            initEquipment = initEquipment,
            maxNumber = maxNumber
        )

        val (equivalentBranch, equivalentEquipment) = connectionResult.first().branchToEquipment.entries.first()
        assertThat(equivalentBranch.mRID, equalTo("branchMrid"))
        assertThat(equivalentBranch.location, equalTo(edgeEquipment.location))
        assertThat(equivalentBranch.baseVoltage, equalTo(edgeEquipment.baseVoltage))
        assertThat(equivalentEquipment.first().mRID, equalTo("equipmentMrid"))
        assertThat(equivalentEquipment.first(), instanceOf(Junction::class.java))
        assertThat(equivalentEquipment.first().location, equalTo(edgeEquipment.location))
        assertThat(equivalentEquipment.first().baseVoltage, equalTo(edgeEquipment.baseVoltage))

        verify {
            addToEdgeBetweenContainers(
                container = eq(lvFeeder),
                otherContainerClass = eq(Feeder::class),
                network = eq(network),
                createEquivalentBranches = any(),
                createEquivalentEquipment = any(),
                maxNumber = eq(maxNumber)
            )
        }
    }

    @Test
    internal fun `check that 'addToEdgeBetween' with containerClass and otherContainerClass calls 'addToEdgeBetweenContainers' with the appropriate arguments`() {
        val (network, edgeEqToCns) = networkWithEdges(LvFeeder::class to Feeder::class)
        val (edgeEquipment) = edgeEqToCns.first()
        edgeEquipment.apply {
            location = Location()
            baseVoltage = BaseVoltage().apply { nominalVoltage = 1 }
        }

        mockkObject(EquivalentNetworkUtils)

        val branchMrid: (EquivalentBranchDetails) -> String = spyk({ "branchMrid" })
        val equipmentMrid: (EquivalentEquipmentDetails) -> String = spyk({ "equipmentMrid" })
        val initBranch: EquivalentBranch.(EquivalentBranchDetails) -> Unit = spyk({})
        val initEquipment: Junction.(EquivalentEquipmentDetails) -> Unit = spyk({})
        val maxNumber = 1
        val connectionResult = network.addToEdgeBetween(
            LvFeeder::class,
            Feeder::class,
            branchMrid = branchMrid,
            equipmentMrid = equipmentMrid,
            initBranch = initBranch,
            initEquipment = initEquipment,
            maxNumber = maxNumber
        )

        val (equivalentBranch, equivalentEquipment) = connectionResult.first().branchToEquipment.entries.first()
        assertThat(equivalentBranch.mRID, equalTo("branchMrid"))
        assertThat(equivalentBranch.location, equalTo(edgeEquipment.location))
        assertThat(equivalentBranch.baseVoltage, equalTo(edgeEquipment.baseVoltage))
        assertThat(equivalentEquipment.first().mRID, equalTo("equipmentMrid"))
        assertThat(equivalentEquipment.first(), instanceOf(Junction::class.java))
        assertThat(equivalentEquipment.first().location, equalTo(edgeEquipment.location))
        assertThat(equivalentEquipment.first().baseVoltage, equalTo(edgeEquipment.baseVoltage))

        verify {
            addToEdgeBetweenContainers(
                containerClass = eq(LvFeeder::class),
                otherContainerClass = eq(Feeder::class),
                network = eq(network),
                createEquivalentBranches = any(),
                createEquivalentEquipment = any(),
                maxNumber = eq(maxNumber)
            )
        }
    }

    @Test
    internal fun `check that 'addToEdgeBetweenContainers' based on containers adds equivalent branch and equipment to edge ConnectivityNode on correct containers`() {
        val (network, edgeEqToCns) = networkWithEdges(LvFeeder::class to Feeder::class)
        val (edgeEquipment, edgeNode) = edgeEqToCns.first()
        val lvFeeder = network.sequenceOf<LvFeeder>().first()
        val hvFeeder = network.sequenceOf<Feeder>().first()

        val equivalentBranch = EquivalentBranch()
        val equivalentEquipment = Junction()
        val equivalentBranchCreator: (EquivalentBranchDetails) -> Sequence<Pair<PhaseCode, EquivalentBranch>> =
            spyk({ (_, _) -> sequenceOf(PhaseCode.AB to equivalentBranch) })
        val equivalentEquipmentCreator: (EquivalentEquipmentDetails) -> Sequence<Pair<PhaseCode, ConductingEquipment>> =
            spyk({ (_, _, _) -> sequenceOf(PhaseCode.A to equivalentEquipment) })

        val connectionResult = addToEdgeBetweenContainers(lvFeeder, hvFeeder, network, equivalentBranchCreator, equivalentEquipmentCreator)

        // NOTE: We cannot use the infix functions (via) inside the verify block because the verify block detects 'via' as a function to verify and messes with
        // the return value. This behaviour only happens when you run all tests in the file. If you run only a single test the infix functions work.
        verify { equivalentBranchCreator(EquivalentBranchDetails(edgeEquipment, edgeNode)) }
        verify { equivalentEquipmentCreator(EquivalentEquipmentDetails(edgeEquipment, edgeNode, equivalentBranch)) }
        verifyEquivalentBranchAdded(edgeEquipment, edgeNode, equivalentBranch, hvFeeder, PhaseCode.AB)
        verifyEquivalentEquipmentAdded(equivalentBranch, equivalentEquipment, hvFeeder, PhaseCode.A)
        assertThat(connectionResult.size, equalTo(1))
        verifyEquivalentNetworkConnection(
            connectionResult.first(),
            edgeEquipment,
            edgeNode,
            mapOf(equivalentBranch to setOf(equivalentEquipment))
        )
    }

    @Test
    internal fun `check that 'addToEdgeBetweenContainers' based on container and container class adds equivalent branch and equipment to edge ConnectivityNode on correct containers`() {
        val (network, edgeEqToCns) = networkWithEdges(Feeder::class to Substation::class)
        val (edgeEquipment, edgeNode) = edgeEqToCns.first()
        val feeder = network.sequenceOf<Feeder>().first()
        val substation = network.sequenceOf<Substation>().first()

        val equivalentBranch = EquivalentBranch()
        val equivalentEquipment = Junction()
        val equivalentBranchCreator: (EquivalentBranchDetails) -> Sequence<Pair<PhaseCode, EquivalentBranch>> =
            spyk({ (_, _) -> sequenceOf(PhaseCode.BC to equivalentBranch) })
        val equivalentEquipmentCreator: (EquivalentEquipmentDetails) -> Sequence<Pair<PhaseCode, ConductingEquipment>> =
            spyk({ (_, _, _) -> sequenceOf(PhaseCode.B to equivalentEquipment) })

        val connectionResult = addToEdgeBetweenContainers(feeder, Substation::class, network, equivalentBranchCreator, equivalentEquipmentCreator)

        // NOTE: We cannot use the infix functions (via) inside the verify block because the verify block detects 'via' as a function to verify and messes with
        // the return value. This behaviour only happens when you run all tests in the file. If you run only a single test the infix functions work.
        verify { equivalentBranchCreator(EquivalentBranchDetails(edgeEquipment, edgeNode)) }
        verify { equivalentEquipmentCreator(EquivalentEquipmentDetails(edgeEquipment, edgeNode, equivalentBranch)) }
        verifyEquivalentBranchAdded(edgeEquipment, edgeNode, equivalentBranch, substation, PhaseCode.BC)
        verifyEquivalentEquipmentAdded(equivalentBranch, equivalentEquipment, substation, PhaseCode.B)
        assertThat(connectionResult.size, equalTo(1))
        verifyEquivalentNetworkConnection(
            connectionResult.first(),
            edgeEquipment,
            edgeNode,
            mapOf(equivalentBranch to setOf(equivalentEquipment))
        )
    }

    @Test
    internal fun `check that 'addToEdgeBetweenContainers' based on container classes adds equivalent branch and equipment to edge ConnectivityNode on correct containers`() {
        val (network, edgeEqToCns) = networkWithEdges(Substation::class to Circuit::class)
        val (edgeEquipment, edgeNode) = edgeEqToCns.first()
        val circuit = network.sequenceOf<Circuit>().first()

        val equivalentBranch = EquivalentBranch()
        val equivalentEquipment = Junction()
        val equivalentBranchCreator: (EquivalentBranchDetails) -> Sequence<Pair<PhaseCode, EquivalentBranch>> =
            spyk({ (_, _) -> sequenceOf(PhaseCode.AC to equivalentBranch) })
        val equivalentEquipmentCreator: (EquivalentEquipmentDetails) -> Sequence<Pair<PhaseCode, ConductingEquipment>> =
            spyk({ (_, _, _) -> sequenceOf(PhaseCode.C to equivalentEquipment) })

        val connectionResult = addToEdgeBetweenContainers(Substation::class, Circuit::class, network, equivalentBranchCreator, equivalentEquipmentCreator)

        // NOTE: We cannot use the infix functions (via) inside the verify block because the verify block detects 'via' as a function to verify and messes with
        // the return value. This behaviour only happens when you run all tests in the file. If you run only a single test the infix functions work.
        verify { equivalentBranchCreator(EquivalentBranchDetails(edgeEquipment, edgeNode)) }
        verify { equivalentEquipmentCreator(EquivalentEquipmentDetails(edgeEquipment, edgeNode, equivalentBranch)) }
        verifyEquivalentBranchAdded(edgeEquipment, edgeNode, equivalentBranch, circuit, PhaseCode.AC)
        verifyEquivalentEquipmentAdded(equivalentBranch, equivalentEquipment, circuit, PhaseCode.C)
        assertThat(connectionResult.size, equalTo(1))
        verifyEquivalentNetworkConnection(
            connectionResult.first(),
            edgeEquipment,
            edgeNode,
            mapOf(equivalentBranch to setOf(equivalentEquipment))
        )
    }

    private fun verifyEquivalentNetworkConnection(
        connection: EquivalentNetworkConnection,
        expectedEdgeEquipment: ConductingEquipment,
        expectedEdgeNode: ConnectivityNode,
        expectedBranchToEquipment: Map<EquivalentBranch, Set<ConductingEquipment>>
    ) {
        assertThat(connection.edgeEquipment, equalTo(expectedEdgeEquipment))
        assertThat(connection.edgeNode, equalTo(expectedEdgeNode))
        assertThat(connection.branchToEquipment, equalTo(expectedBranchToEquipment))
    }

    private fun verifyEquivalentBranchAdded(
        edgeEquipment: ConductingEquipment,
        edgeNode: ConnectivityNode,
        expectedEquivalentBranch: EquivalentBranch,
        expectedContainer: EquipmentContainer,
        expectedPhaseCode: PhaseCode
    ) {
        assertThat(edgeNode.terminals.size, equalTo(2))
        assertThat(edgeNode.terminals.count { t -> t.conductingEquipment == edgeEquipment }, equalTo(1))
        val equivalentBranchTerminal = edgeNode.terminals.first { t -> t.conductingEquipment == expectedEquivalentBranch }
        assertThat(equivalentBranchTerminal.phases, equalTo(expectedPhaseCode))
        assertThat(expectedEquivalentBranch.containers, contains(expectedContainer))
    }

    private fun verifyEquivalentEquipmentAdded(
        equivalentBranch: EquivalentBranch,
        expectedEquivalentEquipment: ConductingEquipment,
        expectedContainer: EquipmentContainer,
        expectedPhaseCode: PhaseCode
    ) {
        assertThat(equivalentBranch.terminals.size, equalTo(2))
        assertThat(expectedEquivalentEquipment.terminals.size, equalTo(1))
        val equivalentEquipmentTerminal = expectedEquivalentEquipment.terminals.first()
        val equivalentBranchTerminal = equivalentBranch.terminals.first { it.connectivityNode == equivalentEquipmentTerminal.connectivityNode }
        assertThat(equivalentBranchTerminal.phases, equalTo(expectedPhaseCode))
        assertThat(equivalentEquipmentTerminal.phases, equalTo(expectedPhaseCode))
        assertThat(expectedEquivalentEquipment.containers, contains(expectedContainer))
    }

    private fun networkWithEdges(vararg neighbours: Pair<KClass<out EquipmentContainer>, KClass<out EquipmentContainer>>): Pair<NetworkService, List<Pair<ConductingEquipment, ConnectivityNode>>> {
        val network = NetworkService()
        val edgeCns = mutableListOf<Pair<ConductingEquipment, ConnectivityNode>>()
        for (n in neighbours) {
            val (a, b) = n
            val (_, edgeEq, edgeCn) = networkWithEdge(a, b, network)
            edgeCns.add(edgeEq to edgeCn)
        }
        return network to edgeCns
    }

    private fun <T : EquipmentContainer, U : EquipmentContainer> networkWithEdge(
        aClass: KClass<T>,
        bClass: KClass<U>,
        network: NetworkService = NetworkService(),
        edgeIsOnFirstContainer: Boolean = false
    ): Triple<NetworkService, ConductingEquipment, ConnectivityNode> {
        val a = createFrom(aClass).also { network.tryAdd(it) }
        val b = createFrom(bClass).also { network.tryAdd(it) }

        val edgeEquipment = Junction().apply {
            a.addEquipment(this)
            addContainer(a)
            b.addEquipment(this)
            addContainer(b)
        }.also {
            network.tryAdd(it)
            createTerminals(network, it, 2)
        }

        val equipment = Junction().apply {
            if (edgeIsOnFirstContainer) {
                b.addEquipment(this)
                addContainer(b)
            } else {
                a.addEquipment(this)
                addContainer(a)
            }
        }.also {
            network.tryAdd(it)
            createTerminal(network, it, sequenceNumber = 1)
        }
        network.connect(edgeEquipment.terminals.first(), equipment.terminals.first())

        val edgeCn = ConnectivityNode()
        edgeEquipment.terminals.first { it.connectivityNode == null }.apply {
            connectivityNode = edgeCn.also {
                it.addTerminal(this)
                network.tryAdd(it)
            }
        }
        return Triple(network, edgeEquipment, edgeCn)
    }

    private fun <T : EquipmentContainer> createFrom(clazz: KClass<T>): T = clazz.createInstance()

}