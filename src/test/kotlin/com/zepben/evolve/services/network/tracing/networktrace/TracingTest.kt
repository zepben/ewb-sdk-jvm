package com.zepben.evolve.services.network.tracing.networktrace

import com.zepben.evolve.services.network.tracing.networktrace.operators.NetworkStateOperators
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.sameInstance
import org.junit.jupiter.api.Test

class TracingTest {

    @Test
    fun normalDownstreamTree() {
        val trace = Tracing.normalDownstreamTree()
        assertThat(trace.stateOperators, sameInstance(NetworkStateOperators.NORMAL))
    }

    @Test
    fun currentDownstreamTree() {
        val trace = Tracing.currentDownstreamTree()
        assertThat(trace.stateOperators, sameInstance(NetworkStateOperators.CURRENT))
    }

    @Test
    fun normalSetDirection() {
        val trace = Tracing.normalSetDirection()
        assertThat(trace.stateOperators, sameInstance(NetworkStateOperators.NORMAL))
    }

    @Test
    fun currentSetDirection() {
        val trace = Tracing.currentSetDirection()
        assertThat(trace.stateOperators, sameInstance(NetworkStateOperators.CURRENT))
    }

    @Test
    fun normalRemoveDirection() {
        val trace = Tracing.normalRemoveDirection()
        assertThat(trace.stateOperators, sameInstance(NetworkStateOperators.NORMAL))
    }

    @Test
    fun currentRemoveDirection() {
        val trace = Tracing.currentRemoveDirection()
        assertThat(trace.stateOperators, sameInstance(NetworkStateOperators.CURRENT))
    }

    @Test
    fun normalAssignEquipmentToFeeders() {
        val trace = Tracing.normalAssignEquipmentToFeeders()
        assertThat(trace.stateOperators, sameInstance(NetworkStateOperators.NORMAL))
    }

    @Test
    fun currentAssignEquipmentToFeeders() {
        val trace = Tracing.currentAssignEquipmentToFeeders()
        assertThat(trace.stateOperators, sameInstance(NetworkStateOperators.CURRENT))
    }

    @Test
    fun normalAssignEquipmentToLvFeeders() {
        val trace = Tracing.normalAssignEquipmentToLvFeeders()
        assertThat(trace.stateOperators, sameInstance(NetworkStateOperators.NORMAL))
    }

    @Test
    fun currentAssignEquipmentToLvFeeders() {
        val trace = Tracing.currentAssignEquipmentToLvFeeders()
        assertThat(trace.stateOperators, sameInstance(NetworkStateOperators.CURRENT))
    }

    @Test
    fun normalSetPhases() {
        val trace = Tracing.normalSetPhases()
        assertThat(trace.stateOperators, sameInstance(NetworkStateOperators.NORMAL))
    }

    @Test
    fun currentSetPhases() {
        val trace = Tracing.currentSetPhases()
        assertThat(trace.stateOperators, sameInstance(NetworkStateOperators.CURRENT))
    }

    @Test
    fun normalPhaseInferrer() {
        val trace = Tracing.normalPhaseInferrer()
        assertThat(trace.stateOperators, sameInstance(NetworkStateOperators.NORMAL))
    }

    @Test
    fun currentPhaseInferrer() {
        val trace = Tracing.currentPhaseInferrer()
        assertThat(trace.stateOperators, sameInstance(NetworkStateOperators.CURRENT))
    }

    @Test
    fun normalRemovePhases() {
        val trace = Tracing.normalRemovePhases()
        assertThat(trace.stateOperators, sameInstance(NetworkStateOperators.NORMAL))
    }

    @Test
    fun currentRemovePhases() {
        val trace = Tracing.currentRemovePhases()
        assertThat(trace.stateOperators, sameInstance(NetworkStateOperators.CURRENT))
    }

    @Test
    fun normalFindSwerEquipment() {
        val trace = Tracing.normalFindSwerEquipment()
        assertThat(trace.stateOperators, sameInstance(NetworkStateOperators.NORMAL))
    }

    @Test
    fun currentFindSwerEquipment() {
        val trace = Tracing.currentFindSwerEquipment()
        assertThat(trace.stateOperators, sameInstance(NetworkStateOperators.CURRENT))
    }
}
