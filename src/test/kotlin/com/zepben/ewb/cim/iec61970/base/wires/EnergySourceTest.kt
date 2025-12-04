/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

import com.zepben.ewb.services.common.extensions.typeNameAndMRID
import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.ewb.utils.PrivateCollectionValidator
import com.zepben.testutils.exception.ExpectException
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class EnergySourceTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(EnergySource("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val energySource = EnergySource(generateId())

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
        assertThat(energySource.isExternalGrid, nullValue())
        assertThat(energySource.rMin, nullValue())
        assertThat(energySource.rnMin, nullValue())
        assertThat(energySource.r0Min, nullValue())
        assertThat(energySource.xMin, nullValue())
        assertThat(energySource.xnMin, nullValue())
        assertThat(energySource.x0Min, nullValue())
        assertThat(energySource.rMax, nullValue())
        assertThat(energySource.rnMax, nullValue())
        assertThat(energySource.r0Max, nullValue())
        assertThat(energySource.xMax, nullValue())
        assertThat(energySource.xnMax, nullValue())
        assertThat(energySource.x0Max, nullValue())

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
        energySource.isExternalGrid = true
        energySource.rMin = 13.0
        energySource.rnMin = 14.0
        energySource.r0Min = 15.0
        energySource.xMin = 16.0
        energySource.xnMin = 17.0
        energySource.x0Min = 18.0
        energySource.rMax = 19.0
        energySource.rnMax = 20.0
        energySource.r0Max = 21.0
        energySource.xMax = 22.0
        energySource.xnMax = 23.0
        energySource.x0Max = 24.0

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
        assertThat(energySource.isExternalGrid, equalTo(true))
        assertThat(energySource.rMin, equalTo(13.0))
        assertThat(energySource.rnMin, equalTo(14.0))
        assertThat(energySource.r0Min, equalTo(15.0))
        assertThat(energySource.xMin, equalTo(16.0))
        assertThat(energySource.xnMin, equalTo(17.0))
        assertThat(energySource.x0Min, equalTo(18.0))
        assertThat(energySource.rMax, equalTo(19.0))
        assertThat(energySource.rnMax, equalTo(20.0))
        assertThat(energySource.r0Max, equalTo(21.0))
        assertThat(energySource.xMax, equalTo(22.0))
        assertThat(energySource.xnMax, equalTo(23.0))
        assertThat(energySource.x0Max, equalTo(24.0))
    }

    @Test
    internal fun assignsEnergySourceToEnergySourcePhaseIfMissing() {
        val energySource = EnergySource(generateId())
        val phase = EnergySourcePhase(generateId())

        energySource.addPhase(phase)
        assertThat(phase.energySource, equalTo(energySource))
    }

    @Test
    internal fun rejectsEnergySourcePhaseWithWrongEnergySource() {
        val energySource1 = EnergySource(generateId())
        val energySource2 = EnergySource(generateId())
        val phase = EnergySourcePhase(generateId()).apply { energySource = energySource2 }

        ExpectException.expect { energySource1.addPhase(phase) }
            .toThrow<IllegalArgumentException>()
            .withMessage("${phase.typeNameAndMRID()} `energySource` property references ${energySource2.typeNameAndMRID()}, expected ${energySource1.typeNameAndMRID()}.")
    }

    @Test
    internal fun energySourcePhases() {
        PrivateCollectionValidator.validateUnordered(
            ::EnergySource,
            ::EnergySourcePhase,
            EnergySource::phases,
            EnergySource::numPhases,
            EnergySource::getPhase,
            EnergySource::addPhase,
            EnergySource::removePhase,
            EnergySource::clearPhases
        )
    }

}
