/*
 * Copyright 2023 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.equivalents

import com.zepben.evolve.cim.iec61970.base.core.*
import com.zepben.evolve.cim.iec61970.base.equivalents.EquivalentBranch
import com.zepben.evolve.cim.iec61970.base.wires.EnergyConsumer
import com.zepben.evolve.cim.iec61970.base.wires.EnergySource
import com.zepben.evolve.cim.iec61970.base.wires.PowerTransformer
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.LvFeeder
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.equivalents.EquivalentNetworkCreator.addToEdgeBetween
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

typealias BranchMridSupplier = (EquivalentBranchDetails) -> String
typealias EquipmentMridSupplier = (EquivalentEquipmentDetails) -> String
typealias BranchInitialisation = EquivalentBranch.(EquivalentBranchDetails) -> Unit
typealias EquipmentInitialisation<T> = T.(EquivalentEquipmentDetails) -> Unit

typealias EquivalentBranchesCreator = (EquivalentBranchDetails) -> Sequence<EquivalentBranch>
typealias EquivalentEquipmentCreator = (EquivalentEquipmentDetails) -> Sequence<Pair<PhaseCode?, ConductingEquipment>>

/**
 * Utility class that contains methods to add 'equivalent networks' to the edges between [EquipmentContainer]s. The 'equivalent networks' are network
 * simplifications comprised of groups of [EquivalentBranch]es with [ConductingEquipment].
 *
 * Use cases examples:
 * - Adding [EnergyConsumer]s to the edge between a [Feeder] and its [LvFeeder]s. Essentially simplifying the lv side.
 * - Adding [EnergySource]s to the edge between a [Feeder] and its [Substation]. Essentially simplifying the feeders upstream network.
 *
 * IMPORTANT:
 * For the purpose of these functions, we are only interested in [EquipmentContainer]s that divide the network into different levels of voltage e.g.
 * [LvFeeder], [Feeder], [Substation], [Circuit]. As such we filter out [Site]s whenever trying to determine edges.
 *
 * NOTE:
 * This class' methods identify a [ConductingEquipment] as an edge equipment if it belongs to two neighbouring [EquipmentContainer]s. If they find a
 * [ConnectivityNode] associated with a [Terminal] belonging to a [ConductingEquipment] that was identified as an edge equipment, they will treat it as an edge
 * and attach an [EquivalentBranch] to it.
 */
object EquivalentNetworkCreator {

    /**
     * Function to add an 'equivalent network' to the edge between an [EquipmentContainer] whose equipment is present in a [NetworkService] and a
     * neighbouring [EquipmentContainer] whose equipment is missing from the [NetworkService].
     *
     * The 'equivalent network' created by this function will be a single [EquivalentBranch] connected to a single [ConductingEquipment] of type [T], with
     * phases matching those of the connected [ConductingEquipment] in the actual network. If you require more control over the created 'equivalent network',
     * please use the more complex version of this function using the factory methods.
     *
     * @param T The type of [ConductingEquipment] to add to the edge.
     * @param network The [NetworkService] that we need to add the [EquivalentBranch] and [ConductingEquipment] to.
     * @param edgeDetectionDetails The [EdgeDetectionDetails] defining the edges of the network to add an 'equivalent network'.
     * @param branchMrid A factory method to produce the mRID of the created [EquivalentBranch].
     * @param equipmentMrid A factory method to produce the mRID of the created [ConductingEquipment].
     * @param initBranch A callback function that allows the caller to initialise the [EquivalentBranch].
     * @param initEquipment A callback function that allows the caller to initialise the [ConductingEquipment].
     * @param maxNumber Used to limit the number of 'equivalent networks' you want to add e.g. create only a single infeed for a substation. The default null
     *   value will create as many as possible.
     *
     * @return a set of [EquivalentNetworkConnection]s that hold information and references to the edge [ConnectivityNode], edge [ConductingEquipment], and
     *   added [EquivalentBranch]es and their [ConductingEquipment].
     */
    inline fun <reified T : ConductingEquipment> addToEdgeBetween(
        network: NetworkService,
        edgeDetectionDetails: EdgeDetectionDetails,
        noinline branchMrid: BranchMridSupplier = { (edgeEquipment, _) -> "${edgeEquipment.mRID}-eb" },
        noinline equipmentMrid: EquipmentMridSupplier = { (edgeEquipment, _, _) -> "${edgeEquipment.mRID}-eeq" },
        noinline initBranch: BranchInitialisation = {},
        noinline initEquipment: EquipmentInitialisation<T> = {},
        maxNumber: Int? = null
    ): Set<EquivalentNetworkConnection> =
        addToEdgeBetween(
            network,
            edgeDetectionDetails,
            maxNumber,
            singleEquivalentBranchCreator(branchMrid, initBranch),
            singleEquipmentCreator(equipmentMrid, initEquipment)
        )

