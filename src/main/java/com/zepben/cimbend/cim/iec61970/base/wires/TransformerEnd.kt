/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
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
