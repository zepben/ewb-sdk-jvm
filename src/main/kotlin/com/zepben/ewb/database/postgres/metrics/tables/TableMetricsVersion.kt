/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.postgres.metrics.tables

import com.zepben.ewb.database.postgres.common.PostgresTableVersion

/**
 * The `version` table in the metrics database.
 */
val tableMetricsVersion: PostgresTableVersion = PostgresTableVersion(1)
