/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.upgrade

internal fun changeSet17() = ChangeSet(17) {
    listOf(
        "ALTER TABLE meters ADD location_mrid TEXT NULL",
        "ALTER TABLE poles ADD location_mrid TEXT NULL",
        "ALTER TABLE streetlights ADD location_mrid TEXT NULL"
    )
}
