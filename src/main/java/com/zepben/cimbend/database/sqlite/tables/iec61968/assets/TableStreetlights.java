/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.tables.iec61968.assets;

import com.zepben.annotations.EverythingIsNonnullByDefault;
import com.zepben.cimbend.database.Column;

import static com.zepben.cimbend.database.Column.Nullable.NOT_NULL;
import static com.zepben.cimbend.database.Column.Nullable.NULL;

@EverythingIsNonnullByDefault
public class TableStreetlights extends TableAssets {

    public final Column POLE_MRID = new Column(++columnIndex, "pole_mrid", "TEXT", NULL);

    public final Column LAMP_KIND = new Column(++columnIndex, "lamp_kind", "TEXT", NOT_NULL);

    public final Column LIGHT_RATING = new Column(++columnIndex, "light_rating", "NUMBER", NOT_NULL);


    @Override
    public String name() {
        return "streetlights";
    }

    @Override
    protected Class<?> getTableClass() {
        return TableStreetlights.class;
    }

    @Override
    protected Object getTableClassInstance() {
        return this;
    }

}
