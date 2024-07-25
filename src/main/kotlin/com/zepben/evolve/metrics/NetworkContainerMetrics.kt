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
class NetworkContainerMetrics(private val map: MutableMap<String, Double> = mutableMapOf()) : MutableMap<String, Double> by map {

    /**
     * Increment the value of the metric named [key]. If no such metric exists, it is automatically created with an initial value of zero.
     */
    fun inc(key: String): NetworkContainerMetrics = plus(key, 1.0)

    /**
     * Increase the value of the metric named [key] by [amount]. If no such metric exists, it is automatically created with an initial value of zero.
     */
    fun plus(key: String, amount: Number = 1.0): NetworkContainerMetrics = apply { merge(key, amount.toDouble(), Double::plus) }

    /**
     * Set the value of the metric named [key] to [value].
     */
    operator fun set(key: String, value: Int) {
        this[key] = value.toDouble()
    }

    // Make this work as the map.
    override fun hashCode(): Int = map.hashCode()
    override fun equals(other: Any?): Boolean = map == other
    override fun toString(): String = map.toString()
}
