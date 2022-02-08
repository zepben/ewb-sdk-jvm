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
import com.zepben.evolve.cim.iec61970.base.wires.AcLineSegment
import com.zepben.evolve.cim.iec61970.base.wires.Breaker
import com.zepben.evolve.cim.iec61970.base.wires.EnergySource
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.tracing.Tracing

/**
 *
 */
class TestNetworkBuilder private constructor() {

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
     *
     * @return This [TestNetworkBuilder] to allow for fluent use.
     */
    fun fromSource(phases: PhaseCode): TestNetworkBuilder {
        current = network.createExternalSource(phases)
        return this
    }

    /**
     * Start a new network island from a [Breaker], updating the network pointer to the new [Breaker].
     *
     * @param nominalPhases The nominal phases for the new [Breaker].
     * @param isNormallyOpen The normal state of the switch. Defaults to false.
     * @param isOpen The current state of the switch. Defaults to [isNormallyOpen].
     *
     * @return This [TestNetworkBuilder] to allow for fluent use.
     */
    fun fromBreaker(nominalPhases: PhaseCode, isNormallyOpen: Boolean = false, isOpen: Boolean? = null): TestNetworkBuilder {
        current = network.createBreaker(nominalPhases, isNormallyOpen = isNormallyOpen, isOpen = isOpen ?: isNormallyOpen)
        return this
    }

    /**
     * Start a new network island from an [AcLineSegment], updating the network pointer to the new [AcLineSegment].
     *
     * @param nominalPhases The nominal phases for the new [AcLineSegment].
     *
     * @return This [TestNetworkBuilder] to allow for fluent use.
     */
    fun fromAcls(nominalPhases: PhaseCode): TestNetworkBuilder {
        current = network.createAcls(nominalPhases)
        return this
    }

    /**
     * Add a new [AcLineSegment] to the network and connect it to the current network pointer, updating the network pointer to the new [AcLineSegment].
     *
     * @param nominalPhases The nominal phases for the new [AcLineSegment].
     *
     * @return This [TestNetworkBuilder] to allow for fluent use.
     */
    fun toAcls(nominalPhases: PhaseCode): TestNetworkBuilder {
        current = network.createAcls(nominalPhases).also {
            connect(current!!, it)
        }
        return this
    }

    /**
     * Add a new [Breaker] to the network and connect it to the current network pointer, updating the network pointer to the new [Breaker].
     *
     * @param nominalPhases The nominal phases for the new [Breaker].
     * @param isNormallyOpen The normal state of the switch. Defaults to false.
     * @param isOpen The current state of the switch. Defaults to [isNormallyOpen].
     *
     * @return This [TestNetworkBuilder] to allow for fluent use.
     */
    fun toBreaker(nominalPhases: PhaseCode, isNormallyOpen: Boolean = false, isOpen: Boolean? = null): TestNetworkBuilder {
        current = network.createBreaker(nominalPhases, isNormallyOpen = isNormallyOpen, isOpen = isOpen ?: isNormallyOpen).also {
            connect(current!!, it)
        }
        return this
    }

    /**
     * Add a new [EnergySource] to the network and connect it to the current network pointer, updating the network pointer to the new [EnergySource].
     *
     * @param phases The [PhaseCode] for the new [EnergySource], used as both the nominal and energising phases. Must be a subset of [PhaseCode.ABCN].
     *
     * @return This [TestNetworkBuilder] to allow for fluent use.
     */
    fun toSource(phases: PhaseCode): TestNetworkBuilder {
        current = network.createExternalSource(phases).also {
            connect(current!!, it)
        }
        return this
    }

    /**
     * Move the current network pointer to the specified [from] allowing branching of the network. This has the effect of changing the current network pointer.
     *
     * @param from The mRID of the [ConductingEquipment] to split from.
     *
     * @return This [TestNetworkBuilder] to allow for fluent use.
     */
    fun splitFrom(from: String): TestNetworkBuilder {
        current = network[from]!!
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
     * Create a new feeder with the specified terminal as the head terminal.
     *
     * @param headMrid The mRID of the head [ConductingEquipment].
     * @param sequenceNumber The [Terminal] sequence number of the head terminal. Defaults to
     *
     * @return This [TestNetworkBuilder] to allow for fluent use.
     */
    fun addFeeder(headMrid: String, sequenceNumber: Int? = null): TestNetworkBuilder {
        network.createFeeder(network[headMrid]!!, sequenceNumber)
        return this
    }

    /**
     * Get the [NetworkService] after apply traced phasing and feeder directions.
     *
     * Does not infer phasing.
     *
     * @return The [NetworkService] created by this [TestNetworkBuilder]
     */
    fun build(): NetworkService {
        Tracing.setPhases().run(network)
        return network
    }

    private fun nextId(type: String): String = "$type${count++}"

    private fun connect(from: ConductingEquipment, to: ConductingEquipment, fromTerminal: Int? = null, toTerminal: Int? = null) {
        network.connect(
            from.getTerminal(fromTerminal ?: if (from is EnergySource) 1 else 2)!!,
            to.getTerminal(toTerminal ?: 1)!!
        )
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

    private fun NetworkService.createBreaker(phaseCode: PhaseCode, isNormallyOpen: Boolean, isOpen: Boolean) =
        nextId("b").let { id ->
            Breaker(id).apply {
                setNormallyOpen(isNormallyOpen)
                setOpen(isOpen)

                addTerminal(Terminal("$id-t1").apply { phases = phaseCode }.also { add(it) })
                addTerminal(Terminal("$id-t2").apply { phases = phaseCode }.also { add(it) })
            }.also { add(it) }
        }

    private fun NetworkService.createFeeder(headEquipment: ConductingEquipment, sequenceNumber: Int?) =
        nextId("fdr").let { id ->
            Feeder(id).apply {
                normalHeadTerminal = headEquipment.getTerminal(sequenceNumber ?: if (headEquipment is EnergySource) 1 else 2)!!
            }.also { add(it) }
        }

    companion object {

        /**
         * Create a [TestNetworkBuilder] that starts with an [EnergySource].
         *
         * @param phases The nominal phases for the starting [Breaker].
         *
         * @return The [TestNetworkBuilder] with the current network pointer assigned to the created [EnergySource].
         */
        @JvmStatic
        fun startWithSource(phases: PhaseCode): TestNetworkBuilder =
            TestNetworkBuilder().apply {
                fromSource(phases)
            }

        /**
         * Create a [TestNetworkBuilder] that starts with an [AcLineSegment].
         *
         * @param nominalPhases The nominal phases for the starting [AcLineSegment].
         *
         * @return The [TestNetworkBuilder] with the current network pointer assigned to the created [AcLineSegment].
         */
        @JvmStatic
        fun startWithAcls(nominalPhases: PhaseCode): TestNetworkBuilder =
            TestNetworkBuilder().apply {
                fromAcls(nominalPhases)
            }

        /**
         * Create a [TestNetworkBuilder] that starts with a [Breaker].
         *
         * @param nominalPhases The nominal phases for the starting [Breaker].
         * @param isNormallyOpen The normal state of the switch. Defaults to false.
         * @param isOpen The current state of the switch. Defaults to [isNormallyOpen].
         *
         * @return The [TestNetworkBuilder] with the current network pointer assigned to the created [Breaker].
         */
        @JvmStatic
        fun startWithBreaker(nominalPhases: PhaseCode, isNormallyOpen: Boolean = false, isOpen: Boolean? = null): TestNetworkBuilder =
            TestNetworkBuilder().apply {
                fromBreaker(nominalPhases, isNormallyOpen = isNormallyOpen, isOpen = isOpen)
            }

    }

}
