/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
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
