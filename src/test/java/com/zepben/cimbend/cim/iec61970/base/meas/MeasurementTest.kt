/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.cim.iec61970.base.meas

import com.zepben.cimbend.cim.iec61970.base.core.PhaseCode
import com.zepben.cimbend.cim.iec61970.base.core.Terminal
import com.zepben.cimbend.cim.iec61970.base.domain.UnitSymbol
import com.zepben.cimbend.cim.iec61970.base.scada.RemoteSource
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class MeasurementTest {
    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        MatcherAssert.assertThat(object : Measurement() {}.mRID, Matchers.not(Matchers.equalTo("")))
        MatcherAssert.assertThat(object : Measurement("id") {}.mRID, Matchers.equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val measurement = object : Measurement() {}
        val remoteSource = RemoteSource()

        assertThat(measurement.powerSystemResourceMRID, Matchers.nullValue())
        assertThat(measurement.remoteSource, Matchers.nullValue())
        assertThat(measurement.terminalMRID, Matchers.nullValue())
        assertThat(measurement.phases, Matchers.`is`(PhaseCode.ABC))
        assertThat(measurement.unitSymbol, Matchers.`is`(UnitSymbol.NONE))

        measurement.powerSystemResourceMRID = "powerSystemResourceMRID"
        measurement.remoteSource = remoteSource
        measurement.terminalMRID = "terminalMRID"
        measurement.phases = PhaseCode.XYN
        measurement.unitSymbol = UnitSymbol.BAR

        assertThat(measurement.powerSystemResourceMRID, equalTo("powerSystemResourceMRID"))
        assertThat(measurement.remoteSource, equalTo(remoteSource))
        assertThat(measurement.terminalMRID, equalTo("terminalMRID"))
        assertThat(measurement.phases, Matchers.`is`(PhaseCode.XYN))
        assertThat(measurement.unitSymbol, Matchers.`is`(UnitSymbol.BAR))
    }
}
