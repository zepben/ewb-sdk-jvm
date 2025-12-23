/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables

import com.zepben.ewb.database.sqlite.common.SqliteTableVersion

/**
 * The `version` table in the CIM databases. Increment this when doing a schema update.
 */
val tableCimVersion: SqliteTableVersion = SqliteTableVersion(64)