    /**
     * Function to add an 'equivalent network' to the edge between an [EquipmentContainer] whose equipment is present in a [NetworkService] and a
     * neighbouring [EquipmentContainer] whose equipment is missing from the [NetworkService].
     *
     * The 'equivalent network' created by this function is controlled by the factory methods [createEquivalentBranches] and [createEquivalentEquipment]. If you
     * do not need this level of control, please use the simpler versions of this function which creates single branches and equipment instances.
     *
     * @param network The network service that we need to add the equivalent branches and conducting equipment to.
     * @param edgeDetectionDetails The [EdgeDetectionDetails] defining the edges of the network to add an 'equivalent network'.
     * @param createEquivalentBranches A factory method called for every extremity where an equivalent branch is required to represent an `equivalent network`.
     *   You can create more than one equivalent branch if different parameters are required.
     *
     *   By default, the equivalent branch will be created using the phases of the conducting equipment it is being connected to. To only connect a subset of
     *   these phases, create a terminal on the branch with the required phases.
     *
     *   The equivalent branches returned from this factory will be updated to ensure they have two terminals. The first will be used to connect to the actual
     *   network, the second to the conducting equipment returned by [createEquivalentEquipment].
     *
     *   NOTE: Any objects created other than the equivalent branch must be manually added to the [network], including terminals.
     * @param maxNumber Used to limit the number of 'equivalent networks' you want to add e.g. create only a single infeed for a substation. The default null
     *   value will create as many as possible.
     * @param createEquivalentEquipment A factory method called for every equivalent branch created by [createEquivalentBranches], allowing you to create
     *   conducting equipment to attach to the equivalent branch.
     *
     *   All conducting equipment returned from this factory method will be attached to the equivalent branch using the specified phases, so there is no need to
     *   connect them yourself. If you specify a phase code of `null`, the phases will be pulled from the equivalent branch instead.
     *
     *   The conducting equipment will be connected via their first terminal (or one will be created), so if you want to create a chain of equipment, you will
     *   need to create multiple terminals and use the other terminals to connect your chain.
     *
     *   NOTE: Any objects created other than the equivalent branch must be manually added to the [network], including terminals.
     *
     * @return a set of [EquivalentNetworkConnection]s that hold information and references to the edge [ConnectivityNode], edge [ConductingEquipment], and
     *   added [EquivalentBranch]es and their [ConductingEquipment].
     */
    fun addToEdgeBetween(
        network: NetworkService,
        edgeDetectionDetails: EdgeDetectionDetails,
        maxNumber: Int? = null,
        createEquivalentBranches: EquivalentBranchesCreator = singleEquivalentBranchCreator(),
        createEquivalentEquipment: EquivalentEquipmentCreator
    ): Set<EquivalentNetworkConnection> {
        val edgeCnn = getEdgeNodes(network, edgeDetectionDetails)
        val edgeCnToProcess = maxNumber?.let { edgeCnn.take(it) } ?: edgeCnn

        return edgeCnToProcess.map { cn -> addEquivalentNetwork(network, cn, createEquivalentBranches, createEquivalentEquipment) }.toSet()
    }

