/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.extensions

import java.lang.reflect.Field


@Throws(NoSuchFieldException::class)
fun Class<*>.getFieldExt(fieldName: String): Field {
    return try {
        getDeclaredField(fieldName)
    } catch (e: NoSuchFieldException) {
        val superClass = superclass
        superClass?.getField(fieldName) ?: throw e
    }
}

