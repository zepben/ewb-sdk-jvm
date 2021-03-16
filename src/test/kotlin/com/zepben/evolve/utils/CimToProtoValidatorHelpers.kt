/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.utils

import com.google.protobuf.ProtocolStringList
import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*

internal fun validateMRID(pb: String, mrid: String?) {
    mrid?.let { assertThat(pb, equalTo(it)) } ?: assertThat(pb, emptyString())
}

internal fun validateMRID(pb: String, cim: IdentifiedObject?) {
    validateMRID(pb, cim?.mRID)
}

internal fun validateMRIDList(pb: ProtocolStringList, cim: Collection<IdentifiedObject>) {
    assertThat(pb.size, equalTo(cim.size))
    if (cim.isNotEmpty())
        assertThat(pb, containsInAnyOrder(*cim.stream().map { it.mRID }.toArray()))
}

internal fun validateMRIDList(pb: ProtocolStringList, cim: List<IdentifiedObject>) {
    assertThat(pb.size, equalTo(cim.size))
    if (cim.isNotEmpty())
        assertThat(pb, contains(*cim.stream().map { it.mRID }.toArray()))
}
