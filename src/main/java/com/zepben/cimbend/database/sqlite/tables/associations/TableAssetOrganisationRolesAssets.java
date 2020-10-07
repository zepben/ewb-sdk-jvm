/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.tables.associations;

import com.zepben.annotations.EverythingIsNonnullByDefault;
import com.zepben.cimbend.database.Column;
import com.zepben.cimbend.database.sqlite.tables.SqliteTable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.zepben.cimbend.database.Column.Nullable.NOT_NULL;

@EverythingIsNonnullByDefault
public class TableAssetOrganisationRolesAssets extends SqliteTable {

    public final Column ASSET_ORGANISATION_ROLE_MRID = new Column(++columnIndex, "asset_organisation_role_mrid", "TEXT", NOT_NULL);
    public final Column ASSET_MRID = new Column(++columnIndex, "asset_mrid", "TEXT", NOT_NULL);

    @Override
    public String name() {
        return "asset_organisation_roles_assets";
    }

    @Override
    public List<List<Column>> uniqueIndexColumns() {
        List<List<Column>> cols = super.uniqueIndexColumns();
        cols.add(Arrays.asList(ASSET_ORGANISATION_ROLE_MRID, ASSET_MRID));
        return cols;
    }

    @Override
    public List<List<Column>> nonUniqueIndexColumns() {
        List<List<Column>> cols = super.nonUniqueIndexColumns();
        cols.add(Collections.singletonList(ASSET_ORGANISATION_ROLE_MRID));
        cols.add(Collections.singletonList(ASSET_MRID));
        return cols;
    }

    @Override
    protected Class<?> getTableClass() {
        return TableAssetOrganisationRolesAssets.class;
    }

    @Override
    protected Object getTableClassInstance() {
        return this;
    }

}
