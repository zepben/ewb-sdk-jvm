/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.generators

import com.zepben.ewb.database.sql.common.tables.Column

/**
 * Format the SQL statements using Sqlite strings.
 */
object SqliteGenerator : SqlGenerator(Column.Type::sqlite, Column.Nullable::sqlite)
