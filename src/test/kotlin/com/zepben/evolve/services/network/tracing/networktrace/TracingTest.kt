/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace

import com.zepben.evolve.services.network.tracing.networktrace.operators.NetworkStateOperators
import io.mockk.mockk
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.sameInstance
import org.junit.jupiter.api.Test

class TracingTest {

    @Test
    fun downstreamTree() {
        val operators = mockk<NetworkStateOperators>()
        val trace = Tracing.downstreamTree(operators)
        assertThat(trace.stateOperators, sameInstance(operators))
    }

    @Test
    fun downstreamTreeDefaultsToNormal() {
        val trace = Tracing.downstreamTree()
        assertThat(trace.stateOperators, sameInstance(NetworkStateOperators.NORMAL))
    }

    @Test
    fun normalSetDirection() {
        val operators = mockk<NetworkStateOperators>()
        val trace = Tracing.setDirection(operators)
        assertThat(trace.stateOperators, sameInstance(operators))
    }

    @Test
    fun setDirectionDefaultsToNormal() {
        val trace = Tracing.setDirection()
        assertThat(trace.stateOperators, sameInstance(NetworkStateOperators.NORMAL))
    }

    @Test
    fun normalRemoveDirection() {
        val operators = mockk<NetworkStateOperators>()
        val trace = Tracing.removeDirection(operators)
        assertThat(trace.stateOperators, sameInstance(operators))
    }

    @Test
    fun removeDirectionDefaultsToNormal() {
        val trace = Tracing.removeDirection()
        assertThat(trace.stateOperators, sameInstance(NetworkStateOperators.NORMAL))
    }

    @Test
    fun normalAssignEquipmentToFeeders() {
        val operators = mockk<NetworkStateOperators>()
        val trace = Tracing.assignEquipmentToFeeders(operators)
        assertThat(trace.stateOperators, sameInstance(operators))
    }

    @Test
    fun assignEquipmentToFeedersDefaultsToNormal() {
        val trace = Tracing.assignEquipmentToFeeders()
        assertThat(trace.stateOperators, sameInstance(NetworkStateOperators.NORMAL))
    }

    @Test
    fun normalAssignEquipmentToLvFeeders() {
        val operators = mockk<NetworkStateOperators>()
        val trace = Tracing.assignEquipmentToLvFeeders(operators)
        assertThat(trace.stateOperators, sameInstance(operators))
    }

    @Test
    fun assignEquipmentToLvFeedersDefaultsToNormal() {
        val trace = Tracing.assignEquipmentToLvFeeders()
        assertThat(trace.stateOperators, sameInstance(NetworkStateOperators.NORMAL))
    }

    @Test
    fun normalSetPhases() {
        val operators = mockk<NetworkStateOperators>()
        val trace = Tracing.setPhases(operators)
        assertThat(trace.stateOperators, sameInstance(operators))
    }

    @Test
    fun setPhasesDefaultsToNormal() {
        val trace = Tracing.setPhases()
        assertThat(trace.stateOperators, sameInstance(NetworkStateOperators.NORMAL))
    }

    @Test
    fun normalPhaseInferrer() {
        val operators = mockk<NetworkStateOperators>()
        val trace = Tracing.phaseInferrer(operators)
        assertThat(trace.stateOperators, sameInstance(operators))
    }

    @Test
    fun phaseInferrerDefaultsToNormal() {
        val trace = Tracing.phaseInferrer()
        assertThat(trace.stateOperators, sameInstance(NetworkStateOperators.NORMAL))
    }

    @Test
    fun normalRemovePhases() {
        val operators = mockk<NetworkStateOperators>()
        val trace = Tracing.removePhases(operators)
        assertThat(trace.stateOperators, sameInstance(operators))
    }

    @Test
    fun removePhasesDefaultsToNormal() {
        val trace = Tracing.removePhases()
        assertThat(trace.stateOperators, sameInstance(NetworkStateOperators.NORMAL))
    }

    @Test
    fun normalFindSwerEquipment() {
        val operators = mockk<NetworkStateOperators>()
        val trace = Tracing.findSwerEquipment(operators)
        assertThat(trace.stateOperators, sameInstance(operators))
    }

    @Test
    fun findSwerEquipmentDefaultsToNormal() {
        val trace = Tracing.findSwerEquipment()
        assertThat(trace.stateOperators, sameInstance(NetworkStateOperators.NORMAL))
    }
}
