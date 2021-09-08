/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.upgrade.changesets

import com.zepben.evolve.database.sqlite.upgrade.ChangeSet

internal fun changeSet36() = ChangeSet(36) {
    listOf(
        *`Add shunt compensator info table`
    )
}

@Suppress("ObjectPropertyName")
private val `Add shunt compensator info table` = arrayOf(
    """
    CREATE TABLE shunt_compensator_info (
        mrid TEXT NOT NULL, 
        name TEXT NOT NULL, 
        description TEXT NOT NULL, 
        num_diagram_objects INTEGER NOT NULL, 
        max_power_loss INTEGER NULL,
        rated_current INTEGER NULL,
        rated_reactive_power INTEGER NULL,
        rated_voltage INTEGER NULL
    )
    """,
    "CREATE UNIQUE INDEX shunt_compensator_info_mrid ON shunt_compensator_info (mrid)",
    "CREATE INDEX shunt_compensator_info_name ON shunt_compensator_info (name)"

)
