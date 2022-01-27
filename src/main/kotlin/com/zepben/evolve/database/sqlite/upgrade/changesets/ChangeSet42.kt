/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.upgrade.changesets

import com.zepben.evolve.database.sqlite.upgrade.ChangeSet

internal fun changeSet42() = ChangeSet(42) {
    listOf(
        *`Add energy source fields`,
        *`Set energy source is_external_grid`
    )
}

@Suppress("ObjectPropertyName")
private val `Add energy source fields` = arrayOf(
    "ALTER TABLE energy_sources ADD is_external_grid BOOLEAN NOT NULL DEFAULT false",
    "ALTER TABLE energy_sources ADD r_min NUMBER NULL",
    "ALTER TABLE energy_sources ADD rn_min NUMBER NULL",
    "ALTER TABLE energy_sources ADD r0_min NUMBER NULL",
    "ALTER TABLE energy_sources ADD x_min NUMBER NULL",
    "ALTER TABLE energy_sources ADD xn_min NUMBER NULL",
    "ALTER TABLE energy_sources ADD x0_min NUMBER NULL",
    "ALTER TABLE energy_sources ADD r_max NUMBER NULL",
    "ALTER TABLE energy_sources ADD rn_max NUMBER NULL",
    "ALTER TABLE energy_sources ADD r0_max NUMBER NULL",
    "ALTER TABLE energy_sources ADD x_max NUMBER NULL",
    "ALTER TABLE energy_sources ADD xn_max NUMBER NULL",
    "ALTER TABLE energy_sources ADD x0_max NUMBER NULL",
)

@Suppress("ObjectPropertyName")
private val `Set energy source is_external_grid` = arrayOf(
    """
    UPDATE
        energy_sources
    SET
        is_external_grid = true
    WHERE
        mrid in (select energy_source_mrid from energy_source_phases)
    """
)
