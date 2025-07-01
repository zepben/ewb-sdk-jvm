/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.common.meta

import com.zepben.ewb.services.common.extensions.asUnmodifiable

/***
 * Class containing metadata for the services written to a database.
 *
 * @property dataSources a [List] of [DataSource]s used to create the services.
 */
class MetadataCollection {

    val dataSources: List<DataSource> get() = _dataSources.asUnmodifiable()

    private val _dataSources = mutableListOf<DataSource>()

    /***
     * Add a data source to the metadata.
     *
     * @param dataSource the [DataSource] to add.
     */
    fun add(dataSource: DataSource): Boolean = _dataSources.add(dataSource)

}
