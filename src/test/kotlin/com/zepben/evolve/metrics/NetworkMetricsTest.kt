/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.metrics

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.anEmptyMap
import org.junit.jupiter.api.Test

internal class NetworkMetricsTest {

    @Test
    internal fun defaultValue() {
        val metrics = NetworkMetrics()
        assertThat(metrics[TotalNetworkContainer], anEmptyMap())
    }

}
