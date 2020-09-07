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

import com.zepben.cimbend.cim.iec61970.base.core.PowerSystemResource

/**
 * Mechanism for changing transformer winding tap positions.
 *
 * @property controlEnabled Specifies the regulation status of the equipment.  True is regulating, false is not regulating.
 * @property highStep Highest possible tap step position, advance from neutral. The attribute shall be greater than lowStep.
 * @property lowStep Lowest possible tap step position, retard from neutral.
 * @property neutralStep The neutral tap step position for this winding.
 *                       The attribute shall be equal or greater than lowStep and equal or less than highStep.
 * @property neutralU Voltage at which the winding operates at the neutral tap setting.
 * @property normalStep The tap step position used in "normal" network operation for this winding. For a "Fixed" tap changer indicates the current physical tap setting.
 *                      The attribute shall be equal or greater than lowStep and equal or less than highStep.
 * @property step Tap changer position.
 *                Starting step for a steady state solution. Non integer values are allowed to support continuous tap variables.
 *                The reasons for continuous value are to support study cases where no discrete tap changers has yet been designed,
 *                a solutions where a narrow voltage band force the tap step to oscillate or accommodate for a continuous solution as input.
 *                The attribute shall be equal or greater than lowStep and equal or less than highStep.
 */
abstract class TapChanger(mRID: String = "") : PowerSystemResource(mRID) {

    var controlEnabled: Boolean = true

    var highStep: Int = 1
        set(value) {
            check(value > lowStep) { "high step [$value] must be greater than low step [$lowStep]." }
            field = value
        }

    var lowStep: Int = 0
        set(value) {
            check(value < highStep) { "low step [$value] must be lower than high step [$highStep]." }
            field = value
        }

    var neutralStep: Int = 0
        set(value) {
            check(value in lowStep..highStep) { "neutral step [$value] must be between high step [$highStep] and low step [$lowStep]." }
            field = value
        }

    var neutralU: Int = 0

    var normalStep: Int = 0
        set(value) {
            check(value in lowStep..highStep) { "normal step [$value] must be between high step [$highStep] and low step [$lowStep]." }
            field = value
        }

    var step: Double = 0.0
        set(value) {
            check(value >= lowStep && value <= highStep) { "step [$value] must be between high step [$highStep] and low step [$lowStep]." }
            field = value
        }
}
