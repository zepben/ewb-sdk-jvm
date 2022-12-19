/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.protection

import com.zepben.evolve.cim.iec61968.infiec61968.infassetinfo.CurrentRelayInfo

/**
 * A device that checks current flow values in any direction or designated direction.
 *
 * @property currentLimit1 Current limit number 1 for inverse time pickup in amperes.
 * @property inverseTimeFlag Set true if the current relay has inverse time characteristic.
 * @property timeDelay1 Inverse time delay number 1 for current limit number 1 in seconds.
 * @property assetInfo Datasheet information for this CurrentRelay.
 */
class CurrentRelay(mRID: String = "") : ProtectionEquipment(mRID) {

    override var assetInfo: CurrentRelayInfo? = null

    var currentLimit1: Double? = null
    var inverseTimeFlag: Boolean? = null
    var timeDelay1: Double? = null

}
