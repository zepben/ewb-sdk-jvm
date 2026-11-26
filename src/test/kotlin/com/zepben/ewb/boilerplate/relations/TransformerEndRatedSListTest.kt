/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.boilerplate.relations

import com.zepben.ewb.cim.extensions.iec61970.base.wires.TransformerCoolingType
import com.zepben.ewb.cim.extensions.iec61970.base.wires.TransformerEndRatedS
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class TransformerEndRatedSListTest {

    companion object {
        @JvmField
        @RegisterExtension
        val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()
    }

    @Test
    internal fun `add supports default and explicit cooling types`() {
        var backing: MutableList<TransformerEndRatedS>? = null
        val ratings = TransformerEndRatedSList({ backing }, { backing = it })

        assertThat(ratings.add(100), equalTo(true))
        assertThat(ratings.add(200, TransformerCoolingType.KNAF), equalTo(true))
        assertThat(ratings.get(TransformerCoolingType.UNKNOWN)?.ratedS, equalTo(100))
        assertThat(ratings.get(TransformerCoolingType.KNAF)?.ratedS, equalTo(200))
    }

    @Test
    internal fun `getByCoolingType returns null when no rating matches`() {
        var backing: MutableList<TransformerEndRatedS>? = null
        val ratings = TransformerEndRatedSList({ backing }, { backing = it })
        ratings.add(100, TransformerCoolingType.KFWF)

        assertThat(ratings.get(TransformerCoolingType.KNAF), nullValue())
    }

    @Test
    internal fun `removeByCoolingType returns and removes a match or returns null`() {
        var backing: MutableList<TransformerEndRatedS>? = null
        val ratings = TransformerEndRatedSList({ backing }, { backing = it })
        ratings.add(100, TransformerCoolingType.KFWF)
        ratings.add(200, TransformerCoolingType.KNAF)

        val removed = ratings.removeByCoolingType(TransformerCoolingType.KNAF)

        assertThat(removed?.ratedS, equalTo(200))
        assertThat(ratings.get(TransformerCoolingType.KNAF), nullValue())
        assertThat(ratings.removeByCoolingType(TransformerCoolingType.KNAF), nullValue())
        assertThat(ratings.get(TransformerCoolingType.KFWF), sameInstance(backing?.single()))
    }

}
