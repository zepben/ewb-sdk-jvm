/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects

/**
 * Enum describing the different relationships two objects may have.
 */
@Suppress("EnumEntryName")
enum class DependencyKind {
    UNKNOWN,

    /**
     * The dependencies cannot exist together
     */
    mutuallyExclusive,

    /**
     * The dependencies must exist together
     */
    required,

}
