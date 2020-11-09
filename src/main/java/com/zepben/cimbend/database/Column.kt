/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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
