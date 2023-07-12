/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.testing

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.Feeder
import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.*
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.LvFeeder
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.tracing.Tracing
import kotlin.reflect.full.primaryConstructor

/**
 * A class for building simple test networks, often used for unit testing.
 */
open class TestNetworkBuilder {

    private var currentTerminal: Int? = null

    /**
     * The network where objects are created for this [TestNetworkBuilder]. You should not be readily required to access the network via this property,
     * but should instead access it via [build] to ensure the correct tracing is applied before use.
     */
    val network: NetworkService = NetworkService()

    private var count = 0
    private var current: ConductingEquipment? = null

    /**
     * Start a new network island from an [EnergySource], updating the network pointer to the new [EnergySource].
     *
     * @param phases The [PhaseCode] for the new [EnergySource], used as both the nominal and energising phases. Must be a subset of [PhaseCode.ABCN].
     * @param mRID Optional mRID for the new [EnergySource].
     * @param action An action that accepts the new [EnergySource] to allow for additional initialisation.
     *
     * @return This [TestNetworkBuilder] to allow for fluent use.
     */
    @JvmOverloads
    fun fromSource(
        phases: PhaseCode = PhaseCode.ABC,
        mRID: String? = null,
        action: EnergySource.() -> Unit = {}
    ): TestNetworkBuilder {
        current = network.createExternalSource(mRID, phases).also(action)
        return this
    }

    /**
     * Add a new [EnergySource] to the network and connect it to the current network pointer, updating the network pointer to the new [EnergySource].
     *
     * @param phases The [PhaseCode] for the new [EnergySource], used as both the nominal and energising phases. Must be a subset of [PhaseCode.ABCN].
     * @param mRID Optional mRID for the new [EnergySource].
     * @param connectivityNodeMrid Optional id of the connectivity node used to connect this [EnergySource] to the previous item. Will only be used if the
     * previous item is not already connected.
     * @param action An action that accepts the new [EnergySource] to allow for additional initialisation.
     *
     * @return This [TestNetworkBuilder] to allow for fluent use.
     */
    @JvmOverloads
    fun toSource(
        phases: PhaseCode = PhaseCode.ABC,
        mRID: String? = null,
        connectivityNodeMrid: String? = null,
        action: EnergySource.() -> Unit = {}
    ): TestNetworkBuilder {
        current = network.createExternalSource(mRID, phases).also {
            connect(current!!, it, connectivityNodeMrid)
            action(it)
        }
        return this
    }

    /**
     * Start a new network island from an [AcLineSegment], updating the network pointer to the new [AcLineSegment].
     *
     * @param nominalPhases The nominal phases for the new [AcLineSegment].
     * @param mRID Optional mRID for the new [AcLineSegment].
     * @param action An action that accepts the new [AcLineSegment] to allow for additional initialisation.
     *
     * @return This [TestNetworkBuilder] to allow for fluent use.
     */
    @JvmOverloads
    fun fromAcls(
        nominalPhases: PhaseCode = PhaseCode.ABC,
        mRID: String? = null,
        action: AcLineSegment.() -> Unit = {}
    ): TestNetworkBuilder {
        current = network.createAcls(mRID, nominalPhases).also(action)
        return this
    }

    /**
     * Add a new [AcLineSegment] to the network and connect it to the current network pointer, updating the network pointer to the new [AcLineSegment].
     *
     * @param nominalPhases The nominal phases for the new [AcLineSegment].
     * @param mRID Optional mRID for the new [AcLineSegment].
     * @param connectivityNodeMrid Optional id of the connectivity node used to connect this [AcLineSegment] to the previous item. Will only be used if the
     * previous item is not already connected.
     * @param action An action that accepts the new [AcLineSegment] to allow for additional initialisation.
     *
     * @return This [TestNetworkBuilder] to allow for fluent use.
     */
    @JvmOverloads
    fun toAcls(
        nominalPhases: PhaseCode = PhaseCode.ABC,
        mRID: String? = null,
        connectivityNodeMrid: String? = null,
        action: AcLineSegment.() -> Unit = {}
    ): TestNetworkBuilder {
        current = network.createAcls(mRID, nominalPhases).also {
            connect(current!!, it, connectivityNodeMrid)
            action(it)
        }
        return this
    }

