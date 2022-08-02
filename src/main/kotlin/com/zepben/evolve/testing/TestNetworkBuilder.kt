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
import org.junit.jupiter.api.Test

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
     * @param action An action that accepts the new [EnergySource] to allow for additional initialisation.
     *
     * @return This [TestNetworkBuilder] to allow for fluent use.
     */
    @JvmOverloads
    fun fromSource(phases: PhaseCode = PhaseCode.ABC, action: EnergySource.() -> Unit = {}): TestNetworkBuilder {
        current = network.createExternalSource(phases).also(action)
        return this
    }

    /**
     * Add a new [EnergySource] to the network and connect it to the current network pointer, updating the network pointer to the new [EnergySource].
     *
     * @param phases The [PhaseCode] for the new [EnergySource], used as both the nominal and energising phases. Must be a subset of [PhaseCode.ABCN].
     * @param action An action that accepts the new [EnergySource] to allow for additional initialisation.
     *
     * @return This [TestNetworkBuilder] to allow for fluent use.
     */
    @JvmOverloads
    fun toSource(phases: PhaseCode = PhaseCode.ABC, action: EnergySource.() -> Unit = {}): TestNetworkBuilder {
        current = network.createExternalSource(phases).also {
            connect(current!!, it)
            action(it)
        }
        return this
    }

    /**
     * Start a new network island from an [AcLineSegment], updating the network pointer to the new [AcLineSegment].
     *
     * @param nominalPhases The nominal phases for the new [AcLineSegment].
     * @param action An action that accepts the new [AcLineSegment] to allow for additional initialisation.
     *
     * @return This [TestNetworkBuilder] to allow for fluent use.
     */
    @JvmOverloads
    fun fromAcls(nominalPhases: PhaseCode = PhaseCode.ABC, action: AcLineSegment.() -> Unit = {}): TestNetworkBuilder {
        current = network.createAcls(nominalPhases).also(action)
        return this
    }

    /**
     * Add a new [AcLineSegment] to the network and connect it to the current network pointer, updating the network pointer to the new [AcLineSegment].
     *
     * @param nominalPhases The nominal phases for the new [AcLineSegment].
     * @param action An action that accepts the new [AcLineSegment] to allow for additional initialisation.
     *
     * @return This [TestNetworkBuilder] to allow for fluent use.
     */
    @JvmOverloads
    fun toAcls(nominalPhases: PhaseCode = PhaseCode.ABC, action: AcLineSegment.() -> Unit = {}): TestNetworkBuilder {
        current = network.createAcls(nominalPhases).also {
            connect(current!!, it)
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
     * @param action An action that accepts the new [Breaker] to allow for additional initialisation.
     *
     * @return This [TestNetworkBuilder] to allow for fluent use.
     */
    @JvmOverloads
    fun fromBreaker(
        nominalPhases: PhaseCode = PhaseCode.ABC,
        isNormallyOpen: Boolean = false,
        isOpen: Boolean? = null,
        action: Breaker.() -> Unit = {}
    ): TestNetworkBuilder {
        current = network.createBreaker(nominalPhases, isNormallyOpen = isNormallyOpen, isOpen = isOpen ?: isNormallyOpen).also(action)
        return this
    }

    /**
     * Add a new [Breaker] to the network and connect it to the current network pointer, updating the network pointer to the new [Breaker].
     *
     * @param nominalPhases The nominal phases for the new [Breaker].
     * @param isNormallyOpen The normal state of the switch. Defaults to false.
     * @param isOpen The current state of the switch. Defaults to [isNormallyOpen].
     * @param action An action that accepts the new [Breaker] to allow for additional initialisation.
     *
     * @return This [TestNetworkBuilder] to allow for fluent use.
     */
    @JvmOverloads
    fun toBreaker(
        nominalPhases: PhaseCode = PhaseCode.ABC,
        isNormallyOpen: Boolean = false,
        isOpen: Boolean? = null,
        action: Breaker.() -> Unit = {}
    ): TestNetworkBuilder {
        current = network.createBreaker(nominalPhases, isNormallyOpen = isNormallyOpen, isOpen = isOpen ?: isNormallyOpen).also {
            connect(current!!, it)
            action(it)
        }
        return this
    }

    /**
     * Start a new network island from a [Junction], updating the network pointer to the new [Junction].
     *
     * @param nominalPhases The nominal phases for the new [Junction].
     * @param numTerminals The number of terminals to create on the new [Junction]. Defaults to 2.
     * @param action An action that accepts the new [Junction] to allow for additional initialisation.
     *
     * @return This [TestNetworkBuilder] to allow for fluent use.
     */
    @JvmOverloads
    fun fromJunction(nominalPhases: PhaseCode = PhaseCode.ABC, numTerminals: Int? = null, action: Junction.() -> Unit = {}): TestNetworkBuilder {
        current = network.createJunction(nominalPhases, numTerminals).also(action)
        return this
    }

    /**
     * Add a new [Junction] to the network and connect it to the current network pointer, updating the network pointer to the new [Junction].
     *
     * @param nominalPhases The nominal phases for the new [Junction].
     * @param numTerminals The number of terminals to create on the new [Junction]. Defaults to 2.
     * @param action An action that accepts the new [Junction] to allow for additional initialisation.
     *
     * @return This [TestNetworkBuilder] to allow for fluent use.
     */
    @JvmOverloads
    fun toJunction(nominalPhases: PhaseCode = PhaseCode.ABC, numTerminals: Int? = null, action: Junction.() -> Unit = {}): TestNetworkBuilder {
        current = network.createJunction(nominalPhases, numTerminals).also {
            connect(current!!, it)
            action(it)
        }
        return this
    }

    /**
     * Start a new network island from a [PowerTransformer], updating the network pointer to the new [PowerTransformer].
     *
     * @param nominalPhases The nominal phases for each end of the new [PowerTransformer]. Defaults to two [PhaseCode.ABC] ends.
     * @param endActions Actions that accepts the new [PowerTransformerEnd] to allow for additional initialisation.
     * @param action An action that accepts the new [PowerTransformer] to allow for additional initialisation.
     *
     * @return This [TestNetworkBuilder] to allow for fluent use.
     */
    @JvmOverloads
    fun fromPowerTransformer(
        nominalPhases: List<PhaseCode> = listOf(PhaseCode.ABC, PhaseCode.ABC),
        endActions: List<PowerTransformerEnd.() -> Unit>? = null,
        action: PowerTransformer.() -> Unit = {}
    ): TestNetworkBuilder {
        current = network.createPowerTransformer(nominalPhases).also {
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
     * @param action An action that accepts the new [PowerTransformer] to allow for additional initialisation.
     *
     * @return This [TestNetworkBuilder] to allow for fluent use.
     */
    @JvmOverloads
    fun toPowerTransformer(
        nominalPhases: List<PhaseCode> = listOf(PhaseCode.ABC, PhaseCode.ABC),
        endActions: List<PowerTransformerEnd.() -> Unit>? = null,
        action: PowerTransformer.() -> Unit = {}
    ): TestNetworkBuilder {
        current = network.createPowerTransformer(nominalPhases).also {
            connect(current!!, it)
            endActions?.forEachIndexed { index, endAction ->
                endAction(it.ends[index])
            }
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
     * @param action An action that accepts the new [ConductingEquipment] to allow for additional initialisation.
     *
     * @return This [TestNetworkBuilder] to allow for fluent use.
     */
    @JvmOverloads
    fun fromOther(
        creator: (String) -> ConductingEquipment,
        nominalPhases: PhaseCode = PhaseCode.ABC,
        numTerminals: Int? = null,
        action: ConductingEquipment.() -> Unit = {}
    ): TestNetworkBuilder {
        current = network.createOther(creator, nominalPhases, numTerminals).also(action)
        return this
    }

    /**
     * Add a new [ConductingEquipment] to the network and connect it to the current network pointer, updating the network pointer to the new [ConductingEquipment].
     *
     * @param creator Creator of the new [ConductingEquipment].
     * @param nominalPhases The nominal phases for the new [ConductingEquipment].
     * @param numTerminals The number of terminals to create on the new [ConductingEquipment]. Defaults to 2.
     * @param action An action that accepts the new [ConductingEquipment] to allow for additional initialisation.
     *
     * @return This [TestNetworkBuilder] to allow for fluent use.
     */
    @JvmOverloads
    fun toOther(
        creator: (String) -> ConductingEquipment,
        nominalPhases: PhaseCode = PhaseCode.ABC,
        numTerminals: Int? = null,
        action: ConductingEquipment.() -> Unit = {}
    ): TestNetworkBuilder {
        current = network.createOther(creator, nominalPhases, numTerminals).also {
            connect(current!!, it)
            action(it)
        }
        return this
    }

    /**
     * Move the current network pointer to the specified [from] allowing branching of the network. This has the effect of changing the current network pointer.
     *
     * @param from The mRID of the [ConductingEquipment] to branch from.
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
     *
     * @return This [TestNetworkBuilder] to allow for fluent use.
     */
    fun connect(from: String, to: String, fromTerminal: Int, toTerminal: Int): TestNetworkBuilder {
        connect(network[from]!!, network[to]!!, fromTerminal, toTerminal)
        return this
    }

    /**
     * Create a new HV/MV feeder with the specified terminal as the head terminal.
     *
     * @param headMrid The mRID of the head [ConductingEquipment].
     * @param sequenceNumber The [Terminal] sequence number of the head terminal. Defaults to last terminal.
     *
     * @return This [TestNetworkBuilder] to allow for fluent use.
     */
    @JvmOverloads
    fun addFeeder(headMrid: String, sequenceNumber: Int? = null): TestNetworkBuilder {
        network.createFeeder(network[headMrid]!!, sequenceNumber)
        return this
    }

    /**
     * Create a new LV feeder with the specified terminal as the head terminal.
     *
     * @param headMrid The mRID of the head [ConductingEquipment].
     * @param sequenceNumber The [Terminal] sequence number of the head terminal. Defaults to last terminal.
     *
     * @return This [TestNetworkBuilder] to allow for fluent use.
     */
    fun addLvFeeder(headMrid: String, sequenceNumber: Int? = null): TestNetworkBuilder {
        network.createLvFeeder(network[headMrid]!!, sequenceNumber)
        return this
    }

    /**
     * Get the [NetworkService] after apply traced phasing and feeder directions.
     *
     * Does not infer phasing.
     *
     * @return The [NetworkService] created by this [TestNetworkBuilder]
     */
    fun build(applyDirectionsFromSources: Boolean = true): NetworkService {
        Tracing.setDirection().run(network)
        Tracing.setPhases().run(network)

        if (applyDirectionsFromSources)
            network.sequenceOf<EnergySource>().flatMap { it.terminals }.forEach { Tracing.setDirection().run(it) }

        Tracing.assignEquipmentContainersToFeeders().run(network)

        return network
    }

    private fun nextId(type: String): String = "$type${count++}"

    private fun connect(from: ConductingEquipment, to: ConductingEquipment, fromTerminal: Int? = null, toTerminal: Int? = null) {
        network.connect(
            from.getTerminal(fromTerminal ?: currentTerminal ?: from.numTerminals())!!,
            to.getTerminal(toTerminal ?: 1)!!
        )
        currentTerminal = null
    }

    private fun NetworkService.createExternalSource(phaseCode: PhaseCode) =
        nextId("s").let { id ->
            if (phaseCode.singlePhases.any { it !in PhaseCode.ABCN })
                throw IllegalArgumentException("EnergySource phases must be a subset of ABCN")

            EnergySource(id).apply {
                isExternalGrid = true
                addTerminal(Terminal("$id-t1").apply { phases = phaseCode }.also { add(it) })
            }.also { add(it) }
        }

    private fun NetworkService.createAcls(phaseCode: PhaseCode) =
        nextId("c").let { id ->
            AcLineSegment(id).apply {
                addTerminal(Terminal("$id-t1").apply { phases = phaseCode }.also { add(it) })
                addTerminal(Terminal("$id-t2").apply { phases = phaseCode }.also { add(it) })
            }.also { add(it) }
        }

    private fun NetworkService.createBreaker(phaseCode: PhaseCode = PhaseCode.ABC, isNormallyOpen: Boolean, isOpen: Boolean) =
        nextId("b").let { id ->
            Breaker(id).apply {
                setNormallyOpen(isNormallyOpen)
                setOpen(isOpen)

                addTerminal(Terminal("$id-t1").apply { phases = phaseCode }.also { add(it) })
                addTerminal(Terminal("$id-t2").apply { phases = phaseCode }.also { add(it) })
            }.also { add(it) }
        }

    private fun NetworkService.createJunction(phaseCode: PhaseCode = PhaseCode.ABC, numTerminals: Int?) =
        nextId("j").let { id ->
            Junction(id).apply {
                for (i in 1..(numTerminals ?: 2))
                    addTerminal(Terminal("$id-t$i").apply { phases = phaseCode }.also { add(it) })
            }.also { add(it) }
        }

    private fun NetworkService.createPowerTransformer(nominalPhases: List<PhaseCode>) =
        nextId("tx").let { id ->
            PowerTransformer(id).apply {
                nominalPhases.forEachIndexed { i, phaseCode ->
                    val t = Terminal("$id-t${i + 1}").apply { phases = phaseCode }.also { add(it) }

                    addTerminal(t)
                    addEnd(PowerTransformerEnd("$id-e${i + 1}").apply { terminal = t }.also { add(it) })
                }
            }.also { add(it) }
        }

    private fun NetworkService.createOther(
        creator: (String) -> ConductingEquipment,
        phaseCode: PhaseCode = PhaseCode.ABC,
        numTerminals: Int?
    ) =
        nextId("o").let { id ->
            creator(id).apply {
                for (i in 1..(numTerminals ?: 2))
                    addTerminal(Terminal("$mRID-t$i").apply { phases = phaseCode }.also { add(it) })
            }.also { tryAdd(it) }
        }

    private fun NetworkService.createFeeder(headEquipment: ConductingEquipment, sequenceNumber: Int?) =
        nextId("fdr").let { id ->
            Feeder(id).apply {
                normalHeadTerminal = headEquipment.getTerminal(sequenceNumber ?: headEquipment.numTerminals())!!

                addEquipment(headEquipment)
            }.also {
                headEquipment.addContainer(it)
                add(it)
            }
        }

    private fun NetworkService.createLvFeeder(headEquipment: ConductingEquipment, sequenceNumber: Int?) =
        nextId("lvf").let { id ->
            LvFeeder(id).apply {
                normalHeadTerminal = headEquipment.getTerminal(sequenceNumber ?: headEquipment.numTerminals())!!

                addEquipment(headEquipment)
            }.also {
                headEquipment.addContainer(it)
                add(it)
            }
        }

}
