/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.network.model

import com.zepben.cimbend.cim.iec61968.assets.Pole
import com.zepben.cimbend.cim.iec61968.metering.Meter
import com.zepben.cimbend.cim.iec61970.base.core.Substation
import com.zepben.cimbend.cim.iec61970.base.meas.*
import com.zepben.cimbend.cim.iec61970.base.wires.PowerTransformer
import com.zepben.cimbend.cim.iec61970.base.wires.PowerTransformerEnd
import com.zepben.cimbend.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.cimbend.cim.iec61970.infiec61970.feeder.Loop
import com.zepben.cimbend.measurement.toPb
import com.zepben.cimbend.network.NetworkService
import com.zepben.cimbend.network.fillFields
import com.zepben.testutils.junit.SystemLogExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class NetworkCimToProtoTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val validator = NetworkCimToProtoTestValidator()

    @Test
    internal fun convertsPowerTransformer() {
        val networkService = NetworkService()
        val cim = PowerTransformer()
        validator.validate(cim, cim.toPb())
        validator.validate(cim.fillFields(networkService), cim.toPb())
    }

    @Test
    internal fun convertsPowerTransformerEnd() {
        val networkService = NetworkService()
        val cim = PowerTransformerEnd()
        validator.validate(cim, cim.toPb())
        validator.validate(cim.fillFields(networkService), cim.toPb())
    }

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