    /**
     * Start a new network island from a [Breaker], updating the network pointer to the new [Breaker].
     *
     * @param nominalPhases The nominal phases for the new [Breaker].
     * @param isNormallyOpen The normal state of the switch. Defaults to false.
     * @param isOpen The current state of the switch. Defaults to [isNormallyOpen].
     * @param mRID Optional mRID for the new [Breaker].
     * @param action An action that accepts the new [Breaker] to allow for additional initialisation.
     *
     * @return This [TestNetworkBuilder] to allow for fluent use.
     */
    @JvmOverloads
    fun fromBreaker(
        nominalPhases: PhaseCode = PhaseCode.ABC,
        isNormallyOpen: Boolean = false,
        isOpen: Boolean? = null,
        mRID: String? = null,
        action: Breaker.() -> Unit = {}
    ): TestNetworkBuilder {
        current = network.createBreaker(mRID, nominalPhases, isNormallyOpen = isNormallyOpen, isOpen = isOpen ?: isNormallyOpen).also(action)
        return this
    }

    /**
     * Add a new [Breaker] to the network and connect it to the current network pointer, updating the network pointer to the new [Breaker].
     *
     * @param nominalPhases The nominal phases for the new [Breaker].
     * @param isNormallyOpen The normal state of the switch. Defaults to false.
     * @param isOpen The current state of the switch. Defaults to [isNormallyOpen].
     * @param mRID Optional mRID for the new [Breaker].
     * @param connectivityNodeMrid Optional id of the connectivity node used to connect this [Breaker] to the previous item. Will only be used if the previous
     * item is not already connected.
     * @param action An action that accepts the new [Breaker] to allow for additional initialisation.
     *
     * @return This [TestNetworkBuilder] to allow for fluent use.
     */
    @JvmOverloads
    fun toBreaker(
        nominalPhases: PhaseCode = PhaseCode.ABC,
        isNormallyOpen: Boolean = false,
        isOpen: Boolean? = null,
        mRID: String? = null,
        connectivityNodeMrid: String? = null,
        action: Breaker.() -> Unit = {}
    ): TestNetworkBuilder {
        current = network.createBreaker(mRID, nominalPhases, isNormallyOpen = isNormallyOpen, isOpen = isOpen ?: isNormallyOpen).also {
            connect(current!!, it, connectivityNodeMrid)
            action(it)
        }
        return this
    }

    /**
     * Start a new network island from a [Junction], updating the network pointer to the new [Junction].
     *
     * @param nominalPhases The nominal phases for the new [Junction].
     * @param numTerminals The number of terminals to create on the new [Junction]. Defaults to 2.
     * @param mRID Optional mRID for the new [Junction].
     * @param action An action that accepts the new [Junction] to allow for additional initialisation.
     *
     * @return This [TestNetworkBuilder] to allow for fluent use.
     */
    @JvmOverloads
    fun fromJunction(
        nominalPhases: PhaseCode = PhaseCode.ABC,
        numTerminals: Int? = null,
        mRID: String? = null,
        action: Junction.() -> Unit = {}
    ): TestNetworkBuilder {
        current = network.createJunction(mRID, nominalPhases, numTerminals).also(action)
        return this
    }

