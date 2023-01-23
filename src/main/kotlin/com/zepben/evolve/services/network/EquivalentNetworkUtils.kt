/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network

import com.zepben.evolve.cim.iec61970.base.core.*
import com.zepben.evolve.cim.iec61970.base.equivalents.EquivalentBranch
import com.zepben.evolve.cim.iec61970.base.wires.EnergyConsumer
import com.zepben.evolve.cim.iec61970.base.wires.EnergySource
import com.zepben.evolve.cim.iec61970.base.wires.PowerTransformer
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.LvFeeder
import com.zepben.evolve.services.common.extensions.typeNameAndMRID
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

/**
 * Utility class that contains methods to add 'equivalent networks' to the edges between [EquipmentContainer]s.
 * The 'equivalent networks' are network simplifications comprised of groups of [EquivalentBranch]es with [ConductingEquipment].
 * Use cases examples:
 * - Adding [EnergyConsumer]s to the edge between a [Feeder] and its [LvFeeder]s. Essentially simplifying the lv side.
 * - Adding [EnergySource]s to the edge between a [Feeder] and its [Substation]. Essentially simplifying the feeders upstream network.
 *
 * NOTE:
 * This class' methods identify a [ConductingEquipment] as an edge equipment if it belongs to two neighbouring [EquipmentContainer]s.
 * If they find a [ConnectivityNode] associated with a [Terminal] belonging to a [ConductingEquipment] that was identified as an edge equipment
 * they will treat it as an edge and attach an [EquivalentBranch] to it.
 */
object EquivalentNetworkUtils {

    /**
     * This is just an extension function to improve readability.
     * Refer to 'addToEdgeBetween' function below for documentation.
     */
    inline fun <reified T : ConductingEquipment> NetworkService.addToEdgeBetween(
        container: EquipmentContainer,
        otherContainer: EquipmentContainer,
        maxNumber: Int? = null,
        crossinline branchMrid: (EquivalentBranchDetails) -> String = { (edgeEquipment) -> "${edgeEquipment.mRID}-eb" },
        crossinline equipmentMrid: (EquivalentEquipmentDetails) -> String = { (edgeEquipment) -> "${edgeEquipment.mRID}-eeq" },
        crossinline initBranch: EquivalentBranch.(EquivalentBranchDetails) -> Unit = {},
        crossinline initEquipment: T.(EquivalentEquipmentDetails) -> Unit = {},
    ): Set<EquivalentNetworkConnection> = addToEdgeBetween(container, otherContainer, this, maxNumber, branchMrid, equipmentMrid, initBranch, initEquipment)

    /**
     * This is just an extension function to improve readability.
     * Refer to 'addToEdgeBetween' function below for documentation.
     */
    inline fun <reified T : ConductingEquipment> NetworkService.addToEdgeBetween(
        container: EquipmentContainer,
        otherContainerClass: KClass<out EquipmentContainer>,
        maxNumber: Int? = null,
        crossinline branchMrid: (EquivalentBranchDetails) -> String = { (edgeEquipment) -> "${edgeEquipment.mRID}-eb" },
        crossinline equipmentMrid: (EquivalentEquipmentDetails) -> String = { (edgeEquipment) -> "${edgeEquipment.mRID}-eeq" },
        crossinline initBranch: EquivalentBranch.(EquivalentBranchDetails) -> Unit = {},
        crossinline initEquipment: T.(EquivalentEquipmentDetails) -> Unit = {},
    ): Set<EquivalentNetworkConnection> =
        addToEdgeBetween(container, otherContainerClass, this, maxNumber, branchMrid, equipmentMrid, initBranch, initEquipment)


    /**
     * This is just an extension function to improve readability.
     * Refer to 'addToEdgeBetween' function below for documentation.
     */
    inline fun <reified T : ConductingEquipment> NetworkService.addToEdgeBetween(
        containerClass: KClass<out EquipmentContainer>,
        otherContainerClass: KClass<out EquipmentContainer>,
        maxNumber: Int? = null,
        crossinline branchMrid: (EquivalentBranchDetails) -> String = { (edgeEquipment) -> "${edgeEquipment.mRID}-eb" },
        crossinline equipmentMrid: (EquivalentEquipmentDetails) -> String = { (edgeEquipment) -> "${edgeEquipment.mRID}-eeq" },
        crossinline initBranch: EquivalentBranch.(EquivalentBranchDetails) -> Unit = {},
        crossinline initEquipment: T.(EquivalentEquipmentDetails) -> Unit = {},
    ): Set<EquivalentNetworkConnection> =
        addToEdgeBetween(containerClass, otherContainerClass, this, maxNumber, branchMrid, equipmentMrid, initBranch, initEquipment)


