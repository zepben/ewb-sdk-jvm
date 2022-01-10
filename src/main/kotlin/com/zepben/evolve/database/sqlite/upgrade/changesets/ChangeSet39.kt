/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.upgrade.changesets

import com.zepben.evolve.database.sqlite.upgrade.ChangeSet

internal fun changeSet39() = ChangeSet(39) {
    listOf(
        *`Add power transformer fields`,
        *`Set power transformer fields based on diagram objects`
    )
}

@Suppress("ObjectPropertyName")
private val `Add power transformer fields` = arrayOf(
    "ALTER TABLE power_transformers ADD construction_kind TEXT NOT NULL DEFAULT 'unknown'",
    "ALTER TABLE power_transformers ADD function TEXT NOT NULL DEFAULT 'other'"
)

@Suppress("ObjectPropertyName")
private val `Set power transformer fields based on diagram objects` = arrayOf(
    """
    UPDATE
        power_transformers
    SET
        function = 'distributionTransformer'
    WHERE
        mrid in (
            SELECT
                identified_object_mrid
            FROM
                diagram_objects
            WHERE
                style = 'DIST_TRANSFORMER'
        )
    """,
    """
    UPDATE
        power_transformers
    SET
        function = 'voltageRegulator'
    WHERE
        mrid in (
            SELECT
                identified_object_mrid
            FROM
                diagram_objects
            WHERE
                style = 'REVERSIBLE_REGULATOR'
                OR style = 'NON_REVERSIBLE_REGULATOR'
        )
    """,
    """
    UPDATE
        power_transformers
    SET
        function = 'isolationTransformer'
    WHERE
        mrid in (
            SELECT
                identified_object_mrid
            FROM
                diagram_objects
            WHERE
                style = 'ISO_TRANSFORMER'
        )
    """,
    """
    UPDATE
        power_transformers
    SET
        function = 'powerTransformer'
    WHERE
        mrid in (
            SELECT
                identified_object_mrid
            FROM
                diagram_objects
            WHERE
                style = 'ZONE_TRANSFORMER'
        )
    """
)
