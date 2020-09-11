/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.database.sqlite.upgrade

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
