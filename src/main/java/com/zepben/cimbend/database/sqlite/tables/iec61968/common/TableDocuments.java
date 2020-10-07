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
import com.zepben.cimbend.database.sqlite.tables.iec61970.base.core.TableIdentifiedObjects;

import static com.zepben.cimbend.database.Column.Nullable.NOT_NULL;
import static com.zepben.cimbend.database.Column.Nullable.NULL;

@EverythingIsNonnullByDefault
public abstract class TableDocuments extends TableIdentifiedObjects {

    public final Column TITLE = new Column(++columnIndex, "title", "TEXT", NOT_NULL);
    public final Column CREATED_DATE_TIME = new Column(++columnIndex, "created_date_time", "TEXT", NULL);
    public final Column AUTHOR_NAME = new Column(++columnIndex, "author_name", "TEXT", NOT_NULL);
    public final Column TYPE = new Column(++columnIndex, "type", "TEXT", NOT_NULL);
    public final Column STATUS = new Column(++columnIndex, "status", "TEXT", NOT_NULL);
    public final Column COMMENT = new Column(++columnIndex, "comment", "TEXT", NOT_NULL);

}
