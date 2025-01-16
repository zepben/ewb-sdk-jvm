/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.feeder

import com.zepben.evolve.cim.iec61970.base.core.*
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.LvFeeder
import com.zepben.evolve.services.network.tracing.networktrace.operators.NetworkStateOperators


internal fun Terminal.isFeederHeadTerminal(): Boolean =
    conductingEquipment?.let { ce ->
        ce.containers
            .asSequence()
            .filterIsInstance<Feeder>()
            .any { it.normalHeadTerminal == this }
    } == true

/**
 * Find all LV feeders containing any [Equipment] in the [Site].
 */
internal fun Collection<Site>.findLvFeeders(lvFeederStartPoints: Set<ConductingEquipment>, stateOperators: NetworkStateOperators): Iterable<LvFeeder> =
    asSequence()
        .flatMap { it.equipment }
        .filter { it in lvFeederStartPoints }
        .flatMap { equipment -> equipment.getFilteredContainers<LvFeeder>(stateOperators) }
        .asIterable()

/**
 * Retrieves a collection of containers associated with the given equipment.
 *
 * @receiver The equipment for which to get the associated containers.
 * @param T The type of containers to find.
 * @param operators The [NetworkStateOperators] used to select the containers.
 * @return A collection of containers of the specified type that contain the specified equipment.
 */
internal inline fun <reified T : EquipmentContainer> Equipment?.getFilteredContainers(operators: NetworkStateOperators): Collection<T> = when (this) {
    null -> emptyList()
    else -> operators.getContainers(this).filterIsInstance<T>()
}
