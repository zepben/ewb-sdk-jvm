/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.upgrade.changesets

import com.zepben.evolve.database.sqlite.upgrade.ChangeSet

internal fun changeSet27() = ChangeSet(27) {
    listOf(
        """CREATE TABLE load_break_switches(
            mrid TEXT NOT NULL,
            name TEXT NOT NULL,
            description TEXT NOT NULL,
            num_diagram_objects INTEGER NOT NULL,
            location_mrid TEXT NULL,
            num_controls INTEGER NOT NULL,
            normally_in_service BOOLEAN,
            in_service BOOLEAN,
            base_voltage_mrid TEXT NULL,
            normal_open INTEGER NOT_NULL,
            open INTEGER NOT_NULL
        );""",
        "CREATE UNIQUE INDEX load_break_switches_mrid ON load_break_switches (mrid);",
        "CREATE INDEX load_break_switches_name ON load_break_switches (name);"
    )
}
