/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql

/**
 * Represents a column in a database table.
 *
 * @property queryIndex Index of the column in the table. This should range from 1 through N, where N is the number of columns in the table.
 * @property name Name of the column in the table.
 * @property type The data type of column. Supported data types depend on the implementation of SQL of being targeted (e.g. Postgres).
 * @property nullable How the nullability of the column is specified when creating the table.
 */
class Column @Deprecated("Use the `Type` variant of the constructor instead.") internal constructor(
    val queryIndex: Int,
    val name: String,
    val type: String,
    val nullable: Nullable = Nullable.NONE
) {

    @Suppress("DEPRECATION")
    internal constructor(
        queryIndex: Int,
        name: String,
        type: Type,
        nullable: Nullable = Nullable.NONE
    ) : this(queryIndex, name, type.sqlite, nullable)

    /**
     * Ways of specifying whether a column is nullable.
     */
    enum class Nullable {
        /**
         * Nullability is left unspecified, which should default to nullable in every ANSI-compliant implementation of SQL.
         */
        NONE,

        /**
         * Column is specified with the NOT NULL constraint.
         */
        NOT_NULL,

        /**
         * Column is explicitly nullable via the NULL constraint.
         */
        NULL
    }

    /**
     * Ways of specifying the columns data type.
     *
     * @property sqlite The type definition for the column when creating it in Sqlite.
     */
    enum class Type(val sqlite: String) {
        /**
         * The column stores a string.
         */
        STRING(sqlite = "TEXT"),

        /**
         * The column stores an integer.
         */
        INTEGER(sqlite = "INTEGER"),

        /**
         * The column stores a double.
         */
        DOUBLE(sqlite = "NUMBER"),

        /**
         * The column stores a boolean.
         */
        BOOLEAN(sqlite = "BOOLEAN"),
    }

    init {
        require(queryIndex > 0) { "You must use a positive query index." }
    }

}