    /**
     * Add a new [Junction] to the network and connect it to the current network pointer, updating the network pointer to the new [Junction].
     *
     * @param nominalPhases The nominal phases for the new [Junction].
     * @param numTerminals The number of terminals to create on the new [Junction]. Defaults to 2.
     * @param mRID Optional mRID for the new [Junction].
     * @param connectivityNodeMrid Optional id of the connectivity node used to connect this [Junction] to the previous item. Will only be used if the previous
     * item is not already connected.
     * @param action An action that accepts the new [Junction] to allow for additional initialisation.
     *
     * @return This [TestNetworkBuilder] to allow for fluent use.
     */
    @JvmOverloads
    fun toJunction(
        nominalPhases: PhaseCode = PhaseCode.ABC,
        numTerminals: Int? = null,
        mRID: String? = null,
        connectivityNodeMrid: String? = null,
        action: Junction.() -> Unit = {}
    ): TestNetworkBuilder {
        current = network.createJunction(mRID, nominalPhases, numTerminals).also {
            connect(current!!, it, connectivityNodeMrid)
            action(it)
        }
        return this
    }

    /**
     * Add a new [PowerElectronicsConnection] to the network and connect it to the current network pointer, updating the network pointer to the new
     * [PowerElectronicsConnection].
     *
     * @param nominalPhases The nominal phases for the new [PowerElectronicsConnection].
     * @param numTerminals The number of terminals to create on the new [PowerElectronicsConnection]. Defaults to 2.
     * @param mRID Optional mRID for the new [PowerElectronicsConnection].
     * @param connectivityNodeMrid Optional id of the connectivity node used to connect this [PowerElectronicsConnection] to the previous item. Will only be
     * used if the previous item is not already connected.
     * @param action An action that accepts the new [PowerElectronicsConnection] to allow for additional initialisation.
     *
     * @return This [TestNetworkBuilder] to allow for fluent use.
     */
    @JvmOverloads
    fun toPowerElectronicsConnection(
        nominalPhases: PhaseCode = PhaseCode.ABC,
        numTerminals: Int = 2,
        mRID: String? = null,
        connectivityNodeMrid: String? = null,
        action: PowerElectronicsConnection.() -> Unit = {}
    ): TestNetworkBuilder {
        current = network.createPowerElectronicsConnection(mRID, nominalPhases, numTerminals).also {
            connect(current!!, it, connectivityNodeMrid)
            action(it)
        }
        return this
    }

    /**
     * Start a new network island from a [PowerTransformer], updating the network pointer to the new [PowerTransformer].
     *
     * @param nominalPhases The nominal phases for each end of the new [PowerTransformer]. Defaults to two [PhaseCode.ABC] ends.
     * @param endActions Actions that accepts the new [PowerTransformerEnd] to allow for additional initialisation.
     * @param mRID Optional mRID for the new [PowerTransformer].
     * @param action An action that accepts the new [PowerTransformer] to allow for additional initialisation.
     *
     * @return This [TestNetworkBuilder] to allow for fluent use.
     */
    @JvmOverloads
    fun fromPowerTransformer(
        nominalPhases: List<PhaseCode> = listOf(PhaseCode.ABC, PhaseCode.ABC),
        endActions: List<PowerTransformerEnd.() -> Unit>? = null,
        mRID: String? = null,
        action: PowerTransformer.() -> Unit = {}
    ): TestNetworkBuilder {
        current = network.createPowerTransformer(mRID, nominalPhases).also {
            endActions?.forEachIndexed { index, endAction ->
                endAction(it.ends[index])
            }
            action(it)
        }
        return this
    }

