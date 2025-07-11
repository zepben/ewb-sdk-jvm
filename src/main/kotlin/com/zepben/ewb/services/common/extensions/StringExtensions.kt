/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.common.extensions

fun String?.emptyIfNull(): String = this ?: ""

/**
 * Most database drivers perform `new String()` on seeing an empty string, making each one take an additional reference.
 * This extension function stops that from being a problem.
 */
fun String.internEmpty(): String = ifEmpty { "" }
