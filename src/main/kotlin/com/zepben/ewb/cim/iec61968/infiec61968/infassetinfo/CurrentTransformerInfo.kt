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
 * Properties of current transformer asset.
 *
 * @property accuracyClass CT accuracy classification.
 * @property accuracyLimit Accuracy limit.
 * @property coreCount Number of cores.
 * @property ctClass CT classification; i.e. class 10P.
 * @property kneePointVoltage Maximum voltage in volts across the secondary terminals where the CT still displays linear characteristics.
 * @property maxRatio Maximum ratio between the primary and secondary current.
 * @property nominalRatio Nominal ratio between the primary and secondary current; i.e. 100:5
 * @property primaryRatio Ratio for the primary winding tap changer (numerator).
 * @property ratedCurrent Rated current on the primary side in amperes.
 * @property secondaryFlsRating Full load secondary (FLS) rating for secondary winding in amperes.
 * @property secondaryRatio Ratio for the secondary winding tap changer (denominator).
 * @property usage Intended usage of the CT; i.e. metering, protection.
 */
class CurrentTransformerInfo @JvmOverloads constructor(mRID: String = "") : AssetInfo(mRID) {

    var accuracyClass: String? = null
    var accuracyLimit: Double? = null
    var coreCount: Int? = null
    var ctClass: String? = null
    var kneePointVoltage: Int? = null
    var maxRatio: Ratio? = null
    var nominalRatio: Ratio? = null
    var primaryRatio: Double? = null
    var ratedCurrent: Int? = null
    var secondaryFlsRating: Int? = null
    var secondaryRatio: Double? = null
    var usage: String? = null

}
