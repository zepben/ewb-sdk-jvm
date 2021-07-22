/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network

/**
 * @property r Positive sequence series resistance.
 * @property r0 Zero sequence series resistance.
 * @property x Positive sequence series reactance.
 * @property x0 Zero sequence series reactance.
 */
data class ResistanceReactance(
    val r: Double? = null,
    val x: Double? = null,
    val r0: Double? = null,
    val x0: Double? = null
) {

    fun isEmpty(): Boolean = (r == null) && (x == null) && (r0 == null) && (x0 == null)

    fun isComplete(): Boolean = (r != null) && (x != null) && (r0 != null) && (x0 != null)

}

internal fun ResistanceReactance.mergeIfIncomplete(toMerge: () -> ResistanceReactance?): ResistanceReactance {
    return when {
        isComplete() -> this
        else -> toMerge()?.let { rr ->
            ResistanceReactance(
                r ?: rr.r,
                x ?: rr.x,
                r0 ?: rr.r0,
                x0 ?: rr.x0
            )
        } ?: this
    }
}
