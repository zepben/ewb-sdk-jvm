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
 * Properties of potential transformer asset.
 */
class PotentialTransformerInfo @JvmOverloads constructor(mRID: String = "") : AssetInfo(mRID) {

    /**
     * PT accuracy classification.
     */
    var accuracyClass: String? = null

    /**
     * Nominal ratio between the primary and secondary voltage.
     */
    var nominalRatio: Ratio? = null

    /**
     * Ratio for the primary winding tap changer (numerator).
     */
    var primaryRatio: Double? = null

    /**
     * Potential transformer (PT) classification covering burden.
     */
    var ptClass: String? = null

    /**
     * Rated voltage on the primary side in Volts.
     */
    var ratedVoltage: Int? = null

    /**
     * Ratio for the secondary winding tap changer (denominator).
     */
    var secondaryRatio: Double? = null

}