    /**
     * Add a new [PowerTransformer] to the network and connect it to the current network pointer, updating the network pointer to the new [PowerTransformer].
     *
     * @param nominalPhases The nominal phases for each end of the new [PowerTransformer]. Defaults to two [PhaseCode.ABC] ends.
     * @param endActions Actions that accepts the new [PowerTransformerEnd] to allow for additional initialisation.
     * @param mRID Optional mRID for the new [PowerTransformer].
     * @param connectivityNodeMrid Optional id of the connectivity node used to connect this [PowerTransformer] to the previous item. Will only be used if the
     * previous item is not already connected.
     * @param action An action that accepts the new [PowerTransformer] to allow for additional initialisation.
     *
     * @return This [TestNetworkBuilder] to allow for fluent use.
     */
    @JvmOverloads
    fun toPowerTransformer(
        nominalPhases: List<PhaseCode> = listOf(PhaseCode.ABC, PhaseCode.ABC),
        endActions: List<PowerTransformerEnd.() -> Unit>? = null,
        mRID: String? = null,
        connectivityNodeMrid: String? = null,
        action: PowerTransformer.() -> Unit = {}
    ): TestNetworkBuilder {
        current = network.createPowerTransformer(mRID, nominalPhases).also {
            connect(current!!, it, connectivityNodeMrid)
            endActions?.forEachIndexed { index, endAction ->
                endAction(it.ends[index])
            }
            action(it)
        }
        return this
    }

    /**
     * Add a new [EnergyConsumer] to the network and connect it to the current network pointer, updating the network pointer to the new
     * [EnergyConsumer].
     *
     * @param nominalPhases The nominal phases for the new [EnergyConsumer].
     * @param mRID Optional mRID for the new [EnergyConsumer].
     * @param connectivityNodeMrid Optional id of the connectivity node used to connect this [EnergyConsumer] to the previous item. Will only be used if the
     * previous item is not already connected.
     * @param action An action that accepts the new [EnergyConsumer] to allow for additional initialisation.
     *
     * @return This [TestNetworkBuilder] to allow for fluent use.
     */
    @JvmOverloads
    fun toEnergyConsumer(
        nominalPhases: PhaseCode = PhaseCode.ABC,
        mRID: String? = null,
        connectivityNodeMrid: String? = null,
        action: EnergyConsumer.() -> Unit = {}
    ): TestNetworkBuilder {
        current = network.createEnergyConsumer(mRID, nominalPhases).also {
            connect(current!!, it, connectivityNodeMrid)
            action(it)
        }
        return this
    }

    /**
     * Start a new network island from a [ConductingEquipment], updating the network pointer to the new [ConductingEquipment].
     *
     * @param creator Creator of the new [ConductingEquipment].
     * @param nominalPhases The nominal phases for the new [ConductingEquipment].
     * @param numTerminals The number of terminals to create on the new [ConductingEquipment]. Defaults to 2.
     * @param mRID Optional mRID for the new [ConductingEquipment].
     * @param action An action that accepts the new [ConductingEquipment] to allow for additional initialisation.
     *
     * @return This [TestNetworkBuilder] to allow for fluent use.
     */
    @JvmOverloads
    fun fromOther(
        creator: (String) -> ConductingEquipment,
        nominalPhases: PhaseCode = PhaseCode.ABC,
        numTerminals: Int? = null,
        mRID: String? = null,
        action: ConductingEquipment.() -> Unit = {}
    ): TestNetworkBuilder {
        current = network.createOther(mRID, creator, nominalPhases, numTerminals).also(action)
        return this
    }

    /**
     * Start a new network island from a [ConductingEquipment], updating the network pointer to the new [ConductingEquipment].
     *
     * @param T The type of object to create.
     * @param nominalPhases The nominal phases for the new [ConductingEquipment].
     * @param numTerminals The number of terminals to create on the new [ConductingEquipment]. Defaults to 2.
     * @param mRID Optional mRID for the new [ConductingEquipment].
     * @param action An action that accepts the new [ConductingEquipment] to allow for additional initialisation.
     *
     * @return This [TestNetworkBuilder] to allow for fluent use.
     */
    inline fun <reified T : ConductingEquipment> fromOther(
        nominalPhases: PhaseCode = PhaseCode.ABC,
        numTerminals: Int? = null,
        mRID: String? = null,
        noinline action: ConductingEquipment.() -> Unit = {}
    ): TestNetworkBuilder =
        fromOther({ T::class.primaryConstructor!!.call(it) }, nominalPhases, numTerminals, mRID, action)

