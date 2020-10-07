/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.cim.iec61970.base.wires

import com.zepben.cimbend.cim.iec61970.base.core.BaseVoltage
import com.zepben.cimbend.cim.iec61970.base.core.IdentifiedObject
import com.zepben.cimbend.cim.iec61970.base.core.Terminal

/**
 * A conducting connection point of a power transformer. It corresponds to a physical transformer winding terminal.
 * In earlier CIM versions, the TransformerWinding class served a similar purpose, but this class is more flexible
 * because it associates to terminal but is not a specialization of ConductingEquipment.
 *
 * @property grounded (for Yn and Zn connections) True if the neutral is solidly grounded.
 * @property rGround (for Yn and Zn connections) Resistance part of neutral impedance where 'grounded' is true
 * @property xGround (for Yn and Zn connections) Reactive part of neutral impedance where 'grounded' is true
 * @property baseVoltage Base voltage of the transformer end.  This is essential for PU calculation.
 * @property ratioTapChanger Ratio tap changer associated with this transformer end.
 * @property terminal The terminal of the transformer that this end is associated with
 */
abstract class TransformerEnd(mRID: String = "") : IdentifiedObject(mRID) {

    var grounded: Boolean = false
    var rGround: Double = 0.0
    var xGround: Double = 0.0
    var baseVoltage: BaseVoltage? = null
    var ratioTapChanger: RatioTapChanger? = null
    // TODO: Should we validate this terminal actually belongs to a transformer when it is set?
    var terminal: Terminal? = null
    var endNumber: Int = 0
}
