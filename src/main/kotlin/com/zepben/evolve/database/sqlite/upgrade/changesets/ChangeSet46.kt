/*
 * Copyright 2023 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.upgrade.changesets

import com.zepben.evolve.database.sqlite.upgrade.ChangeSet

internal fun changeSet46() = ChangeSet(
    46,
    `Make potential_transformers type non-null`
)

@Suppress("ObjectPropertyName")
private val `Make potential_transformers type non-null` = listOf(
    """
    CREATE TABLE potential_transformers_new (
        mrid TEXT NOT NULL,
        name TEXT NOT NULL,
        description TEXT NOT NULL,
        num_diagram_objects INTEGER NOT NULL,
        location_mrid TEXT NULL,
        num_controls INTEGER NOT NULL,
        normally_in_service BOOLEAN,
        in_service BOOLEAN,
        terminal_mrid TEXT NULL,
        potential_transformer_info_mrid TEXT NULL,
        type TEXT NOT NULL
    );
    """.trimIndent(),
    """
    INSERT INTO potential_transformers_new
    SELECT 
        mrid,
        name,
        description,
        num_diagram_objects,
        location_mrid,
        num_controls,
        normally_in_service,
        in_service,
        terminal_mrid,
        potential_transformer_info_mrid,
        IFNULL(type, 'UNKNOWN')
    FROM potential_transformers;
    """.trimIndent(),
    "DROP TABLE potential_transformers;",
    "ALTER TABLE potential_transformers_new RENAME TO potential_transformers;",
    "CREATE UNIQUE INDEX potential_transformers_mrid ON potential_transformers (mrid);",
    "CREATE INDEX potential_transformers_name ON potential_transformers (name);"
)
