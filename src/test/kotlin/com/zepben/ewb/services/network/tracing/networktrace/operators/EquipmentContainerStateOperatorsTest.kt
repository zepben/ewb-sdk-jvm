/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.tracing.networktrace.operators

import com.zepben.ewb.boilerplate.collections.BackedMridList
import com.zepben.ewb.boilerplate.collections.interfaces.MridCollection
import com.zepben.ewb.cim.iec61970.base.core.Equipment
import com.zepben.ewb.cim.iec61970.base.core.EquipmentContainer
import com.zepben.testutils.junit.SystemLogExtension
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class EquipmentContainerStateOperatorsTest {

    companion object {
        @JvmField
        @RegisterExtension
        val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()
    }

    private val normal = EquipmentContainerStateOperators.NORMAL
    private val current = EquipmentContainerStateOperators.CURRENT

    @Test
    fun getEquipment() {
        val container = object : EquipmentContainer("container") {
            override val currentEquipment: MridCollection<Equipment> =
                BackedMridList(owner = this, elementDescription = "Test Equipment")
        }
        val normalEquipment = object : Equipment("normal") {}
        val currentEquipment = object : Equipment("current") {}

        container.equipment.add(normalEquipment)
        container.currentEquipment.add(currentEquipment)

        assertThat(normal.getEquipment(container).toList(), equalTo(listOf(normalEquipment)))
        assertThat(current.getEquipment(container).toList(), equalTo(listOf(currentEquipment)))
    }

    @Test
    fun getContainers() {
        val equipment = object : Equipment("equipment") {}
        val normalContainer = object : EquipmentContainer("normal") {}
        val currentContainer = object : EquipmentContainer("current") {}

        equipment.containers.add(normalContainer)
        equipment.currentContainers.add(currentContainer)

        assertThat(normal.getContainers(equipment).toList(), equalTo(listOf(normalContainer)))
        assertThat(current.getContainers(equipment).toList(), equalTo(listOf(currentContainer)))
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
