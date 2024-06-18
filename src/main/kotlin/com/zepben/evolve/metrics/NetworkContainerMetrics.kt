/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.metrics

/**
 * A map from metric names to their values.
 */
typealias NetworkContainerMetrics = MutableMap<String, Double>

/**
 * Increases a metric by name and delta. If no metric by that name is found, it defaults to 0 first before the addition of the delta.
 *
 * @param key The name of the metric to increase.
 * @param delta The amount to increase the metric by. This value may be negative.
 *
 * @return The new value of the metric.
 */
fun NetworkContainerMetrics.increase(key: String, delta: Number = 1.0): Double = compute(key) { _, n -> (n ?: 0.0) + delta.toDouble() }!!

/**
 * Helper function to set a metric to an integer value.
 *
 * @param key The name of the metric to set.
 * @param n The value to set the metric to.
 */
operator fun NetworkContainerMetrics.set(key: String, n: Int) { this[key] = n.toDouble() }
