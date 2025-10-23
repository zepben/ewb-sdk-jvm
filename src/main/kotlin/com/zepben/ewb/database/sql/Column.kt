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
class Column internal constructor(
    val queryIndex: Int,
    val name: String,
    val type: Type,
    val nullable: Nullable
) {

    /**
     * Ways of specifying whether a column is nullable.
     *
     * @property sqlite The nullability definition for the column when creating it in Sqlite.
     * @property postgres The nullability definition for the column when creating it in Postgres.
     */
    enum class Nullable(val sqlite: String, val postgres: String) {

        /**
         * Column is specified with the NOT NULL constraint.
         */
        NOT_NULL(sqlite = "NOT NULL", postgres = "NOT NULL"),

        /**
         * Column is explicitly nullable via the NULL constraint.
         */
        NULL(sqlite = "NULL", postgres = "NULL")

    }

    /**
     * Ways of specifying the columns data type.
     *
     * @property sqlite The type definition for the column when creating it in Sqlite.
     * @property postgres The type definition for the column when creating it in Postgres.
     */
    enum class Type(val sqlite: String, val postgres: String) {

        /**
         * The column stores a string.
         */
        STRING(sqlite = "TEXT", postgres = "TEXT"),

        /**
         * The column stores an integer.
         */
        INTEGER(sqlite = "INTEGER", postgres = "INTEGER"),

        /**
         * The column stores a double.
         */
        DOUBLE(sqlite = "NUMBER", postgres = "DOUBLE PRECISION"),

        /**
         * The column stores a boolean.
         */
        BOOLEAN(sqlite = "BOOLEAN", postgres = "BOOLEAN"),

        /**
         * The column stores a UUID.
         */
        UUID(sqlite = "TEXT", postgres = "UUID"),

        /**
         * The column stores a TIMESTAMP.
         */
        TIMESTAMP(sqlite = "TEXT", postgres = "TIMESTAMP"),

        /**
         * The column stores a BYTEA.
         */
        BYTES(sqlite = "BLOB", postgres = "BYTEA"),

    }

    init {
        require(queryIndex > 0) { "You must use a positive query index." }
    }

}
