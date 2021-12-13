/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.upgrade.changesets

import com.zepben.evolve.database.sqlite.upgrade.ChangeSet

internal fun changeSet38() = ChangeSet(38) {
    listOf(
        *`Change base_voltage column to nominal_voltage in base_voltage table`,
        *`Change power_system_resource_mrid column to control_mrid in remote_controls table`,
        *`Change power_system_resource_mrid column to measurement_mrid in remote_sources table`
    )
}

@Suppress("ObjectPropertyName")
private val `Change base_voltage column to nominal_voltage in base_voltage table` = arrayOf(
    "ALTER TABLE base_voltages RENAME TO base_voltages_old",
    """
        CREATE TABLE base_voltages (
            mrid TEXT NOT NULL,
            name TEXT NOT NULL,
            description TEXT NOT NULL,
            num_diagram_objects INTEGER NOT NULL,
            nominal_voltage INTEGER NOT NULL
        )
    """,
    """
    INSERT INTO base_voltages
        SELECT mrid, name, description, num_diagram_objects, base_voltage
        FROM base_voltages_old
    """,
    "DROP TABLE base_voltages_old",
    "CREATE UNIQUE INDEX base_voltages_mrid ON base_voltages (mrid)",
    "CREATE INDEX base_voltages_name ON base_voltages (name)",
)

@Suppress("ObjectPropertyName")
private val `Change power_system_resource_mrid column to control_mrid in remote_controls table` = arrayOf(
    "ALTER TABLE remote_controls RENAME TO remote_controls_old",
    """
        CREATE TABLE remote_controls (
            mrid TEXT NOT NULL, 
            name TEXT NOT NULL, 
            description TEXT NOT NULL,
            num_diagram_objects INTEGER NOT NULL,
            control_mrid TEXT NULL
        )
    """,
    """
    INSERT INTO remote_controls
        SELECT mrid, name, description, num_diagram_objects, power_system_resource_mrid
        FROM remote_controls_old
    """,
    "DROP TABLE remote_controls_old",
    "CREATE UNIQUE INDEX remote_controls_mrid ON remote_controls (mrid)",
    "CREATE INDEX remote_controls_name ON remote_controls (name)",
)

@Suppress("ObjectPropertyName")
private val `Change power_system_resource_mrid column to measurement_mrid in remote_sources table` = arrayOf(
    "ALTER TABLE remote_sources RENAME TO remote_sources_old",
    """
        CREATE TABLE remote_sources (
            mrid TEXT NOT NULL,
            name TEXT NOT NULL,
            description TEXT NOT NULL,
            num_diagram_objects INTEGER NOT NULL,
            measurement_mrid TEXT NULL
        )
    """,
    """
    INSERT INTO remote_sources
        SELECT mrid, name, description, num_diagram_objects, power_system_resource_mrid
        FROM remote_sources_old
    """,
    "DROP TABLE remote_sources_old",
    "CREATE UNIQUE INDEX remote_sources_mrid ON remote_sources (mrid)",
    "CREATE INDEX remote_sources_name ON remote_sources (name)",
)
