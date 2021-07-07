/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.upgrade.changesets

import com.zepben.evolve.database.sqlite.upgrade.ChangeSet

internal fun changeSet34() = ChangeSet(34) {
    listOf(
        *`Create equivalent branches table`,
    )
}

@Suppress("ObjectPropertyName")
private val `Create equivalent branches table` = arrayOf(
    """
    CREATE TABLE equivalent_branches (
        mrid TEXT NOT NULL,
        name TEXT NOT NULL,
        description TEXT NOT NULL,
        num_diagram_objects INTEGER NOT NULL,
        location_mrid TEXT NULL,
        num_controls INTEGER NOT NULL,
        normally_in_service BOOLEAN,
        in_service BOOLEAN,
        base_voltage_mrid TEXT NULL,
        negative_r12 NUMBER NULL,
        negative_r21 NUMBER NULL,
        negative_x12 NUMBER NULL,
        negative_x21 NUMBER NULL,
        positive_r12 NUMBER NULL,
        positive_r21 NUMBER NULL,
        positive_x12 NUMBER NULL,
        positive_x21 NUMBER NULL,
        r NUMBER NULL,
        r21 NUMBER NULL,
        x NUMBER NULL,
        x21 NUMBER NULL,
        zero_r12 NUMBER NULL,
        zero_r21 NUMBER NULL,
        zero_x12 NUMBER NULL,
        zero_x21 NUMBER NULL
    )
    """,
    "CREATE UNIQUE INDEX equivalent_branches_mrid ON equivalent_branches (mrid)",
    "CREATE INDEX equivalent_branches_name ON equivalent_branches (name)"
)
