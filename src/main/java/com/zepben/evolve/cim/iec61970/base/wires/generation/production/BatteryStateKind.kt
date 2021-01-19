/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.wires.generation.production

/**
 * Battery state.
 * @property discharging Stored energy is decreasing.
 * @property full Unable to charge, and not discharging.
 * @property waiting Neither charging not discharging, but able to do so.
 * @property charging Stored energy is increasing.
 * @property empty Unable to discharge, and not charging.
 */
@Suppress("EnumEntryName")
enum class BatteryStateKind {
    discharging,
    full,
    waiting,
    charging,
    empty
}
