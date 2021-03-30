/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.upgrade.changesets

import com.zepben.evolve.database.sqlite.upgrade.ChangeSet

internal fun changeSet16() = ChangeSet(16) {
    listOf(
        "CREATE TABLE poles(mrid TEXT NOT NULL, name TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL)",
        "CREATE TABLE streetlights(mrid TEXT NOT NULL, name TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, pole_mrid TEXT, lamp_kind TEXT NOT NULL, light_rating NUMBER NOT NULL)"
    )
}
