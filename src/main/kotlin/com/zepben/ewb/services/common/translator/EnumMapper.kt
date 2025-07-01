/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.common.translator

import kotlin.enums.EnumEntries

/**
 * A class for mapping between CIM and protobuf variants of the same Enum.
 */
internal class EnumMapper<TCimEnum : Enum<TCimEnum>, TPbEnum : Enum<TPbEnum>>(
    cimEnumEntries: EnumEntries<TCimEnum>,
    pbEnumEntries: EnumEntries<TPbEnum>,
) {

    private val cimToProto: Map<TCimEnum, TPbEnum>
    private val protoToCim: Map<TPbEnum, TCimEnum>

    init {
        val pbCommonKey = pbEnumEntries.commonPrefix

        val cimByKey = cimEnumEntries.associateBy { it.extractKeyFromCim() }
        val pbByKey = pbEnumEntries.withoutUnrecognized.associateBy { it.extractKeyFromPb(pbCommonKey) }

        cimToProto = cimByKey.entries.associateBy({ (_, cim) -> cim }) { (key, cim) ->
            requireNotNull(pbByKey[key]) { "$cim: CIM key '$key' wasn't found in the protobuf enum mappings $pbByKey" }
        }

        protoToCim = pbByKey.entries.associateBy({ (_, pb) -> pb }) { (key, pb) ->
            requireNotNull(cimByKey[key]) { "$pb: Protobuf key '$key' wasn't found in the CIM enum mappings $cimByKey" }
        }
    }

    /**
     * Convert the CIM enum value to the equivalent protobuf variant.
     */
    fun toPb(cim: TCimEnum): TPbEnum = requireNotNull(cimToProto[cim])

    /**
     * Convert the protobuf enum value to the equivalent CIM variant.
     */
    fun toCim(pb: TPbEnum): TCimEnum = requireNotNull(protoToCim[pb])

    private fun String.extractKey() = uppercase().replace("_", "")
    private fun Enum<*>.extractKeyFromCim() = name.extractKey()
    private fun Enum<*>.extractKeyFromPb(pbCommonKey: String) = name.removePrefix(pbCommonKey).extractKey()

    /**
     * Find the common starting for the protobuf enum.
     */
    private val <T : Enum<T>> EnumEntries<T>.commonPrefix: String
        get() = withoutUnrecognized.fold(first().name) { prefix, next -> prefix.commonPrefixWith(next.name) }

    /**
     * We drop the last entry as it is always "UNRECOGNIZED", which is added by protobuf itself, and doesn't have the prefix.
     */
    private val <T : Enum<T>> EnumEntries<T>.withoutUnrecognized: List<T>
        get() = dropLast(1)

}
