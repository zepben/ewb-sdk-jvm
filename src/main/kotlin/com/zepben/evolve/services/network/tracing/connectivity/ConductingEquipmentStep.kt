/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.connectivity

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment

/**
 * A class that can be used for traversing [ConductingEquipment] while keeping track of the number of steps taken.
 *
 * @property conductingEquipment The [ConductingEquipment] being processed by this step.
 * @property step The number of steps from the initial [ConductingEquipment].
 */
data class ConductingEquipmentStep(
    val conductingEquipment: ConductingEquipment,
    val step: Int = 0
)