    /**
     * Data class that holds instances used to create [ConductingEquipment] connected across [EquivalentBranch].
     */
    data class EquivalentEquipmentDetails(
        val edgeEquipment: ConductingEquipment,
        val edgeNode: ConnectivityNode,
        val equivalentBranch: EquivalentBranch
    )

    /**
     * Data class that holds instances used to create [EquivalentBranch].
     */
    data class EquivalentBranchDetails(
        val edgeEquipment: ConductingEquipment,
        val edgeNode: ConnectivityNode
    )

    /**
     * Data class that holds references to the edge [ConductingEquipment] and [ConnectivityNode] to which an 'equivalent network' was attached.
     * Additionally, is holds references to each [EquivalentBranch] and their [ConductingEquipment] that make up the 'equivalent network'.
     */
    data class EquivalentNetworkConnection(
        val edgeEquipment: ConductingEquipment,
        val edgeNode: ConnectivityNode,
        val branchToEquipment: Map<EquivalentBranch, Set<ConductingEquipment>>
    )

    infix fun ConductingEquipment.via(cn: ConnectivityNode): EquivalentBranchDetails = EquivalentBranchDetails(this, cn)

    infix fun EquivalentBranchDetails.via(eb: EquivalentBranch): EquivalentEquipmentDetails = EquivalentEquipmentDetails(this.edgeEquipment, edgeNode, eb)

    /**
     * Function to add an 'equivalent network' to the edge between an [EquipmentContainer] whose equipment is present in a [NetworkService] and a
     * neighbouring [EquipmentContainer] whose equipment is missing from the [NetworkService].
     *
     * This function wraps the 'addToEdgeBetweenContainers' function and provides a simpler signature that is more convenient to use in most cases.
     *
     * For more information read the documentation for the 'addToEdgeBetweenContainers' function in this class.
     *
     * @param container The [EquipmentContainer] that has its equipment in the [NetworkService] (it doesn't hold the edge [ConnectivityNode]).
     * @param otherContainer The edge [EquipmentContainer] that doesn't have its equipment in the [NetworkService] (it holds the edge [ConnectivityNode]).
     * @param network the [NetworkService] that we need to add the [EquivalentBranch] and [ConductingEquipment] to.
     * @param maxNumber The maximum number of 'equivalent networks' you want to add. The default null value will create as many as possible.
     * @param branchMrid factory function to produce the mRID of the created [EquivalentBranch].
     * @param equipmentMrid factory function to produce the mRID of the created [ConductingEquipment].
     * @param initBranch callback function that allows the caller to initialise the [EquivalentBranch].
     * @param initEquipment callback function that allows the caller to initialise the [ConductingEquipment].
     *
     * @return a set of [EquivalentNetworkConnection]s that hold information and references to the edge [ConnectivityNode], edge [ConductingEquipment], and added [EquivalentBranch]es and their [ConductingEquipment].
     */
    inline fun <reified T : ConductingEquipment> addToEdgeBetween(
        container: EquipmentContainer,
        otherContainer: EquipmentContainer,
        network: NetworkService,
        maxNumber: Int? = null,
        crossinline branchMrid: (EquivalentBranchDetails) -> String = { (edgeEquipment) -> "${edgeEquipment.mRID}-eb" },
        crossinline equipmentMrid: (EquivalentEquipmentDetails) -> String = { (edgeEquipment) -> "${edgeEquipment.mRID}-eeq" },
        crossinline initBranch: EquivalentBranch.(EquivalentBranchDetails) -> Unit = {},
        crossinline initEquipment: T.(EquivalentEquipmentDetails) -> Unit = {},
    ): Set<EquivalentNetworkConnection> = addToEdgeBetweenContainers(
        container,
        otherContainer,
        network,
        defaultEquivalentBranchCreator(branchMrid, initBranch),
        defaultEquivalentEquipmentCreator(equipmentMrid, initEquipment),
        maxNumber
    )

