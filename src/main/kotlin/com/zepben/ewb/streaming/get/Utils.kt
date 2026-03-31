/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.streaming.get

import com.zepben.ewb.database.paths.VariantContents
import com.zepben.ewb.services.common.translator.EnumMapper
import com.zepben.ewb.services.common.translator.toTimestamp
import java.time.LocalDate
import java.time.ZoneOffset


val mapVariantContents: EnumMapper<VariantContents, com.zepben.protobuf.vc.VariantContents> = EnumMapper(VariantContents.entries, com.zepben.protobuf.vc.VariantContents.entries)


fun LocalDate?.toTimestamp(offset: ZoneOffset = ZoneOffset.UTC) = this?.atStartOfDay()?.toInstant(offset)?.toTimestamp()
