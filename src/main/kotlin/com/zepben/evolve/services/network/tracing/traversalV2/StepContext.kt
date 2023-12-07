/*
 * Copyright 2023 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.traversalV2

class StepContext(
    val isStartItem: Boolean,
    val stepNumber: Int = 0,
    private var customValues: MutableMap<String, Any?>? = null
) {
    var isStopping: Boolean = false
        internal set

    fun setValue(key: String, value: Any?) {
        customValues = customValues ?: mutableMapOf()
        customValues!![key] = value
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getValue(key: String): T? {
        return customValues?.get(key) as? T?
    }
}