    /**
     * Function to add an 'equivalent network' to the edge between an [EquipmentContainer] whose equipment is present in a [NetworkService] and all
     * neighbouring [EquipmentContainer]s of the 'otherContainerClass' class whose equipment is missing from the [NetworkService].
     *
     * This function wraps the 'addToEdgeBetweenContainers' function and provides a simpler signature that is more convenient to use in most cases.
     *
     * For more information read the documentation for the 'addToEdgeBetweenContainers' function in this class.
     *
     * @param container The [EquipmentContainer] that has its equipment in the [NetworkService] (it doesn't hold the edge [ConnectivityNode]).
     * @param otherContainerClass The edge [EquipmentContainer] class that doesn't have its equipment in the [NetworkService] (it holds the edge [ConnectivityNode]).
     * @param network the [NetworkService] that we need to add the [EquivalentBranch] and [ConductingEquipment] to.
     * @param maxNumber The maximum number of 'equivalent networks' you want to add. The default null value will create as many as possible.
     * @param branchMrid factory function to produce the mRID of the created [EquivalentBranch].
     * @param equipmentMrid factory function to produce the mRID of the created [ConductingEquipment].
     * @param initBranch callback function that allows the caller to initialise the [EquivalentBranch].
     * @param initEquipment callback function that allows the caller to initialise the [ConductingEquipment].
     *
     * @return a set of [EquivalentNetworkConnection]s that hold information and references to the edge [ConnectivityNode], edge [ConductingEquipment], and added [EquivalentBranch]es and their [ConductingEquipment].
     */
    inline fun <reified T : ConductingEquipment> addToEdgeBetween(
        container: EquipmentContainer,
        otherContainerClass: KClass<out EquipmentContainer>,
        network: NetworkService,
        maxNumber: Int? = null,
        crossinline branchMrid: (EquivalentBranchDetails) -> String = { (edgeEquipment) -> "${edgeEquipment.mRID}-eb" },
        crossinline equipmentMrid: (EquivalentEquipmentDetails) -> String = { (edgeEquipment) -> "${edgeEquipment.mRID}-eeq" },
        crossinline initBranch: EquivalentBranch.(EquivalentBranchDetails) -> Unit = {},
        crossinline initEquipment: T.(EquivalentEquipmentDetails) -> Unit = {},
    ): Set<EquivalentNetworkConnection> = addToEdgeBetweenContainers(
        container,
        otherContainerClass,
        network,
        defaultEquivalentBranchCreator(branchMrid, initBranch),
        defaultEquivalentEquipmentCreator(equipmentMrid, initEquipment),
        maxNumber
    )

    /**
     * Function to add an 'equivalent network' to the edge between all [EquipmentContainer]s of the 'containerClass' class whose equipment is present
     * in a [NetworkService] and all neighbouring [EquipmentContainer]s of the 'otherContainerClass' class whose equipment is missing from the [NetworkService].
     *
     * This function wraps the 'addToEdgeBetweenContainers' function and provides a simpler signature that is more convenient to use in most cases.
     *
     * For more information read the documentation for the 'addToEdgeBetweenContainers' function in this class.
     *
     * @param containerClass The [EquipmentContainer] class that has its equipment in the [NetworkService] (it doesn't hold the edge [ConnectivityNode]).
     * @param otherContainerClass The edge [EquipmentContainer] class that doesn't have its equipment in the [NetworkService] (it holds the edge [ConnectivityNode]).
     * @param network the [NetworkService] that we need to add the [EquivalentBranch] and [ConductingEquipment] to.
     * @param maxNumber The maximum number of 'equivalent networks' you want to add. The default null value will create as many as possible.
     * @param branchMrid factory function to produce the mRID of the created [EquivalentBranch].
     * @param equipmentMrid factory function to produce the mRID of the created [ConductingEquipment].
     * @param initBranch callback function that allows the caller to initialise the [EquivalentBranch].
     * @param initEquipment callback function that allows the caller to initialise the [ConductingEquipment].
     *
     * @return a set of [EquivalentNetworkConnection]s that hold information and references to the edge [ConnectivityNode], edge [ConductingEquipment], and added [EquivalentBranch]es and their [ConductingEquipment].
     */
    inline fun <reified T : ConductingEquipment> addToEdgeBetween(
        containerClass: KClass<out EquipmentContainer>,
        otherContainerClass: KClass<out EquipmentContainer>,
        network: NetworkService,
        maxNumber: Int? = null,
        crossinline branchMrid: (EquivalentBranchDetails) -> String = { (edgeEquipment) -> "${edgeEquipment.mRID}-eb" },
        crossinline equipmentMrid: (EquivalentEquipmentDetails) -> String = { (edgeEquipment) -> "${edgeEquipment.mRID}-eeq" },
        crossinline initBranch: EquivalentBranch.(EquivalentBranchDetails) -> Unit = {},
        crossinline initEquipment: T.(EquivalentEquipmentDetails) -> Unit = {},
    ): Set<EquivalentNetworkConnection> = addToEdgeBetweenContainers(
        containerClass,
        otherContainerClass,
        network,
        defaultEquivalentBranchCreator(branchMrid, initBranch),
        defaultEquivalentEquipmentCreator(equipmentMrid, initEquipment),
        maxNumber
    )

