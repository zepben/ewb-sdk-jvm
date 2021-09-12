/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.upgrade.changesets

import com.zepben.evolve.database.sqlite.upgrade.ChangeSet

internal fun changeSet37() = ChangeSet(37) {
    listOf(
        *`Add shunt compensator info link`,
    )
}

@Suppress("ObjectPropertyName")
private val `Add shunt compensator info link` = arrayOf(
    "ALTER TABLE linear_shunt_compensators ADD shunt_compensator_info_mrid TEXT NULL",
)