    /**
     * An [EquivalentBranch] creator that should cover most use cases. It creates a single [EquivalentBranch] per edge detected using all the phases of the
     * actual network it is protruding from. The [EquivalentBranch] will not have any terminals, create them yourself in the [initBranch] function if you need
     * to set the phases, otherwise leave them blank and the default will be to connect with the appropriate phases.
     *
     * @param branchMrid A factory function to override the production of the mRID of the created [EquivalentBranch].
     * @param initBranch A callback function that allows the caller to initialise the [EquivalentBranch].
     *
     * @return A sequence containing the single [EquivalentBranch] that has been created and needs to be connected to the network.
     */
    fun singleEquivalentBranchCreator(
        branchMrid: BranchMridSupplier = { (edgeEquipment, _) -> "${edgeEquipment.mRID}-eb" },
        initBranch: BranchInitialisation = {},
    ): EquivalentBranchesCreator =
        { (edgeEquipment, edgeNode) ->
            sequenceOf(
                EquivalentBranch(branchMrid(edgeEquipment via edgeNode)).apply {
                    location = edgeEquipment.location
                    baseVoltage = edgeEquipment.getEquivalentBaseVoltage(edgeNode)
                    initBranch(edgeEquipment via edgeNode)
                }
            )
        }

    /**
     * Create a [EquivalentEquipmentCreator] that creates a single [T] to attach to each [EquivalentBranch].
     *
     * @param T The type of [ConductingEquipment] to create.
     * @param equipmentMrid A factory method to produce the mRID of the created [ConductingEquipment].
     * @param initEquipment A callback function that allows the caller to initialise the [ConductingEquipment].
     *
     * @return The [EquivalentEquipmentCreator].
     */
    inline fun <reified T : ConductingEquipment> singleEquipmentCreator(
        noinline equipmentMrid: EquipmentMridSupplier = { (edgeEquipment, _, _) -> "${edgeEquipment.mRID}-eeq" },
        noinline initEquipment: EquipmentInitialisation<T> = {},
    ): EquivalentEquipmentCreator =
        singleEquipmentCreator(T::class, equipmentMrid, initEquipment)

    /**
     * Create a [EquivalentEquipmentCreator] that creates a single instance of [klass] to attach to each [EquivalentBranch].
     *
     * @param klass The type of [ConductingEquipment] to create.
     * @param equipmentMrid A factory method to produce the mRID of the created [ConductingEquipment].
     * @param initEquipment A callback function that allows the caller to initialise the [ConductingEquipment].
     *
     * @return The [EquivalentEquipmentCreator].
     */
    fun <T : ConductingEquipment> singleEquipmentCreator(
        klass: KClass<T>,
        equipmentMrid: EquipmentMridSupplier = { (edgeEquipment, _, _) -> "${edgeEquipment.mRID}-eeq" },
        initEquipment: EquipmentInitialisation<T> = {},
    ): EquivalentEquipmentCreator =
        { (edgeEquipment, edgeNode, equivalentBranch) ->
            sequenceOf(
                equivalentBranch.terminals.last().phases to
                    klass.primaryConstructor!!
                        .call(equipmentMrid(edgeEquipment via edgeNode via equivalentBranch))
                        .apply {
                            location = edgeEquipment.location
                            baseVoltage = edgeEquipment.getEquivalentBaseVoltage(edgeNode)
                            initEquipment(edgeEquipment via edgeNode via equivalentBranch)
                        }
            )
        }

    /**
     * Convenience function to get the [BaseVoltage] from an edge [ConductingEquipment].
     */
    private fun ConductingEquipment.getEquivalentBaseVoltage(edgeNode: ConnectivityNode): BaseVoltage? =
        when (this) {
            is PowerTransformer -> getBaseVoltage(edgeNode)
            else -> baseVoltage
        }

    private fun addEquivalentNetwork(
        network: NetworkService,
        edgeNode: ConnectivityNode,
        createEquivalentBranches: EquivalentBranchesCreator,
        createEquivalentEquipment: EquivalentEquipmentCreator
    ): EquivalentNetworkConnection {
        // An edgeNode by definition must have an associated ConductingEquipment with multiple equipment containers, so conductingEquipment must be non-null.
        val edgeEquipment = edgeNode.terminals.first().conductingEquipment!!
        val containersForEquivalentNetwork = determineContainersForEquivalentNetwork(edgeNode)

        val branchToEquipment = createEquivalentBranches(edgeEquipment via edgeNode)
            .onEach { equivalentBranch -> equivalentBranch.connectAndAdd(network, edgeNode, containersForEquivalentNetwork) }
            .associateWith { equivalentBranch ->
                val eqBranchTerminal = equivalentBranch.terminals.last()

                createEquivalentEquipment(edgeEquipment via edgeNode via equivalentBranch)
                    .onEach { (phaseCode, conEq) -> conEq.connectAndAdd(network, phaseCode, eqBranchTerminal, containersForEquivalentNetwork) }
                    .map { (_, conEq) -> conEq }
                    .toSet()
            }

        return EquivalentNetworkConnection(edgeEquipment, edgeNode, branchToEquipment)
    }