    /**
     * A default EquivalentBranch creator that should cover most use cases. If you need to override this you should call
     * addToEdgeBetweenContainers directly.
     */
    inline fun defaultEquivalentBranchCreator(
        crossinline branchMrid: (EquivalentBranchDetails) -> String = { (edgeEquipment) -> "${edgeEquipment.mRID}-eb" },
        crossinline initBranch: EquivalentBranch.(EquivalentBranchDetails) -> Unit = {},
    ): (EquivalentBranchDetails) -> Sequence<Pair<PhaseCode, EquivalentBranch>> =
        { (edgeEquipment, edgeNode) ->
            sequenceOf(
                getPhaseCode(edgeNode) to EquivalentBranch(branchMrid(edgeEquipment via edgeNode))
                    .apply {
                        location = edgeEquipment.location
                        baseVoltage = getEquivalentBaseVoltage(edgeEquipment, edgeNode)
                        initBranch(edgeEquipment via edgeNode)
                    }
            )
        }

    /**
     * A default EquivalentEquipment creator that should cover most use cases. If you need to override this you should call
     * addToEdgeBetweenContainers directly.
     */
    inline fun <reified T : ConductingEquipment> defaultEquivalentEquipmentCreator(
        crossinline equipmentMrid: (EquivalentEquipmentDetails) -> String = { (edgeEquipment) -> "${edgeEquipment.mRID}-eeq" },
        crossinline initEquipment: T.(EquivalentEquipmentDetails) -> Unit = {},
    ): (EquivalentEquipmentDetails) -> Sequence<Pair<PhaseCode, ConductingEquipment>> =
        { (edgeEquipment, edgeNode, equivalentBranch) ->
            sequenceOf(
                getPhaseCode(edgeNode) to requireNotNull(
                    T::class.primaryConstructor
                        ?.call(equipmentMrid(edgeEquipment via edgeNode via equivalentBranch))
                        ?.apply {
                            location = edgeEquipment.location
                            baseVoltage = getEquivalentBaseVoltage(edgeEquipment, edgeNode)
                            initEquipment(edgeEquipment via edgeNode via equivalentBranch)
                        }
                )
            )
        }

