/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace.operators

import com.zepben.evolve.cim.iec61970.base.core.Equipment
import com.zepben.evolve.cim.iec61970.base.core.EquipmentContainer
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import kotlin.reflect.KProperty1

internal class EquipmentContainerStateOperatorsTest {

    private val normal = EquipmentContainerStateOperators.NORMAL
    private val current = EquipmentContainerStateOperators.CURRENT

    @Test
    fun getEquipment() {
        fun test(operators: EquipmentContainerStateOperators, equipmentProp: KProperty1<EquipmentContainer, Collection<Equipment>>) {
            val equipment1 = mockk<Equipment>()
            val equipment2 = mockk<Equipment>()
            val equipment = listOf(equipment1, equipment2)
            val container = mockk<EquipmentContainer>()
            every { equipmentProp.get(container) } returns equipment

            val result = operators.getEquipment(container)

            assertThat(result, equalTo(equipment))
            verify { equipmentProp.get(container) }
        }

        test(normal, EquipmentContainer::equipment)
        test(current, EquipmentContainer::currentEquipment)
    }

    @Test
    fun getContainers() {
        fun test(operators: EquipmentContainerStateOperators, containersProp: KProperty1<Equipment, Collection<EquipmentContainer>>) {
            val container1 = mockk<EquipmentContainer>()
            val container2 = mockk<EquipmentContainer>()
            val containers = listOf(container1, container2)
            val equipment = mockk<Equipment>()
            every { containersProp.get(equipment) } returns containers

            val result = operators.getContainers(equipment)

            assertThat(result, equalTo(containers))
            verify { containersProp.get(equipment) }
        }

        test(normal, Equipment::containers)
        test(current, Equipment::currentContainers)
    }

    @Test
    fun addEquipmentToContainer() {
        fun test(operators: EquipmentContainerStateOperators, addEquipment: EquipmentContainer.(Equipment) -> EquipmentContainer) {
            val container = mockk<EquipmentContainer>()
            val equipment = mockk<Equipment>()
            every { addEquipment(container, equipment) } returns container

            operators.addEquipmentToContainer(equipment, container)

            verify { addEquipment(container, equipment) }
        }

        test(normal, EquipmentContainer::addEquipment)
        test(current, EquipmentContainer::addCurrentEquipment)
    }

    @Test
    fun addContainerToEquipment() {
        fun test(operators: EquipmentContainerStateOperators, addContainer: Equipment.(EquipmentContainer) -> Equipment) {
            val container = mockk<EquipmentContainer>()
            val equipment = mockk<Equipment>()
            every { addContainer(equipment, container) } returns equipment

            operators.addContainerToEquipment(container, equipment)

            verify { addContainer(equipment, container) }
        }

        test(normal, Equipment::addContainer)
        test(current, Equipment::addCurrentContainer)
    }

    @Test
    fun associateEquipmentAndContainer() {
        fun test(
            operators: EquipmentContainerStateOperators,
            addEquipment: EquipmentContainer.(Equipment) -> EquipmentContainer,
            addContainer: Equipment.(EquipmentContainer) -> Equipment
        ) {
            val container = mockk<EquipmentContainer>()
            val equipment = mockk<Equipment>()
            every { addEquipment(container, equipment) } returns container
            every { addContainer(equipment, container) } returns equipment

            operators.associateEquipmentAndContainer(equipment, container)

            verify { addEquipment(container, equipment) }
            verify { addContainer(equipment, container) }
        }

        test(normal, EquipmentContainer::addEquipment, Equipment::addContainer)
        test(current, EquipmentContainer::addCurrentEquipment, Equipment::addCurrentContainer)
    }

    @Test
    fun removeEquipmentFromContainer() {
        fun test(operators: EquipmentContainerStateOperators, removeEquipment: EquipmentContainer.(Equipment) -> Boolean) {
            val container = mockk<EquipmentContainer>()
            val equipment = mockk<Equipment>()
            every { removeEquipment(container, equipment) } returns true

            operators.removeEquipmentFromContainer(equipment, container)

            verify { removeEquipment(container, equipment) }
        }

        test(normal, EquipmentContainer::removeEquipment)
        test(current, EquipmentContainer::removeCurrentEquipment)
    }

    @Test
    fun removeContainerFromEquipment() {
        fun test(operators: EquipmentContainerStateOperators, removeContainer: Equipment.(EquipmentContainer) -> Boolean) {
            val container = mockk<EquipmentContainer>()
            val equipment = mockk<Equipment>()
            every { removeContainer(equipment, container) } returns true

            operators.removeContainerFromEquipment(container, equipment)

            verify { removeContainer(equipment, container) }
        }

        test(normal, Equipment::removeContainer)
        test(current, Equipment::removeCurrentContainer)
    }

    @Test
    fun disassociateEquipmentAndContainer() {
        fun test(
            operators: EquipmentContainerStateOperators,
            removeEquipment: EquipmentContainer.(Equipment) -> Boolean,
            removeContainer: Equipment.(EquipmentContainer) -> Boolean
        ) {
            val container = mockk<EquipmentContainer>()
            val equipment = mockk<Equipment>()
            every { removeEquipment(container, equipment) } returns true
            every { removeContainer(equipment, container) } returns true

            operators.disassociateEquipmentAndContainer(equipment, container)

            verify { removeEquipment(container, equipment) }
            verify { removeContainer(equipment, container) }
        }

        test(normal, EquipmentContainer::removeEquipment, Equipment::removeContainer)
        test(current, EquipmentContainer::removeCurrentEquipment, Equipment::removeCurrentContainer)
    }

}