    private fun EquivalentBranch.connectAndAdd(network: NetworkService, edgeNode: ConnectivityNode, containersForEquivalentNetwork: Set<EquipmentContainer>) {
        prepareAndAdd(network, terminals.firstOrNull()?.phases ?: edgeNode.terminals.first().phases, 2, containersForEquivalentNetwork)
        network.connect(terminals.first(), edgeNode.mRID)
    }

    private fun ConductingEquipment.connectAndAdd(
        network: NetworkService,
        phaseCode: PhaseCode?,
        eqBranchTerminal: Terminal,
        containersForEquivalentNetwork: Set<EquipmentContainer>
    ) {
        prepareAndAdd(network, phaseCode ?: eqBranchTerminal.phases, 1, containersForEquivalentNetwork)
        network.connect(terminals.first(), eqBranchTerminal)
    }

    private fun ConductingEquipment.prepareAndAdd(
        network: NetworkService,
        phaseCode: PhaseCode,
        minTerminals: Int,
        containersForEquivalentNetwork: Set<EquipmentContainer>
    ) {
        network.tryAdd(this)

        containersForEquivalentNetwork.forEach {
            it.addEquipment(this)
            addContainer(it)
        }

        for (i in numTerminals() until minTerminals)
            addTerminal(Terminal("${mRID}-t${numTerminals() + 1}").apply { phases = phaseCode }.also { network.add(it) })
    }

    /**
     * This function returns the [EquipmentContainer]s associated with an edge node [ConnectivityNode].
     *
     * IMPORTANT: We are only interested in [EquipmentContainer]s that divide the network into different
     * levels of voltage e.g. [LvFeeder], [Feeder], [Substation], [Circuit]. As such we filter out [Site]s.
     */
    private fun determineContainersForEquivalentNetwork(edgeNode: ConnectivityNode): Set<EquipmentContainer> =
        edgeNode.terminals.first().conductingEquipment!!.containers.toSet().minus(
            edgeNode.terminals.first().otherTerminals()
                .flatMap { ot -> ot.connectedTerminals().flatMap { it.conductingEquipment?.containers ?: emptyList() } }
                .toSet()
        )
            .asSequence()
            .filter { it !is Site } // We do not want Sites, so we filter them out.
            .toSet()

    /**
     * This function returns all edge nodes in a [NetworkService] filtered by the [EquipmentContainer]s in 'edgeDetectionDetails.containers', where their
     * associated [ConductingEquipment]'s containers collection has [EquipmentContainer]s of all the classes in 'edgeDetectionDetails.edgeContainerClasses'.

     * An edge node is a [ConnectivityNode] with the following characteristics:
     * 1. It has a single [Terminal].
     * 2. The [Terminal]'s [ConductingEquipment] has multiple [EquipmentContainer]s which flags it as the edge between them.
     */
    private fun getEdgeNodes(network: NetworkService, edgeDetectionDetails: EdgeDetectionDetails): Set<ConnectivityNode> =
        getConnectivityNodeSequence(network, edgeDetectionDetails.containers)
            .filter { cn -> cn.terminals.size == 1 }
            .filter { cn ->
                val containers = cn.terminals.first().conductingEquipment?.containers ?: emptySet()
                edgeDetectionDetails.edgeContainerClasses.all { i -> containers.any { i.isInstance(it) } }
            }
            // NOTE: This second filter makes sure you don't match ConnectivityNodes associated to equipment that are connected to nothing.
            //     ConductingEquipment connected to nothing can be valid state in a service e.g. After resolving references for an EquipmentContainer's
            //     headTerminal, but you didn't request the equipment for the EquipmentContainer.
            .filter { cn -> cn.terminals.first().conductingEquipment?.terminals?.any { it.connectivityNode?.terminals?.size != 1 } ?: false }
            .toSet()

