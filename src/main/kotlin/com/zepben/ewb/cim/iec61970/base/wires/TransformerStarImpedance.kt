/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

import com.zepben.ewb.cim.iec61968.assetinfo.TransformerEndInfo
import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.services.network.ResistanceReactance
import com.zepben.ewb.services.network.mergeIfIncomplete

/**
 * Transformer star impedance (Pi-model) that accurately reflects impedance for transformers with 2 or 3 windings. For transformers
 * with 4 or more windings, TransformerMeshImpedance class shall be used.
 *
 * For transmission networks use PowerTransformerEnd impedances (r, r0, x, x0, b, b0, g and g0).
 *
 * @property r Resistance of the transformer end in ohms.
 * @property r0 Zero sequence series resistance of the transformer end in ohms.
 * @property x Positive sequence series reactance of the transformer end in ohms.
 * @property x0 Zero sequence series reactance of the transformer end in ohms.
 * @property transformerEndInfo Transformer end datasheet used to calculate this transformer star impedance.
 */
class TransformerStarImpedance @JvmOverloads constructor(mRID: String = "") : IdentifiedObject(mRID) {

    var r: Double? = null
    var r0: Double? = null
    var x: Double? = null
    var x0: Double? = null

    var transformerEndInfo: TransformerEndInfo? = null

    /**
     * Get the [ResistanceReactance] for this [TransformerStarImpedance]. If any values are missing
     * attempt to calculate them from the [TransformerEndInfo] tests.
     */
    fun resistanceReactance(): ResistanceReactance =
        ResistanceReactance(r, x, r0, x0).mergeIfIncomplete {
            transformerEndInfo?.calculateResistanceReactanceFromTests()
        }

}
