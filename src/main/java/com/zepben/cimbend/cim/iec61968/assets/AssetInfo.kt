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
package com.zepben.cimbend.cim.iec61968.assets

import com.zepben.cimbend.cim.iec61970.base.core.IdentifiedObject

/**
 * Set of attributes of an asset, representing typical datasheet information of a physical device that can be instantiated and shared in different data
 * exchange contexts:
 * - as attributes of an asset instance (installed or in stock)
 * - as attributes of an asset model (product by a manufacturer)
 * - as attributes of a type asset (generic type of an asset as used in designs/extension planning).
 */
abstract class AssetInfo(mRID: String = "") : IdentifiedObject(mRID)