    /**
     * This function returns a sequence of [ConnectivityNode]s filtered by the [containers] set of [EquipmentContainer]s.
     * Passing an empty set as the [containers] argument will result in the returned sequence having all [ConnectivityNode]s in the [NetworkService].
     */
    private fun getConnectivityNodeSequence(network: NetworkService, containers: Set<EquipmentContainer>): Sequence<ConnectivityNode> =
        if (containers.isEmpty())
            network.sequenceOf()
        else {
            containers
                .asSequence()
                .flatMap { it.equipment }
                .filterIsInstance<ConductingEquipment>()
                .flatMap { eq -> eq.terminals.mapNotNull { it.connectivityNode } }
        }

    private infix fun ConductingEquipment.via(cn: ConnectivityNode): EquivalentBranchDetails = EquivalentBranchDetails(this, cn)
    private infix fun EquivalentBranchDetails.via(eb: EquivalentBranch): EquivalentEquipmentDetails = EquivalentEquipmentDetails(edgeEquipment, edgeNode, eb)

}

/**
 * Function to add an 'equivalent network' to the edge between an [EquipmentContainer] whose equipment is present in a [NetworkService] and a
 * neighbouring [EquipmentContainer] whose equipment is missing from the [NetworkService].
 *
 * The 'equivalent network' created by this function will be a single [EquivalentBranch] connected to a single [ConductingEquipment] of type [T], with
 * phases matching those of the connected [ConductingEquipment] in the actual network. If you require more control over the created 'equivalent network',
 * please use the more complex version of this function using the factory methods.
 *
 * @receiver The [NetworkService] that we need to add the [EquivalentBranch] and [ConductingEquipment] to.
 * @param T The type of [ConductingEquipment] to add to the edge.
 * @param container One of the [EquipmentContainer] containing the edge equipment.
 * @param otherContainer The other [EquipmentContainer] containing the edge equipment.
 *   NOTE: Only one of [container] or [otherContainer] will contain the edge node.
 * @param branchMrid A factory method to produce the mRID of the created [EquivalentBranch].
 * @param equipmentMrid A factory method to produce the mRID of the created [ConductingEquipment].
 * @param initBranch A callback function that allows the caller to initialise the [EquivalentBranch].
 * @param initEquipment A callback function that allows the caller to initialise the [ConductingEquipment].
 * @param maxNumber Used to limit the number of 'equivalent networks' you want to add e.g. create only a single infeed for a substation. The default null
 *   value will create as many as possible.
 *
 * @return a set of [EquivalentNetworkConnection]s that hold information and references to the edge [ConnectivityNode], edge [ConductingEquipment], and
 *   added [EquivalentBranch]es and their [ConductingEquipment].
 */
inline fun <reified T : ConductingEquipment> NetworkService.addToEdgeBetween(
    container: EquipmentContainer,
    otherContainer: EquipmentContainer,
    noinline branchMrid: BranchMridSupplier = { (edgeEquipment, _) -> "${edgeEquipment.mRID}-eb" },
    noinline equipmentMrid: EquipmentMridSupplier = { (edgeEquipment, _, _) -> "${edgeEquipment.mRID}-eeq" },
    maxNumber: Int? = null,
    noinline initBranch: BranchInitialisation = {},
    noinline initEquipment: EquipmentInitialisation<T> = {}
): Set<EquivalentNetworkConnection> =
    addToEdgeBetween(
        this,
        EdgeDetectionDetails.between(container, otherContainer),
        branchMrid,
        equipmentMrid,
        initBranch,
        initEquipment,
        maxNumber
    )

/**
 * Function to add an 'equivalent network' to the edge between an [EquipmentContainer] whose equipment is present in a [NetworkService] and a
 * neighbouring [EquipmentContainer] whose equipment is missing from the [NetworkService].
 *
 * The 'equivalent network' created by this function will be a single [EquivalentBranch] connected to a single [ConductingEquipment] of type [T], with
 * phases matching those of the connected [ConductingEquipment] in the actual network. If you require more control over the created 'equivalent network',
 * please use the more complex version of this function using the factory methods.
 *
 * @receiver The [NetworkService] that we need to add the [EquivalentBranch] and [ConductingEquipment] to.
 * @param T The type of [ConductingEquipment] to add to the edge.
 * @param container One of the [EquipmentContainer] containing the edge equipment.
 * @param OtherContainer The other class of [EquipmentContainer] containing the edge equipment.
 *   NOTE: Only one of [container] or the containers of type [OtherContainer] will contain the edge node.
 * @param branchMrid A factory method to produce the mRID of the created [EquivalentBranch].
 * @param equipmentMrid A factory method to produce the mRID of the created [ConductingEquipment].
 * @param initBranch A callback function that allows the caller to initialise the [EquivalentBranch].
 * @param initEquipment A callback function that allows the caller to initialise the [ConductingEquipment].
 * @param maxNumber Used to limit the number of 'equivalent networks' you want to add e.g. create only a single infeed for a substation. The default null
 *   value will create as many as possible.
 *
 * @return a set of [EquivalentNetworkConnection]s that hold information and references to the edge [ConnectivityNode], edge [ConductingEquipment], and
 *   added [EquivalentBranch]es and their [ConductingEquipment].
 */
