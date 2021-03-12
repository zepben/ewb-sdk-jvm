/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.upgrade.changesets

import com.zepben.evolve.database.sqlite.upgrade.ChangeSet

internal fun changeSet20() = ChangeSet(20) {
    listOf(
        "DROP INDEX power_transformer_ends_power_transformer_mrid_end_number",
        "DROP INDEX terminals_conducting_equipment_mrid_sequence_number",
        "UPDATE terminals SET sequence_number = sequence_number + 1",
        "UPDATE power_transformer_ends SET end_number = end_number + 1",
        "CREATE UNIQUE INDEX power_transformer_ends_power_transformer_mrid_end_number ON power_transformer_ends (power_transformer_mrid, end_number)",
        "CREATE UNIQUE INDEX terminals_conducting_equipment_mrid_sequence_number ON terminals (conducting_equipment_mrid, sequence_number)"
    )
}
