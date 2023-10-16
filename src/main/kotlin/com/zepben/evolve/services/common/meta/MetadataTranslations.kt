/*
 * Copyright 2023 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.common.meta

import com.google.protobuf.Timestamp
import com.zepben.protobuf.metadata.GetMetadataResponse
import com.zepben.protobuf.metadata.DataSource as PBDataSource
import com.zepben.protobuf.metadata.ServiceInfo as PBServiceInfo
import java.time.Instant

fun PBDataSource.fromPb(): DataSource =
    DataSource(
        source,
        version,
        Instant.ofEpochSecond(timestamp.seconds, timestamp.nanos.toLong())
    )

fun DataSource.toPb(): PBDataSource =
    PBDataSource.newBuilder()
        .setSource(source)
        .setVersion(version)
        .setTimestamp(
            Timestamp.newBuilder()
                .setSeconds(timestamp.epochSecond)
                .setNanos(timestamp.nano)
        )
        .build()

fun PBServiceInfo.fromPb(): ServiceInfo =
    ServiceInfo(
        title,
        version,
        dataSourcesList.map { it.fromPb() }
    )

fun ServiceInfo.toPb(): PBServiceInfo =
    PBServiceInfo.newBuilder()
        .setTitle(title)
        .setVersion(version)
        .addAllDataSources(dataSources.map { it.toPb() })
        .build()
