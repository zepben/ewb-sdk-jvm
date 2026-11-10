/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.boilerplate.collections.interfaces

import com.zepben.ewb.cim.iec61970.base.core.Identifiable

interface MridCollection<T : Identifiable> : ArcCollection<T> {

    /** Returns the element with [mRID], or `null` when it is not present. */
    fun getByMrid(mRID: String): T?

    operator fun get(mRID: String): T?

}
