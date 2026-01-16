/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.upgrade.changesets.network

import com.zepben.ewb.database.paths.DatabaseType
import com.zepben.ewb.database.sqlite.upgrade.changesets.ChangeSetValidator
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import java.sql.Statement

object ChangeSet59NetworkValidator : ChangeSetValidator(DatabaseType.NETWORK_MODEL, 59) {

    //
    // NOTE: In the validators we are only checking the columns that were actually changed.
    //

    override fun setUpStatements(): List<String> = listOf(
    )

    override fun populateStatements(): List<String> = listOf(
        "INSERT INTO assets_power_system_resources (asset_mrid, power_system_resource_mrid) VALUES ('asset_mrid', 'power_system_resource_mrid');",
    )

    override fun validateChanges(statement: Statement) {
        ensureAddedAssetsPowerSystemResources(statement)
        validateRows(
            statement, "SELECT * FROM assets_power_system_resources",
            { rs ->
                assertThat(rs.getString("asset_mrid"), equalTo("asset_mrid"))
                assertThat(rs.getString("power_system_resource_mrid"), equalTo("power_system_resource_mrid"))
            }
        )
    }

    override fun tearDownStatements(): List<String> =
        listOf(
            "DELETE FROM assets_power_system_resources;",
        )


    private fun ensureAddedAssetsPowerSystemResources(statement: Statement) {
        ensureTables(statement, "assets_power_system_resources")
        ensureIndexes(
            statement,
            "assets_power_system_resources_asset_mrid",
            "assets_power_system_resources_asset_mrid",
            "assets_power_system_resources_power_system_resource_mrid"
        )
    }

}