    /**
     * Add a new [ConductingEquipment] to the network and connect it to the current network pointer, updating the network pointer to the new [ConductingEquipment].
     *
     * @param creator Creator of the new [ConductingEquipment].
     * @param nominalPhases The nominal phases for the new [ConductingEquipment].
     * @param numTerminals The number of terminals to create on the new [ConductingEquipment]. Defaults to 2.
     * @param mRID Optional mRID for the new [ConductingEquipment].
     * @param connectivityNodeMrid Optional id of the connectivity node used to connect this [ConductingEquipment] to the previous item. Will only be used if
     * the previous item is not already connected.
     * @param action An action that accepts the new [ConductingEquipment] to allow for additional initialisation.
     *
     * @return This [TestNetworkBuilder] to allow for fluent use.
     */
    @JvmOverloads
    fun toOther(
        creator: (String) -> ConductingEquipment,
        nominalPhases: PhaseCode = PhaseCode.ABC,
        numTerminals: Int? = null,
        mRID: String? = null,
        connectivityNodeMrid: String? = null,
        action: ConductingEquipment.() -> Unit = {}
    ): TestNetworkBuilder {
        current = network.createOther(mRID, creator, nominalPhases, numTerminals).also {
            connect(current!!, it, connectivityNodeMrid)
            action(it)
        }
        return this
    }

    /**
     * Add a new [ConductingEquipment] to the network and connect it to the current network pointer, updating the network pointer to the new [ConductingEquipment].
     *
     * @param T The type of object to create.
     * @param nominalPhases The nominal phases for the new [ConductingEquipment].
     * @param numTerminals The number of terminals to create on the new [ConductingEquipment]. Defaults to 2.
     * @param mRID Optional mRID for the new [ConductingEquipment].
     * @param connectivityNodeMrid Optional id of the connectivity node used to connect this [ConductingEquipment] to the previous item. Will only be used if
     * the previous item is not already connected.
     * @param action An action that accepts the new [ConductingEquipment] to allow for additional initialisation.
     *
     * @return This [TestNetworkBuilder] to allow for fluent use.
     */
    inline fun <reified T : ConductingEquipment> toOther(
        nominalPhases: PhaseCode = PhaseCode.ABC,
        numTerminals: Int? = null,
        mRID: String? = null,
        connectivityNodeMrid: String? = null,
        noinline action: ConductingEquipment.() -> Unit = {}
    ): TestNetworkBuilder =
        toOther({ T::class.primaryConstructor!!.call(it) }, nominalPhases, numTerminals, mRID, connectivityNodeMrid, action)

    /**
     * Move the current network pointer to the specified [from] allowing branching of the network. This has the effect of changing the current network pointer.
     *
     * @param from The mRID of the [ConductingEquipment] to branch from.
     * @param terminal Optional sequence number of the terminal on [from] which will be connected.
     *
     * @return This [TestNetworkBuilder] to allow for fluent use.
     */
    fun branchFrom(from: String, terminal: Int? = null): TestNetworkBuilder {
        current = network[from]!!
        currentTerminal = terminal
        return this
    }

    /**
     * Connect the specified [from] and [to] without moving the current network pointer.
     *
     * @param from The mRID of the first [ConductingEquipment] to be connected.
     * @param to The mRID of the second [ConductingEquipment] to be connected.
     * @param fromTerminal The sequence number of the terminal on [from] which will be connected.
     * @param toTerminal The sequence number of the terminal on [to] which will be connected.
     * @param connectivityNodeMrid Optional id of the connectivity node used to connect the terminals. Will only be used if both terminals are not already
     * connected.
     *
     * @return This [TestNetworkBuilder] to allow for fluent use.
     */
    fun connect(
        from: String,
        to: String,
        fromTerminal: Int,
        toTerminal: Int,
        connectivityNodeMrid: String? = null
    ): TestNetworkBuilder {
        connect(network[from]!!, network[to]!!, connectivityNodeMrid, fromTerminal, toTerminal)
        return this
    }

