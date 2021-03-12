/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.wires

import com.zepben.evolve.cim.iec61968.assetinfo.TransformerEndInfo
import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject

/**
 * Transformer star impedance (Pi-model) that accurately reflects impedance for transformers with 2 or 3 windings. For transformers
 * with 4 or more windings, TransformerMeshImpedance class shall be used.
 *
 * For transmission networks use PowerTransformerEnd impedances (r, r0, x, x0, b, b0, g and g0).
 *
 * @property r Resistance of the transformer end.
 * @property r0 Zero sequence series resistance of the transformer end.
 * @property x Positive sequence series reactance of the transformer end.
 * @property x0 Zero sequence series reactance of the transformer end.
 * @property transformerEndInfo Transformer end datasheet used to calculate this transformer star impedance.
 */
class TransformerStarImpedance(mRID: String = "") : IdentifiedObject(mRID) {

    var r: Double = Double.NaN
    var r0: Double = Double.NaN
    var x: Double = Double.NaN
    var x0: Double = Double.NaN

    var transformerEndInfo: TransformerEndInfo? = null

}
