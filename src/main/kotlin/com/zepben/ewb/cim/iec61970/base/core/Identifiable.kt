/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.core

/**
 * An interface that marks an object as identifiable.
 */
interface Identifiable {

    /**
     * The identifier for this object.
     */
    val mRID: String

    /**
     * Printable version of the object including its type, name and mRID.
     */
    fun typeNameAndMRID(): String

    /**
     * Printable version of the object including its name and mRID.
     */
    fun nameAndMRID(): String
}
