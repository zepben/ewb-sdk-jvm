/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.postgres.common

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.TableVersion

/**
 * Code representation of the `version` table in a Postgres database.
 *
 * @property supportedVersion The supported schema version.
 */
class PostgresTableVersion(override val supportedVersion: Int) : TableVersion, PostgresTable() {

    override val VERSION: Column = Column(++columnIndex, "version", "TEXT", NOT_NULL)

    override val name: String = "version"

}
