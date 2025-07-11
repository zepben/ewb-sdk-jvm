/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.extensions.iec61970.base.wires

import com.zepben.ewb.cim.extensions.ZBEX


/**
 * [ZBEX]
 * Transformer cooling types.
 */
@ZBEX
enum class TransformerCoolingType {

    /**
     * [ZBEX] Default
     */
    @ZBEX
    UNKNOWN,

    /**
     * [ZBEX] Oil natural, air natural
     */
    @ZBEX
    ONAN,

    /**
     * [ZBEX] Oil natural, air forced
     */
    @ZBEX
    ONAF,

    /**
     * [ZBEX] Oil forced, air forced
     */
    @ZBEX
    OFAF,

    /**
     * [ZBEX] Oil forced, water forced
     */
    @ZBEX
    OFWF,

    /**
     * [ZBEX] Oil directed, air forced
     */
    @ZBEX
    ODAF,

    /**
     * [ZBEX] Non-mineral oil natural, air natural
     */
    @ZBEX
    KNAN,

    /**
     * [ZBEX] Non-mineral oil natural, air forced
     */
    @ZBEX
    KNAF,

    /**
     * [ZBEX] Non-mineral oil forced, air forced
     */
    @ZBEX
    KFAF,

    /**
     * [ZBEX] Non-mineral oil forced, water forced
     */
    @ZBEX
    KFWF,

    /**
     * [ZBEX] Non-mineral oil directed, air forced
     */
    @ZBEX
    KDAF

}
