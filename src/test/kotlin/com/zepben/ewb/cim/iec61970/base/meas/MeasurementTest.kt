/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.meas

import com.zepben.ewb.cim.iec61970.base.core.PhaseCode
import com.zepben.ewb.cim.iec61970.base.domain.UnitSymbol
import com.zepben.ewb.cim.iec61970.base.scada.RemoteSource
import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class MeasurementTest {
    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(object : Measurement("id") {}.mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val measurement = object : Measurement(generateId()) {}
        val remoteSource = RemoteSource(generateId())

        assertThat(measurement.powerSystemResourceMRID, nullValue())
        assertThat(measurement.remoteSource, nullValue())
        assertThat(measurement.terminalMRID, nullValue())
        assertThat(measurement.phases, equalTo(PhaseCode.ABC))
        assertThat(measurement.unitSymbol, equalTo(UnitSymbol.NONE))

        measurement.powerSystemResourceMRID = "powerSystemResourceMRID"
        measurement.remoteSource = remoteSource
        measurement.terminalMRID = "terminalMRID"
        measurement.phases = PhaseCode.XYN
        measurement.unitSymbol = UnitSymbol.BAR

        assertThat(measurement.powerSystemResourceMRID, equalTo("powerSystemResourceMRID"))
        assertThat(measurement.remoteSource, equalTo(remoteSource))
        assertThat(measurement.terminalMRID, equalTo("terminalMRID"))
        assertThat(measurement.phases, equalTo(PhaseCode.XYN))
        assertThat(measurement.unitSymbol, equalTo(UnitSymbol.BAR))
    }

}
