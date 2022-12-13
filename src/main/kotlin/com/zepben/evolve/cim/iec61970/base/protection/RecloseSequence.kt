/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.protection

import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject

/**
 * A reclose sequence (open and close) is defined for each possible reclosure of a breaker.
 *
 * @property recloseDelay Indicates the time lapse in seconds before the reclose step will execute a reclose.
 * @property recloseStep Indicates the ordinal position of the reclose step relative to other steps in the sequence.
 */
class RecloseSequence(mRID: String = "") : IdentifiedObject(mRID) {

    var recloseDelay: Double? = null
    var recloseStep: Int? = null

}
