/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.tables.associations

import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NOT_NULL
import com.zepben.evolve.database.sqlite.tables.SqliteTable

@Suppress("PropertyName")
class TableAssetOrganisationRolesAssets : SqliteTable() {

    val ASSET_ORGANISATION_ROLE_MRID: Column =
        Column(++columnIndex, "asset_organisation_role_mrid", "TEXT", NOT_NULL)

    val ASSET_MRID: Column = Column(++columnIndex, "asset_mrid", "TEXT", NOT_NULL)

    override fun name(): String {
        return "asset_organisation_roles_assets"
    }

    override fun uniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.uniqueIndexColumns()

        cols.add(listOf(ASSET_ORGANISATION_ROLE_MRID, ASSET_MRID))

        return cols
    }

    override fun nonUniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.nonUniqueIndexColumns()

        cols.add(listOf(ASSET_ORGANISATION_ROLE_MRID))
        cols.add(listOf(ASSET_MRID))

        return cols
    }

    override val tableClass: Class<TableAssetOrganisationRolesAssets> = this.javaClass
    override val tableClassInstance: TableAssetOrganisationRolesAssets = this

}
