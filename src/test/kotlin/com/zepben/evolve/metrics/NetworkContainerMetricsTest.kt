/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.metrics

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class NetworkContainerMetricsTest {

    @Test
    internal fun plus() {
        val ncMetrics = NetworkContainerMetrics()
            .plus("a", 1)
            .plus("b", 2.5)
            .plus("c", 0)
            .plus("d", -3.3)
        assertThat(ncMetrics, equalTo(mapOf("a" to 1.0, "b" to 2.5, "c" to 0.0, "d" to -3.3)))
    }

    @Test
    internal fun plusExisting() {
        val ncMetrics = NetworkContainerMetrics(mutableMapOf("a" to 1.0)).plus("a", 1)
        assertThat(ncMetrics["a"], equalTo(2.0))
        ncMetrics.plus("a", -2)
        assertThat(ncMetrics["a"], equalTo(0.0))
        ncMetrics.plus("a", 1.5)
        assertThat(ncMetrics["a"], equalTo(1.5))
        assertThat(ncMetrics, equalTo(mapOf("a" to 1.5)))
    }

    @Test
    internal fun inc() {
        val ncMetrics = NetworkContainerMetrics().inc("a")

        assertThat(ncMetrics, equalTo(mapOf("a" to 1.0)))
    }

    @Test
    internal fun incExisting() {
        val ncMetrics = NetworkContainerMetrics(mutableMapOf("a" to 1.5)).inc("a")

        assertThat(ncMetrics, equalTo(mapOf("a" to 2.5)))
    }

    @Test
    internal fun set() {
        val ncMetrics = NetworkContainerMetrics(mutableMapOf("a" to 1.5))
        ncMetrics["a"] = 3
        ncMetrics["b"] = 5
        ncMetrics["c"] = 1.23
        assertThat(ncMetrics, equalTo(mapOf("a" to 3.0, "b" to 5.0, "c" to 1.23)))
    }

}
