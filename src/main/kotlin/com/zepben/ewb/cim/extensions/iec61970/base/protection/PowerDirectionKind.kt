/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.extensions.iec61970.base.protection

import com.zepben.ewb.cim.extensions.ZBEX


/**
 * [ZBEX]
 * The flow of power direction used by a ProtectionEquipment.
 */
@ZBEX
enum class PowerDirectionKind {

    /**
     * [ZBEX] Unknown power direction flow.
     */
    @ZBEX
    UNKNOWN,

    /**
     * [ZBEX] Power direction flow type is not specified.
     */
    @ZBEX
    UNDIRECTED,

    /**
     * [ZBEX] Power direction forward flow is used.
     */
    @ZBEX
    FORWARD,

    /**
     * [ZBEX] Power direction reverse flow is used.
     */
    @ZBEX
    REVERSE

}