    /**
     * Function to add 'equivalent networks' which are groups of [ConductingEquipment] connected through [EquivalentBranch]es that represent simplified networks.
     * These 'equivalent networks' are attached to edge [ConnectivityNode]s, which are [ConnectivityNode]s right at the edge between [EquipmentContainer]s.
     * These edge [ConnectivityNode] are identified by having a single [Terminal] that has a [ConductingEquipment] that belongs to both neighbouring [EquipmentContainer]s.
     *
     * This overload of the function takes two [EquipmentContainer] instances to identify the edge [ConnectivityNode]s between them.
     * If no edge [ConnectivityNode] is found nothing will be done and an empty result will be returned.
     *
     * @param container The [EquipmentContainer] that has its equipment in the [NetworkService] (it doesn't hold the edge [ConnectivityNode]).
     * @param otherContainer The edge [EquipmentContainer] that doesn't have its equipment in the [NetworkService] (it holds the edge [ConnectivityNode]).
     * @param network The [NetworkService] that we need to add the [EquivalentBranch] and [ConductingEquipment] to.
     * @param createEquivalentBranches factory function to create the [EquivalentBranch]es that will be attached to the edge [ConnectivityNode] to represent the 'equivalent network'.
     * @param createEquivalentEquipment factory function to create the [ConductingEquipment]s that will be attached to the [EquivalentBranch]es of the 'equivalent network'.
     * @param maxNumber The maximum number of 'equivalent networks' you want to add. The default null value will create as many as possible.
     *
     * @return a set of [EquivalentNetworkConnection]s that hold information and references to the edge [ConnectivityNode], edge [ConductingEquipment], and added [EquivalentBranch]es and their [ConductingEquipment].
     */
    fun addToEdgeBetweenContainers(
        container: EquipmentContainer,
        otherContainer: EquipmentContainer,
        network: NetworkService,
        createEquivalentBranches: (EquivalentBranchDetails) -> Sequence<Pair<PhaseCode, EquivalentBranch>>,
        createEquivalentEquipment: (EquivalentEquipmentDetails) -> Sequence<Pair<PhaseCode, ConductingEquipment>>,
        maxNumber: Int? = null
    ): Set<EquivalentNetworkConnection> {
        val edgeCnn = getEdgeNodes(network, setOf(container::class, otherContainer::class), container, otherContainer)
        val edgeCnToProcess = (maxNumber?.let { edgeCnn.take(it) } ?: edgeCnn)
        return edgeCnToProcess.map { cn -> addEquivalentNetwork(network, cn, createEquivalentBranches, createEquivalentEquipment) }.toSet()
    }

    /**
     * Function to add 'equivalent networks' which are groups of [ConductingEquipment] connected through [EquivalentBranch]es that represent simplified networks.
     * These 'equivalent networks' are attached to edge [ConnectivityNode]s, which are [ConnectivityNode]s right at the edge between [EquipmentContainer]s.
     * These edge [ConnectivityNode] are identified by having a single [Terminal] that has a [ConductingEquipment] that belongs to both neighbouring [EquipmentContainer]s.
     *
     * This overload of the function takes an [EquipmentContainer] instance and an [EquipmentContainer] class reference to identify the edge [ConnectivityNode]s between them.
     * Equivalent networks will be added on all edges between the 'container' [EquipmentContainer] and all [EquipmentContainer] that have a class of 'otherContainerClass'.
     * If no edge [ConnectivityNode] is found nothing will be done and an empty result will be returned.
     *
     * @param container The [EquipmentContainer] that has its equipment in the [NetworkService] (it doesn't hold the edge [ConnectivityNode]).
     * @param otherContainerClass The edge [EquipmentContainer] class that doesn't have its equipment in the [NetworkService] (it holds the edge [ConnectivityNode]).
     * @param network The [NetworkService] that we need to add the [EquivalentBranch] and [ConductingEquipment] to.
     * @param createEquivalentBranches factory function to create the [EquivalentBranch]es that will be attached to the edge [ConnectivityNode] to represent the 'equivalent network'.
     * @param createEquivalentEquipment factory function to create the [ConductingEquipment]s that will be attached to the [EquivalentBranch]es of the 'equivalent network'.
     * @param maxNumber The maximum number of 'equivalent networks' you want to add. The default null value will create as many as possible.
     *
     * @return a set of [EquivalentNetworkConnection]s that hold information and references to the edge [ConnectivityNode], edge [ConductingEquipment], and added [EquivalentBranch]es and their [ConductingEquipment].
     */
    fun addToEdgeBetweenContainers(
        container: EquipmentContainer,
        otherContainerClass: KClass<out EquipmentContainer>,
        network: NetworkService,
        createEquivalentBranches: (EquivalentBranchDetails) -> Sequence<Pair<PhaseCode, EquivalentBranch>>,
        createEquivalentEquipment: (EquivalentEquipmentDetails) -> Sequence<Pair<PhaseCode, ConductingEquipment>>,
        maxNumber: Int? = null
    ): Set<EquivalentNetworkConnection> {
        val edgeCnn = getEdgeNodes(network, setOf(container::class, otherContainerClass), container)
        val edgeCnToProcess = (maxNumber?.let { edgeCnn.take(it) } ?: edgeCnn)
        return edgeCnToProcess.map { cn -> addEquivalentNetwork(network, cn, createEquivalentBranches, createEquivalentEquipment) }.toSet()
    }

