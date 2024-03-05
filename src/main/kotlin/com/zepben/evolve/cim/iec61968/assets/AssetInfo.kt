/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61968.assets

import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject

/**
 * Set of attributes of an asset, representing typical datasheet information of a physical device that can be instantiated and shared in different data
 * exchange contexts:
 * - as attributes of an asset instance (installed or in stock)
 * - as attributes of an asset model (product by a manufacturer)
 * - as attributes of a type asset (generic type of asset as used in designs/extension planning).
 */
abstract class AssetInfo(mRID: String = "") : IdentifiedObject(mRID)
