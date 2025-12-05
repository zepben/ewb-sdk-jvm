/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.infiec61968.infassetinfo

import com.zepben.ewb.cim.iec61968.assets.AssetInfo
import com.zepben.ewb.cim.iec61968.infiec61968.infcommon.Ratio

/**
 * Properties of potential transformer asset.
 *
 * @property accuracyClass PT accuracy classification.
 * @property nominalRatio Nominal ratio between the primary and secondary voltage.
 * @property primaryRatio Ratio for the primary winding tap changer (numerator).
 * @property ptClass Potential transformer (PT) classification covering burden.
 * @property ratedVoltage Rated voltage on the primary side in Volts.
 * @property secondaryRatio Ratio for the secondary winding tap changer (denominator).
 */
class PotentialTransformerInfo(mRID: String) : AssetInfo(mRID) {

    var accuracyClass: String? = null
    var nominalRatio: Ratio? = null
    var primaryRatio: Double? = null
    var ptClass: String? = null
    var ratedVoltage: Int? = null
    var secondaryRatio: Double? = null

}
