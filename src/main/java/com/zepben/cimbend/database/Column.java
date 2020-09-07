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
package com.zepben.cimbend.database;

import com.zepben.annotations.EverythingIsNonnullByDefault;

/**
 * Represents a column in a database table.
 */
@EverythingIsNonnullByDefault
public class Column {

    public enum Nullable {
        NONE, NOT_NULL, NULL;

        public String sqlString() {
            if (this == NULL) return "NULL";
            else if (this == NOT_NULL) return "NOT NULL";
            else return "";
        }
    }

    private final int queryIndex;
    private final String name;
    private final String type;
    private final Nullable nullable;

    public Column(int queryIndex, String name, String type) {
        this(queryIndex, name, type, Nullable.NONE);
    }

    public Column(int queryIndex, String name, String type, Nullable nullable) {

        if (queryIndex < 0)
            throw new IllegalArgumentException("You cannot use a negative query indexes.");

        this.queryIndex = queryIndex;
        this.name = name;
        this.type = type;
        this.nullable = nullable;
    }

    public int queryIndex() {
        return queryIndex;
    }

    public String name() {
        return name;
    }

    public String type() {
        return type;
    }

    public Nullable nullable() {
        return nullable;
    }

    public String sqlString() {
        return (name + " " + type + " " + nullable.sqlString()).trim();
    }

}
