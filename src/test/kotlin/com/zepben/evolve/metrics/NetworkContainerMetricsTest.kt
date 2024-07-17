/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.metrics

import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test

internal class NetworkContainerMetricsTest {

    @Test
    internal fun increaseMissing() {
        val ncMetrics = mutableMapOf<String, Double>()
        MatcherAssert.assertThat(ncMetrics.increase("a"), Matchers.equalTo(1.0))
        MatcherAssert.assertThat(ncMetrics.increase("b", 2.5), Matchers.equalTo(2.5))
        MatcherAssert.assertThat(ncMetrics.increase("c", 0), Matchers.equalTo(0.0))
        MatcherAssert.assertThat(ncMetrics.increase("d", -3.3), Matchers.equalTo(-3.3))
        MatcherAssert.assertThat(ncMetrics, Matchers.equalTo(mapOf("a" to 1.0, "b" to 2.5, "c" to 0.0, "d" to -3.3)))
    }

    @Test
    internal fun increaseExisting() {
        val ncMetrics = mutableMapOf("a" to 1.0)
        MatcherAssert.assertThat(ncMetrics.increase("a"), Matchers.equalTo(2.0))
        MatcherAssert.assertThat(ncMetrics, Matchers.equalTo(mapOf("a" to 2.0)))
        MatcherAssert.assertThat(ncMetrics.increase("a", -2), Matchers.equalTo(0.0))
        MatcherAssert.assertThat(ncMetrics, Matchers.equalTo(mapOf("a" to 0.0)))
        MatcherAssert.assertThat(ncMetrics.increase("a", 1.5), Matchers.equalTo(1.5))
        MatcherAssert.assertThat(ncMetrics, Matchers.equalTo(mapOf("a" to 1.5)))
    }

    @Test
    internal fun set() {
        val ncMetrics = mutableMapOf("a" to 1.5)
        ncMetrics["a"] = 3
        ncMetrics["b"] = 5
        MatcherAssert.assertThat(ncMetrics, Matchers.equalTo(mapOf("a" to 3.0, "b" to 5.0)))
    }

}
