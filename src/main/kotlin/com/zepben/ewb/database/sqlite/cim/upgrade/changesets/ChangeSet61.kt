/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.upgrade.changesets

import com.zepben.ewb.database.paths.DatabaseType
import com.zepben.ewb.database.sqlite.cim.upgrade.Change
import com.zepben.ewb.database.sqlite.cim.upgrade.ChangeSet

internal fun changeSet60() = ChangeSet(
    60,
    listOf(
        // Network Change
        `Create directional current relay table`
    )
)

// ###################
// # Network Changes #
// ###################

@Suppress("ObjectPropertyName")
private val `Create directional current relay table` = Change(
    listOf(
        """CREATE TABLE directional_current_relays (
        directional_characteristic_angle NUMBER,
        polarizing_quantity_type TEXT,
        relay_element_phase TEXT,
        minimum_pickup_current NUMBER,
        current_limit_1 NUMBER,
        inverse_time_flaG BOOLEAN,
        time_delay_1 NUMBER,
            
        );""".trimIndent(),
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)
