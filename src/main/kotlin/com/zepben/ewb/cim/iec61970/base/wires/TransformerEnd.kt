/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

import com.zepben.ewb.cim.iec61968.assetinfo.PowerTransformerInfo
import com.zepben.ewb.cim.iec61970.base.core.BaseVoltage
import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.cim.iec61970.base.core.Terminal
import com.zepben.ewb.services.common.extensions.typeNameAndMRID
import com.zepben.ewb.services.network.ResistanceReactance

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
 * @property endNumber Number for this transformer end, corresponding to the end's order in the power transformer vector group or phase angle clock number.
 * Highest voltage winding should be 1. Each end within a power transformer should have a unique subsequent end number. Note the transformer end number need not
 * match the terminal sequence number.
 *
 * @property starImpedance (accurate for 2- or 3-winding transformers only) Pi-model impedances of this transformer end. By convention, for a two winding
 * transformer, the full values of the transformer should be entered on the high voltage end (endNumber=1).
 * When [starImpedance] is set here it indicates that impedance values are measured on the [TransformerEnd] itself rather than from the [PowerTransformer] data
 * sheet, and thus the corresponding [PowerTransformer] for this end cannot have an associated [PowerTransformerInfo].
 */
abstract class TransformerEnd(mRID: String = "") : IdentifiedObject(mRID) {

    var grounded: Boolean = false
    var rGround: Double? = null
    var xGround: Double? = null
    var baseVoltage: BaseVoltage? = null
    var ratioTapChanger: RatioTapChanger? = null

    var terminal: Terminal? = null
        set(value) {
            // We do not require the terminal's equipment to be non-null here, to accommodate reconstruction from protobuf messages in any order.
            if (value?.conductingEquipment != null) {
                require(value.conductingEquipment is PowerTransformer) {
                    "Cannot assign ${typeNameAndMRID()} to ${value.typeNameAndMRID()}, which is connected to " +
                        "${value.conductingEquipment?.typeNameAndMRID()} rather than a PowerTransformer."
                }
            }
            field = value
        }
    var endNumber: Int = 0

    var starImpedance: TransformerStarImpedance? = null
        set(value) {
            if (this is PowerTransformerEnd && value != null) {
                require(powerTransformer?.assetInfo == null) {
                    "Unable to use a star impedance for ${typeNameAndMRID()} directly because ${powerTransformer?.typeNameAndMRID()} references ${powerTransformer?.assetInfo?.typeNameAndMRID()}."
                }
            }
            field = value
        }

    /**
     * Interface for getting access to the resistance and reactance characteristics of this end. Implementation details will vary
     * based on end type.
     */
    open fun resistanceReactance(): ResistanceReactance =
        throw NotImplementedError("Unknown transformer end leaf type: ${typeNameAndMRID()}. Add support which should at least include `starImpedance?.resistanceReactance() ?: ResistanceReactance()`.")

}
