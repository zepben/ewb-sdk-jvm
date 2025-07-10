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
        `rename PowerDirectionKind UNKNOWN_DIRECTION to UNKNOWN`,
        `rename RegulatingControlModeKind UNKNOWN_CONTROL_MODE to UNKNOWN`,
        `rename TransformerCoolingType UNKNOWN_COOLING_TYPE to UNKNOWN`,
        `rename WindingConnection UNKNOWN_WINDING to UNKNOWN`,
    )
)

// ###################
// # Network Changes #
// ###################

@Suppress("ObjectPropertyName")
private val `rename PowerDirectionKind UNKNOWN_DIRECTION to UNKNOWN` = Change(
    listOf(
        "UPDATE current_relays SET power_direction = 'UNKNOWN' WHERE power_direction = 'UNKNOWN_DIRECTION'",
        "UPDATE distance_relays SET power_direction = 'UNKNOWN' WHERE power_direction = 'UNKNOWN_DIRECTION'",
        "UPDATE voltage_relays SET power_direction = 'UNKNOWN' WHERE power_direction = 'UNKNOWN_DIRECTION'",
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

@Suppress("ObjectPropertyName")
private val `rename RegulatingControlModeKind UNKNOWN_CONTROL_MODE to UNKNOWN` = Change(
    listOf(
        "UPDATE battery_controls SET mode = 'UNKNOWN' WHERE mode = 'UNKNOWN_CONTROL_MODE'",
        "UPDATE tap_changer_controls SET mode = 'UNKNOWN' WHERE mode = 'UNKNOWN_CONTROL_MODE'",
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

@Suppress("ObjectPropertyName")
private val `rename TransformerCoolingType UNKNOWN_COOLING_TYPE to UNKNOWN` = Change(
    listOf(
        "UPDATE power_transformer_end_ratings SET cooling_type = 'UNKNOWN' WHERE cooling_type = 'UNKNOWN_COOLING_TYPE'",
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

@Suppress("ObjectPropertyName")
private val `rename WindingConnection UNKNOWN_WINDING to UNKNOWN` = Change(
    listOf(
        "UPDATE power_transformer_ends SET connection_kind = 'UNKNOWN' WHERE connection_kind = 'UNKNOWN_WINDING'",
        "UPDATE transformer_end_info SET connection_kind = 'UNKNOWN' WHERE connection_kind = 'UNKNOWN_WINDING'",
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)
