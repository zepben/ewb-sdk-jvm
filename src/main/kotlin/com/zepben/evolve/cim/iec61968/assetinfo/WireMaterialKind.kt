/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61968.assetinfo

/**
 * Kind of wire material.
 * @property UNKNOWN
 * @property aaac Aluminum-alloy conductor steel reinforced.
 * @property acsr Aluminum conductor steel reinforced.
 * @property acsrAz Aluminum conductor steel reinforced, aluminumized steel core
 * @property aluminum Aluminum wire.
 * @property aluminumAlloy Aluminum-alloy wire.
 * @property aluminumAlloySteel Aluminum-alloy-steel wire.
 * @property aluminumSteel Aluminum-steel wire.
 * @property copper Copper wire.
 * @property copperCadmium Copper cadmium wire.
 * @property other Other wire material.
 * @property steel Steel wire.
 */
@Suppress("EnumEntryName")
enum class WireMaterialKind {

    UNKNOWN,
    aaac,
    acsr,
    acsrAz,
    aluminum,
    aluminumAlloy,
    aluminumAlloySteel,
    aluminumSteel,
    copper,
    copperCadmium,
    other,
    steel
}
