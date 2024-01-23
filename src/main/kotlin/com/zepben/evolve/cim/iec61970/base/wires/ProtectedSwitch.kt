/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.wires

/**
 * A ProtectedSwitch is a switching device that can be operated by ProtectionEquipment.
 *
 * @property breakingCapacity The maximum fault current in amps a breaking device can break safely under prescribed conditions of use.
 */
abstract class ProtectedSwitch(mRID: String = "") : Switch(mRID) {

    var breakingCapacity: Int? = null

}