    /**
     * Function to add 'equivalent networks' which are groups of [ConductingEquipment] connected through [EquivalentBranch]es that represent simplified networks.
     * These 'equivalent networks' are attached to edge [ConnectivityNode]s, which are [ConnectivityNode]s right at the edge between [EquipmentContainer]s.
     * These edge [ConnectivityNode] are identified by having a single [Terminal] that has a [ConductingEquipment] that belongs to both neighbouring [EquipmentContainer]s.
     *
     * This overload of the function takes two [EquipmentContainer] class references to identify the edge [ConnectivityNode]s between them.
     * Equivalent networks will be added on all edges between all [EquipmentContainer]s that have a class of 'container' and all [EquipmentContainer]s
     * that have a class of 'otherContainerClass'.
     * If no edge [ConnectivityNode] is found nothing will be done and an empty result will be returned.
     *
     * @param containerClass The [EquipmentContainer] class that has its equipment in the [NetworkService] (it doesn't hold the edge [ConnectivityNode]).
     * @param otherContainerClass The edge [EquipmentContainer] class that doesn't have its equipment in the [NetworkService] (it holds the edge [ConnectivityNode]).
     * @param network The [NetworkService] that we need to add the [EquivalentBranch] and [ConductingEquipment] to.
     * @param createEquivalentBranches factory function to create the [EquivalentBranch]es that will be attached to the edge [ConnectivityNode] to represent the 'equivalent network'.
     * @param createEquivalentEquipment factory function to create the [ConductingEquipment]s that will be attached to the [EquivalentBranch]es of the 'equivalent network'.
     * @param maxNumber The maximum number of 'equivalent networks' you want to add. The default null value will create as many as possible.
     *
     * @return a set of [EquivalentNetworkConnection]s that hold information and references to the edge [ConnectivityNode], edge [ConductingEquipment], and added [EquivalentBranch]es and their [ConductingEquipment].
     */
    fun addToEdgeBetweenContainers(
        containerClass: KClass<out EquipmentContainer>,
        otherContainerClass: KClass<out EquipmentContainer>,
        network: NetworkService,
        createEquivalentBranches: (EquivalentBranchDetails) -> Sequence<Pair<PhaseCode, EquivalentBranch>>,
        createEquivalentEquipment: (EquivalentEquipmentDetails) -> Sequence<Pair<PhaseCode, ConductingEquipment>>,
        maxNumber: Int? = null
    ): Set<EquivalentNetworkConnection> {
        val edgeCnn = getEdgeNodes(network, setOf(containerClass, otherContainerClass))
        val edgeCnToProcess = (maxNumber?.let { edgeCnn.take(it) } ?: edgeCnn)
        return edgeCnToProcess.map { cn -> addEquivalentNetwork(network, cn, createEquivalentBranches, createEquivalentEquipment) }.toSet()
    }

    /**
     * Convenience function to get the first [PhaseCode] from a [ConnectivityNode]s [Terminal]s.
     * This is intended to work for [ConnectivityNode]s with only 1 [Terminal].
     */
    fun getPhaseCode(edgeNode: ConnectivityNode): PhaseCode = edgeNode.terminals.map { it.phases }.first()

    /**
     * Convenience function to get the [BaseVoltage] from an edge [ConductingEquipment].
     */
    fun getEquivalentBaseVoltage(ce: ConductingEquipment, edgeNode: ConnectivityNode): BaseVoltage? =
        when (ce) {
            is PowerTransformer -> ce.getBaseVoltage(edgeNode)
            else -> ce.baseVoltage
        }

