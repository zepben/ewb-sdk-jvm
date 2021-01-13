/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.upgrade

internal fun changeSet24() = ChangeSet(24) {
    listOf(
        "CREATE TABLE power_transformer_info(mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL)",
        "ALTER TABLE power_transformers ADD power_transformer_info_mrid TEXT"
    )
}
