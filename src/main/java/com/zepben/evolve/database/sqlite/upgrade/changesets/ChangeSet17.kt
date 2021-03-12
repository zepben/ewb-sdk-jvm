/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.upgrade.changesets

import com.zepben.evolve.database.sqlite.upgrade.ChangeSet

internal fun changeSet17() = ChangeSet(17) {
    listOf(
        "ALTER TABLE meters ADD location_mrid TEXT NULL",
        "ALTER TABLE poles ADD location_mrid TEXT NULL",
        "ALTER TABLE streetlights ADD location_mrid TEXT NULL"
    )
}
