/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.upgrade.changesets

import com.zepben.evolve.database.sqlite.upgrade.ChangeSet

internal fun changeSet33() = ChangeSet(33) {
    listOf(
        *`Change nullability of style field in diagram_objects table`,
    )
}

@Suppress("ObjectPropertyName")
private val `Change nullability of style field in diagram_objects table` = arrayOf(
    "ALTER TABLE diagram_objects RENAME TO diagram_objects_old",
    """
        CREATE TABLE diagram_objects (
            mrid TEXT NOT NULL,
            name TEXT NOT NULL,
            description TEXT NOT NULL,
            num_diagram_objects INTEGER NOT NULL,
            identified_object_mrid TEXT NULL,
            diagram_mrid TEXT NULL,
            style TEXT NULL,
            rotation NUMBER NOT NULL
        )
    """,
    """
    INSERT INTO diagram_objects
        SELECT mrid, name, description, num_diagram_objects, identified_object_mrid, diagram_mrid, style, rotation
        FROM diagram_objects_old
    """,
    "DROP TABLE diagram_objects_old",
    "CREATE UNIQUE INDEX diagram_objects_mrid ON diagram_objects (mrid)",
    "CREATE INDEX diagram_objects_name ON diagram_objects (name)",
    "CREATE INDEX diagram_objects_identified_object_mrid ON diagram_objects (identified_object_mrid)",
    "CREATE INDEX diagram_objects_diagram_mrid ON diagram_objects (diagram_mrid)",
)
