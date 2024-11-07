package com.zepben.evolve.services.network.tracing.networktrace.conditions

import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.PowerTransformer
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.cim.iec61970.base.wires.Switch
import com.zepben.evolve.services.network.tracing.feeder.FeederDirection
import com.zepben.evolve.services.network.tracing.networktrace.conditions.Conditions.downstream
import com.zepben.evolve.services.network.tracing.networktrace.conditions.Conditions.stopAtOpen
import com.zepben.evolve.services.network.tracing.networktrace.conditions.Conditions.upstream
import com.zepben.evolve.services.network.tracing.networktrace.operators.FeederDirectionStateOperations
import com.zepben.evolve.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.evolve.services.network.tracing.networktrace.operators.OpenStateOperators
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.instanceOf
import org.junit.jupiter.api.Test

class ConditionsTest {

    @Test
    fun upstream() {
        val getDirection = Terminal::normalFeederDirection
        val condition = upstream<Unit>(getDirection)
        assertThat(condition, instanceOf(DirectionCondition::class.java))
        condition as DirectionCondition
        assertThat(condition.getDirection, equalTo(getDirection))
        assertThat(condition.direction, equalTo(FeederDirection.UPSTREAM))
    }

    @Test
    fun feederDirectionStateOperatorsUpstream() {
        val feederDirectionOperators: FeederDirectionStateOperations = NetworkStateOperators.NORMAL
        val condition = feederDirectionOperators.upstream<Unit>()
        assertThat(condition, instanceOf(DirectionCondition::class.java))
        condition as DirectionCondition
        assertThat(condition.getDirection, equalTo(feederDirectionOperators::getDirection))
        assertThat(condition.direction, equalTo(FeederDirection.UPSTREAM))
    }

    @Test
    fun downstream() {
        val getDirection = Terminal::normalFeederDirection
        val condition = downstream<Unit>(getDirection)
        assertThat(condition, instanceOf(DirectionCondition::class.java))
        condition as DirectionCondition
        assertThat(condition.getDirection, equalTo(getDirection))
        assertThat(condition.direction, equalTo(FeederDirection.DOWNSTREAM))
    }

    @Test
    fun testDownstream() {
        val feederDirectionOperators: FeederDirectionStateOperations = NetworkStateOperators.NORMAL
        val condition = feederDirectionOperators.downstream<Unit>()
        assertThat(condition, instanceOf(DirectionCondition::class.java))
        condition as DirectionCondition
        assertThat(condition.getDirection, equalTo(feederDirectionOperators::getDirection))
        assertThat(condition.direction, equalTo(FeederDirection.DOWNSTREAM))
    }

    @Test
    fun stopAtOpen() {
        val isOpen: (Switch, SinglePhaseKind?) -> Boolean = Switch::isOpen
        val condition = stopAtOpen<Unit>(isOpen, SinglePhaseKind.A)
        assertThat(condition, instanceOf(OpenCondition::class.java))
        condition as OpenCondition
        assertThat(condition.isOpen, equalTo(isOpen))
        assertThat(condition.phase, equalTo(SinglePhaseKind.A))
    }

    @Test
    fun openStateOperatorsStopAtOpen() {
        val operators: OpenStateOperators = NetworkStateOperators.NORMAL
        val condition = operators.stopAtOpen<Unit>(SinglePhaseKind.A)
        assertThat(condition, instanceOf(OpenCondition::class.java))
        condition as OpenCondition
        assertThat(condition.isOpen, equalTo(operators::isOpen))
        assertThat(condition.phase, equalTo(SinglePhaseKind.A))
    }

    @Test
    fun limitEquipmentSteps() {
        val condition = Conditions.limitEquipmentSteps<Unit>(1)
        assertThat(condition, instanceOf(EquipmentStepLimitCondition::class.java))
        condition as EquipmentStepLimitCondition
        assertThat(condition.limit, equalTo(1))
    }

    @Test
    fun testLimitEquipmentTypeSteps() {
        val condition = Conditions.limitEquipmentSteps<Unit>(1, PowerTransformer::class)
        assertThat(condition, instanceOf(EquipmentTypeStepLimitCondition::class.java))
        condition as EquipmentTypeStepLimitCondition
        assertThat(condition.limit, equalTo(1))
        assertThat(condition.equipmentType, equalTo(PowerTransformer::class))
    }

    @Test
    fun testLimitEquipmentTypeStepsJavaInterop() {
        val condition = Conditions.limitEquipmentSteps<Unit>(1, PowerTransformer::class.java)
        assertThat(condition, instanceOf(EquipmentTypeStepLimitCondition::class.java))
        condition as EquipmentTypeStepLimitCondition
        assertThat(condition.limit, equalTo(1))
        assertThat(condition.equipmentType, equalTo(PowerTransformer::class))
    }
}
