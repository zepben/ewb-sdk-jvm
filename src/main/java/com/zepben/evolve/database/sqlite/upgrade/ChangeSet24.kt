/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.upgrade

internal fun changeSet24() = ChangeSet(24) {
    listOf(
        "ALTER TABLE power_transformers RENAME TO power_transformers_old",
        "CREATE TABLE power_transformers(" +
            "mrid TEXT NOT NULL," +
            "vector_group TEXT NOT NULL," +
            "transformer_utilisation NUMBER NULL," +
            "base_voltage_mrid NUMBER NULL," +
            "normally_in_service BOOLEAN," +
            "in_service BOOLEAN," +
            "location_mrid TEXT NULL," +
            "num_controls INTEGER NOT NULL," +
            "name TEXT NOT NULL," +
            "description TEXT NOT NULL," +
            "num_diagram_objects INTEGER NOT_NULL" +
            ")",
        "INSERT INTO power_transformers (" +
            "mrid," +
            "vector_group," +
            "transformer_utilisation," +
            "base_voltage_mrid," +
            "normally_in_service," +
            "in_service," +
            "location_mrid," +
            "num_controls," +
            "name," +
            "description," +
            "num_diagram_objects" +
            ") SELECT " +
            "mrid," +
            "vector_group," +
            "transformer_utilisation," +
            "base_voltage_mrid," +
            "normally_in_service," +
            "in_service," +
            "location_mrid," +
            "num_controls," +
            "name," +
            "description," +
            "num_diagram_objects" +
            " FROM power_transformers_old",
        "DROP TABLE IF EXISTS power_transformers_old",
        "CREATE TABLE power_transformer_info(mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL)",
        "ALTER TABLE power_transformers ADD power_transformer_info_mrid TEXT"
    )
}
