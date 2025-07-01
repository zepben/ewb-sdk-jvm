/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.tracing.networktrace.conditions

import com.zepben.ewb.cim.iec61970.base.wires.PowerTransformer
import com.zepben.ewb.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.ewb.cim.iec61970.base.wires.Switch
import com.zepben.ewb.services.network.tracing.feeder.FeederDirection
import com.zepben.ewb.services.network.tracing.networktrace.conditions.Conditions.downstream
import com.zepben.ewb.services.network.tracing.networktrace.conditions.Conditions.stopAtOpen
import com.zepben.ewb.services.network.tracing.networktrace.conditions.Conditions.upstream
import com.zepben.ewb.services.network.tracing.networktrace.conditions.Conditions.withDirection
import com.zepben.ewb.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.ewb.services.network.tracing.networktrace.operators.OpenStateOperators
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.instanceOf
import org.junit.jupiter.api.Test

class ConditionsTest {

    @Test
    fun stateOperatorsWithDirection() {
        val stateOperators = NetworkStateOperators.NORMAL
        val condition = stateOperators.withDirection<Unit>(FeederDirection.BOTH)
        assertThat(condition, instanceOf(DirectionCondition::class.java))
        condition as DirectionCondition
        assertThat(condition.stateOperators, equalTo(stateOperators))
        assertThat(condition.direction, equalTo(FeederDirection.BOTH))
    }

    @Test
    fun stateOperatorsUpstream() {
        val stateOperators = NetworkStateOperators.NORMAL
        val condition = stateOperators.upstream<Unit>()
        assertThat(condition, instanceOf(DirectionCondition::class.java))
        condition as DirectionCondition
        assertThat(condition.stateOperators, equalTo(stateOperators))
        assertThat(condition.direction, equalTo(FeederDirection.UPSTREAM))
    }

    @Test
    fun stateOperatorsDownstream() {
        val stateOperators = NetworkStateOperators.NORMAL
        val condition = stateOperators.downstream<Unit>()
        assertThat(condition, instanceOf(DirectionCondition::class.java))
        condition as DirectionCondition
        assertThat(condition.stateOperators, equalTo(stateOperators))
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
