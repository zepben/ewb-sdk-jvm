/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.common.translator

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