    /**
     * Create a new HV/MV feeder with the specified terminal as the head terminal.
     *
     * @param headMrid The mRID of the head [ConductingEquipment].
     * @param sequenceNumber The [Terminal] sequence number of the head terminal. Defaults to last terminal.
     * @param mRID Optional mRID for the new [Feeder].
     *
     * @return This [TestNetworkBuilder] to allow for fluent use.
     */
    @JvmOverloads
    fun addFeeder(headMrid: String, sequenceNumber: Int? = null, mRID: String? = null): TestNetworkBuilder {
        network.createFeeder(mRID, network[headMrid]!!, sequenceNumber)
        return this
    }

    /**
     * Create a new LV feeder with the specified terminal as the head terminal.
     *
     * @param headMrid The mRID of the head [ConductingEquipment].
     * @param sequenceNumber The [Terminal] sequence number of the head terminal. Defaults to last terminal.
     * @param mRID Optional mRID for the new [LvFeeder].
     *
     * @return This [TestNetworkBuilder] to allow for fluent use.
     */
    fun addLvFeeder(headMrid: String, sequenceNumber: Int? = null, mRID: String? = null): TestNetworkBuilder {
        network.createLvFeeder(mRID, network[headMrid]!!, sequenceNumber)
        return this
    }

    /**
     * Get the [NetworkService] after apply traced phasing, feeder directions, and HV/LV feeder assignment.
     *
     * Does not infer phasing.
     *
     * @param applyDirectionsFromSources Indicates if directions should be applied from sources in addition to feeders. This is because test networks typically
     * are minimal, and having a source is a common start point.
     *
     * @return The [NetworkService] created by this [TestNetworkBuilder]
     */
    fun build(applyDirectionsFromSources: Boolean = true): NetworkService {
        Tracing.setDirection().run(network)
        Tracing.setPhases().run(network)

        if (applyDirectionsFromSources)
            network.sequenceOf<EnergySource>().flatMap { it.terminals }.forEach { Tracing.setDirection().run(it) }

        Tracing.assignEquipmentToFeeders().run(network)
        Tracing.assignEquipmentToLvFeeders().run(network)

        return network
    }

    private fun String?.orNextId(type: String): String = this ?: "$type${count++}"

    private fun connect(
        from: ConductingEquipment,
        to: ConductingEquipment,
        connectivityNodeMrid: String? = null,
        fromTerminal: Int? = null,
        toTerminal: Int? = null
    ) {
        val fromTerm = from.getTerminal(fromTerminal ?: currentTerminal ?: from.numTerminals())!!
        val toTerm = to.getTerminal(toTerminal ?: 1)!!
        if ((connectivityNodeMrid == null) || fromTerm.isConnected || toTerm.isConnected)
            network.connect(fromTerm, toTerm)
        else {
            network.connect(fromTerm, connectivityNodeMrid)
            network.connect(toTerm, connectivityNodeMrid)
        }
        currentTerminal = null
    }

    private fun NetworkService.createExternalSource(mRID: String?, phaseCode: PhaseCode) =
        mRID.orNextId("s").let { id ->
            if (phaseCode.singlePhases.any { it !in PhaseCode.ABCN })
                throw IllegalArgumentException("EnergySource phases must be a subset of ABCN")

            EnergySource(id).apply {
                isExternalGrid = true
                addTerminal(Terminal("$id-t1").apply { phases = phaseCode }.also { add(it) })
            }.also { add(it) }
        }

    private fun NetworkService.createAcls(mRID: String?, phaseCode: PhaseCode) =
        mRID.orNextId("c").let { id ->
            AcLineSegment(id).apply {
                addTerminal(Terminal("$id-t1").apply { phases = phaseCode }.also { add(it) })
                addTerminal(Terminal("$id-t2").apply { phases = phaseCode }.also { add(it) })
            }.also { add(it) }
        }

