/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.wires

import com.zepben.evolve.services.common.extensions.asUnmodifiable
import com.zepben.protobuf.cim.iec61970.base.protection.RecloseSequence

/**
 * A ProtectedSwitch is a switching device that can be operated by ProtectionEquipment.
 *
 * @property breakingCapacity The maximum fault current in amps a breaking device can break safely under prescribed conditions of use.
 */
abstract class ProtectedSwitch(mRID: String = "") : Switch(mRID) {

    var breakingCapacity: Int? = null
    private var _recloseSequencesById: MutableMap<String, RecloseSequence>? = null

    val recloseSequences: Collection<RecloseSequence> get() = _recloseSequencesById?.values.asUnmodifiable()

//    fun numRecloseSequences(): Int = _recloseSequencesById?.size ?: 0
//
//    fun getRecloseSequence(mRID: String): RecloseSequence? = _recloseSequencesById?.get(mRID)
//
//    fun addRecloseSequence(recloseSequence: RecloseSequence): ProtectedSwitch {
//
//    }

}
