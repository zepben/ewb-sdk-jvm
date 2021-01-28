/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.upgrade

internal fun changeSet26() = ChangeSet(26) {
    listOf(
        """CREATE TABLE busbar_sections(
            mrid TEXT NOT NULL,
            name TEXT NOT NULL,
            description TEXT NOT NULL,
            num_diagram_objects INTEGER NOT NULL,
            location_mrid TEXT NULL,
            num_controls INTEGER NOT NULL,
            normally_in_service BOOLEAN,
            in_service BOOLEAN,
            base_voltage_mrid TEXT NULL
        );""",
        "CREATE UNIQUE INDEX busbar_sections_mrid ON busbar_sections (mrid);",
        "CREATE INDEX busbar_sections_name ON busbar_sections (name);"
    )
}
