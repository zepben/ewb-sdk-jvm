/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.wires

import com.zepben.evolve.utils.PrivateCollectionValidator
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class EnergySourceTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(EnergySource().mRID, not(equalTo("")))
        assertThat(EnergySource("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val energySource = EnergySource()

        assertThat(energySource.activePower, nullValue())
        assertThat(energySource.reactivePower, nullValue())
        assertThat(energySource.voltageAngle, nullValue())
        assertThat(energySource.voltageMagnitude, nullValue())
        assertThat(energySource.pMax, nullValue())
        assertThat(energySource.pMin, nullValue())
        assertThat(energySource.r, nullValue())
        assertThat(energySource.r0, nullValue())
        assertThat(energySource.rn, nullValue())
        assertThat(energySource.x, nullValue())
        assertThat(energySource.x0, nullValue())
        assertThat(energySource.xn, nullValue())

        energySource.activePower = 1.0
        energySource.reactivePower = 2.0
        energySource.voltageAngle = 3.0
        energySource.voltageMagnitude = 4.0
        energySource.pMax = 5.0
        energySource.pMin = 6.0
        energySource.r = 7.0
        energySource.r0 = 8.0
        energySource.rn = 9.0
        energySource.x = 10.0
        energySource.x0 = 11.0
        energySource.xn = 12.0

        assertThat(energySource.activePower, equalTo(1.0))
        assertThat(energySource.reactivePower, equalTo(2.0))
        assertThat(energySource.voltageAngle, equalTo(3.0))
        assertThat(energySource.voltageMagnitude, equalTo(4.0))
        assertThat(energySource.pMax, equalTo(5.0))
        assertThat(energySource.pMin, equalTo(6.0))
        assertThat(energySource.r, equalTo(7.0))
        assertThat(energySource.r0, equalTo(8.0))
        assertThat(energySource.rn, equalTo(9.0))
        assertThat(energySource.x, equalTo(10.0))
        assertThat(energySource.x0, equalTo(11.0))
        assertThat(energySource.xn, equalTo(12.0))
    }

    @Test
    internal fun energySourcePhases() {
        PrivateCollectionValidator.validate(
            { EnergySource() },
            { id, _ -> EnergySourcePhase(id) },
            EnergySource::numPhases,
            EnergySource::getPhase,
            EnergySource::phases,
            EnergySource::addPhase,
            EnergySource::removePhase,
            EnergySource::clearPhases
        )
    }
}
