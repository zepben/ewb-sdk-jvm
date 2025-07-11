/*
 * Copyright 2023 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.common.meta

/**
 * Metadata for a service containing an identifier and the sources of data used to build the service.
 *
 * @property title An identifier for the service.
 * @property version The version of the service.
 * @property dataSources A list of the data sources used to build the service.
 */
data class ServiceInfo(
    val title: String,
    val version: String,
    val dataSources: List<DataSource>
)
