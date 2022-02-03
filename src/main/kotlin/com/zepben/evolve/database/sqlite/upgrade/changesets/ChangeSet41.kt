/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.upgrade.changesets

import com.zepben.evolve.database.sqlite.upgrade.ChangeSet

internal fun changeSet41() = ChangeSet(41) {
    listOf(
        *`Add location street address fields`
    )
}

@Suppress("ObjectPropertyName")
private val `Add location street address fields` = arrayOf(
    "ALTER TABLE location_street_addresses ADD po_box TEXT DEFAULT NULL",
    "ALTER TABLE location_street_addresses ADD building_name TEXT DEFAULT NULL",
    "ALTER TABLE location_street_addresses ADD floor_identification TEXT DEFAULT NULL",
    "ALTER TABLE location_street_addresses ADD name TEXT DEFAULT NULL",
    "ALTER TABLE location_street_addresses ADD number TEXT DEFAULT NULL",
    "ALTER TABLE location_street_addresses ADD suite_number TEXT DEFAULT NULL",
    "ALTER TABLE location_street_addresses ADD type TEXT DEFAULT NULL",
    "ALTER TABLE location_street_addresses ADD display_address TEXT DEFAULT NULL",
)
