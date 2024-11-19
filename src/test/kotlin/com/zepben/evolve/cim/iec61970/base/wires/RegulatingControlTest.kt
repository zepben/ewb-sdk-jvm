/*
 * Copyright 2023 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.wires

import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.testdata.fillFields
import com.zepben.evolve.utils.PrivateCollectionValidator
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class RegulatingControlTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(object : RegulatingControl() {}.mRID, not(equalTo("")))
        assertThat(object : RegulatingControl("id") {}.mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val regulatingControl = object : RegulatingControl() {}

        assertThat(regulatingControl.ctPrimary, nullValue())
        assertThat(regulatingControl.minTargetDeadband, nullValue())
        assertThat(regulatingControl.discrete, nullValue())
        assertThat(regulatingControl.enabled, nullValue())
        assertThat(regulatingControl.mode, equalTo(RegulatingControlModeKind.UNKNOWN_CONTROL_MODE))
        assertThat(regulatingControl.monitoredPhase, equalTo(PhaseCode.NONE))
        assertThat(regulatingControl.targetDeadband, nullValue())
        assertThat(regulatingControl.targetValue, nullValue())
        assertThat(regulatingControl.enabled, nullValue())
        assertThat(regulatingControl.maxAllowedTargetValue, nullValue())
        assertThat(regulatingControl.minAllowedTargetValue, nullValue())
        assertThat(regulatingControl.ratedCurrent, nullValue())
        assertThat(regulatingControl.terminal, nullValue())

        regulatingControl.fillFields(NetworkService())

        assertThat(regulatingControl.ctPrimary, equalTo(1.0))
        assertThat(regulatingControl.minTargetDeadband, equalTo(2.0))
        assertThat(regulatingControl.discrete, equalTo(false))
        assertThat(regulatingControl.mode, equalTo(RegulatingControlModeKind.voltage))
        assertThat(regulatingControl.monitoredPhase, equalTo(PhaseCode.ABC))
        assertThat(regulatingControl.targetDeadband, equalTo(2.0f))
        assertThat(regulatingControl.targetValue, equalTo(100.0))
        assertThat(regulatingControl.enabled, equalTo(true))
        assertThat(regulatingControl.maxAllowedTargetValue, equalTo(200.0))
        assertThat(regulatingControl.minAllowedTargetValue, equalTo(50.0))
        assertThat(regulatingControl.ratedCurrent, equalTo(10.0))
        assertThat(regulatingControl.terminal, notNullValue())
    }

    @Test
    internal fun regulatingCondEqs() {
        PrivateCollectionValidator.validateUnordered(
            { object : RegulatingControl() {} },
            { id -> object : RegulatingCondEq(id) {} },
            RegulatingControl::regulatingCondEqs,
            RegulatingControl::numRegulatingCondEqs,
            RegulatingControl::getRegulatingCondEq,
            RegulatingControl::addRegulatingCondEq,
            RegulatingControl::removeRegulatingCondEq,
            RegulatingControl::clearRegulatingCondEqs
        )
    }
}
