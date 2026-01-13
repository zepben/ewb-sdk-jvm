/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.associations

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.common.tables.SqlTable

/**
 * A class representing the association between AssetOrganisationRoles and Assets.
 *
 * @property ASSET_ORGANISATION_ROLE_MRID The mRID of AssetOrganisationRoles.
 * @property ASSET_MRID The mRID of Assets.
 */
@Suppress("PropertyName")
class TableAssetOrganisationRolesAssets : SqlTable() {

    val ASSET_ORGANISATION_ROLE_MRID: Column = Column(++columnIndex, "asset_organisation_role_mrid", Column.Type.STRING, NOT_NULL)
    val ASSET_MRID: Column = Column(++columnIndex, "asset_mrid", Column.Type.STRING, NOT_NULL)

    override val name: String = "asset_organisation_roles_assets"

    init {
        addUniqueIndexes(
            listOf(ASSET_ORGANISATION_ROLE_MRID, ASSET_MRID)
        )

        addNonUniqueIndexes(
            listOf(ASSET_ORGANISATION_ROLE_MRID),
            listOf(ASSET_MRID)
        )
    }

}
