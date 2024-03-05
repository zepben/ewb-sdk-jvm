/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.common.extensions

import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject
import java.util.*

fun <T> Collection<T>?.asUnmodifiable(): Collection<T> {
    return if (this == null) emptyList() else Collections.unmodifiableCollection(this)
}

fun <K, V> Map<K, V>?.asUnmodifiable(): Map<K, V> {
    return if (this == null) emptyMap() else Collections.unmodifiableMap(this)
}

fun <T> List<T>?.asUnmodifiable(): List<T> {
    return if (this == null) emptyList() else Collections.unmodifiableList(this)
}

fun <T> Set<T>?.asUnmodifiable(): Set<T> {
    return if (this == null) emptySet() else Collections.unmodifiableSet(this)
}

fun <T : IdentifiedObject> MutableCollection<T>?.safeRemove(it: T?): Boolean {
    return this?.remove(it) == true
}
