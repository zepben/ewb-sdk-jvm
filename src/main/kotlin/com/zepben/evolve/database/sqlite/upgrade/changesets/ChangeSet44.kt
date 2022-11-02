/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.upgrade.changesets

import com.zepben.evolve.database.sqlite.upgrade.ChangeSet

internal fun changeSet44() = ChangeSet(44) {
    listOf(
        """
        CREATE TABLE current_transformer_info (
            accuracy_class TEXT NULL,
            accuracy_limit NUMBER NULL,
            core_count INTEGER NULL,
            ct_class TEXT NULL,
            knee_point_voltage INTEGER NULL,
            max_ratio_denominator NUMBER NULL,
            max_ratio_numerator NUMBER NULL,
            nominal_ratio_denominator NUMBER NULL,
            nominal_ratio_numerator NUMBER NULL,
            primary_ratio NUMBER NULL,
            rated_current INTEGER NULL,
            secondary_fls_rating INTEGER NULL,
            secondary_ratio NUMBER NULL,
            usage TEXT NULL
        );
        """.trimIndent(),
        """
        CREATE TABLE potential_transformer_info (
            accuracy_class TEXT NULL,
            nominal_ratio_denominator NUMBER NULL,
            nominal_ratio_numerator NUMBER NULL,
            primary_ratio NUMBER NULL,
            pt_class TEXT NULL,
            rated_voltage INTEGER NULL,
            secondary_ratio NUMBER NULL
        );
        """.trimIndent(),
        """
        CREATE TABLE current_transformers (
            core_burden INTEGER NULL
        );
        """.trimIndent(),
        """
        CREATE TABLE potential_transformers (
            type TEXT NOT NULL
        );
        """.trimIndent()
    )
}