inline fun <reified OtherContainer : EquipmentContainer, reified T : ConductingEquipment> NetworkService.addToEdgeBetween(
    container: EquipmentContainer,
    noinline branchMrid: BranchMridSupplier = { (edgeEquipment, _) -> "${edgeEquipment.mRID}-eb" },
    noinline equipmentMrid: EquipmentMridSupplier = { (edgeEquipment, _, _) -> "${edgeEquipment.mRID}-eeq" },
    maxNumber: Int? = null,
    noinline initBranch: BranchInitialisation = {},
    noinline initEquipment: EquipmentInitialisation<T> = {}
): Set<EquivalentNetworkConnection> =
    addToEdgeBetween(
        this,
        EdgeDetectionDetails.between(container, OtherContainer::class),
        branchMrid,
        equipmentMrid,
        initBranch,
        initEquipment,
        maxNumber
    )

/**
 * Function to add an 'equivalent network' to the edge between an [EquipmentContainer] whose equipment is present in a [NetworkService] and a
 * neighbouring [EquipmentContainer] whose equipment is missing from the [NetworkService].
 *
 * The 'equivalent network' created by this function will be a single [EquivalentBranch] connected to a single [ConductingEquipment] of type [T], with
 * phases matching those of the connected [ConductingEquipment] in the actual network. If you require more control over the created 'equivalent network',
 * please use the more complex version of this function using the factory methods.
 *
 * @receiver The [NetworkService] that we need to add the [EquivalentBranch] and [ConductingEquipment] to.
 * @param T The type of [ConductingEquipment] to add to the edge.
 * @param Container One of the [EquipmentContainer] containing the edge equipment.
 * @param OtherContainer The other class of [EquipmentContainer] containing the edge equipment.
 *   NOTE: Only one of the containers of type [Container] or [OtherContainer] will contain the edge node.
 * @param branchMrid A factory method to produce the mRID of the created [EquivalentBranch].
 * @param equipmentMrid A factory method to produce the mRID of the created [ConductingEquipment].
 * @param initBranch A callback function that allows the caller to initialise the [EquivalentBranch].
 * @param initEquipment A callback function that allows the caller to initialise the [ConductingEquipment].
 * @param maxNumber Used to limit the number of 'equivalent networks' you want to add e.g. create only a single infeed for a substation. The default null
 *   value will create as many as possible.
 *
 * @return a set of [EquivalentNetworkConnection]s that hold information and references to the edge [ConnectivityNode], edge [ConductingEquipment], and
 *   added [EquivalentBranch]es and their [ConductingEquipment].
 */
inline fun <reified Container : EquipmentContainer, reified OtherContainer : EquipmentContainer, reified T : ConductingEquipment> NetworkService.addToEdgeBetween(
    noinline branchMrid: BranchMridSupplier = { (edgeEquipment, _) -> "${edgeEquipment.mRID}-eb" },
    noinline equipmentMrid: EquipmentMridSupplier = { (edgeEquipment, _, _) -> "${edgeEquipment.mRID}-eeq" },
    maxNumber: Int? = null,
    noinline initBranch: BranchInitialisation = {},
    noinline initEquipment: EquipmentInitialisation<T> = {}
): Set<EquivalentNetworkConnection> =
    addToEdgeBetween(
        this,
        EdgeDetectionDetails.between(Container::class, OtherContainer::class),
        branchMrid,
        equipmentMrid,
        initBranch,
        initEquipment,
        maxNumber
    )