    private fun addEquivalentNetwork(
        network: NetworkService,
        edgeNode: ConnectivityNode,
        createEquivalentBranches: (EquivalentBranchDetails) -> Sequence<Pair<PhaseCode, EquivalentBranch>>,
        createEquivalentEquipment: (EquivalentEquipmentDetails) -> Sequence<Pair<PhaseCode, ConductingEquipment>>
    ): EquivalentNetworkConnection {
        val edgeEquipment = edgeNode.terminals.first().conductingEquipment
            ?: throw IllegalStateException(
                "[Internal Error]: ${edgeNode.typeNameAndMRID()} for EquivalentBranch must have a ConductingEquipment on " +
                    "its terminal ${edgeNode.terminals.first().typeNameAndMRID()}"
            )
        //NOTE: This exception should never occur as an edgeNode by definition must have an associated ConductingEquipment with multiple equipment containers

        val containersForEquivalentNetwork = getContainersForEdgeConnectivityNode(edgeNode)
        val branchToEquipment = createEquivalentBranches(edgeEquipment via edgeNode).map { (phaseCode, equivalentBranch) ->
            equivalentBranch.apply {
                network.add(this)
                containersForEquivalentNetwork.forEach {
                    it.addEquipment(this)
                    addContainer(it)
                }

                Terminal("$mRID-t${numTerminals() + 1}").also {
                    network.add(it)
                    addTerminal(it)
                    it.conductingEquipment = this
                    it.phases = phaseCode
                    network.connect(it, edgeNode.mRID)
                }
            } to
                createEquivalentEquipment(edgeEquipment via edgeNode via equivalentBranch).map { (phaseCode, conEq) ->
                    conEq.apply {
                        network.tryAdd(this)
                        containersForEquivalentNetwork.forEach {
                            it.addEquipment(this)
                            this.addContainer(it)
                        }

                        val eqBranchTerminal = Terminal("${equivalentBranch.mRID}-t${equivalentBranch.numTerminals() + 1}").also {
                            network.add(it)
                            equivalentBranch.addTerminal(it)
                            it.conductingEquipment = equivalentBranch
                            it.phases = phaseCode
                        }
                        val conEqTerminal = Terminal("$mRID-t${numTerminals() + 1}").also {
                            network.add(it)
                            conEq.addTerminal(it)
                            it.conductingEquipment = this
                            it.phases = phaseCode
                        }
                        network.connect(eqBranchTerminal, conEqTerminal)
                    }
                }.toSet()
        }.toMap()

        return EquivalentNetworkConnection(edgeEquipment, edgeNode, branchToEquipment)
    }

    private fun getContainersForEdgeConnectivityNode(cn: ConnectivityNode): Set<EquipmentContainer> =
        (cn.terminals.first().conductingEquipment?.containers?.toSet() ?: emptySet()).minus(
            (cn.terminals as List).first().otherTerminals()
                .flatMap { ot -> ot.connectedTerminals().flatMap { it.conductingEquipment?.containers ?: emptyList() } }.filter { it !is Site }.toSet()
        )

    /**
     * An edge node is a [ConnectivityNode] with the following characteristics:
     * 1. It has a single [Terminal].
     * 2. The [Terminal]'s [ConductingEquipment] has multiple [EquipmentContainer]s which flags it as the edge between them.
     *
     * This function returns all edge nodes in a [NetworkService] filtered by the [EquipmentContainer]s in the 'containers', where their
     * associated [ConductingEquipment]'s containers collection has [EquipmentContainer]s of all the classes specified in 'edgeContainerClasses'.
     *
     */
    private fun getEdgeNodes(
        network: NetworkService,
        edgeContainerClasses: Set<KClass<out EquipmentContainer>>,
        vararg containers: EquipmentContainer
    ): Set<ConnectivityNode> =
        getConnectivityNodeSequence(network, containers.toSet())
            .filter { cn ->
                cn.terminals.size == 1 &&
                    cn.terminals.first().conductingEquipment?.containers
                        ?.let { containers ->
                            edgeContainerClasses
                                .map { i -> containers.any { i.isInstance(it) } }
                                .reduce { acc, i -> acc && i }
                        } ?: false
            }
            // NOTE: This second filter makes sure you don't match ConnectivityNodes associated to equipment that are connected to nothing.
            //     ConductingEquipment connected to nothing can be valid state in a service e.g. After resolving references for an EquipmentContainer's
            //     headTerminal, but you didn't request the equipment for the EquipmentContainer.
            .filter { cn ->
                cn.terminals.first().conductingEquipment?.terminals?.all { it.connectivityNode?.terminals?.size == 1 }?.let { !it } ?: false
            }
            .toSet()

    private fun getConnectivityNodeSequence(network: NetworkService, containers: Set<EquipmentContainer>): Sequence<ConnectivityNode> =
        if (containers.isEmpty())
            network.sequenceOf()
        else
            containers.flatMap { it.equipment }
                .asSequence()
                .filterIsInstance<ConductingEquipment>()
                .flatMap { eq -> eq.terminals.mapNotNull { it.connectivityNode } }

}
