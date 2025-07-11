/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.assetinfo

/**
 * Kind of wire material.
 */
@Suppress("EnumEntryName")
enum class WireMaterialKind {

    /**
     * Unknown.
     */
    UNKNOWN,

    /**
     * Aluminum-alloy conductor steel reinforced.
     */
    aaac,

    /**
     * Aluminum conductor steel reinforced.
     */
    acsr,

    /**
     * Aluminum conductor steel reinforced, aluminumized steel core
     */
    acsrAz,

    /**
     * Aluminum wire.
     */
    aluminum,

    /**
     * Aluminum-alloy wire.
     */
    aluminumAlloy,

    /**
     * Aluminum-alloy-steel wire.
     */
    aluminumAlloySteel,

    /**
     * Aluminum-steel wire.
     */
    aluminumSteel,

    /**
     * Copper wire.
     */
    copper,

    /**
     * Copper cadmium wire.
     */
    copperCadmium,

    /**
     * Other wire material.
     */
    other,

    /**
     * Steel wire.
     */
    steel

}
