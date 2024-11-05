package com.zepben.evolve.services.network.tracing.networktrace.operators

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.Equipment
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.cim.iec61970.base.wires.Switch
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import kotlin.reflect.KMutableProperty1

class OpenStateOperatorsTest {

    private val normal = OpenStateOperators.NORMAL
    private val current = OpenStateOperators.CURRENT

    @Test
    fun isOpenChecksInService() {
        fun test(operators: OpenStateOperators, inServiceProp: KMutableProperty1<Equipment, Boolean>) {
            val equipment = mockk<ConductingEquipment>()
            every { inServiceProp(equipment) } returns false andThen true

            assertThat(operators.isOpen(equipment), equalTo(true))
            assertThat(operators.isOpen(equipment), equalTo(false))
            verify(exactly = 2) { inServiceProp(equipment) }
        }

        test(normal, Equipment::normallyInService)
        test(current, Equipment::inService)
    }

    @Test
    fun isOpenChecksSwitchOpenState() {
        fun test(operators: OpenStateOperators, inServiceProp: KMutableProperty1<Equipment, Boolean>, isOpen: (Switch, SinglePhaseKind?) -> Boolean) {
            val switch = mockk<Switch>()
            every { inServiceProp(switch) } returns false andThen true
            every { isOpen(switch, SinglePhaseKind.A) } returns false andThen true

            assertThat(operators.isOpen(switch, SinglePhaseKind.A), equalTo(true))
            assertThat(operators.isOpen(switch, SinglePhaseKind.A), equalTo(false))
            assertThat(operators.isOpen(switch, SinglePhaseKind.A), equalTo(true))
            verify(exactly = 3) { inServiceProp(switch) }
            verify(exactly = 2) { isOpen(switch, SinglePhaseKind.A) }
        }

        test(normal, Equipment::normallyInService, Switch::isNormallyOpen)
        test(current, Equipment::inService, Switch::isOpen)
    }

    @Test
    fun setOpen() {
        fun test(operators: OpenStateOperators, setOpen: (Switch, Boolean, SinglePhaseKind?) -> Switch) {
            val switch = mockk<Switch>()
            every { setOpen(switch, any(), SinglePhaseKind.A) } returns switch

            operators.setOpen(switch, true, SinglePhaseKind.A)
            verify { setOpen(switch, true, SinglePhaseKind.A) }
        }

        test(normal, Switch::setNormallyOpen)
        test(current, Switch::setOpen)
    }
}
