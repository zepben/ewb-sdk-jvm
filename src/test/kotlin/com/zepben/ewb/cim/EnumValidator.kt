/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim

import com.google.protobuf.ProtocolMessageEnum
import com.zepben.ewb.services.common.translator.EnumMapper
import com.zepben.testutils.exception.ExpectException.Companion.expect
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import kotlin.enums.EnumEntries

internal inline fun <reified T : Enum<T>, reified U : Enum<U>> validateEnum(cimEnum: EnumEntries<T>, pbEnum: EnumEntries<U>) {
    System.err.println("cim ${cimEnum.toTypedArray()}")
    System.err.println("   vs")
    System.err.println("pb ${pbEnum.toTypedArray()}'")

    assertThat(cimEnum, hasSize(pbEnum.size - 1))

    val pbUnrecognized = pbEnum[cimEnum.size]
    assertThat(pbUnrecognized.name, equalTo("UNRECOGNIZED"))
    assertThat(pbUnrecognized.ordinal, equalTo(cimEnum.size))
    expect { (pbUnrecognized as ProtocolMessageEnum).number }
        .toThrow<IllegalArgumentException>()
        .withMessage("Can't get the number of an unknown enum value.")

    val mapper = EnumMapper(cimEnum, pbEnum)

    cimEnum.forEach { cim ->
        val pb = pbEnum[cim.ordinal]
        assertThat(
            "invalid value mapping for ${cim.ordinal}: cim '${cim.name}' vs pb '${pb.name}'",
            pb.name.uppercase().replace("_", ""),
            endsWith(cim.name.uppercase().replace("_", ""))
        )

        assertThat(mapper.toCim(mapper.toPb(cim)), equalTo(cim))
        assertThat(mapper.toPb(mapper.toCim(pb)), equalTo(pb))
    }

}
