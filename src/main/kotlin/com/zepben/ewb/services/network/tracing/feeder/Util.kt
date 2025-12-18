/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.tracing.feeder

import com.zepben.ewb.cim.extensions.iec61970.base.core.Site
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvFeeder
import com.zepben.ewb.cim.iec61970.base.core.*
import com.zepben.ewb.services.network.tracing.networktrace.operators.NetworkStateOperators


fun Terminal.isFeederHeadTerminal(): Boolean = when (val ce = conductingEquipment) {
    null -> false
    else -> ce.containers.asSequence().filterIsInstance<Feeder>().any { it.normalHeadTerminal == this }
}

/**
 * Find all LV feeders containing any [Equipment] in the [EquipmentContainer].
 * Note this is primarily used for Sites and LvSubstations, as they will typically contain LvFeeder start points.
 */
fun Collection<EquipmentContainer>.findLvFeeders(lvFeederStartPoints: Set<ConductingEquipment>, stateOperators: NetworkStateOperators): Iterable<LvFeeder> =
    asSequence()
        .flatMap { stateOperators.getEquipment(it) }
        .filterIsInstance<ConductingEquipment>()
        .filter { it in lvFeederStartPoints }
        .filter { !stateOperators.isOpen(it) }      // Exclude any open switch that might be energised by a different feeder on the other side.
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
inline fun <reified T : EquipmentContainer> Equipment?.getFilteredContainers(operators: NetworkStateOperators): Collection<T> = when (this) {
    null -> emptyList()
    else -> operators.getContainers(this).filterIsInstance<T>()
}
