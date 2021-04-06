/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.common.translator

import com.google.protobuf.Timestamp
import com.zepben.protobuf.cim.iec61968.common.Document
import com.zepben.protobuf.cim.iec61968.common.Organisation
import com.zepben.protobuf.cim.iec61968.common.OrganisationRole
import com.zepben.protobuf.cim.iec61970.base.core.IdentifiedObject
import java.time.Instant


fun OrganisationRole.mRID(): String = io.mrid
fun Document.mRID(): String = io.mrid
fun Organisation.mRID(): String = io.mrid

fun IdentifiedObject.nameAndMRID(): String = "$mrid${if (name.isNotBlank()) " [$name]" else ""}"
fun Document.nameAndMRID(): String = io.nameAndMRID()

fun Timestamp.toInstant(): Instant? = if (seconds == 0L && nanos == 0) null else Instant.ofEpochSecond(seconds, nanos.toLong())
fun Instant?.toTimestamp(): Timestamp? {
    return this?.let {
        Timestamp.newBuilder().apply {
            seconds = epochSecond
            nanos = nano
        }.build()
    }
}
