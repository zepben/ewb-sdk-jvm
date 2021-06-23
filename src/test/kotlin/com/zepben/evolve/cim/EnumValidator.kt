/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim

import com.google.protobuf.ProtocolMessageEnum
import com.zepben.testutils.exception.ExpectException.expect
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

fun <T : Enum<T>, U : Enum<U>> validateEnum(cimEnum: Array<T>, pbEnum: Array<U>) {
    System.err.println("cim ${cimEnum.asList()}")
    System.err.println("   vs")
    System.err.println("pb ${pbEnum.asList()}'")

    assertThat(cimEnum.size, equalTo(pbEnum.size - 1))

    val pbUnrecognized = pbEnum[cimEnum.size]
    assertThat(pbUnrecognized.name, equalTo("UNRECOGNIZED"))
    assertThat(pbUnrecognized.ordinal, equalTo(cimEnum.size))
    expect { (pbUnrecognized as ProtocolMessageEnum).number }
        .toThrow(IllegalArgumentException::class.java)
        .withMessage("Can't get the number of an unknown enum value.")

    cimEnum.forEach { cim ->
        val pb = pbEnum[cim.ordinal]
        assertThat("invalid value mapping for ${cim.ordinal}: cim '${cim.name}' vs pb '${pb.name}'", pb.name, equalTo(cim.name))
    }

}
