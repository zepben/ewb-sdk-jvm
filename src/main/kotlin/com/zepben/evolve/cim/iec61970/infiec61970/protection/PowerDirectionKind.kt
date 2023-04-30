/*
 * Copyright 2023 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.infiec61970.protection


/**
 * The flow of power direction used by a ProtectionEquipment.
 *
 * @property UNKNOWN_DIRECTION Unknown power direction flow.
 * @property UNDIRECTED Power direction flow type is not specified.
 * @property FORWARD Power direction forward flow is used.
 * @property REVERSE Power direction reverse flow is used.
 */
enum class PowerDirectionKind {

    UNKNOWN_DIRECTION,
    UNDIRECTED,
    FORWARD,
    REVERSE

}