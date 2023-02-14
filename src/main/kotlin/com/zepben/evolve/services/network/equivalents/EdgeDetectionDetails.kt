/*
 * Copyright 2023 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.equivalents

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.EquipmentContainer
import com.zepben.evolve.services.network.NetworkService
import kotlin.reflect.KClass

/**
 * The details of the [EquipmentContainer] instances/classes that defines the edges of the network that require an 'equivalent network' to be created.
 *
 * See [EdgeDetectionDetails.between] for helper functions used to create [EdgeDetectionDetails], or use the [NetworkService.addToEdgeBetween] extensions.
 *
 * @property edgeContainerClasses A [Set] of [EquipmentContainer] classes used to detect [ConductingEquipment] at the edges of the network, where the
 *   [ConductingEquipment] is considered at the edge if it has at least one [EquipmentContainer] of each defined class.
 * @property containers A [Set] of [EquipmentContainer] instances that must contain the edge [ConductingEquipment] before an 'equivalent network' will be created.
 */
data class EdgeDetectionDetails(
    val edgeContainerClasses: Set<KClass<out EquipmentContainer>>,
    val containers: Set<EquipmentContainer>,
) {

    companion object {

        /**
         * Create an [EdgeDetectionDetails] from the containers of interest.
         *
         * @param container One of the [EquipmentContainer] containing the edge equipment.
         * @param otherContainer The other [EquipmentContainer] containing the edge equipment.
         *   NOTE: Only one of [container] or [otherContainer] will contain the edge node.
         *
         * @return The [EdgeDetectionDetails].
         */
        @JvmStatic
        fun between(container: EquipmentContainer, otherContainer: EquipmentContainer): EdgeDetectionDetails =
            EdgeDetectionDetails(setOf(container::class, otherContainer::class), setOf(container, otherContainer))

        /**
         * Create an [EdgeDetectionDetails] from the container and other class of interest.
         *
         * @param container One of the [EquipmentContainer] containing the edge equipment.
         * @param OtherContainer The other class of [EquipmentContainer] containing the edge equipment.
         *   NOTE: Only one of [container] or the containers of type [OtherContainer] will contain the edge node.
         *
         * @return The [EdgeDetectionDetails].
         */
        inline fun <reified OtherContainer : EquipmentContainer> between(container: EquipmentContainer): EdgeDetectionDetails =
            EdgeDetectionDetails(setOf(container::class, OtherContainer::class), setOf(container))

        /**
         * Create an [EdgeDetectionDetails] from the container and other class of interest.
         *
         * @param container One of the [EquipmentContainer] containing the edge equipment.
         * @param otherContainerClass The other class of [EquipmentContainer] containing the edge equipment.
         *   NOTE: Only one of [container] or the containers of type [otherContainerClass] will contain the edge node.
         *
         * @return The [EdgeDetectionDetails].
         */
        @JvmStatic
        fun between(container: EquipmentContainer, otherContainerClass: KClass<out EquipmentContainer>): EdgeDetectionDetails =
            EdgeDetectionDetails(setOf(container::class, otherContainerClass), setOf(container))

        /**
         * Create an [EdgeDetectionDetails] from the container classes of interest.
         *
         * @param Container One of the [EquipmentContainer] containing the edge equipment.
         * @param OtherContainer The other class of [EquipmentContainer] containing the edge equipment.
         *   NOTE: Only one of the containers of type [Container] or [OtherContainer] will contain the edge node.
         *
         * @return The [EdgeDetectionDetails].
         */
        inline fun <reified Container : EquipmentContainer, reified OtherContainer : EquipmentContainer> between(): EdgeDetectionDetails =
            EdgeDetectionDetails(setOf(Container::class, OtherContainer::class), emptySet())

        /**
         * Create an [EdgeDetectionDetails] from the container classes of interest.
         *
         * @param containerClass One of the [EquipmentContainer] containing the edge equipment.
         * @param otherContainerClass The other class of [EquipmentContainer] containing the edge equipment.
         *   NOTE: Only one of the containers of type [containerClass] or [otherContainerClass] will contain the edge node.
         *
         * @return The [EdgeDetectionDetails].
         */
        @JvmStatic
        fun between(containerClass: KClass<out EquipmentContainer>, otherContainerClass: KClass<out EquipmentContainer>): EdgeDetectionDetails =
            EdgeDetectionDetails(setOf(containerClass, otherContainerClass), emptySet())

    }

}
