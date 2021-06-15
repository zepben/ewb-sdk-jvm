/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61968.assetinfo

/**
 * Open-circuit test results verify winding turn ratios and phase shifts. They include induced voltage and phase shift measurements on open-circuit windings,
 * with voltage applied to the energised end. For three-phase windings, the excitation can be a positive sequence (the default) or a zero sequence.
 *
 * @property energisedEndStep Tap step number for the energised end of the test pair.
 * @property energisedEndVoltage Voltage applied to the winding (end) during test in volts.
 * @property openEndStep Tap step number for the open end of the test pair.
 * @property openEndVoltage Voltage measured at the open-circuited end, with the energised end set to rated voltage and all other ends open in volts.
 * @property phaseShift Phase shift measured at the open end with the energised end set to rated voltage and all other ends open in angle degrees.
 */
class OpenCircuitTest(mRID: String = "") : TransformerTest(mRID) {

    var energisedEndStep: Int? = null
    var energisedEndVoltage: Int? = null
    var openEndStep: Int? = null
    var openEndVoltage: Int? = null
    var phaseShift: Double? = null

}
