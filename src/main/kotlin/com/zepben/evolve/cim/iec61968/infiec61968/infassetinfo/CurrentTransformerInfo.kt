/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61968.infiec61968.infassetinfo

import com.zepben.evolve.cim.iec61968.assets.AssetInfo
import com.zepben.evolve.cim.iec61968.infiec61968.infcommon.Ratio

/**
 * Properties of current transformer asset.
 */
class CurrentTransformerInfo @JvmOverloads constructor(mRID: String = "") : AssetInfo(mRID) {

    /**
     * CT accuracy classification.
     */
    var accuracyClass: String? = null

    /**
     * Accuracy limit.
     */
    var accuracyLimit: Double? = null

    /**
     * Number of cores.
     */
    var coreCount: Int? = null

    /**
     * CT classification; i.e. class 10P.
     */
    var ctClass: String? = null

    /**
     * Maximum voltage in volts across the secondary terminals where the CT still displays linear characteristicts.
     */
    var kneePointVoltage: Int? = null

    /**
     * Maximum ratio between the primary and secondary current.
     */
    var maxRatio: Ratio? = null

    /**
     * Nominal ratio between the primary and secondary current; i.e. 100:5
     */
    var nominalRatio: Ratio? = null

    /**
     * Ratio for the primary winding tap changer (numerator).
     */
    var primaryRatio: Double? = null

    /**
     * Rated current on the primary side in amperes.
     */
    var ratedCurrent: Int? = null

    /**
     * Full load secondary (FLS) rating for secondary winding in amperes.
     */
    var secondaryFlsRating: Int? = null

    /**
     * Ratio for the secondary winding tap changer (denominator).
     */
    var secondaryRatio: Double? = null

    /**
     * Intended usage of the CT; i.e. metering, protection.
     */
    var usage: String? = null

}
