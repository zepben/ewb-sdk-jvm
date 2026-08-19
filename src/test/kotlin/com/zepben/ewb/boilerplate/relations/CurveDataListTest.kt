/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.boilerplate.relations

import com.zepben.ewb.cim.iec61970.base.core.CurveData
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class CurveDataListTest {

    companion object {
        @JvmField
        @RegisterExtension
        val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()
    }

    @Test
    internal fun `get returns the matching data or null`() {
        val first = CurveData(1.0f, 10.0f)
        val second = CurveData(2.0f, 20.0f)
        var backing: MutableList<CurveData>? = mutableListOf(first, second)
        val data = CurveDataList({ backing }, { backing = it })

        assertThat(data.get(2.0f), sameInstance(second))
        assertThat(data.get(3.0f), nullValue())
    }

    @Test
    internal fun `removeAt removes the matching data and reports missing data`() {
        val first = CurveData(1.0f, 10.0f)
        val second = CurveData(2.0f, 20.0f)
        var backing: MutableList<CurveData>? = mutableListOf(first, second)
        val data = CurveDataList({ backing }, { backing = it })

        assertThat(data.removeAt(2.0f), equalTo(true))
        assertThat(backing, contains(first))
        assertThat(data.removeAt(2.0f), equalTo(false))
    }

}
