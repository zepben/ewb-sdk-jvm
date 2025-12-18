/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.assetinfo

import com.zepben.ewb.cim.iec61968.assets.AssetInfo

/**
 * Wire data that can be specified per line segment phase, or for the line segment as a whole in case its phases all have the same wire characteristics.
 *
 * @property ratedCurrent Current carrying capacity of the wire under stated thermal conditions.
 * @property material Conductor material.
 * @property sizeDescription Describes the wire gauge or cross section (e.g., 4/0,
 * @property strandCount Number of strands in the conductor.
 * @property coreStrandCount (if used) Number of strands in the steel core.
 * @property insulated True if conductor is insulated.
 * @property insulationMaterial (if insulated conductor) Material used for insulation.
 * @property insulationThickness (if insulated conductor) Thickness of the insulation.
 */
abstract class WireInfo(mRID: String) : AssetInfo(mRID) {

    var ratedCurrent: Int? = null

    var material: WireMaterialKind = WireMaterialKind.UNKNOWN

    var sizeDescription: String? = null

    var strandCount: String? = null

    var coreStrandCount: String? = null

    var insulated: Boolean? = null

    var insulationMaterial: WireInsulationKind = WireInsulationKind.UNKNOWN

    var insulationThickness: Double? = null

}