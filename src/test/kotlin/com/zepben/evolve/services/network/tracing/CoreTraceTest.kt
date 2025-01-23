/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.services.network.testdata.PhaseSwapLoopNetwork
import com.zepben.evolve.services.network.testdata.setFeederDirections
import com.zepben.evolve.services.network.testdata.setPhases
import com.zepben.evolve.services.network.tracing.feeder.DirectionLogger
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTrace
import com.zepben.evolve.services.network.tracing.networktrace.Tracing
import com.zepben.evolve.services.network.tracing.networktrace.conditions.Conditions.downstream
import com.zepben.evolve.services.network.tracing.networktrace.conditions.Conditions.stopAtOpen
import com.zepben.evolve.services.network.tracing.networktrace.conditions.Conditions.upstream
import com.zepben.evolve.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.evolve.services.network.tracing.networktrace.run
import com.zepben.evolve.services.network.tracing.phases.PhaseLogger
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class CoreTraceTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun traceCores() {
        val n = getNetwork()

        // Trace all cores, we should visit everything
        var start = n.get<ConductingEquipment>("j0")!!
        var visited = currentNonDirectionalTrace(start, PhaseCode.ABCN)
        assertThat(visited, hasSize(22))

        // Trace from J9 on phase Y. Expect to visit J6 twice, once on Y and once on X. ac10 and j8 should never be visited.
        start = n["j9"]!!
        visited = currentNonDirectionalTrace(start, PhaseCode.Y)
        assertThat(visited, hasSize(21))
        assertThat(
            visited,
            hasItems(
                TrackedPhases(n["acLineSegment11"]!!, SinglePhaseKind.Y),
                TrackedPhases(n["j5"]!!, SinglePhaseKind.Y),
                TrackedPhases(n["j0"]!!, SinglePhaseKind.B),
                TrackedPhases(n["j6"]!!, SinglePhaseKind.X),
                TrackedPhases(n["j6"]!!, SinglePhaseKind.Y),
                TrackedPhases(n["j7"]!!, SinglePhaseKind.B),
            )
        )

        assertThat(visited.map { it.equipment.mRID }, not(hasItems("ac10", "j8")))
    }

    @Test
    internal fun traceSingleCoresDownstream() {
        val n = getNetwork()

        // Test from the "source" of the network downstream
        var start = n.get<ConductingEquipment>("j0")!!
        var visited = currentDownstreamTrace(start, PhaseCode.A)

        // j7, j9, acLineSegment8, acLineSegment9 and acLineSegment11 should not be traced.
        assertThat(
            visited,
            containsInAnyOrder(
                TrackedPhases(n["j0"]!!, SinglePhaseKind.A),
                TrackedPhases(n["j1"]!!, SinglePhaseKind.A),
                TrackedPhases(n["j2"]!!, SinglePhaseKind.A),
                TrackedPhases(n["j3"]!!, SinglePhaseKind.A),
                TrackedPhases(n["j4"]!!, SinglePhaseKind.X),
                TrackedPhases(n["j5"]!!, SinglePhaseKind.X),
                TrackedPhases(n["j6"]!!, SinglePhaseKind.X),
                TrackedPhases(n["j8"]!!, SinglePhaseKind.X),
                TrackedPhases(n["acLineSegment0"]!!, SinglePhaseKind.A),
                TrackedPhases(n["acLineSegment1"]!!, SinglePhaseKind.A),
                TrackedPhases(n["acLineSegment2"]!!, SinglePhaseKind.A),
                TrackedPhases(n["acLineSegment3"]!!, SinglePhaseKind.A),
                TrackedPhases(n["acLineSegment4"]!!, SinglePhaseKind.A),
                TrackedPhases(n["acLineSegment5"]!!, SinglePhaseKind.X),
                TrackedPhases(n["acLineSegment6"]!!, SinglePhaseKind.X),
                TrackedPhases(n["acLineSegment7"]!!, SinglePhaseKind.X),
                TrackedPhases(n["acLineSegment10"]!!, SinglePhaseKind.X)
            )
        )

        // Test from partway downstream to make sure we don't go upstream
        start = n["j1"]!!
        visited = normalDownstreamTrace(start, PhaseCode.A)
        val visitedMRIDs = visited.map { it.equipment.mRID }
        assertThat(visited, hasSize(4))
        assertThat(visited, hasItems(TrackedPhases(n["j1"]!!, SinglePhaseKind.A), TrackedPhases(n["j2"]!!, SinglePhaseKind.A)))
        assertThat(visitedMRIDs, not(contains("acLineSegment1")))
        assertThat(visitedMRIDs, not(contains("acLineSegment1")))

        // Test on a core that splits onto different cores on different branches
        start = n["j0"]!!
        visited = normalDownstreamTrace(start, PhaseCode.C)
        assertThat(visited, hasSize(11))
        assertThat(
            visited,
            hasItems(
                TrackedPhases(n["j1"]!!, SinglePhaseKind.C),
                TrackedPhases(n["j2"]!!, SinglePhaseKind.C),
                TrackedPhases(n["j7"]!!, SinglePhaseKind.C),
                TrackedPhases(n["j6"]!!, SinglePhaseKind.Y)
            )
        )
        assertThat(visitedMRIDs, not(contains("j3")))
    }

    @Test
    internal fun traceMultipleCoresDownstream() {
        val n = getNetwork()

        // Test from the "source" of the network downstream
        val start = n.get<ConductingEquipment>("j0")!!
        val visited = normalDownstreamTrace(start, PhaseCode.BC)

        // j8 and acLineSegment10 should not be traced.
        assertThat(
            visited,
            containsInAnyOrder(
                TrackedPhases(n["j0"]!!, SinglePhaseKind.B, SinglePhaseKind.C),
                TrackedPhases(n["j1"]!!, SinglePhaseKind.B, SinglePhaseKind.C),
                TrackedPhases(n["j2"]!!, SinglePhaseKind.B, SinglePhaseKind.C),
                TrackedPhases(n["j3"]!!, SinglePhaseKind.B),
                TrackedPhases(n["j4"]!!, SinglePhaseKind.Y),
                TrackedPhases(n["j5"]!!, SinglePhaseKind.Y),
                TrackedPhases(n["j6"]!!, SinglePhaseKind.X, SinglePhaseKind.Y),
                TrackedPhases(n["j6"]!!, SinglePhaseKind.Y),
                TrackedPhases(n["j7"]!!, SinglePhaseKind.B, SinglePhaseKind.C),
                TrackedPhases(n["j9"]!!, SinglePhaseKind.Y),
                TrackedPhases(n["acLineSegment0"]!!, SinglePhaseKind.B, SinglePhaseKind.C),
                TrackedPhases(n["acLineSegment1"]!!, SinglePhaseKind.B, SinglePhaseKind.C),
                TrackedPhases(n["acLineSegment2"]!!, SinglePhaseKind.B, SinglePhaseKind.C),
                TrackedPhases(n["acLineSegment3"]!!, SinglePhaseKind.B, SinglePhaseKind.C),
                TrackedPhases(n["acLineSegment4"]!!, SinglePhaseKind.B),
                TrackedPhases(n["acLineSegment5"]!!, SinglePhaseKind.Y),
                TrackedPhases(n["acLineSegment6"]!!, SinglePhaseKind.Y),
                TrackedPhases(n["acLineSegment7"]!!, SinglePhaseKind.Y),
                TrackedPhases(n["acLineSegment8"]!!, SinglePhaseKind.X, SinglePhaseKind.Y),
                TrackedPhases(n["acLineSegment9"]!!, SinglePhaseKind.B, SinglePhaseKind.C),
                TrackedPhases(n["acLineSegment11"]!!, SinglePhaseKind.Y),
            )
        )
    }

    @Test
    internal fun traceSingleCoresUpstream() {
        val n = getNetwork()
        val start = n.get<ConductingEquipment>("acLineSegment11")!!
        val visited = normalUpstreamTrace(start, PhaseCode.Y)

        assertThat(visited, hasSize(9))
        assertThat(
            visited,
            containsInAnyOrder(
                TrackedPhases(n["acLineSegment11"]!!, SinglePhaseKind.Y),
                TrackedPhases(n["j5"]!!, SinglePhaseKind.Y),
                TrackedPhases(n["acLineSegment6"]!!, SinglePhaseKind.Y),
                TrackedPhases(n["j4"]!!, SinglePhaseKind.Y),
                TrackedPhases(n["acLineSegment5"]!!, SinglePhaseKind.Y),
                TrackedPhases(n["j3"]!!, SinglePhaseKind.B),
                TrackedPhases(n["acLineSegment4"]!!, SinglePhaseKind.B),
                TrackedPhases(n["acLineSegment0"]!!, SinglePhaseKind.B),
                TrackedPhases(n["j0"]!!, SinglePhaseKind.B)
            )
        )
    }

    @Test
    internal fun traceMultipleCoresUpstream() {
        val n = getNetwork()
        val start = n.get<ConductingEquipment>("acLineSegment8")!!
        val visited = normalUpstreamTrace(start, PhaseCode.XY)

        assertThat(
            visited,
            containsInAnyOrder(
                TrackedPhases(n["acLineSegment8"]!!, SinglePhaseKind.X, SinglePhaseKind.Y),
                TrackedPhases(n["j7"]!!, SinglePhaseKind.B, SinglePhaseKind.C),
                TrackedPhases(n["acLineSegment9"]!!, SinglePhaseKind.B, SinglePhaseKind.C),
                TrackedPhases(n["acLineSegment2"]!!, SinglePhaseKind.B, SinglePhaseKind.C),
                TrackedPhases(n["j1"]!!, SinglePhaseKind.B, SinglePhaseKind.C),
                TrackedPhases(n["acLineSegment1"]!!, SinglePhaseKind.B, SinglePhaseKind.C),
                TrackedPhases(n["acLineSegment0"]!!, SinglePhaseKind.B, SinglePhaseKind.C),
                TrackedPhases(n["j0"]!!, SinglePhaseKind.B, SinglePhaseKind.C),
            )
        )
    }

    private fun getNetwork() = PhaseSwapLoopNetwork.create().also {
        it.setPhases()
        it.setFeederDirections()
    }

    private fun currentNonDirectionalTrace(start: ConductingEquipment, phases: PhaseCode): List<TrackedPhases> {
        return runTrace(Tracing.networkTrace(NetworkStateOperators.CURRENT).addCondition { stopAtOpen() }, start, phases)
    }

    private fun normalDownstreamTrace(start: ConductingEquipment, phases: PhaseCode): List<TrackedPhases> {
        return runTrace(Tracing.networkTrace(NetworkStateOperators.NORMAL).addCondition { downstream() }, start, phases)
    }

    private fun currentDownstreamTrace(start: ConductingEquipment, phases: PhaseCode): List<TrackedPhases> {
        return runTrace(Tracing.networkTrace(NetworkStateOperators.CURRENT).addCondition { downstream() }, start, phases)
    }

    private fun normalUpstreamTrace(start: ConductingEquipment, phases: PhaseCode): List<TrackedPhases> {
        return runTrace(Tracing.networkTrace(NetworkStateOperators.NORMAL).addCondition { upstream() }, start, phases)
    }

    private fun runTrace(trace: NetworkTrace<Unit>, start: ConductingEquipment, phases: PhaseCode): List<TrackedPhases> {
        val visited = mutableListOf<TrackedPhases>()

        PhaseLogger.trace(start)
        DirectionLogger.trace(start)

        trace.addStepAction { (path), ctx -> System.err.println("$path - isStopping:${ctx.isStopping}") }
            .addStepAction { step, _ ->
                visited.add(TrackedPhases(step.path.toEquipment, step.path.nominalPhasePaths.map { it.to }.toSet()))
            }
            .run(start, phases)

        return visited
    }

    data class TrackedPhases(val equipment: ConductingEquipment, val phases: Set<SinglePhaseKind>) {
        constructor(conductingEquipment: ConductingEquipment, vararg phases: SinglePhaseKind) : this(conductingEquipment, phases.toSet())
    }

}
