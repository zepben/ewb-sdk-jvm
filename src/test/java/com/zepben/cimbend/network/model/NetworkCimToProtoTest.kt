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
package com.zepben.cimbend.network.model

import com.zepben.cimbend.cim.iec61968.assets.Pole
import com.zepben.cimbend.cim.iec61968.metering.Meter
import com.zepben.cimbend.cim.iec61970.base.core.Substation
import com.zepben.cimbend.cim.iec61970.base.meas.*
import com.zepben.cimbend.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.cimbend.cim.iec61970.infiec61970.feeder.Loop
import com.zepben.cimbend.measurement.toPb
import com.zepben.cimbend.network.NetworkService
import com.zepben.cimbend.network.fillFields
import com.zepben.test.util.junit.SystemLogExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class NetworkCimToProtoTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val validator = NetworkCimToProtoTestValidator()

    @Test
    internal fun convertsMeters() {
        val cim = Meter()
        validator.validate(cim, cim.toPb())
        validator.validate(cim.fillFields(NetworkService()), cim.toPb())
    }

    @Test
    internal fun convertsSubstations() {
        val cim = Substation()
        validator.validate(cim, cim.toPb())
        validator.validate(cim.fillFields(NetworkService()), cim.toPb())
    }

    @Test
    internal fun convertsCircuits() {
        val cim = Circuit()
        validator.validate(cim, cim.toPb())
        validator.validate(cim.fillFields(NetworkService()), cim.toPb())
    }

    @Test
    internal fun convertsLoops() {
        val cim = Loop()
        validator.validate(cim, cim.toPb())
        validator.validate(cim.fillFields(NetworkService()), cim.toPb())
    }

    @Test
    internal fun convertsPoles() {
        val cim = Pole()
        validator.validate(cim, cim.toPb())
        validator.validate(cim.fillFields(NetworkService()), cim.toPb())
    }

    @Test
    internal fun convertsAnalog() {
        val cim = Analog()
        validator.validate(cim, cim.toPb())
        validator.validate(cim.fillFields(NetworkService()), cim.toPb())
    }

    @Test
    internal fun convertsAccumulator() {
        val cim = Accumulator()
        validator.validate(cim, cim.toPb())
        validator.validate(cim.fillFields(NetworkService()), cim.toPb())
    }

    @Test
    internal fun convertsDiscrete() {
        val cim = Discrete()
        validator.validate(cim, cim.toPb())
        validator.validate(cim.fillFields(NetworkService()), cim.toPb())
    }

    @Test
    internal fun convertsAnalogValue() {
        val cim = AnalogValue()
        validator.validate(cim, cim.toPb())
        validator.validate(cim.fillFields(NetworkService()), cim.toPb())
    }

    @Test
    internal fun convertsAccumulatorValue() {
        val cim = AccumulatorValue()
        validator.validate(cim, cim.toPb())
        validator.validate(cim.fillFields(NetworkService()), cim.toPb())
    }

    @Test
    internal fun convertsDiscreteValue() {
        val cim = DiscreteValue()
        validator.validate(cim, cim.toPb())
        validator.validate(cim.fillFields(NetworkService()), cim.toPb())
    }
}
