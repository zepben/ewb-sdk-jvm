/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.associations

/**
 * An enum representing the direction of energisation between a `Loop` and a `Substation`.
 */
enum class LoopSubstationRelationship {

    /**
     * The substation energises the loop.
     */
    SUBSTATION_ENERGIZES_LOOP,

    /**
     * The loop energises the substation.
     */
    LOOP_ENERGIZES_SUBSTATION,

}
