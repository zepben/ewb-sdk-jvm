/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.common.meta

import java.time.Instant

/***
 * Class containing metadata information about a data source.
 *
 * @property source the name of the data source.
 * @property version the version of the data source.
 * @property timestamp the date/time when the data source was added.
 */
data class DataSource(
    val source: String,
    val version: String,
    val timestamp: Instant = Instant.now()
)
