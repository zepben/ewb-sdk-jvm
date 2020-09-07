/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.common.extensions

import com.zepben.cimbend.cim.iec61970.base.core.IdentifiedObject
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
