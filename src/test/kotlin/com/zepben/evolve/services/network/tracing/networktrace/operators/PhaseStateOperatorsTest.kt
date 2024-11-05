package com.zepben.evolve.services.network.tracing.networktrace.operators

import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.services.network.tracing.phases.PhaseStatus
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.sameInstance
import org.junit.jupiter.api.Test

import kotlin.reflect.KProperty1

class PhaseStateOperatorsTest {

    private val normal = PhaseStateOperators.NORMAL
    private val current = PhaseStateOperators.CURRENT

    @Test
    fun phaseStatus() {
        fun test(operators: PhaseStateOperators, phasesProp: KProperty1<Terminal, PhaseStatus>) {
            val terminal = Terminal()
            val result = operators.phaseStatus(terminal)
            assertThat(result, sameInstance(phasesProp.get(terminal)))
        }

        test(normal, Terminal::normalPhases)
        test(current, Terminal::currentPhases)
    }
}
