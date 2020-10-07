/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.tables.iec61968.common;

import com.zepben.annotations.EverythingIsNonnullByDefault;
import com.zepben.cimbend.database.Column;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.zepben.cimbend.database.Column.Nullable.NOT_NULL;

@EverythingIsNonnullByDefault
public class TableLocationStreetAddresses extends TableStreetAddresses {

    public final Column LOCATION_MRID = new Column(++columnIndex, "location_mrid", "TEXT", NOT_NULL);
    public final Column ADDRESS_FIELD = new Column(++columnIndex, "address_field", "TEXT", NOT_NULL);

    @Override
    public String name() {
        return "location_street_addresses";
    }

    @Override
    public List<List<Column>> uniqueIndexColumns() {
        List<List<Column>> cols = super.uniqueIndexColumns();
        cols.add(Arrays.asList(LOCATION_MRID, ADDRESS_FIELD));
        return super.uniqueIndexColumns();
    }

    @Override
    public List<List<Column>> nonUniqueIndexColumns() {
        List<List<Column>> cols = super.nonUniqueIndexColumns();
        cols.add(Collections.singletonList(LOCATION_MRID));
        return cols;
    }

    @Override
    protected Class<?> getTableClass() {
        return TableLocationStreetAddresses.class;
    }

    @Override
    protected Object getTableClassInstance() {
        return this;
    }

}