    private fun NetworkService.createBreaker(mRID: String?, phaseCode: PhaseCode = PhaseCode.ABC, isNormallyOpen: Boolean, isOpen: Boolean) =
        mRID.orNextId("b").let { id ->
            Breaker(id).apply {
                setNormallyOpen(isNormallyOpen)
                setOpen(isOpen)

                addTerminal(Terminal("$id-t1").apply { phases = phaseCode }.also { add(it) })
                addTerminal(Terminal("$id-t2").apply { phases = phaseCode }.also { add(it) })
            }.also { add(it) }
        }

    private fun NetworkService.createJunction(mRID: String?, phaseCode: PhaseCode = PhaseCode.ABC, numTerminals: Int?) =
        mRID.orNextId("j").let { id ->
            Junction(id).apply {
                for (i in 1..(numTerminals ?: 2))
                    addTerminal(Terminal("$id-t$i").apply { phases = phaseCode }.also { add(it) })
            }.also { add(it) }
        }

    private fun NetworkService.createPowerElectronicsConnection(mRID: String?, phaseCode: PhaseCode = PhaseCode.ABC, numTerminals: Int?) =
        mRID.orNextId("pec").let { id ->
            PowerElectronicsConnection(id).apply {
                for (i in 1..(numTerminals ?: 2))
                    addTerminal(Terminal("$id-t$i").apply { phases = phaseCode }.also { add(it) })
            }.also { add(it) }
        }

    private fun NetworkService.createPowerTransformer(mRID: String?, nominalPhases: List<PhaseCode>) =
        mRID.orNextId("tx").let { id ->
            PowerTransformer(id).apply {
                nominalPhases.forEachIndexed { i, phaseCode ->
                    val t = Terminal("$id-t${i + 1}").apply { phases = phaseCode }.also { add(it) }

                    addTerminal(t)
                    addEnd(PowerTransformerEnd("$id-e${i + 1}").apply { terminal = t }.also { add(it) })
                }
            }.also { add(it) }
        }

    private fun NetworkService.createEnergyConsumer(mRID: String?, phaseCode: PhaseCode = PhaseCode.ABC) =
        mRID.orNextId("ec").let { id ->
            EnergyConsumer(id).apply {
                addTerminal(Terminal("$id-t1").apply { phases = phaseCode }.also { add(it) })
            }.also { add(it) }
        }

    private fun NetworkService.createOther(
        mRID: String?,
        creator: (String) -> ConductingEquipment,
        phaseCode: PhaseCode = PhaseCode.ABC,
        numTerminals: Int?
    ) =
        mRID.orNextId("o").let { id ->
            creator(id).apply {
                for (i in 1..(numTerminals ?: 2))
                    addTerminal(Terminal("${this.mRID}-t$i").apply { phases = phaseCode }.also { add(it) })
            }.also { tryAdd(it) }
        }

    private fun NetworkService.createFeeder(mRID: String?, headEquipment: ConductingEquipment, sequenceNumber: Int?) =
        mRID.orNextId("fdr").let { id ->
            Feeder(id).apply {
                normalHeadTerminal = headEquipment.getTerminal(sequenceNumber ?: headEquipment.numTerminals())!!

                addEquipment(headEquipment)
            }.also {
                headEquipment.addContainer(it)
                add(it)
            }
        }

    private fun NetworkService.createLvFeeder(mRID: String?, headEquipment: ConductingEquipment, sequenceNumber: Int?) =
        mRID.orNextId("lvf").let { id ->
            LvFeeder(id).apply {
                normalHeadTerminal = headEquipment.getTerminal(sequenceNumber ?: headEquipment.numTerminals())!!

                addEquipment(headEquipment)
            }.also {
                headEquipment.addContainer(it)
                add(it)
            }
        }

}
