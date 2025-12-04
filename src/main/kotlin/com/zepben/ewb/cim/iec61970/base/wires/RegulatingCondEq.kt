/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

/**
 * A type of conducting equipment that can regulate a quantity (i.e. voltage or flow) at a specific point in the network.
 * @property controlEnabled Specifies the regulation status of the equipment.  True is regulating, false is not regulating.
 * @property regulatingControl The [RegulatingControl] associated with this [RegulatingCondEq]
 */
abstract class RegulatingCondEq(mRID: String) : EnergyConnection(mRID) {

    var controlEnabled: Boolean? = null
    var regulatingControl: RegulatingControl? = null
        set(value) {
            field =
                if (field == null || field === value) value else throw IllegalStateException("regulatingControl has already been set to $field. Cannot set this field again")
        }
}
