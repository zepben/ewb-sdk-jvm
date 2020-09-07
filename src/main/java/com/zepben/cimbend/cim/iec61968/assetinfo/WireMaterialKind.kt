/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.cim.iec61968.assetinfo

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
