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
import com.zepben.evolve.services.common.UNKNOWN_DOUBLE
import com.zepben.evolve.services.network.ResistanceReactance
import com.zepben.evolve.services.network.mergeIfIncomplete

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
class TransformerStarImpedance(mRID: String = "") : IdentifiedObject(mRID) {

    var r: Double?
        get() = _r.takeIf { it != UNKNOWN_DOUBLE }
        set(value) {
            _r = value ?: UNKNOWN_DOUBLE
        }
    var r0: Double?
        get() = _r0.takeIf { it != UNKNOWN_DOUBLE }
        set(value) {
            _r0 = value ?: UNKNOWN_DOUBLE
        }
    var x: Double?
        get() = _x.takeIf { it != UNKNOWN_DOUBLE }
        set(value) {
            _x = value ?: UNKNOWN_DOUBLE
        }
    var x0: Double?
        get() = _x0.takeIf { it != UNKNOWN_DOUBLE }
        set(value) {
            _x0 = value ?: UNKNOWN_DOUBLE
        }

    var transformerEndInfo: TransformerEndInfo? = null

    //
    // NOTE: These fields are using non null primitive backing types to save memory.
    //
    private var _r: Double = UNKNOWN_DOUBLE
    private var _r0: Double = UNKNOWN_DOUBLE
    private var _x: Double = UNKNOWN_DOUBLE
    private var _x0: Double = UNKNOWN_DOUBLE

    /**
     * Get the [ResistanceReactance] for this [TransformerStarImpedance]. If any values are missing
     * attempt to calculate them from the [TransformerEndInfo] tests.
     */
    fun resistanceReactance(): ResistanceReactance =
        ResistanceReactance(r, r0, x, x0).mergeIfIncomplete {
            transformerEndInfo?.calculateResistanceReactanceFromTests()
        }

}