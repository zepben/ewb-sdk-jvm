/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.common.translator

import com.google.protobuf.Timestamp
import com.zepben.protobuf.cim.iec61968.common.Document
import com.zepben.protobuf.cim.iec61968.common.Organisation
import com.zepben.protobuf.cim.iec61968.common.OrganisationRole
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset


/**
 * Extract the mRID from the composed classes.
 */
fun OrganisationRole.mRID(): String = io.mrid

/**
 * Extract the mRID from the composed classes.
 */
fun Document.mRID(): String = io.mrid

/**
 * Extract the mRID from the composed classes.
 */
fun Organisation.mRID(): String = io.mrid

internal fun Timestamp.toInstant(): Instant = Instant.ofEpochSecond(seconds, nanos.toLong())
internal fun Instant.toTimestamp(): Timestamp {
    return Timestamp.newBuilder().apply {
        seconds = epochSecond
        nanos = nano
    }.build()
}

internal fun Timestamp.toLocalDateTime(): LocalDateTime = LocalDateTime.ofInstant(toInstant(), ZoneOffset.UTC)
internal fun LocalDateTime.toTimestamp(): Timestamp = toInstant(ZoneOffset.UTC).toTimestamp()
