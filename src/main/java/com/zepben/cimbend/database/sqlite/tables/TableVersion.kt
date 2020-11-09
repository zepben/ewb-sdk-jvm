/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.tables;

import com.zepben.annotations.EverythingIsNonnullByDefault;
import com.zepben.cimbend.database.Column;

import static com.zepben.cimbend.database.Column.Nullable.NOT_NULL;

@EverythingIsNonnullByDefault
public class TableVersion extends SqliteTable {

    public final int SUPPORTED_VERSION = 21;

    public final Column VERSION = new Column(++columnIndex, "version", "TEXT", NOT_NULL);

    @Override
    public String name() {
        return "version";
    }

    @Override
    protected Class<?> getTableClass() {
        return TableVersion.class;
    }

    @Override
    protected Object getTableClassInstance() {
        return this;
    }

}
